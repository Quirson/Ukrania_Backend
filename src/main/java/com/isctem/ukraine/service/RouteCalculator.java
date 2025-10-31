package com.isctem.ukraine.service;

import com.isctem.ukraine.model.*;
import java.util.*;

/**
 * Service especializado em cálculo e comparação de rotas
 */
public class RouteCalculator {

    private final GraphService graphService;

    public RouteCalculator(GraphService graphService) {
        this.graphService = graphService;
    }

    /**
     * Compara resultados de múltiplos algoritmos
     */
    public ComparisonResult compareAlgorithms(String startId, String endId) {
        Map<AlgorithmType, AlgorithmResult> results =
                graphService.executeAllAlgorithms(startId, endId);

        return new ComparisonResult(results);
    }

    /**
     * Encontra as N melhores rotas alternativas
     */
    public List<Route> findTopNRoutes(String startId, String endId, int n) {
        Map<AlgorithmType, AlgorithmResult> results =
                graphService.executeAllAlgorithms(startId, endId);

        return results.values().stream()
                .filter(AlgorithmResult::isSuccess)
                .map(AlgorithmResult::getMainRoute)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(Route::getTotalDistance))
                .limit(n)
                .toList();
    }

    /**
     * Calcula rota de evacuação (da frontline para zona segura)
     */
    public Route calculateEvacuationRoute(String frontlineId) {
        Oblast frontline = graphService.getGraph().getOblast(frontlineId);
        if (frontline == null || !frontline.isFrontline()) {
            return null;
        }

        // Encontrar oblast seguro mais próximo
        List<Oblast> safeOblasts = graphService.getSafeOblasts();

        Route bestRoute = null;
        double shortestDist = Double.POSITIVE_INFINITY;

        for (Oblast safe : safeOblasts) {
            AlgorithmResult result = graphService.executeAlgorithm(
                    AlgorithmType.DIJKSTRA, frontlineId, safe.getId());

            if (result.isSuccess() && result.getMainRoute() != null) {
                double dist = result.getMainRoute().getTotalDistance();
                if (dist < shortestDist) {
                    shortestDist = dist;
                    bestRoute = result.getMainRoute();
                }
            }
        }

        return bestRoute;
    }

    /**
     * Calcula tour visitando múltiplos pontos (TSP simplificado)
     */
    public Route calculateTour(List<String> oblastIds) {
        if (oblastIds.size() < 2) return null;

        List<Oblast> path = new ArrayList<>();
        path.add(graphService.getGraph().getOblast(oblastIds.get(0)));

        double totalDistance = 0;

        for (int i = 0; i < oblastIds.size() - 1; i++) {
            String from = oblastIds.get(i);
            String to = oblastIds.get(i + 1);

            AlgorithmResult segment = graphService.executeAlgorithm(
                    AlgorithmType.DIJKSTRA, from, to);

            if (segment.isSuccess()) {
                Route segmentRoute = segment.getMainRoute();
                // Adicionar caminho sem duplicar o primeiro nó
                path.addAll(segmentRoute.getPath().subList(1, segmentRoute.getPath().size()));
                totalDistance += segmentRoute.getTotalDistance();
            }
        }

        return new Route.Builder()
                .setPath(path)
                .setTotalDistance(totalDistance)
                .setAlgorithm("Multi-Point Tour")
                .build();
    }

    /**
     * Calcula estatísticas de uma rota
     */
    public RouteStatistics analyzeRoute(Route route) {
        if (route == null) return null;

        List<Oblast> path = route.getPath();

        int frontlineCount = (int) path.stream().filter(Oblast::isFrontline).count();
        int safeCount = path.size() - frontlineCount;

        double avgSegmentDist = path.size() > 1 ?
                route.getTotalDistance() / (path.size() - 1) : 0;

        Oblast mostCritical = path.stream()
                .min(Comparator.comparingInt(Oblast::getSupplyLevel))
                .orElse(null);

        return new RouteStatistics(
                route.getTotalDistance(),
                path.size(),
                frontlineCount,
                safeCount,
                avgSegmentDist,
                mostCritical != null ? mostCritical.getName() : "N/A",
                route.getAlgorithmUsed()
        );
    }

    /**
     * Valida se uma rota é viável (sem conexões destruídas)
     */
    public boolean isRouteViable(Route route) {
        if (route == null) return false;

        List<Oblast> path = route.getPath();
        Graph graph = graphService.getGraph();

        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i).getId();
            String to = path.get(i + 1).getId();

            Connection conn = graph.getConnection(from, to);
            if (conn == null || conn.isDestroyed()) {
                return false;
            }

            if (path.get(i).isDestroyed() || path.get(i + 1).isDestroyed()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calcula tempo estimado de viagem (assumindo velocidade média)
     */
    public double estimateTravelTime(Route route, double avgSpeedKmH) {
        return route.getTotalDistance() / avgSpeedKmH;
    }

    /**
     * Calcula custo da rota (baseado em distância e condições)
     */
    public double calculateRouteCost(Route route) {
        double baseCost = route.getTotalDistance() * 10; // 10 units per km

        Graph graph = graphService.getGraph();
        List<Oblast> path = route.getPath();

        // Adicionar custo extra para zonas de frontline
        double frontlinePenalty = 0;
        for (Oblast oblast : path) {
            if (oblast.isFrontline()) {
                frontlinePenalty += 1000; // Alto custo por passar em frontline
            }
            if (oblast.getSupplyLevel() < 30) {
                frontlinePenalty += 500; // Custo médio por oblast crítico
            }
        }

        return baseCost + frontlinePenalty;
    }

    // ========== CLASSES AUXILIARES ==========

    public record RouteStatistics(
            double totalDistance,
            int stepsCount,
            int frontlineStops,
            int safeStops,
            double avgSegmentDistance,
            String mostCriticalPoint,
            String algorithm
    ) {
        @Override
        public String toString() {
            return String.format("""
                ========== ESTATÍSTICAS DA ROTA ==========
                Distância Total: %.2f km
                Número de Paradas: %d
                Paradas em Frontline: %d
                Paradas Seguras: %d
                Distância Média/Segmento: %.2f km
                Ponto Mais Crítico: %s
                Algoritmo Usado: %s
                ==========================================
                """,
                    totalDistance, stepsCount, frontlineStops, safeStops,
                    avgSegmentDistance, mostCriticalPoint, algorithm
            );
        }
    }

    public static class ComparisonResult {
        private final Map<AlgorithmType, AlgorithmResult> results;

        public ComparisonResult(Map<AlgorithmType, AlgorithmResult> results) {
            this.results = results;
        }

        public AlgorithmResult getFastest() {
            return results.values().stream()
                    .filter(AlgorithmResult::isSuccess)
                    .min(Comparator.comparingLong(AlgorithmResult::getExecutionTimeMs))
                    .orElse(null);
        }

        public AlgorithmResult getShortest() {
            return results.values().stream()
                    .filter(r -> r.isSuccess() && r.getMainRoute() != null)
                    .min(Comparator.comparingDouble(r -> r.getMainRoute().getTotalDistance()))
                    .orElse(null);
        }

        public AlgorithmResult getMostEfficient() {
            return results.values().stream()
                    .filter(AlgorithmResult::isSuccess)
                    .max(Comparator.comparingDouble(AlgorithmResult::getEfficiency))
                    .orElse(null);
        }

        public String getComparisonTable() {
            StringBuilder sb = new StringBuilder();

            sb.append("\n╔════════════════════════════════════════════════════════════════════╗\n");
            sb.append("║                  COMPARAÇÃO DE ALGORITMOS                          ║\n");
            sb.append("╠════════════════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ %-15s | %10s | %10s | %10s ║\n",
                    "Algoritmo", "Distância", "Tempo(ms)", "Nós Visit."));
            sb.append("╠════════════════════════════════════════════════════════════════════╣\n");

            for (Map.Entry<AlgorithmType, AlgorithmResult> entry : results.entrySet()) {
                AlgorithmResult result = entry.getValue();
                if (result.isSuccess() && result.getMainRoute() != null) {
                    sb.append(String.format("║ %-15s | %8.1f km | %8d ms | %10d ║\n",
                            entry.getKey().name(),
                            result.getMainRoute().getTotalDistance(),
                            result.getExecutionTimeMs(),
                            result.getNodesVisited()
                    ));
                }
            }

            sb.append("╠════════════════════════════════════════════════════════════════════╣\n");

            AlgorithmResult fastest = getFastest();
            AlgorithmResult shortest = getShortest();

            if (fastest != null) {
                sb.append(String.format("║ ⚡ Mais Rápido: %-48s ║\n",
                        fastest.getAlgorithmName()));
            }
            if (shortest != null) {
                sb.append(String.format("║ 🎯 Menor Distância: %-44s ║\n",
                        shortest.getAlgorithmName()));
            }

            sb.append("╚════════════════════════════════════════════════════════════════════╝\n");

            return sb.toString();
        }

        public Map<AlgorithmType, AlgorithmResult> getAllResults() {
            return results;
        }
    }
}