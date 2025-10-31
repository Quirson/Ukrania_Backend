package com.isctem.ukraine.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.isctem.ukraine.model.*;
import java.util.stream.Collectors;
import com.isctem.ukraine.util.SoundManager;
import com.isctem.ukraine.util.ReportGenerator;

/**
 * Painel de Controle com todas as operações
 */
public class ControlPanel {

    private final VBox pane;
    private final MainViewController mainController;

    private ComboBox<String> startCombo;
    private ComboBox<String> endCombo;
    private ComboBox<AlgorithmType> algorithmCombo;
    private Slider attackSlider;

    public ControlPanel(MainViewController mainController) {
        this.mainController = mainController;
        this.pane = new VBox(15);
        this.pane.setPadding(new Insets(15));
        this.pane.setStyle("-fx-background-color: #252525;");
        this.pane.setPrefWidth(320);

        buildPanel();
    }

    private void buildPanel() {
        // Título
        Label title = createSectionTitle("⚙️ PAINEL DE CONTROLE");

        // Seção 1: Seleção de Pontos
        VBox pointSelection = createPointSelectionSection();

        // Seção 2: Algoritmos
        VBox algorithmSection = createAlgorithmSection();

        // Seção 3: Simulação de Guerra
        VBox warSection = createWarSimulationSection();

        // Seção 4: Ferramentas
        VBox toolsSection = createToolsSection();

        // Separadores
        Separator sep1 = new Separator();
        Separator sep2 = new Separator();
        Separator sep3 = new Separator();

        pane.getChildren().addAll(
                title,
                pointSelection,
                sep1,
                algorithmSection,
                sep2,
                warSection,
                sep3,
                toolsSection
        );

        ScrollPane scroll = new ScrollPane(pane);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #252525; -fx-background-color: #252525;");
    }

    private VBox createPointSelectionSection() {
        VBox section = new VBox(10);

        Label sectionTitle = createSubTitle("📍 Seleção de Rotas");

        // ComboBox de origem
        Label startLabel = new Label("Origem:");
        startLabel.setTextFill(Color.LIGHTGRAY);
        startCombo = new ComboBox<>();
        startCombo.setPrefWidth(280);
        startCombo.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white;");

        // ComboBox de destino
        Label endLabel = new Label("Destino:");
        endLabel.setTextFill(Color.LIGHTGRAY);
        endCombo = new ComboBox<>();
        endCombo.setPrefWidth(280);
        endCombo.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white;");

        // Preencher com Oblasts
        var oblasts = mainController.getGraphService().getGraph().getAllOblasts()
                .stream()
                .map(Oblast::getId)
                .sorted()
                .collect(Collectors.toList());

        startCombo.getItems().addAll(oblasts);
        endCombo.getItems().addAll(oblasts);

        startCombo.setValue("kyiv");
        endCombo.setValue("lviv");

        // Botão de swap
        Button swapBtn = new Button("⇄ Trocar");
        swapBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
        swapBtn.setOnAction(e -> {
            String temp = startCombo.getValue();
            startCombo.setValue(endCombo.getValue());
            endCombo.setValue(temp);
        });

        section.getChildren().addAll(
                sectionTitle,
                startLabel, startCombo,
                endLabel, endCombo,
                swapBtn
        );

        return section;
    }

    private VBox createAlgorithmSection() {
        VBox section = new VBox(10);

        Label sectionTitle = createSubTitle("🧠 Algoritmos");

        // ComboBox de algoritmo
        Label algoLabel = new Label("Selecione o Algoritmo:");
        algoLabel.setTextFill(Color.LIGHTGRAY);

        algorithmCombo = new ComboBox<>();
        algorithmCombo.getItems().addAll(AlgorithmType.values());
        algorithmCombo.setValue(AlgorithmType.DIJKSTRA);
        algorithmCombo.setPrefWidth(280);
        algorithmCombo.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white;");

        // Descrição do algoritmo
        TextArea algoDesc = new TextArea();
        algoDesc.setEditable(false);
        algoDesc.setWrapText(true);
        algoDesc.setPrefHeight(80);
        algoDesc.setStyle("-fx-control-inner-background: #2a2a2a; -fx-text-fill: lightgray;");

        algorithmCombo.setOnAction(e -> {
            AlgorithmType selected = algorithmCombo.getValue();
            algoDesc.setText(String.format(
                    "Nome: %s\n\n%s\n\nComplexidade: %s\n\nUso Ideal: %s",
                    selected.getPortugueseName(),
                    selected.getDescription(),
                    selected.getComplexity(),
                    selected.getIdealUseCase()
            ));
        });

        // Definir descrição inicial
        algorithmCombo.fireEvent(new javafx.event.ActionEvent());

        // Botão Executar
        Button executeBtn = createStyledButton("🚀 EXECUTAR ALGORITMO", "#4CAF50");
        executeBtn.setOnAction(e -> {
            String start = startCombo.getValue();
            String end = endCombo.getValue();
            AlgorithmType algo = algorithmCombo.getValue();

            if (start != null && end != null && algo != null) {
                mainController.executeAlgorithm(algo, start, end);
            }
        });

        // Botão Comparar Todos
        Button compareBtn = createStyledButton("📊 COMPARAR TODOS", "#2196F3");
        compareBtn.setOnAction(e -> {
            String start = startCombo.getValue();
            String end = endCombo.getValue();

            if (start != null && end != null) {
                mainController.compareAllAlgorithms(start, end);
            }
        });

        section.getChildren().addAll(
                sectionTitle,
                algoLabel,
                algorithmCombo,
                algoDesc,
                executeBtn,
                compareBtn
        );

        return section;
    }

