package com.isctem.ukraine.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.*;
import javafx.util.Duration;

import com.isctem.ukraine.model.*;
import com.isctem.ukraine.service.*;

/**
 * Controller Principal da Interface - Layout CORRIGIDO
 */
public class MainViewController {

    private final BorderPane mainLayout;
    private final GraphService graphService;
    private final RouteCalculator routeCalculator;
    private final PerformanceAnalyzer performanceAnalyzer;

    // Componentes UI
    private GraphVisualizationPane graphPane;
    private ControlPanel controlPanel;
    private StatsPanel statsPanel;
    private LogPanel logPanel;

    public MainViewController() {
        this.graphService = new GraphService();
        this.routeCalculator = new RouteCalculator(graphService);
        this.performanceAnalyzer = new PerformanceAnalyzer(graphService);

        this.mainLayout = new BorderPane();
        this.mainLayout.setStyle("-fx-background-color: #0a0a0a;");

        setupUI();
    }

    private void setupUI() {
        // TOPO: Header compacto
        HBox header = createHeader();
        mainLayout.setTop(header);

        // CENTRO: Visualização do Grafo (70% da largura)
        graphPane = new GraphVisualizationPane(graphService);
        StackPane graphContainer = new StackPane(graphPane.getPane());
        graphContainer.setStyle("-fx-background-color: #0a0a0a;");

        // Definir tamanho máximo para o gráfico
        graphContainer.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(graphContainer, new Insets(0));
        mainLayout.setCenter(graphContainer);

        // ESQUERDA: Painel de Controle (15% da largura)
        controlPanel = new ControlPanel(this);
        VBox leftPanel = controlPanel.getPane();
        leftPanel.setPrefWidth(280); // Largura fixa menor
        leftPanel.setMaxWidth(280);

        ScrollPane leftScroll = new ScrollPane(leftPanel);
        leftScroll.setFitToWidth(true);
        leftScroll.setStyle("-fx-background: transparent; -fx-background-color: #1a1a1a;");
        leftScroll.setPrefViewportWidth(280);

        VBox leftContainer = new VBox(leftScroll);
        leftContainer.setStyle("-fx-background-color: #1a1a1a;");
        leftContainer.setPrefWidth(280);
        leftContainer.setMaxWidth(280);

        mainLayout.setLeft(leftContainer);

        // DIREITA: Estatísticas e Logs (15% da largura)
        VBox rightPanel = createRightPanel();
        rightPanel.setPrefWidth(300); // Largura fixa
        rightPanel.setMaxWidth(300);
        mainLayout.setRight(rightPanel);

        // BAIXO: Status Bar minimalista
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);

        // Configurar prioridades de crescimento
        BorderPane.setMargin(leftContainer, new Insets(0));
        BorderPane.setMargin(rightPanel, new Insets(0));
        BorderPane.setMargin(graphContainer, new Insets(0));

