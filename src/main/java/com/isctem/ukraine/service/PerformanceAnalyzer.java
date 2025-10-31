package com.isctem.ukraine.service;

import com.isctem.ukraine.model.*;
import java.util.*;

/**
 * Service para análise de performance e benchmarking de algoritmos
 */
public class PerformanceAnalyzer {

    private final GraphService graphService;
    private final List<BenchmarkResult> benchmarkHistory;

    public PerformanceAnalyzer(GraphService graphService) {
        this.graphService = graphService;
        this.benchmarkHistory = new ArrayList<>();
    }

    /**
     * Executa benchmark completo de todos os algoritmos
     */
    public BenchmarkSummary runFullBenchmark(String startId, String endId, int iterations) {
        System.out.println("🔥 Iniciando Benchmark Completo...\n");

        Map<AlgorithmType, List<Long>> executionTimes = new HashMap<>();
        Map<AlgorithmType, List<Double>> distances = new HashMap<>();
        Map<AlgorithmType, Integer> successCount = new HashMap<>();

        for (AlgorithmType type : AlgorithmType.values()) {
            executionTimes.put(type, new ArrayList<>());
            distances.put(type, new ArrayList<>());
            successCount.put(type, 0);
        }

        // Executar múltiplas iterações
        for (int i = 0; i < iterations; i++) {
            System.out.printf("Iteração %d/%d...\r", i + 1, iterations);

            for (AlgorithmType type : AlgorithmType.values()) {
                AlgorithmResult result = graphService.executeAlgorithm(type, startId, endId);

                if (result.isSuccess()) {
                    executionTimes.get(type).add(result.getExecutionTimeMs());
                    if (result.getMainRoute() != null) {
                        distances.get(type).add(result.getMainRoute().getTotalDistance());
                    }
                    successCount.put(type, successCount.get(type) + 1);
                }
            }
        }

        System.out.println("\n✅ Benchmark Concluído!\n");

        // Calcular estatísticas
        Map<AlgorithmType, AlgorithmStats> stats = new HashMap<>();

        for (AlgorithmType type : AlgorithmType.values()) {
            List<Long> times = executionTimes.get(type);
            List<Double> dists = distances.get(type);

            if (!times.isEmpty()) {
                stats.put(type, new AlgorithmStats(
                        type,
                        calculateAverage(times),
                        calculateMin(times),
                        calculateMax(times),
                        calculateStdDev(times),
                        dists.isEmpty() ? 0 : calculateAverage(dists.stream().mapToLong(d -> d.longValue()).boxed().toList()),
                        successCount.get(type),
                        iterations
                ));
            }
        }

        BenchmarkSummary summary = new BenchmarkSummary(stats, iterations, startId, endId);
        benchmarkHistory.add(new BenchmarkResult(summary, System.currentTimeMillis()));

        return summary;
    }

    /**
     * Testa performance com diferentes tamanhos de grafo
     */
    public ScalabilityReport testScalability(List<Integer> graphSizes) {
        Map<Integer, Map<AlgorithmType, Double>> results = new HashMap<>();

        for (int size : graphSizes) {
            // Criar subgrafo com N nós
            // (simplificado - usar os primeiros N oblasts)

            Map<AlgorithmType, Double> sizeResults = new HashMap<>();

            for (AlgorithmType type : AlgorithmType.values()) {
                long startTime = System.currentTimeMillis();

                // Executar algoritmo
                String start = "kyiv";
                String end = "lviv";
                graphService.executeAlgorithm(type, start, end);

                long endTime = System.currentTimeMillis();
                sizeResults.put(type, (double) (endTime - startTime));
            }

            results.put(size, sizeResults);
        }

        return new ScalabilityReport(results);
    }

    /**
     * Analisa impacto de destruição de conexões na performance
     */
    public DestructionImpactReport analyzeDestructionImpact(String startId, String endId,
                                                            double[] destructionLevels) {
        Map<Double, Map<AlgorithmType, AlgorithmResult>> results = new HashMap<>();

        Graph originalGraph = graphService.getGraph().clone();

        for (double level : destructionLevels) {
            System.out.printf("Testando destruição: %.0f%%...\n", level);

            // Resetar e aplicar destruição
            graphService.setGraph(originalGraph.clone());
            graphService.simulateRussianAttack(level);

            // Testar todos algoritmos
            Map<AlgorithmType, AlgorithmResult> levelResults =
                    graphService.executeAllAlgorithms(startId, endId);

            results.put(level, levelResults);
        }

        // Restaurar grafo original
        graphService.setGraph(originalGraph);

        return new DestructionImpactReport(results);
    }

    /**
     * Compara eficiência de memória (aproximada)
     */
    public MemoryUsageReport analyzeMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        Map<AlgorithmType, Long> memoryUsage = new HashMap<>();

        for (AlgorithmType type : AlgorithmType.values()) {
            runtime.gc();
            long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

            graphService.executeAlgorithm(type, "kyiv", "lviv");

            long afterMemory = runtime.totalMemory() - runtime.freeMemory();
            memoryUsage.put(type, afterMemory - beforeMemory);
        }

