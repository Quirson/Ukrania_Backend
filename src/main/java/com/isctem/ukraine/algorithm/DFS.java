package com.isctem.ukraine.algorithm;

import com.isctem.ukraine.model.*;
import java.util.*;

/**
 * Implementação do algoritmo DFS (Depth-First Search)
 * Busca em Profundidade - explora o máximo possível antes de retroceder
 */
public class DFS {

    /**
     * Executa DFS para encontrar caminho entre origem e destino
     */
    public static AlgorithmResult execute(Graph graph, String startId, String endId) {
        long startTime = System.currentTimeMillis();

        AlgorithmResult.Builder resultBuilder = new AlgorithmResult.Builder()
                .setAlgorithmName("DFS (Depth-First Search)");

        Oblast start = graph.getOblast(startId);
        Oblast end = graph.getOblast(endId);

        if (start == null || end == null) {
            return resultBuilder.setError("Oblast de origem ou destino não encontrado").build();
        }

        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        Map<String, Double> distance = new HashMap<>();
        int[] counters = {0, 0}; // [nodesVisited, edgesExplored]

        boolean found = dfsRecursive(graph, startId, endId, visited, parent, distance, counters);

        long endTime = System.currentTimeMillis();

        if (!found) {
            return resultBuilder
                    .setError("Não existe caminho entre " + start.getName() + " e " + end.getName())
                    .setExecutionTime(endTime - startTime)
                    .setNodesVisited(counters[0])
                    .setEdgesExplored(counters[1])
                    .build();
        }

        List<Oblast> path = reconstructPath(graph, parent, startId, endId);
        double totalDistance = distance.get(endId);

        Route route = new Route.Builder()
                .setPath(path)
                .setTotalDistance(totalDistance)
                .setAlgorithm("DFS")
                .setComputationTime(endTime - startTime)
                .setOptimal(false)
                .build();

        return resultBuilder
                .setMainRoute(route)
                .setExecutionTime(endTime - startTime)
                .setNodesVisited(counters[0])
                .setEdgesExplored(counters[1])
                .addMetadata("visited_order", new ArrayList<>(visited))
                .build();
    }

    private static boolean dfsRecursive(Graph graph, String currentId, String targetId,
                                        Set<String> visited, Map<String, String> parent,
                                        Map<String, Double> distance, int[] counters) {
        visited.add(currentId);
        counters[0]++; // nodesVisited

        if (!distance.containsKey(currentId)) {
            distance.put(currentId, 0.0);
        }

        if (currentId.equals(targetId)) {
            return true;
        }

        for (Oblast neighbor : graph.getNeighbors(currentId)) {
            counters[1]++; // edgesExplored
            String neighborId = neighbor.getId();

            if (!visited.contains(neighborId)) {
                parent.put(neighborId, currentId);
                double currentDist = distance.get(currentId);
                double edgeDist = graph.getDistance(currentId, neighborId);
                distance.put(neighborId, currentDist + edgeDist);

                if (dfsRecursive(graph, neighborId, targetId, visited, parent, distance, counters)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * DFS iterativo usando Stack
     */
    public static AlgorithmResult executeIterative(Graph graph, String startId, String endId) {
        long startTime = System.currentTimeMillis();

        AlgorithmResult.Builder resultBuilder = new AlgorithmResult.Builder()
                .setAlgorithmName("DFS Iterativo");

        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        Map<String, Double> distance = new HashMap<>();

        stack.push(startId);
        distance.put(startId, 0.0);

        int nodesVisited = 0;
        int edgesExplored = 0;
        boolean found = false;

        while (!stack.isEmpty()) {
            String currentId = stack.pop();

            if (visited.contains(currentId)) continue;

            visited.add(currentId);
            nodesVisited++;

            if (currentId.equals(endId)) {
                found = true;
                break;
            }

            for (Oblast neighbor : graph.getNeighbors(currentId)) {
                edgesExplored++;
                String neighborId = neighbor.getId();

                if (!visited.contains(neighborId)) {
                    parent.put(neighborId, currentId);
                    double currentDist = distance.get(currentId);
                    double edgeDist = graph.getDistance(currentId, neighborId);
                    distance.put(neighborId, currentDist + edgeDist);
                    stack.push(neighborId);
                }
            }
        }

        long endTime = System.currentTimeMillis();

        if (!found) {
            return resultBuilder.setError("Caminho não encontrado").build();
        }

        List<Oblast> path = reconstructPath(graph, parent, startId, endId);

        Route route = new Route.Builder()
                .setPath(path)
                .setTotalDistance(distance.get(endId))
                .setAlgorithm("DFS Iterativo")
                .setComputationTime(endTime - startTime)
                .build();

        return resultBuilder
                .setMainRoute(route)
                .setExecutionTime(endTime - startTime)
                .setNodesVisited(nodesVisited)
                .setEdgesExplored(edgesExplored)
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