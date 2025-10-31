package com.isctem.ukraine.algorithm;

import com.isctem.ukraine.model.*;
import java.util.*;

/**
 * Implementação do algoritmo de Dijkstra
 * Encontra o caminho mais curto entre dois vértices em grafo ponderado
 */
public class Dijkstra {

    /**
     * Executa Dijkstra para encontrar o caminho mais curto
     */
    public static AlgorithmResult execute(Graph graph, String startId, String endId) {
        long startTime = System.currentTimeMillis();

        AlgorithmResult.Builder resultBuilder = new AlgorithmResult.Builder()
                .setAlgorithmName("Dijkstra");

        Oblast start = graph.getOblast(startId);
        Oblast end = graph.getOblast(endId);

        if (start == null || end == null) {
            return resultBuilder.setError("Oblast não encontrado").build();
        }

        // Distâncias mínimas
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        // Priority Queue: ordena por distância
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        // Inicialização
        for (Oblast oblast : graph.getAllOblasts()) {
            distances.put(oblast.getId(), Double.POSITIVE_INFINITY);
        }
        distances.put(startId, 0.0);
        parent.put(startId, null);

        pq.offer(new Node(startId, 0.0));

        int nodesVisited = 0;
        int edgesExplored = 0;

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentId = current.id;

            if (visited.contains(currentId)) continue;

            visited.add(currentId);
            nodesVisited++;

            // Se chegou no destino, pode parar (otimização)
            if (currentId.equals(endId)) {
                break;
            }

            double currentDist = distances.get(currentId);

            // Relaxamento das arestas
            for (Oblast neighbor : graph.getNeighbors(currentId)) {
                edgesExplored++;
                String neighborId = neighbor.getId();

                if (visited.contains(neighborId)) continue;

                double edgeDist = graph.getDistance(currentId, neighborId);
                double newDist = currentDist + edgeDist;

                if (newDist < distances.get(neighborId)) {
                    distances.put(neighborId, newDist);
                    parent.put(neighborId, currentId);
                    pq.offer(new Node(neighborId, newDist));
                }
            }
        }

        long endTime = System.currentTimeMillis();

        // Verificar se encontrou caminho
        if (distances.get(endId) == Double.POSITIVE_INFINITY) {
            return resultBuilder
                    .setError("Não existe caminho entre os Oblasts")
                    .setExecutionTime(endTime - startTime)
                    .setNodesVisited(nodesVisited)
                    .setEdgesExplored(edgesExplored)
                    .build();
        }

        // Reconstruir caminho
        List<Oblast> path = reconstructPath(graph, parent, startId, endId);
        double totalDistance = distances.get(endId);

        Route route = new Route.Builder()
                .setPath(path)
                .setTotalDistance(totalDistance)
                .setAlgorithm("Dijkstra")
                .setComputationTime(endTime - startTime)
                .setOptimal(true)
                .build();

        return resultBuilder
                .setMainRoute(route)
                .setExecutionTime(endTime - startTime)
                .setNodesVisited(nodesVisited)
                .setEdgesExplored(edgesExplored)
                .addMetadata("all_distances", new HashMap<>(distances))
                .build();
    }

    /**
     * Executa Dijkstra de um ponto para TODOS os outros (Single-Source Shortest Path)
     */
    public static Map<String, Route> executeToAll(Graph graph, String startId) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        for (Oblast oblast : graph.getAllOblasts()) {
            distances.put(oblast.getId(), Double.POSITIVE_INFINITY);
        }
        distances.put(startId, 0.0);
        parent.put(startId, null);

        pq.offer(new Node(startId, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentId = current.id;

            if (visited.contains(currentId)) continue;
            visited.add(currentId);

            for (Oblast neighbor : graph.getNeighbors(currentId)) {
                String neighborId = neighbor.getId();
                if (visited.contains(neighborId)) continue;

                double edgeDist = graph.getDistance(currentId, neighborId);
                double newDist = distances.get(currentId) + edgeDist;

                if (newDist < distances.get(neighborId)) {
                    distances.put(neighborId, newDist);
                    parent.put(neighborId, currentId);
                    pq.offer(new Node(neighborId, newDist));
                }
            }
        }

        // Criar rotas para todos os destinos
        Map<String, Route> routes = new HashMap<>();
        for (String endId : distances.keySet()) {
            if (!endId.equals(startId) && distances.get(endId) != Double.POSITIVE_INFINITY) {
                List<Oblast> path = reconstructPath(graph, parent, startId, endId);
                Route route = new Route.Builder()
                        .setPath(path)
                        .setTotalDistance(distances.get(endId))
                        .setAlgorithm("Dijkstra")
                        .setOptimal(true)
                        .build();
                routes.put(endId, route);
            }
        }

        return routes;
    }

    private static List<Oblast> reconstructPath(Graph graph, Map<String, String> parent,
                                                String startId, String endId) {
        List<Oblast> path = new ArrayList<>();
        String current = endId;

        while (current != null) {
            path.add(0, graph.getOblast(current));
            current = parent.get(current);
        }

        return path;
    }

    // Classe auxiliar para o PriorityQueue
    private static class Node {
        String id;
        double distance;

        Node(String id, double distance) {
            this.id = id;
            this.distance = distance;
        }
    }
}