package com.isctem.ukraine.service;

import com.isctem.ukraine.algorithm.*;
import com.isctem.ukraine.data.UkraineData;
import com.isctem.ukraine.model.*;

import java.util.*;

/**
 * Service principal que coordena todas as operações do grafo
 */
public class GraphService {

    private Graph graph;
    private final Map<String, AlgorithmResult> cachedResults;

    public GraphService() {
        this.cachedResults = new HashMap<>();
        this.graph = UkraineData.createUkraineGraph();
    }

    public GraphService(Graph graph) {
        this.graph = graph;
        this.cachedResults = new HashMap<>();
    }

    // ========== GESTÃO DO GRAFO ==========

    public Graph getGraph() {
        return graph;
    }

    public void resetGraph() {
        this.graph = UkraineData.createUkraineGraph();
        clearCache();
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        clearCache();
    }

    // ========== EXECUÇÃO DE ALGORITMOS ==========

    /**
     * Executa um algoritmo específico
     */
    public AlgorithmResult executeAlgorithm(AlgorithmType type, String startId, String endId) {
        String cacheKey = type + "-" + startId + "-" + endId;

        if (cachedResults.containsKey(cacheKey)) {
            return cachedResults.get(cacheKey);
        }

        AlgorithmResult result = switch (type) {
            case BFS -> BFS.execute(graph, startId, endId);
            case DFS -> DFS.execute(graph, startId, endId);
            case DIJKSTRA -> Dijkstra.execute(graph, startId, endId);
            case KRUSKAL -> Kruskal.execute(graph);
            case PRIM -> Prim.execute(graph, startId);
        };

        cachedResults.put(cacheKey, result);
        return result;
    }

    /**
     * Executa TODOS os algoritmos e compara
     */
    public Map<AlgorithmType, AlgorithmResult> executeAllAlgorithms(String startId, String endId) {
        Map<AlgorithmType, AlgorithmResult> results = new LinkedHashMap<>();

        results.put(AlgorithmType.BFS, executeAlgorithm(AlgorithmType.BFS, startId, endId));
        results.put(AlgorithmType.DFS, executeAlgorithm(AlgorithmType.DFS, startId, endId));
        results.put(AlgorithmType.DIJKSTRA, executeAlgorithm(AlgorithmType.DIJKSTRA, startId, endId));
        results.put(AlgorithmType.KRUSKAL, executeAlgorithm(AlgorithmType.KRUSKAL, startId, endId));
        results.put(AlgorithmType.PRIM, executeAlgorithm(AlgorithmType.PRIM, startId, endId));

        return results;
    }

    /**
     * Encontra a melhor rota usando todos os algoritmos
     */
    public AlgorithmResult findBestRoute(String startId, String endId) {
        Map<AlgorithmType, AlgorithmResult> allResults = executeAllAlgorithms(startId, endId);

        AlgorithmResult best = null;
        double shortestDistance = Double.POSITIVE_INFINITY;

        for (AlgorithmResult result : allResults.values()) {
            if (result.isSuccess() && result.getMainRoute() != null) {
                double distance = result.getMainRoute().getTotalDistance();
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    best = result;
                }
            }
        }

