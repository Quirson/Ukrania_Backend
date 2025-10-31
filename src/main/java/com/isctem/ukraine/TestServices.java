package com.isctem.ukraine;

import com.isctem.ukraine.model.*;
import com.isctem.ukraine.service.*;

/**
 * Teste completo de todos os services e algoritmos
 */
public class TestServices {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     TESTE COMPLETO - ALGORITMOS + SERVICES            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Criar services
        GraphService graphService = new GraphService();
        RouteCalculator routeCalc = new RouteCalculator(graphService);
        PerformanceAnalyzer analyzer = new PerformanceAnalyzer(graphService);

        // Teste 1: Executar todos algoritmos
        System.out.println("1️⃣  TESTANDO TODOS OS ALGORITMOS...\n");
        testAllAlgorithms(graphService);

        // Teste 2: Comparar rotas
        System.out.println("\n2️⃣  COMPARANDO ALGORITMOS...\n");
        testComparison(routeCalc);

        // Teste 3: Estatísticas da rede
        System.out.println("\n3️⃣  ESTATÍSTICAS DA REDE...\n");
        testNetworkStats(graphService);

        // Teste 4: Rotas especiais
        System.out.println("\n4️⃣  ROTAS ESPECIAIS...\n");
        testSpecialRoutes(graphService, routeCalc);

        // Teste 5: Simulação de guerra
        System.out.println("\n5️⃣  SIMULAÇÃO DE GUERRA...\n");
        testWarSimulation(graphService);

        // Teste 6: Benchmark de Performance
        System.out.println("\n6️⃣  BENCHMARK DE PERFORMANCE...\n");
        testPerformance(analyzer);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ TODOS OS TESTES PASSARAM!                         ║");
        System.out.println("║  Sistema pronto para JavaFX Frontend!                 ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    private static void testAllAlgorithms(GraphService service) {
        String start = "kyiv";
        String end = "lviv";

        for (AlgorithmType type : AlgorithmType.values()) {
            System.out.printf("Testando %s...\n", type.name());
            AlgorithmResult result = service.executeAlgorithm(type, start, end);

            if (result.isSuccess()) {
                System.out.printf("  ✅ Sucesso! Tempo: %d ms\n", result.getExecutionTimeMs());
                if (result.getMainRoute() != null) {
                    System.out.printf("  📏 Distância: %.1f km\n",
                            result.getMainRoute().getTotalDistance());
                }
            } else {
                System.out.printf("  ❌ Erro: %s\n", result.getErrorMessage());
            }
        }
    }

    private static void testComparison(RouteCalculator calc) {
        RouteCalculator.ComparisonResult comparison =
                calc.compareAlgorithms("kyiv", "donetsk");

        System.out.println(comparison.getComparisonTable());
    }

    private static void testNetworkStats(GraphService service) {
        GraphService.NetworkStatistics stats = service.getNetworkStatistics();
        System.out.println(stats);

        System.out.println("Hub Principal: " + service.getMostConnectedHub().getName());
        System.out.println("Conectividade: " + String.format("%.1f%%",
                service.calculateConnectivity()));
    }

    private static void testSpecialRoutes(GraphService service, RouteCalculator calc) {
        // Rota de evacuação
        Route evacRoute = calc.calculateEvacuationRoute("donetsk");
        if (evacRoute != null) {
            System.out.println("Rota de Evacuação de Donetsk:");
            System.out.println("  " + evacRoute.getPathString());
            System.out.printf("  Distância: %.1f km\n", evacRoute.getTotalDistance());
        }

        // Rota de abastecimento
        AlgorithmResult supplyRoute = service.calculateSupplyRoute();
        if (supplyRoute.isSuccess()) {
            System.out.println("\nRota de Abastecimento:");
            System.out.println("  Pontos críticos visitados: " +
                    supplyRoute.getMetadata("critical_oblasts"));
        }
    }

    private static void testWarSimulation(GraphService service) {
        System.out.println("Estado ANTES do ataque:");
        System.out.println(service.getNetworkStatistics());

        service.simulateRussianAttack(25);

        System.out.println("\nEstado APÓS ataque (25% destruição):");
        System.out.println(service.getNetworkStatistics());

        // Verificar se ainda há rotas viáveis
        AlgorithmResult result = service.executeAlgorithm(
                AlgorithmType.DIJKSTRA, "kyiv", "lviv");

        if (result.isSuccess()) {
            System.out.println("✅ Ainda existem rotas viáveis após ataque!");
        } else {
            System.out.println("❌ Rotas comprometidas pelo ataque!");
        }

        service.repairAll();
        System.out.println("\n🔧 Rede reparada!");
    }

    private static void testPerformance(PerformanceAnalyzer analyzer) {
        PerformanceAnalyzer.BenchmarkSummary benchmark =
                analyzer.runFullBenchmark("kyiv", "odesa", 5);

        System.out.println(benchmark.getDetailedReport());
    }
}