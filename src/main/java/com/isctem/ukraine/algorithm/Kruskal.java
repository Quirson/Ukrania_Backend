package com.isctem.ukraine.algorithm;

import com.isctem.ukraine.model.*;
import java.util.*;

/**
 * Implementação do algoritmo de Kruskal
 * Encontra a Árvore Geradora Mínima (Minimum Spanning Tree)
 * usando ordenação de arestas e Union-Find
 */
public class Kruskal {

    /**
     * Executa Kruskal para encontrar MST
     */
    public static AlgorithmResult execute(Graph graph) {
        long startTime = System.currentTimeMillis();

        AlgorithmResult.Builder resultBuilder = new AlgorithmResult.Builder()
                .setAlgorithmName("Kruskal (MST)");

        // 1. Obter todas as arestas
        List<Edge> edges = getAllEdges(graph);

        // 2. Ordenar arestas por peso
        edges.sort(Comparator.comparingDouble(e -> e.weight));

        // 3. Inicializar Union-Find
        UnionFind uf = new UnionFind(graph.getAllOblasts());

        // 4. MST result
        List<Edge> mstEdges = new ArrayList<>();
        double totalWeight = 0;
        int edgesExplored = 0;

        // 5. Algoritmo de Kruskal
        for (Edge edge : edges) {
            edgesExplored++;

            // Se não forma ciclo, adiciona à MST
            if (uf.find(edge.from) != uf.find(edge.to)) {
                uf.union(edge.from, edge.to);
                mstEdges.add(edge);
                totalWeight += edge.weight;

                // MST completa quando tem (V-1) arestas
                if (mstEdges.size() == graph.getNodeCount() - 1) {
                    break;
                }
            }
        }

        long endTime = System.currentTimeMillis();

        // Construir resultado
        List<Oblast> pathOblasts = new ArrayList<>();
        List<Connection> connections = new ArrayList<>();

        for (Edge edge : mstEdges) {
            Connection conn = graph.getConnection(edge.from, edge.to);
            if (conn != null) {
                connections.add(conn);
            }

            Oblast fromOblast = graph.getOblast(edge.from);
            if (!pathOblasts.contains(fromOblast)) {
                pathOblasts.add(fromOblast);
            }
            Oblast toOblast = graph.getOblast(edge.to);
            if (!pathOblasts.contains(toOblast)) {
                pathOblasts.add(toOblast);
            }
        }

        Route route = new Route.Builder()
                .setPath(pathOblasts)
                .setTotalDistance(totalWeight)
                .setAlgorithm("Kruskal")
                .setComputationTime(endTime - startTime)
                .setOptimal(true)
                .build();

        return resultBuilder
                .setMainRoute(route)
                .setExecutionTime(endTime - startTime)
                .setNodesVisited(pathOblasts.size())
                .setEdgesExplored(edgesExplored)
                .addMetadata("mst_edges", mstEdges)
                .addMetadata("total_weight", totalWeight)
                .addMetadata("edges_count", mstEdges.size())
                .build();
    }

    /**
     * Retorna a MST como novo grafo
     */
    public static Graph getMSTGraph(Graph originalGraph) {
        AlgorithmResult result = execute(originalGraph);

        if (!result.isSuccess()) return null;

        Graph mstGraph = new Graph(false);

        @SuppressWarnings("unchecked")
        List<Edge> mstEdges = (List<Edge>) result.getMetadata("mst_edges");

        for (Edge edge : mstEdges) {
            Oblast from = originalGraph.getOblast(edge.from);
            Oblast to = originalGraph.getOblast(edge.to);

            mstGraph.addOblast(from);
            mstGraph.addOblast(to);
            mstGraph.addConnection(from.getId(), to.getId(), edge.weight);
        }

        return mstGraph;
    }

    private static List<Edge> getAllEdges(Graph graph) {
        List<Edge> edges = new ArrayList<>();
        Set<String> processed = new HashSet<>();

        for (Connection conn : graph.getAllConnections()) {
            String key = conn.getFrom().getId() + "-" + conn.getTo().getId();
            String reverseKey = conn.getTo().getId() + "-" + conn.getFrom().getId();

            if (!processed.contains(key) && !processed.contains(reverseKey)) {
                edges.add(new Edge(conn.getFrom().getId(),
                        conn.getTo().getId(),
                        conn.getDistance()));
                processed.add(key);
                processed.add(reverseKey);
            }
        }

        return edges;
    }

    // Classe auxiliar para arestas
    static class Edge {
        String from;
        String to;
        double weight;

        Edge(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return String.format("%s -- %s (%.1f km)", from, to, weight);
        }
    }

    // Union-Find (Disjoint Set Union)
    static class UnionFind {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;

        UnionFind(Collection<Oblast> oblasts) {
            parent = new HashMap<>();
            rank = new HashMap<>();

            for (Oblast oblast : oblasts) {
                parent.put(oblast.getId(), oblast.getId());
                rank.put(oblast.getId(), 0);
            }
        }

        String find(String id) {
            if (!parent.get(id).equals(id)) {
                parent.put(id, find(parent.get(id))); // Path compression
            }
            return parent.get(id);
        }

        void union(String id1, String id2) {
            String root1 = find(id1);
            String root2 = find(id2);

            if (root1.equals(root2)) return;

            // Union by rank
            int rank1 = rank.get(root1);
            int rank2 = rank.get(root2);

            if (rank1 < rank2) {
                parent.put(root1, root2);
            } else if (rank1 > rank2) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank1 + 1);
            }
        }
    }
}