    private VBox createWarSimulationSection() {
        VBox section = new VBox(10);

        Label sectionTitle = createSubTitle("💥 Simulação de Guerra");

        Label sliderLabel = new Label("Destruição da Rede:");
        sliderLabel.setTextFill(Color.LIGHTGRAY);

        attackSlider = new Slider(0, 100, 25);
        attackSlider.setShowTickLabels(true);
        attackSlider.setShowTickMarks(true);
        attackSlider.setMajorTickUnit(25);
        attackSlider.setBlockIncrement(5);

        Label percentLabel = new Label("25%");
        percentLabel.setTextFill(Color.web("#FF5252"));
        percentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        attackSlider.valueProperty().addListener((obs, old, newVal) -> {
            percentLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });

        Button attackBtn = createStyledButton("💣 SIMULAR ATAQUE", "#F44336");
        attackBtn.setOnAction(e -> {
            mainController.simulateAttack(attackSlider.getValue());
        });

        Button repairBtn = createStyledButton("🔧 REPARAR REDE", "#4CAF50");
        repairBtn.setOnAction(e -> {
            mainController.repairNetwork();
        });

        section.getChildren().addAll(
                sectionTitle,
                sliderLabel,
                attackSlider,
                percentLabel,
                attackBtn,
                repairBtn
        );

        return section;
    }

    private VBox createToolsSection() {
        VBox section = new VBox(10);

        Label sectionTitle = createSubTitle("🛠️ Ferramentas");

        Button benchmarkBtn = createStyledButton("⏱️ Benchmark", "#FF9800");
        benchmarkBtn.setOnAction(e -> mainController.runBenchmark());

        Button clearBtn = createStyledButton("🧹 Limpar Visualização", "#9E9E9E");
        clearBtn.setOnAction(e -> mainController.clearVisualization());

        Button resetBtn = createStyledButton("🔄 Resetar Sistema", "#607D8B");
        resetBtn.setOnAction(e -> {
            mainController.getGraphService().resetGraph();
            mainController.clearVisualization();
            mainController.getLogPanel().addLog("🔄 Sistema resetado!");
        });

        // NOVO: Botão de teste de som
        Button soundTestBtn = createStyledButton("🔊 Testar Som", "#9C27B0");
        soundTestBtn.setOnAction(e -> {
            com.isctem.ukraine.util.SoundManager sm = com.isctem.ukraine.util.SoundManager.getInstance();
            mainController.getLogPanel().addLog(sm.getDebugInfo());

            // Tocar todos os sons
            mainController.getLogPanel().addLog("🎵 Testando sons...");
            sm.play(com.isctem.ukraine.util.SoundManager.SoundType.TRAIN_HORN);

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            pause.setOnFinished(ev -> {
                sm.play(com.isctem.ukraine.util.SoundManager.SoundType.EXPLOSION);
            });
            pause.play();
        });
        Button guideBtn = createStyledButton("Guia de Uso", "#2196F3");
        guideBtn.setOnAction(e -> UserGuideView.show());

        Button aboutBtn = createStyledButton("Desenvolvedores", "#9C27B0");
        aboutBtn.setOnAction(e -> AboutDevelopersView.show());

        Button reportBtn = createStyledButton("Gerar Relatório", "#FF5722");
        reportBtn.setOnAction(e -> {
            // Implementar geração de relatório
            mainController.getLogPanel().addLog("Funcionalidade em desenvolvimento...");
        });
        // 2. Adição de TODOS os botões ao GridPane de forma SEQUENCIAL
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        grid.add(benchmarkBtn, 0, 0);
        grid.add(clearBtn, 0, 1);
        grid.add(resetBtn, 0, 2);
        grid.add(soundTestBtn, 0, 3);
        grid.add(guideBtn, 0, 4); // Adicionado aqui
        grid.add(aboutBtn, 0, 5); // Adicionado aqui
        grid.add(reportBtn, 0, 6); // Adicionado aqui

        // 3. Adição final do GridPane ao VBox (Apenas UMA VEZ!)
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    // ========== HELPERS ==========

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.web("#FFD500"));
        return label;
    }

    private Label createSubTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#00E5FF"));
        return label;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(280);
        btn.setPrefHeight(40);
        btn.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 13px; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;",
                color
        ));

        btn.setOnMouseEntered(e ->
                btn.setStyle(btn.getStyle() + "-fx-opacity: 0.8;")
        );
        btn.setOnMouseExited(e ->
                btn.setStyle(btn.getStyle().replace("-fx-opacity: 0.8;", ""))
        );

        return btn;
    }

    public VBox getPane() {
        return pane;
    }
}