        return best;
    }

    // ========== OPERAÇÕES DE SIMULAÇÃO ==========

    /**
     * Simula ataque russo destruindo conexões
     */
    public void simulateRussianAttack(double destructionPercent) {
        UkraineData.simulateWarDamage(graph, destructionPercent);
        clearCache();
    }

    /**
     * Destrói Oblast específico
     */
    public void destroyOblast(String oblastId) {
        graph.destroyOblast(oblastId);
        clearCache();
    }

    /**
     * Destrói conexão específica
     */
    public void destroyConnection(String fromId, String toId) {
        graph.destroyConnection(fromId, toId);
        clearCache();
    }

    /**
     * Repara todas as conexões destruídas
     */
    public void repairAll() {
        for (Connection conn : graph.getAllConnections()) {
            conn.setDestroyed(false);
            conn.setCondition(100);
        }
        for (Oblast oblast : graph.getAllOblasts()) {
            oblast.setDestroyed(false);
            oblast.setSupplyLevel(100);
        }
        clearCache();
    }

    // ========== CONSULTAS E ANÁLISES ==========

    /**
     * Retorna Oblasts mais críticos (baixo supply)
     */
    public List<Oblast> getCriticalOblasts() {
        return graph.getAllOblasts().stream()
                .filter(o -> o.getSupplyLevel() < 30)
                .sorted(Comparator.comparingInt(Oblast::getSupplyLevel))
                .toList();
    }

    /**
     * Retorna Oblasts seguros (oeste)
     */
    public List<Oblast> getSafeOblasts() {
        return graph.getAllOblasts().stream()
                .filter(o -> !o.isFrontline() && o.getSupplyLevel() > 70)
                .toList();
    }

    /**
     * Retorna Oblasts na frontline
     */
    public List<Oblast> getFrontlineOblasts() {
        return graph.getAllOblasts().stream()
                .filter(Oblast::isFrontline)
                .toList();
    }

    /**
     * Calcula conectividade do grafo
     */
    public double calculateConnectivity() {
        int totalPossible = graph.getNodeCount() * (graph.getNodeCount() - 1) / 2;
        int actualConnections = graph.getAllConnections().size();
        return (double) actualConnections / totalPossible * 100;
    }

    /**
     * Retorna hub mais importante (mais conexões)
     */
    public Oblast getMostConnectedHub() {
        return graph.getAllOblasts().stream()
                .max(Comparator.comparingInt(o -> graph.getNeighbors(o.getId()).size()))
                .orElse(null);
    }

    /**
     * Calcula distância total da rede
     */
    public double getTotalNetworkDistance() {
        return graph.getAllConnections().stream()
                .mapToDouble(Connection::getDistance)
                .sum();
    }

    /**
     * Retorna estatísticas completas
     */
    public NetworkStatistics getNetworkStatistics() {
        int totalOblasts = graph.getNodeCount();
        int destroyedOblasts = (int) graph.getAllOblasts().stream()
                .filter(Oblast::isDestroyed).count();
        int frontlineOblasts = (int) graph.getAllOblasts().stream()
                .filter(Oblast::isFrontline).count();

        int totalConnections = graph.getAllConnections().size();
        int destroyedConnections = (int) graph.getAllConnections().stream()
                .filter(Connection::isDestroyed).count();

        double avgSupplyLevel = graph.getAllOblasts().stream()
                .mapToInt(Oblast::getSupplyLevel)
                .average().orElse(0);

        double connectivity = calculateConnectivity();
        double totalDistance = getTotalNetworkDistance();
        Oblast mainHub = getMostConnectedHub();

        return new NetworkStatistics(
                totalOblasts, destroyedOblasts, frontlineOblasts,
                totalConnections, destroyedConnections,
                avgSupplyLevel, connectivity, totalDistance,
                mainHub != null ? mainHub.getName() : "N/A"
        );
    }

    // ========== ROTAS ESPECIAIS ==========

    /**
     * Calcula rota de abastecimento visitando todos os Oblasts críticos
     */
    public AlgorithmResult calculateSupplyRoute() {
        List<Oblast> criticalOblasts = getCriticalOblasts();

        if (criticalOblasts.isEmpty()) {
            return new AlgorithmResult.Builder()
                    .setAlgorithmName("Supply Route")
                    .setError("Nenhum Oblast crítico encontrado")
                    .build();
        }

        // Começar do hub mais conectado
        Oblast hub = getMostConnectedHub();
        String startId = hub != null ? hub.getId() : "kyiv";

        // Usar Dijkstra para conectar todos os pontos críticos
        List<Oblast> route = new ArrayList<>();
        route.add(graph.getOblast(startId));

        double totalDistance = 0;
        String current = startId;

        Set<String> visited = new HashSet<>();
        visited.add(startId);

        while (visited.size() < criticalOblasts.size() + 1) {
            String nearest = findNearestUnvisited(current, criticalOblasts, visited);
            if (nearest == null) break;

            AlgorithmResult segment = Dijkstra.execute(graph, current, nearest);
            if (segment.isSuccess()) {
                route.addAll(segment.getMainRoute().getPath().subList(1,
                        segment.getMainRoute().getPath().size()));
                totalDistance += segment.getMainRoute().getTotalDistance();
                current = nearest;
                visited.add(nearest);
            }
        }

        Route finalRoute = new Route.Builder()
                .setPath(route)
                .setTotalDistance(totalDistance)
                .setAlgorithm("Supply Route Optimizer")
                .setOptimal(false)
                .build();

        return new AlgorithmResult.Builder()
                .setAlgorithmName("Supply Route")
                .setMainRoute(finalRoute)
                .addMetadata("critical_oblasts", criticalOblasts.size())
                .build();
    }

    private String findNearestUnvisited(String current, List<Oblast> targets, Set<String> visited) {
        double minDist = Double.POSITIVE_INFINITY;
        String nearest = null;

        for (Oblast target : targets) {
            if (!visited.contains(target.getId())) {
                double dist = graph.getDistance(current, target.getId());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = target.getId();
                }
            }
        }

        return nearest;
    }

    private void clearCache() {
        cachedResults.clear();
    }

    // ========== CLASSE AUXILIAR ==========

    public record NetworkStatistics(
            int totalOblasts,
            int destroyedOblasts,
            int frontlineOblasts,
            int totalConnections,
            int destroyedConnections,
            double avgSupplyLevel,
            double connectivity,
            double totalDistance,
            String mainHub
    ) {
        @Override
        public String toString() {
            return String.format("""
                ========== ESTATÍSTICAS DA REDE ==========
                Oblasts Totais: %d
                Oblasts Destruídos: %d
                Oblasts na Frontline: %d
                Conexões Totais: %d
                Conexões Destruídas: %d
                Nível Médio de Suprimento: %.1f%%
                Conectividade: %.1f%%
                Distância Total: %.1f km
                Hub Principal: %s
                =========================================
                """,
                    totalOblasts, destroyedOblasts, frontlineOblasts,
                    totalConnections, destroyedConnections,
                    avgSupplyLevel, connectivity, totalDistance, mainHub
            );
        }
    }
}