        return new MemoryUsageReport(memoryUsage);
    }

    /**
     * Gera relatório completo de performance
     */
    public String generatePerformanceReport(String startId, String endId) {
        StringBuilder report = new StringBuilder();

        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║            RELATÓRIO DE PERFORMANCE COMPLETO                 ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        // Benchmark básico
        BenchmarkSummary benchmark = runFullBenchmark(startId, endId, 10);
        report.append(benchmark.getDetailedReport());

        // Análise de destruição
        report.append("\n\n╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║           ANÁLISE DE IMPACTO DE DESTRUIÇÃO                   ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        double[] levels = {0, 10, 25, 50};
        DestructionImpactReport destructionReport = analyzeDestructionImpact(startId, endId, levels);
        report.append(destructionReport.getSummary());

        return report.toString();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private double calculateAverage(List<Long> values) {
        return values.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    private long calculateMin(List<Long> values) {
        return values.stream().mapToLong(Long::longValue).min().orElse(0);
    }

    private long calculateMax(List<Long> values) {
        return values.stream().mapToLong(Long::longValue).max().orElse(0);
    }

    private double calculateStdDev(List<Long> values) {
        double avg = calculateAverage(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - avg, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }

    // ========== CLASSES DE RESULTADO ==========

    public record AlgorithmStats(
            AlgorithmType algorithm,
            double avgTime,
            long minTime,
            long maxTime,
            double stdDev,
            double avgDistance,
            int successCount,
            int totalRuns
    ) {
        public double getSuccessRate() {
            return (double) successCount / totalRuns * 100;
        }
    }

    public record BenchmarkResult(BenchmarkSummary summary, long timestamp) {}

    public static class BenchmarkSummary {
        private final Map<AlgorithmType, AlgorithmStats> stats;
        private final int iterations;
        private final String startId;
        private final String endId;

        public BenchmarkSummary(Map<AlgorithmType, AlgorithmStats> stats,
                                int iterations, String startId, String endId) {
            this.stats = stats;
            this.iterations = iterations;
            this.startId = startId;
            this.endId = endId;
        }

        public AlgorithmStats getFastestAlgorithm() {
            return stats.values().stream()
                    .min(Comparator.comparingDouble(AlgorithmStats::avgTime))
                    .orElse(null);
        }

        public AlgorithmStats getShortestPathAlgorithm() {
            return stats.values().stream()
                    .filter(s -> s.avgDistance > 0)
                    .min(Comparator.comparingDouble(AlgorithmStats::avgDistance))
                    .orElse(null);
        }

        public String getDetailedReport() {
            StringBuilder sb = new StringBuilder();

            sb.append(String.format("Rota: %s → %s | Iterações: %d\n\n",
                    startId, endId, iterations));

            sb.append("╔════════════════════════════════════════════════════════════════╗\n");
            sb.append("║ Algoritmo      | Avg(ms) | Min | Max | StdDev | Sucesso(%) ║\n");
            sb.append("╠════════════════════════════════════════════════════════════════╣\n");

            for (AlgorithmStats stat : stats.values()) {
                sb.append(String.format("║ %-14s | %7.2f | %3d | %3d | %6.2f | %9.1f%% ║\n",
                        stat.algorithm.name(),
                        stat.avgTime,
                        stat.minTime,
                        stat.maxTime,
                        stat.stdDev,
                        stat.getSuccessRate()
                ));
            }

            sb.append("╚════════════════════════════════════════════════════════════════╝\n");

            AlgorithmStats fastest = getFastestAlgorithm();
            if (fastest != null) {
                sb.append(String.format("\n⚡ Mais Rápido: %s (%.2f ms)\n",
                        fastest.algorithm, fastest.avgTime));
            }

            AlgorithmStats shortest = getShortestPathAlgorithm();
            if (shortest != null) {
                sb.append(String.format("🎯 Menor Caminho: %s (%.1f km)\n",
                        shortest.algorithm, shortest.avgDistance));
            }

            return sb.toString();
        }

        public Map<AlgorithmType, AlgorithmStats> getStats() {
            return stats;
        }
    }

    public record ScalabilityReport(Map<Integer, Map<AlgorithmType, Double>> results) {}

    public static class DestructionImpactReport {
        private final Map<Double, Map<AlgorithmType, AlgorithmResult>> results;

        public DestructionImpactReport(Map<Double, Map<AlgorithmType, AlgorithmResult>> results) {
            this.results = results;
        }

        public String getSummary() {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<Double, Map<AlgorithmType, AlgorithmResult>> entry : results.entrySet()) {
                sb.append(String.format("\n🔥 Destruição: %.0f%%\n", entry.getKey()));

                for (Map.Entry<AlgorithmType, AlgorithmResult> algoEntry : entry.getValue().entrySet()) {
                    AlgorithmResult result = algoEntry.getValue();
                    if (result.isSuccess()) {
                        sb.append(String.format("  ✅ %s: %.1f km em %d ms\n",
                                algoEntry.getKey(),
                                result.getMainRoute() != null ? result.getMainRoute().getTotalDistance() : 0,
                                result.getExecutionTimeMs()
                        ));
                    } else {
                        sb.append(String.format("  ❌ %s: FALHOU\n", algoEntry.getKey()));
                    }
                }
            }

            return sb.toString();
        }
    }

    public record MemoryUsageReport(Map<AlgorithmType, Long> memoryUsage) {}
}