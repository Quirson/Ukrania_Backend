package com.isctem.ukraine.ui;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.animation.*;
import javafx.util.Duration;

/**
 * Guia de Uso do Sistema - FULLSCREEN Moderna
 */
public class UserGuideView {

    private static Stage stage;

    public static void show() {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        // Root com blur background
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(10,10,10,0.92);");

        // Conteúdo principal
        BorderPane mainContainer = createMainContainer();
        root.getChildren().add(mainContainer);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        stage.show();
    }

    private static BorderPane createMainContainer() {
        BorderPane container = new BorderPane();
        container.setMaxSize(1400, 900);
        container.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1a1a2a, #2a1a2a); " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-color: linear-gradient(to right, #005BBB, #FFD500); " +
                        "-fx-border-width: 4; " +
                        "-fx-border-radius: 25;"
        );
        container.setEffect(new javafx.scene.effect.DropShadow(50, Color.BLACK));

        // Header
        VBox header = createHeader();
        container.setTop(header);

        // Conteúdo
        ScrollPane content = createContent();
        container.setCenter(content);

        // Footer
        HBox footer = createFooter();
        container.setBottom(footer);

        return container;
    }

    private static VBox createHeader() {
        VBox header = new VBox(10);
        header.setStyle("-fx-background-color: linear-gradient(to right, #005BBB, #003D7A);");
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setAlignment(Pos.CENTER);

        Label title = new Label("📚 GUIA COMPLETO DO SISTEMA");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        title.setTextFill(Color.WHITE);
        title.setEffect(new javafx.scene.effect.Glow(0.3));

        Label subtitle = new Label("Ukraine Military Logistics System - v2.0");
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setTextFill(Color.web("#FFD500"));

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private static ScrollPane createContent() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: transparent;");

        // Seções do guia
        String[][] sections = {
                {
                        "🚀 INÍCIO RÁPIDO",
                        "• Selecione ORIGEM e DESTINO nos dropdowns\n" +
                                "• Escolha um ALGORITMO (Dijkstra recomendado)\n" +
                                "• Clique em 'EXECUTAR ALGORITMO'\n" +
                                "• Visualize a rota no mapa e anime o comboio"
                },
                {
                        "🎯 VISUALIZAÇÃO DO MAPA",
                        "• 🔍 ZOOM: Scroll do mouse ou botões +/-\n" +
                                "• 🖱️ PAN: Arraste para navegar\n" +
                                "• 🔄 RESET: Botão reset para vista padrão\n" +
                                "• 👆 DUPLO CLIQUE: Detalhes da cidade"
                },
                {
                        "🚂 ANIMAÇÃO DO COMBOIO",
                        "• ▶️ ANIMAR: Inicia viagem após cálculo\n" +
                                "• 🎚️ VELOCIDADE: Slider para controle\n" +
                                "• ⏸️ PAUSAR: Pausa a animação\n" +
                                "• ⏹️ PARAR: Cancela a viagem\n" +
                                "• 📯 BUZINA: Efeito sonoro realista"
                },
                {
                        "🧠 ALGORITMOS DISPONÍVEIS",
                        "🔹 BFS (Breadth-First): Busca em largura\n" +
                                "🔹 DFS (Depth-First): Busca em profundidade\n" +
                                "🔹 DIJKSTRA: Caminho mais curto ⭐ RECOMENDADO\n" +
                                "🔹 KRUSKAL: Árvore geradora mínima\n" +
                                "🔹 PRIM: Árvore geradora mínima alternativa"
                },
                {
                        "💥 SIMULAÇÃO DE GUERRA",
                        "• 🎚️ DESTRUIR: Slider define % de dano\n" +
                                "• 💣 ATAQUE: Simula ataque russo\n" +
                                "• ⛔ BLOQUEIO: Comboio não passa em áreas destruídas\n" +
                                "• 🔧 REPARAR: Restaura rede completa"
                },
                {
                        "📊 FERRAMENTAS AVANÇADAS",
                        "• 📊 COMPARAR: Executa todos algoritmos\n" +
                                "• ⏱️ BENCHMARK: Análise de performance\n" +
                                "• 📄 RELATÓRIO: Gera documentação\n" +
                                "• 📈 ESTATÍSTICAS: Métricas em tempo real"
                },
                {
                        "🎨 LEGENDA DO MAPA",
                        "✅ Verde: Oblast operacional\n" +
                                "⚠️ Laranja: Oblast na frontline\n" +
                                "🟡 Amarelo: Supply crítico (<30%)\n" +
                                "💥 Vermelho: Oblast destruído\n" +
                                "🚂 Comboio: Em movimento\n" +
                                "💣 X Vermelho: Conexão destruída"
                },
                {
                        "🔊 SISTEMA DE SOM",
                        "• 🔊 VOLUME: Slider principal\n" +
                                "• 🔇 TOGGLE: Liga/desliga som\n" +
                                "• 🎵 EFEITOS: Buzina, explosões, alertas\n" +
                                "• 🚂 AMBIENTE: Som contínuo do trem"
                },
                {
                        "⚡ DICAS PROFISSIONAIS",
                        "✨ Use Dijkstra para rotas ótimas\n" +
                                "✨ Evite oblasts da frontline\n" +
                                "✨ Compare algoritmos academicamente\n" +
                                "✨ Teste diferentes cenários de guerra\n" +
                                "✨ Gere relatórios para documentação"
                }
        };

        for (String[] section : sections) {
            content.getChildren().add(createSection(section[0], section[1]));
        }

        scroll.setContent(content);
        return scroll;
    }

    private static VBox createSection(String title, String content) {
        VBox section = new VBox(12);
        section.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05); " +
                        "-fx-border-color: #005BBB; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 20;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#FFD600"));
        titleLabel.setWrapText(true);

        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Arial", 14));
        contentLabel.setTextFill(Color.LIGHTGRAY);
        contentLabel.setWrapText(true);
        contentLabel.setLineSpacing(2);

        section.getChildren().addAll(titleLabel, contentLabel);

        // Efeito hover
        section.setOnMouseEntered(e -> {
            section.setStyle(section.getStyle().replace("0.05", "0.08"));
        });
        section.setOnMouseExited(e -> {
            section.setStyle(section.getStyle().replace("0.08", "0.05"));
        });

        return section;
    }

    private static HBox createFooter() {
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));
        footer.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Button closeBtn = new Button("✅ ENTENDI TUDO!");
        closeBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4CAF50, #45a049); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 40; " +
                        "-fx-background-radius: 25; " +
                        "-fx-cursor: hand;"
        );
        closeBtn.setEffect(new javafx.scene.effect.DropShadow(10, Color.GREEN));
        closeBtn.setOnAction(e -> stage.close());

        // Efeito hover
        closeBtn.setOnMouseEntered(e -> {
            closeBtn.setStyle(closeBtn.getStyle().replace("#45a049", "#4CCF50"));
        });
        closeBtn.setOnMouseExited(e -> {
            closeBtn.setStyle(closeBtn.getStyle().replace("#4CCF50", "#45a049"));
        });

        footer.getChildren().add(closeBtn);
        return footer;
    }
}