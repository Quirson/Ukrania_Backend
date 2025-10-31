package com.isctem.ukraine.util;

import com.isctem.ukraine.model.*;
import com.isctem.ukraine.service.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Gerador de Relatórios em TXT e HTML
 */
public class ReportGenerator {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Gera relatório completo da última viagem
     */
    public static String generateTripReport(Route route, AlgorithmResult result,
                                            GraphService.NetworkStatistics stats) {
        StringBuilder report = new StringBuilder();

        report.append("╔════════════════════════════════════════════════════════════════╗\n");
        report.append("║     RELATÓRIO DE VIAGEM - UKRAINE LOGISTICS SYSTEM             ║\n");
        report.append("╚════════════════════════════════════════════════════════════════╝\n\n");

        report.append("📅 Data/Hora: ").append(LocalDateTime.now().format(formatter)).append("\n");
        report.append("🚂 Sistema: Ukraine Military Logistics v1.0\n");
        report.append("👥 Desenvolvido por: Quirson Ngale, Celestino Sitoe, Stanley Cossa\n\n");

        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("                    INFORMAÇÕES DA ROTA\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");

        if (route != null) {
            report.append("🎯 Origem: ").append(route.getStart().getName()).append("\n");
            report.append("🏁 Destino: ").append(route.getEnd().getName()).append("\n");
            report.append("📏 Distância Total: ").append(String.format("%.2f km", route.getTotalDistance())).append("\n");
            report.append("🔢 Número de Paradas: ").append(route.getStepCount()).append("\n");
            report.append("🧠 Algoritmo Usado: ").append(route.getAlgorithmUsed()).append("\n");
            report.append("⏱️  Tempo de Computação: ").append(route.getComputationTimeMs()).append(" ms\n");
            report.append("✅ Rota Ótima: ").append(route.isOptimal() ? "Sim" : "Não").append("\n\n");

            report.append("📍 CAMINHO PERCORRIDO:\n");
            List<Oblast> path = route.getPath();
            for (int i = 0; i < path.size(); i++) {
                Oblast oblast = path.get(i);
                report.append(String.format("   %2d. %-20s", i + 1, oblast.getName()));

                if (oblast.isFrontline()) {
                    report.append(" ⚠️ FRONTLINE");
                }
                if (oblast.isDestroyed()) {
                    report.append(" 💥 DESTRUÍDO");
                }

                report.append("\n");

                if (i < path.size() - 1) {
                    Oblast next = path.get(i + 1);
                    double segDist = oblast.distanceTo(next);
                    report.append(String.format("        ↓ %.1f km\n", segDist));
                }
            }
        }

        report.append("\n═══════════════════════════════════════════════════════════════\n");
        report.append("                MÉTRICAS DE PERFORMANCE\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");

        if (result != null) {
            report.append("⚡ Tempo de Execução: ").append(result.getExecutionTimeMs()).append(" ms\n");
            report.append("🔍 Nós Visitados: ").append(result.getNodesVisited()).append("\n");
            report.append("🔗 Arestas Exploradas: ").append(result.getEdgesExplored()).append("\n");
            report.append("📊 Eficiência: ").append(String.format("%.2f", result.getEfficiency())).append(" nós/ms\n");
        }

        report.append("\n═══════════════════════════════════════════════════════════════\n");
        report.append("              ESTADO DA REDE FERROVIÁRIA\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");

        if (stats != null) {
            report.append("🏛️  Total de Oblasts: ").append(stats.totalOblasts()).append("\n");
            report.append("💥 Oblasts Destruídos: ").append(stats.destroyedOblasts()).append("\n");
            report.append("⚠️  Oblasts na Frontline: ").append(stats.frontlineOblasts()).append("\n");
            report.append("🔗 Total de Conexões: ").append(stats.totalConnections()).append("\n");
            report.append("❌ Conexões Destruídas: ").append(stats.destroyedConnections()).append("\n");
            report.append("📦 Nível Médio de Supply: ").append(String.format("%.1f%%", stats.avgSupplyLevel())).append("\n");
            report.append("📡 Conectividade da Rede: ").append(String.format("%.1f%%", stats.connectivity())).append("\n");
            report.append("🎯 Hub Principal: ").append(stats.mainHub()).append("\n");
            report.append("📏 Distância Total da Rede: ").append(String.format("%.1f km", stats.totalDistance())).append("\n");
        }

        report.append("\n═══════════════════════════════════════════════════════════════\n");
        report.append("                      OBSERVAÇÕES\n");
        report.append("═══════════════════════════════════════════════════════════════\n\n");

        if (route != null) {
            long frontlineStops = route.getPath().stream().filter(Oblast::isFrontline).count();
            if (frontlineStops > 0) {
                report.append("⚠️  ALERTA: Rota passa por ").append(frontlineStops)
                        .append(" oblast(s) na frontline!\n");
                report.append("   Recomendação: Considere rotas alternativas mais seguras.\n\n");
            }

            if (route.getTotalDistance() > 1000) {
                report.append("📏 AVISO: Rota muito longa (> 1000 km)\n");
                report.append("   Recomendação: Considere paradas para reabastecimento.\n\n");
            }
        }

        report.append("\n╔════════════════════════════════════════════════════════════════╗\n");
        report.append("║              FIM DO RELATÓRIO - SLAVA UKRAINI! 🇺🇦               ║\n");
        report.append("╚════════════════════════════════════════════════════════════════╝\n");

        return report.toString();
    }

