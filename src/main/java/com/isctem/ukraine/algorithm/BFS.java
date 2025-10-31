package com.isctem.ukraine.algorithm;

import com.isctem.ukraine.model.*;
import java.util.*;

/**
 * Implementação do algoritmo BFS (Breadth-First Search)
 * Busca em Largura - explora todos os vizinhos antes de ir para o próximo nível
 */
public class BFS {

    /**
     * Executa BFS para encontrar caminho entre origem e destino
     */
    public static AlgorithmResult execute(Graph graph, String startId, String endId) {
        long startTime = System.currentTimeMillis();

        AlgorithmResult.Builder resultBuilder = new AlgorithmResult.Builder()
                .setAlgorithmName("BFS (Breadth-First Search)");

        Oblast start = graph.getOblast(startId);
        Oblast end = graph.getOblast(endId);

        if (start == null || end == null) {
            return resultBuilder.setError("Oblast de origem ou destino não encontrado").build();
        }

        // Estruturas auxiliares
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        Map<String, Double> distance = new HashMap<>();

        // Inicialização
        queue.offer(startId);
        visited.add(startId);
        parent.put(startId, null);
        distance.put(startId, 0.0);

        int nodesVisited = 0;
        int edgesExplored = 0;
        boolean foundPath = false;

        // BFS Loop
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            nodesVisited++;

            // Se chegou no destino, para
            if (currentId.equals(endId)) {
                foundPath = true;
                break;
            }

            // Explorar vizinhos
            List<Oblast> neighbors = graph.getNeighbors(currentId);
            for (Oblast neighbor : neighbors) {
                String neighborId = neighbor.getId();
                edgesExplored++;

                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    parent.put(neighborId, currentId);

                    double currentDist = distance.get(currentId);
                    double edgeDist = graph.getDistance(currentId, neighborId);
                    distance.put(neighborId, currentDist + edgeDist);

                    queue.offer(neighborId);
                }
            }
        }

        long endTime = System.currentTimeMillis();

        if (!foundPath) {
            return resultBuilder
                    .setError("Não existe caminho entre " + start.getName() + " e " + end.getName())
                    .setExecutionTime(endTime - startTime)
                    .setNodesVisited(nodesVisited)
                    .setEdgesExplored(edgesExplored)
                    .build();
        }

        // Reconstruir caminho
        List<Oblast> path = reconstructPath(graph, parent, startId, endId);
        double totalDistance = distance.get(endId);

        Route route = new Route.Builder()
                .setPath(path)
                .setTotalDistance(totalDistance)
                .setAlgorithm("BFS")
                .setComputationTime(endTime - startTime)
                .setOptimal(false)
                .build();

        return resultBuilder
                .setMainRoute(route)
                .setExecutionTime(endTime - startTime)
                .setNodesVisited(nodesVisited)
                .setEdgesExplored(edgesExplored)
                .addMetadata("visited_order", new ArrayList<>(visited))
                .build();
    }

    /**
     * Executa BFS visitando todos os nós (traversal completo)
     */
    public static AlgorithmResult executeFullTraversal(Graph graph, String startId) {
        long startTime = System.currentTimeMillis();

        AlgorithmResult.Builder resultBuilder = new AlgorithmResult.Builder()
                .setAlgorithmName("BFS Full Traversal");

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<String> visitOrder = new ArrayList<>();

        queue.offer(startId);
        visited.add(startId);

        int nodesVisited = 0;
        int edgesExplored = 0;

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            visitOrder.add(currentId);
            nodesVisited++;

            for (Oblast neighbor : graph.getNeighbors(currentId)) {
                edgesExplored++;
                if (!visited.contains(neighbor.getId())) {
                    visited.add(neighbor.getId());
                    queue.offer(neighbor.getId());
                }
            }
        }

        long endTime = System.currentTimeMillis();

        return resultBuilder
                .setExecutionTime(endTime - startTime)
                .setNodesVisited(nodesVisited)
                .setEdgesExplored(edgesExplored)
                .addMetadata("visit_order", visitOrder)
                .addMetadata("total_reachable", visited.size())
                .build();
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
}