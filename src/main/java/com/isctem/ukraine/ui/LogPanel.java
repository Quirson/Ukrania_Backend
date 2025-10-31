package com.isctem.ukraine.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Painel de Logs em Tempo Real
 */
public class LogPanel {

    private final VBox pane;
    private TextArea logArea;
    private final DateTimeFormatter timeFormatter;
    private int logCount = 0;

    public LogPanel() {
        this.pane = new VBox(10);
        this.pane.setPadding(new Insets(15));
        this.pane.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 10;");

        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        buildPanel();
    }

    private void buildPanel() {
        // Título
        Label title = new Label("📋 CONSOLE DE LOGS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#00E5FF"));

        // TextArea para logs
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(300);
        logArea.setStyle(
                "-fx-control-inner-background: #1a1a1a; " +
                        "-fx-text-fill: #00FF00; " +
                        "-fx-font-family: 'Consolas', 'Monaco', monospace; " +
                        "-fx-font-size: 11px;"
        );

        // Botão limpar
        Button clearBtn = new Button("🗑️ Limpar Logs");
        clearBtn.setStyle(
                "-fx-background-color: #444; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 11px; " +
                        "-fx-cursor: hand;"
        );
        clearBtn.setOnAction(e -> clearLogs());

        pane.getChildren().addAll(title, logArea, clearBtn);
    }

    /**
     * Adiciona um log com timestamp
     */
    public void addLog(String message) {
        Platform.runLater(() -> {
            logCount++;
            String timestamp = LocalTime.now().format(timeFormatter);
            String logLine = String.format("[%s] #%03d: %s\n", timestamp, logCount, message);

            logArea.appendText(logLine);

            // Auto-scroll para o final
            logArea.setScrollTop(Double.MAX_VALUE);

            // Limitar logs (manter apenas últimos 100)
            String[] lines = logArea.getText().split("\n");
            if (lines.length > 100) {
                StringBuilder newText = new StringBuilder();
                for (int i = lines.length - 100; i < lines.length; i++) {
                    newText.append(lines[i]).append("\n");
                }
                logArea.setText(newText.toString());
            }
        });
    }

    /**
     * Limpa todos os logs
     */
    public void clearLogs() {
        logArea.clear();
        logCount = 0;
        addLog("Console limpo");
    }

    public VBox getPane() {
        return pane;
    }
}