    /**
     * Salva relatório em arquivo TXT
     */
    public static void saveReport(String report, String filename) {
        try {
            File file = new File(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(report);
            }
            System.out.println("✅ Relatório salvo: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("❌ Erro ao salvar relatório: " + e.getMessage());
        }
    }

    /**
     * Gera relatório HTML (mais bonito)
     */
    public static String generateHTMLReport(Route route, AlgorithmResult result,
                                            GraphService.NetworkStatistics stats) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>Relatório de Viagem - Ukraine Logistics</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; background: linear-gradient(135deg, #005BBB 0%, #FFD500 100%); margin: 0; padding: 20px; }\n");
        html.append(".container { max-width: 900px; margin: 0 auto; background: white; border-radius: 15px; box-shadow: 0 10px 40px rgba(0,0,0,0.3); overflow: hidden; }\n");
        html.append(".header { background: linear-gradient(135deg, #005BBB 0%, #0066CC 100%); color: white; padding: 30px; text-align: center; }\n");
        html.append(".header h1 { margin: 0; font-size: 32px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }\n");
        html.append(".content { padding: 30px; }\n");
        html.append(".section { margin-bottom: 30px; padding: 20px; background: #f8f9fa; border-left: 5px solid #005BBB; border-radius: 8px; }\n");
        html.append(".section h2 { color: #005BBB; margin-top: 0; }\n");
        html.append(".metric { display: inline-block; margin: 10px 20px 10px 0; padding: 15px 25px; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n");
        html.append(".metric-label { font-size: 12px; color: #666; text-transform: uppercase; }\n");
        html.append(".metric-value { font-size: 24px; font-weight: bold; color: #005BBB; }\n");
        html.append(".path-step { padding: 10px; margin: 5px 0; background: white; border-radius: 5px; }\n");
        html.append(".frontline { color: #FF6F00; font-weight: bold; }\n");
        html.append(".destroyed { color: #F44336; font-weight: bold; }\n");
        html.append(".footer { background: #2c3e50; color: white; padding: 20px; text-align: center; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class='container'>\n");
        html.append("<div class='header'>\n");
        html.append("<h1>🚂 RELATÓRIO DE VIAGEM</h1>\n");
        html.append("<p>Ukraine Military Logistics System</p>\n");
        html.append("<p>").append(LocalDateTime.now().format(formatter)).append("</p>\n");
        html.append("</div>\n");

        html.append("<div class='content'>\n");

        if (route != null) {
            html.append("<div class='section'>\n");
            html.append("<h2>📍 Informações da Rota</h2>\n");
            html.append("<div class='metric'><div class='metric-label'>Origem</div><div class='metric-value'>")
                    .append(route.getStart().getName()).append("</div></div>\n");
            html.append("<div class='metric'><div class='metric-label'>Destino</div><div class='metric-value'>")
                    .append(route.getEnd().getName()).append("</div></div>\n");
            html.append("<div class='metric'><div class='metric-label'>Distância</div><div class='metric-value'>")
                    .append(String.format("%.1f km", route.getTotalDistance())).append("</div></div>\n");
            html.append("<div class='metric'><div class='metric-label'>Paradas</div><div class='metric-value'>")
                    .append(route.getStepCount()).append("</div></div>\n");
            html.append("</div>\n");

            html.append("<div class='section'>\n");
            html.append("<h2>🛤️ Caminho Percorrido</h2>\n");
            List<Oblast> path = route.getPath();
            for (int i = 0; i < path.size(); i++) {
                Oblast o = path.get(i);
                html.append("<div class='path-step'>");
                html.append(i + 1).append(". <strong>").append(o.getName()).append("</strong>");
                if (o.isFrontline()) html.append(" <span class='frontline'>⚠️ FRONTLINE</span>");
                if (o.isDestroyed()) html.append(" <span class='destroyed'>💥 DESTRUÍDO</span>");
                html.append("</div>\n");
            }
            html.append("</div>\n");
        }

        html.append("</div>\n");

        html.append("<div class='footer'>\n");
        html.append("<p>Desenvolvido por: Quirson Ngale • Celestino Sitoe • Stanley Cossa</p>\n");
        html.append("<p>Instituto Superior de Ciências e Tecnologia de Moçambique</p>\n");
        html.append("</div>\n");

        html.append("</div>\n</body>\n</html>");

        return html.toString();
    }
}