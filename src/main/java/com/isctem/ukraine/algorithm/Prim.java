package com.isctem.ukraine.algorithm;

import com.isctem.ukraine.model.*;
import java.util.*;

/**
 * Implementação do algoritmo de Prim
 * Encontra a Árvore Geradora Mínima crescendo a partir de um vértice
 */
public class Prim {

    /**
     * Executa Prim começando de um Oblast específico
     */
    public static AlgorithmResult execute(Graph graph, String startId) {
        long startTime = System.currentTimeMillis();

        AlgorithmResult.Builder resultBuilder = new AlgorithmResult.Builder()
                .setAlgorithmName("Prim (MST)");

        Oblast start = graph.getOblast(startId);
        if (start == null) {
            return resultBuilder.setError("Oblast inicial não encontrado").build();
        }

        Set<String> inMST = new HashSet<>();
        List<Edge> mstEdges = new ArrayList<>();
        double totalWeight = 0;

        // Priority Queue para arestas (peso, from, to)
        PriorityQueue<EdgeWithWeight> pq = new PriorityQueue<>(
                Comparator.comparingDouble(e -> e.weight));

        int nodesVisited = 0;
        int edgesExplored = 0;

        // Começar do vértice inicial
        inMST.add(startId);
        nodesVisited++;
        addEdgesToQueue(graph, startId, pq, inMST);

        // Algoritmo de Prim
        while (!pq.isEmpty() && inMST.size() < graph.getNodeCount()) {
            EdgeWithWeight edge = pq.poll();
            edgesExplored++;

            // Se o destino já está na MST, pular
            if (inMST.contains(edge.to)) {
                continue;
            }

            // Adicionar aresta à MST
            mstEdges.add(new Edge(edge.from, edge.to, edge.weight));
            totalWeight += edge.weight;
            inMST.add(edge.to);
            nodesVisited++;

            // Adicionar novas arestas do vértice recém-adicionado
            addEdgesToQueue(graph, edge.to, pq, inMST);
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
                .setAlgorithm("Prim")
                .setComputationTime(endTime - startTime)
                .setOptimal(true)
                .build();

        return resultBuilder
                .setMainRoute(route)
                .setExecutionTime(endTime - startTime)
                .setNodesVisited(nodesVisited)
                .setEdgesExplored(edgesExplored)
                .addMetadata("mst_edges", mstEdges)
                .addMetadata("total_weight", totalWeight)
                .addMetadata("edges_count", mstEdges.size())
                .addMetadata("start_oblast", startId)
                .build();
    }

    /**
     * Executa Prim começando do primeiro Oblast disponível
     */
    public static AlgorithmResult execute(Graph graph) {
        String startId = graph.getAllOblasts().iterator().next().getId();
        return execute(graph, startId);
    }

    /**
     * Retorna a MST como novo grafo
     */
    public static Graph getMSTGraph(Graph originalGraph, String startId) {
        AlgorithmResult result = execute(originalGraph, startId);

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

    private static void addEdgesToQueue(Graph graph, String fromId,
                                        PriorityQueue<EdgeWithWeight> pq,
                                        Set<String> inMST) {
        for (Oblast neighbor : graph.getNeighbors(fromId)) {
            String toId = neighbor.getId();

            if (!inMST.contains(toId)) {
                double weight = graph.getDistance(fromId, toId);
                pq.offer(new EdgeWithWeight(fromId, toId, weight));
            }
        }
    }

    // Classes auxiliares
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

    static class EdgeWithWeight {
        String from;
        String to;
        double weight;

        EdgeWithWeight(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}