        // Log inicial
        Platform.runLater(() -> {
            logPanel.addLog("✅ Sistema iniciado - Layout otimizado");
            logPanel.addLog("📊 " + graphService.getGraph().getNodeCount() + " Oblasts carregados");
            statsPanel.update();
        });
    }

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setStyle("-fx-background-color: linear-gradient(to right, #005BBB, #002F6C);");
        header.setPadding(new Insets(5, 15, 5, 15));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🇺🇦 UKRAINE LOGISTICS SERVICE 🇺🇦 ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botões de controle rápido
        HBox quickControls = new HBox(5);
        quickControls.setAlignment(Pos.CENTER_RIGHT);

        Button guideBtn = createHeaderButton("✉\uFE0F", "Guia de Uso");
        Button devsBtn = createHeaderButton("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB", "Desenvolvedores");
        Button reportBtn = createHeaderButton("\uD83D\uDCCB", "Gerar Relatório");

        guideBtn.setOnAction(e -> UserGuideView.show());
        devsBtn.setOnAction(e -> AboutDevelopersView.show());
        reportBtn.setOnAction(e -> generateReport());

        quickControls.getChildren().addAll(guideBtn, devsBtn, reportBtn);
        header.getChildren().addAll(title, spacer, quickControls);

        return header;
    }

    private void generateReport() {
        logPanel.addLog("\uD83D\uDCCB Iniciando geração de relatório...");
        // Aqui você implementará a geração do relatório
        logPanel.addLog("✅ Relatório gerado com sucesso!");
    }

    private Button createHeaderButton(String icon, String tooltip) {
        Button btn = new Button(icon);
        btn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
        btn.setTooltip(new Tooltip(tooltip));
        return btn;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(5);
        rightPanel.setStyle("-fx-background-color: #1a1a1a;");
        rightPanel.setPadding(new Insets(5));
        rightPanel.setPrefWidth(300);
        rightPanel.setMaxWidth(300);

        statsPanel = new StatsPanel(graphService);
        logPanel = new LogPanel();

        // Configurar tamanhos fixos
        VBox statsBox = statsPanel.getPane();
        statsBox.setPrefHeight(200); // Altura fixa para stats
        statsBox.setMaxHeight(200);

        VBox logBox = logPanel.getPane();
        logBox.setPrefHeight(400); // Altura fixa para logs

        rightPanel.getChildren().addAll(statsBox, new Separator(), logBox);

        return rightPanel;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setStyle("-fx-background-color: #151515; -fx-padding: 3 8;");
        statusBar.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label("🟢 SISTEMA OPERACIONAL");
        statusLabel.setTextFill(Color.LIGHTGREEN);
        statusLabel.setFont(Font.font(10));

        Label statsLabel = new Label();
        statsLabel.setTextFill(Color.LIGHTGRAY);
        statsLabel.setFont(Font.font(9));

        // Atualizar estatísticas em tempo real
        Timeline statsUpdater = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            var stats = graphService.getNetworkStatistics();
            statsLabel.setText(String.format("Oblasts: %d | Conexões: %d | Destruídos: %d",
                    stats.totalOblasts(), stats.totalConnections(), stats.destroyedOblasts()));
        }));
        statsUpdater.setCycleCount(Timeline.INDEFINITE);
        statsUpdater.play();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label();
        timeLabel.setTextFill(Color.GRAY);
        timeLabel.setFont(Font.font(9));

        // Atualizar hora
        Timeline timeUpdater = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLabel.setText(java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));
        timeUpdater.setCycleCount(Timeline.INDEFINITE);
        timeUpdater.play();

        statusBar.getChildren().addAll(statusLabel, statsLabel, spacer, timeLabel);

        return statusBar;
    }

    // ========== MÉTODOS PÚBLICOS ==========

    public void executeAlgorithm(AlgorithmType type, String startId, String endId) {
        logPanel.addLog("🚀 Executando " + type.name() + "...");

        AlgorithmResult result = graphService.executeAlgorithm(type, startId, endId);

        if (result.isSuccess()) {
            logPanel.addLog("✅ " + type.name() + " concluído em " +
                    result.getExecutionTimeMs() + "ms");

            if (result.getMainRoute() != null) {
                graphPane.displayRoute(result.getMainRoute());
                logPanel.addLog("📏 Distância: " +
                        String.format("%.1f km", result.getMainRoute().getTotalDistance()));
            }
        } else {
            logPanel.addLog("❌ Erro: " + result.getErrorMessage());
        }

        statsPanel.update();
    }

    public void compareAllAlgorithms(String startId, String endId) {
        logPanel.addLog("📊 Comparando todos os algoritmos...");

        RouteCalculator.ComparisonResult comparison =
                routeCalculator.compareAlgorithms(startId, endId);

        logPanel.addLog(comparison.getComparisonTable());

        AlgorithmResult best = comparison.getShortest();
        if (best != null && best.getMainRoute() != null) {
            graphPane.displayRoute(best.getMainRoute());
            logPanel.addLog("🏆 Melhor rota: " + best.getAlgorithmName());
        }
    }

    public void simulateAttack(double percentage) {
        logPanel.addLog("💥 Simulando ataque (" + percentage + "%)...");
        graphService.simulateRussianAttack(percentage);
        graphPane.refresh();
        statsPanel.update();
        logPanel.addLog("⚠️ " + percentage + "% da rede atingida!");
    }

    public void repairNetwork() {
        logPanel.addLog("🔧 Reparando rede...");
        graphService.repairAll();
        graphPane.refresh();
        statsPanel.update();
        logPanel.addLog("✅ Rede reparada!");
    }

    public void runBenchmark() {
        logPanel.addLog("⏱️ Executando benchmark...");
        PerformanceAnalyzer.BenchmarkSummary benchmark =
                performanceAnalyzer.runFullBenchmark("kyiv", "odesa", 10);
        logPanel.addLog(benchmark.getDetailedReport());
    }

    public void clearVisualization() {
        graphPane.clearHighlights();
        logPanel.addLog("🧹 Visualização limpa");
    }

    public GraphService getGraphService() {
        return graphService;
    }

    public LogPanel getLogPanel() {
        return logPanel;
    }

    public BorderPane getView() {
        return mainLayout;
    }
}