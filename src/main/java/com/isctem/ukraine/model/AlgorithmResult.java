package com.isctem.ukraine.model;

import java.util.*;

/**
 * Classe que encapsula o resultado da execução de um algoritmo.
 * Contém rotas, métricas de performance e informações adicionais.
 */
public class AlgorithmResult {
    private final String algorithmName;
    private final Route mainRoute;
    private final List<Route> alternativeRoutes;
    private final long executionTimeMs;
    private final int nodesVisited;
    private final int edgesExplored;
    private final Map<String, Object> metadata;
    private final boolean success;
    private final String errorMessage;

    /**
     * Construtor completo
     */
    private AlgorithmResult(Builder builder) {
        this.algorithmName = builder.algorithmName;
        this.mainRoute = builder.mainRoute;
        this.alternativeRoutes = new ArrayList<>(builder.alternativeRoutes);
        this.executionTimeMs = builder.executionTimeMs;
        this.nodesVisited = builder.nodesVisited;
        this.edgesExplored = builder.edgesExplored;
        this.metadata = new HashMap<>(builder.metadata);
        this.success = builder.success;
        this.errorMessage = builder.errorMessage;
    }

    // Getters
    public String getAlgorithmName() {
        return algorithmName;
    }

    public Route getMainRoute() {
        return mainRoute;
    }

    public List<Route> getAlternativeRoutes() {
        return new ArrayList<>(alternativeRoutes);
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public int getEdgesExplored() {
        return edgesExplored;
    }

    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Retorna a melhor rota (menor distância)
     */
    public Route getBestRoute() {
        if (mainRoute == null) return null;

        Route best = mainRoute;
        for (Route alt : alternativeRoutes) {
            if (alt.getTotalDistance() < best.getTotalDistance()) {
                best = alt;
            }
        }
        return best;
    }

    /**
     * Retorna todas as rotas ordenadas por distância
     */
    public List<Route> getAllRoutesSorted() {
        List<Route> allRoutes = new ArrayList<>();
        if (mainRoute != null) {
            allRoutes.add(mainRoute);
        }
        allRoutes.addAll(alternativeRoutes);
        allRoutes.sort(Comparator.comparingDouble(Route::getTotalDistance));
        return allRoutes;
    }

    /**
     * Adiciona metadado customizado
     */
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    /**
     * Retorna metadado específico
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Calcula eficiência do algoritmo (nós visitados / tempo)
     */
    public double getEfficiency() {
        if (executionTimeMs == 0) return Double.POSITIVE_INFINITY;
        return (double) nodesVisited / executionTimeMs;
    }

    /**
     * Retorna relatório completo
     */
    public String getFullReport() {
        StringBuilder sb = new StringBuilder();

        sb.append("╔════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║ RESULTADO DO ALGORITMO: %-26s ║\n", algorithmName));
        sb.append("╠════════════════════════════════════════════════════╣\n");

        if (!success) {
            sb.append(String.format("║ Status: FALHA                                      ║\n"));
            sb.append(String.format("║ Erro: %-44s ║\n", errorMessage));
        } else {
            sb.append(String.format("║ Status: SUCESSO                                    ║\n"));
            sb.append(String.format("║ Tempo de Execução: %-31d ms ║\n", executionTimeMs));
            sb.append(String.format("║ Nós Visitados: %-35d ║\n", nodesVisited));
            sb.append(String.format("║ Arestas Exploradas: %-31d ║\n", edgesExplored));

            if (mainRoute != null) {
                sb.append("╠════════════════════════════════════════════════════╣\n");
                sb.append(String.format("║ Distância Total: %-33.2f km ║\n",
                        mainRoute.getTotalDistance()));
                sb.append(String.format("║ Número de Paradas: %-31d ║\n",
                        mainRoute.getStepCount()));
                StringBuilder append = sb.append(String.format("║ Origem: %-42s ║\n",
                        mainRoute.getStart().getName()));
                sb.append(String.format("║ Destino: %-41s ║\n",
                        mainRoute.getEnd().getName()));
            }

            if (!alternativeRoutes.isEmpty()) {
                sb.append("╠════════════════════════════════════════════════════╣\n");
                sb.append(String.format("║ Rotas Alternativas: %-30d ║\n",
                        alternativeRoutes.size()));
            }
        }

        sb.append("╚════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    /**
     * Retorna comparação com outro resultado
     */
    public String compareWith(AlgorithmResult other) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║              COMPARAÇÃO DE ALGORITMOS                      ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ %-25s | %-25s ║\n",
                this.algorithmName, other.algorithmName));
        sb.append("╠════════════════════════════════════════════════════════════╣\n");

        sb.append(String.format("║ Tempo: %-18d ms | %-18d ms ║\n",
                this.executionTimeMs, other.executionTimeMs));

        sb.append(String.format("║ Nós Visitados: %-12d | %-18d ║\n",
                this.nodesVisited, other.nodesVisited));

        if (this.mainRoute != null && other.mainRoute != null) {
            sb.append(String.format("║ Distância: %-15.2f km | %-15.2f km ║\n",
                    this.mainRoute.getTotalDistance(),
                    other.mainRoute.getTotalDistance()));
        }

        sb.append("╠════════════════════════════════════════════════════════════╣\n");

        // Determinar vencedor
        String winner = "EMPATE";
        if (this.executionTimeMs < other.executionTimeMs) {
            winner = this.algorithmName + " (mais rápido)";
        } else if (this.executionTimeMs > other.executionTimeMs) {
            winner = other.algorithmName + " (mais rápido)";
        }

        sb.append(String.format("║ Vencedor: %-48s ║\n", winner));
        sb.append("╚════════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("AlgorithmResult{%s, success=%s, time=%dms, nodes=%d}",
                algorithmName, success, executionTimeMs, nodesVisited);
    }

    /**
     * Builder para construir resultados de forma fluente
     */
    public static class Builder {
        private String algorithmName = "UNKNOWN";
        private Route mainRoute;
        private List<Route> alternativeRoutes = new ArrayList<>();
        private long executionTimeMs = 0;
        private int nodesVisited = 0;
        private int edgesExplored = 0;
        private Map<String, Object> metadata = new HashMap<>();
        private boolean success = true;
        private String errorMessage = null;

        public Builder setAlgorithmName(String name) {
            this.algorithmName = name;
            return this;
        }

        public Builder setMainRoute(Route route) {
            this.mainRoute = route;
            return this;
        }

        public Builder addAlternativeRoute(Route route) {
            this.alternativeRoutes.add(route);
            return this;
        }

        public Builder setAlternativeRoutes(List<Route> routes) {
            this.alternativeRoutes = new ArrayList<>(routes);
            return this;
        }

        public Builder setExecutionTime(long timeMs) {
            this.executionTimeMs = timeMs;
            return this;
        }

        public Builder setNodesVisited(int count) {
            this.nodesVisited = count;
            return this;
        }

        public Builder setEdgesExplored(int count) {
            this.edgesExplored = count;
            return this;
        }

        public Builder addMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder setSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder setError(String message) {
            this.success = false;
            this.errorMessage = message;
            return this;
        }

        public AlgorithmResult build() {
            return new AlgorithmResult(this);
        }
    }
}