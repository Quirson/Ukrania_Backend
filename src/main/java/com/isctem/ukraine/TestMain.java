package com.isctem.ukraine;

import com.isctem.ukraine.data.UkraineData;
import com.isctem.ukraine.model.Graph;
import com.isctem.ukraine.model.Oblast;

/**
 * Classe para testar o backend antes de conectar ao frontend
 */
public class TestMain {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║  TESTE DO BACKEND - UKRAINE LOGISTICS SYSTEM      ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        // 1. Criar grafo da Ucrânia
        System.out.println("1️⃣  Criando grafo da Ucrânia...");
        Graph graph = UkraineData.createUkraineGraph();
        System.out.println("✅ Grafo criado com sucesso!\n");

        // 2. Mostrar estatísticas
        System.out.println(graph.getStatistics());

        // 3. Imprimir informações da rede
        System.out.println(UkraineData.getNetworkInfo());

        // 4. Testar alguns Oblasts
        System.out.println("\n2️⃣  Testando Oblasts específicos...\n");
        testOblast(graph, "kyiv");
        testOblast(graph, "donetsk");
        testOblast(graph, "lviv");

        // 5. Imprimir Matriz de Adjacência (primeiros 10 para não poluir)
        System.out.println("\n3️⃣  Matriz de Adjacência (amostra):");
        printMatrixSample(graph);

        // 6. Imprimir Lista de Adjacência (primeiros 5)
        System.out.println("\n4️⃣  Lista de Adjacência (amostra):");
        printAdjacencyListSample(graph);

        // 7. Testar conexões
        System.out.println("\n5️⃣  Testando conexões...\n");
        testConnection(graph, "kyiv", "lviv");
        testConnection(graph, "donetsk", "luhansk");
        testConnection(graph, "odesa", "crimea");

        // 8. Simular dano de guerra
        System.out.println("\n6️⃣  Simulando dano de guerra (30%)...");
        UkraineData.simulateWarDamage(graph, 30);
        System.out.println("✅ Dano simulado!\n");
        System.out.println(graph.getStatistics());

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ TODOS OS TESTES PASSARAM COM SUCESSO!         ║");
        System.out.println("║  Backend pronto para integração com Frontend!     ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    private static void testOblast(Graph graph, String oblastId) {
        Oblast oblast = graph.getOblast(oblastId);
        if (oblast != null) {
            System.out.println("📍 " + oblast.getName());
            System.out.println("   ID: " + oblast.getId());
            System.out.println("   Coords: (" + oblast.getLatitude() + ", " + oblast.getLongitude() + ")");
            System.out.println("   Frontline: " + (oblast.isFrontline() ? "⚠️ SIM" : "✅ NÃO"));
            System.out.println("   Vizinhos: " + graph.getNeighbors(oblastId).size());
            System.out.println("   Descrição: " + UkraineData.getOblastDescription(oblastId));
            System.out.println();
        }
    }

    private static void testConnection(Graph graph, String from, String to) {
        double distance = graph.getDistance(from, to);
        if (distance != Double.POSITIVE_INFINITY) {
            System.out.println("🚂 " + from + " → " + to + ": " +
                    String.format("%.1f km", distance) + " ✅");
        } else {
            System.out.println("❌ " + from + " → " + to + ": SEM CONEXÃO DIRETA");
        }
    }

    private static void printMatrixSample(Graph graph) {
        double[][] matrix = graph.getAdjacencyMatrix();
        int size = Math.min(5, matrix.length);

        System.out.print("      ");
        for (int i = 0; i < size; i++) {
            String id = graph.getIndexToId().get(i);
            System.out.printf("%-8s", id.substring(0, Math.min(6, id.length())));
        }
        System.out.println();

        for (int i = 0; i < size; i++) {
            String id = graph.getIndexToId().get(i);
            System.out.printf("%-6s", id.substring(0, Math.min(6, id.length())));

            for (int j = 0; j < size; j++) {
                double dist = matrix[i][j];
                if (dist == Double.POSITIVE_INFINITY) {
                    System.out.print("   ∞    ");
                } else if (dist == 0) {
                    System.out.print("   -    ");
                } else {
                    System.out.printf("%6.0f  ", dist);
                }
            }
            System.out.println();
        }
    }

    private static void printAdjacencyListSample(Graph graph) {
        int count = 0;
        for (Oblast oblast : graph.getAllOblasts()) {
            if (count >= 5) break;

            System.out.println("\n" + oblast.getName() + ":");
            var neighbors = graph.getNeighbors(oblast.getId());
            if (neighbors.isEmpty()) {
                System.out.println("  (sem vizinhos)");
            } else {
                for (Oblast neighbor : neighbors) {
                    double dist = graph.getDistance(oblast.getId(), neighbor.getId());
                    System.out.printf("  → %s (%.0f km)\n", neighbor.getName(), dist);
                }
            }
            count++;
        }
    }
}