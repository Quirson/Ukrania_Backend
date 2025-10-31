package com.isctem.ukraine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma rota calculada entre Oblasts.
 * Usada para armazenar resultados de algoritmos de busca.
 */
public class Route {
    private final List<Oblast> path;
    private final List<Connection> connections;
    private final double totalDistance;
    private final String algorithmUsed;
    private final long computationTimeMs;
    private final boolean isOptimal;

    /**
     * Construtor completo
     */
    public Route(List<Oblast> path, List<Connection> connections,
                 double totalDistance, String algorithmUsed,
                 long computationTimeMs, boolean isOptimal) {
        this.path = new ArrayList<>(path);
        this.connections = new ArrayList<>(connections);
        this.totalDistance = totalDistance;
        this.algorithmUsed = algorithmUsed;
        this.computationTimeMs = computationTimeMs;
        this.isOptimal = isOptimal;
    }

    /**
     * Construtor simplificado
     */
    public Route(List<Oblast> path, double totalDistance, String algorithmUsed) {
        this(path, new ArrayList<>(), totalDistance, algorithmUsed, 0, false);
    }

    // Getters
    public List<Oblast> getPath() {
        return new ArrayList<>(path);
    }

    public List<Connection> getConnections() {
        return new ArrayList<>(connections);
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public long getComputationTimeMs() {
        return computationTimeMs;
    }

    public boolean isOptimal() {
        return isOptimal;
    }

    public int getStepCount() {
        return path.size();
    }

    public Oblast getStart() {
        return path.isEmpty() ? null : path.get(0);
    }

    public Oblast getEnd() {
        return path.isEmpty() ? null : path.get(path.size() - 1);
    }

    /**
     * Verifica se a rota passa por um Oblast específico
     */
    public boolean passesThrough(String oblastId) {
        return path.stream().anyMatch(o -> o.getId().equals(oblastId));
    }

    /**
     * Verifica se a rota é válida (sem gaps)
     */
    public boolean isValid(Graph graph) {
        if (path.size() < 2) return true;

        for (int i = 0; i < path.size() - 1; i++) {
            Oblast current = path.get(i);
            Oblast next = path.get(i + 1);

            if (!graph.hasConnection(current.getId(), next.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retorna a rota em formato de string simples
     */
    public String getPathString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) {
                sb.append(" → ");
            }
        }
        return sb.toString();
    }

    /**
     * Retorna a rota com IDs
     */
    public String getPathIds() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getId());
            if (i < path.size() - 1) {
                sb.append(" → ");
            }
        }
        return sb.toString();
    }

    /**
     * Retorna representação detalhada da rota
     */
    public String getDetailedDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("""
            ========== ROTA DETALHADA ==========
            Algoritmo: %s
            Distância Total: %.2f km
            Número de Paradas: %d
            Tempo de Computação: %d ms
            Ótima: %s
            
            Caminho:
            """,
                algorithmUsed, totalDistance, path.size(),
                computationTimeMs, isOptimal ? "Sim" : "Não"));

        for (int i = 0; i < path.size(); i++) {
            Oblast oblast = path.get(i);
            sb.append(String.format("%d. %s (%s)\n",
                    i + 1, oblast.getName(), oblast.getId()));

            if (i < path.size() - 1) {
                Oblast next = path.get(i + 1);
                double segmentDist = oblast.distanceTo(next);
                sb.append(String.format("   ↓ %.2f km\n", segmentDist));
            }
        }

        sb.append("====================================\n");
        return sb.toString();
    }

    /**
     * Compara esta rota com outra
     */
    public int compareTo(Route other) {
        return Double.compare(this.totalDistance, other.totalDistance);
    }

    /**
     * Retorna métricas de eficiência
     */
    public String getEfficiencyMetrics() {
        double avgSegmentDistance = path.size() > 1 ?
                totalDistance / (path.size() - 1) : 0;

        return String.format("""
            Distância Média por Segmento: %.2f km
            Eficiência: %.2f km/parada
            Velocidade de Computação: %.2f paradas/ms
            """,
                avgSegmentDistance,
                totalDistance / Math.max(1, path.size()),
                path.size() / Math.max(1.0, computationTimeMs));
    }

    @Override
    public String toString() {
        return String.format("Route{%s, %.1fkm, %d paradas, %s}",
                getStart() != null ? getStart().getName() : "?",
                totalDistance, path.size(), algorithmUsed);
    }

    /**
     * Builder para construir rotas de forma fluente
     */
    public static class Builder {
        private List<Oblast> path = new ArrayList<>();
        private List<Connection> connections = new ArrayList<>();
        private double totalDistance = 0;
        private String algorithmUsed = "UNKNOWN";
        private long computationTimeMs = 0;
        private boolean isOptimal = false;

        public Builder addOblast(Oblast oblast) {
            this.path.add(oblast);
            return this;
        }

        public Builder addConnection(Connection connection) {
            this.connections.add(connection);
            this.totalDistance += connection.getDistance();
            return this;
        }

        public Builder setPath(List<Oblast> path) {
            this.path = new ArrayList<>(path);
            return this;
        }

        public Builder setTotalDistance(double distance) {
            this.totalDistance = distance;
            return this;
        }

        public Builder setAlgorithm(String algorithm) {
            this.algorithmUsed = algorithm;
            return this;
        }

        public Builder setComputationTime(long timeMs) {
            this.computationTimeMs = timeMs;
            return this;
        }

        public Builder setOptimal(boolean optimal) {
            this.isOptimal = optimal;
            return this;
        }

        public Route build() {
            return new Route(path, connections, totalDistance,
                    algorithmUsed, computationTimeMs, isOptimal);
        }
    }
}