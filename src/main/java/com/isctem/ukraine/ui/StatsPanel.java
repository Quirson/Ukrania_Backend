package com.isctem.ukraine.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Separator;

import com.isctem.ukraine.service.GraphService;

/**
 * Painel de Estatísticas em Tempo Real
 */
public class StatsPanel {

    private final VBox pane;
    private final GraphService graphService;

    // Labels dinâmicos
    private Label oblastsLabel;
    private Label destroyedOblastsLabel;
    private Label frontlineLabel;
    private Label connectionsLabel;
    private Label destroyedConnsLabel;
    private Label supplyLabel;
    private Label connectivityLabel;
    private Label hubLabel;

    public StatsPanel(GraphService graphService) {
        this.graphService = graphService;
        this.pane = new VBox(10);
        this.pane.setPadding(new Insets(15));
        this.pane.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 10;");

        buildPanel();
    }

    private void buildPanel() {
        // Título menor
        Label title = new Label("📊 ESTATÍSTICAS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#FFD500"));

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);







                grid.setPadding(new Insets(8));

        int row = 0;

        // Oblasts
        grid.add(createIconLabel("🏛️"), 0, row);
        oblastsLabel = createValueLabel("0");
        grid.add(createKeyLabel("Oblasts Totais:"), 1, row);
        grid.add(oblastsLabel, 2, row);
        row++;

        // Oblasts Destruídos
        grid.add(createIconLabel("💥"), 0, row);
        destroyedOblastsLabel = createValueLabel("0", Color.web("#F44336"));
        grid.add(createKeyLabel("Destruídos:"), 1, row);
        grid.add(destroyedOblastsLabel, 2, row);
        row++;

        // Frontline
        grid.add(createIconLabel("⚠️"), 0, row);
        frontlineLabel = createValueLabel("0", Color.web("#FF9800"));
        grid.add(createKeyLabel("Frontline:"), 1, row);
        grid.add(frontlineLabel, 2, row);
        row++;

        // Separador
        grid.add(new Separator(), 0, row, 3, 1);
        row++;

        // Conexões
        grid.add(createIconLabel("🔗"), 0, row);
        connectionsLabel = createValueLabel("0");
        grid.add(createKeyLabel("Conexões:"), 1, row);
        grid.add(connectionsLabel, 2, row);
        row++;

        // Conexões Destruídas
        grid.add(createIconLabel("⛔"), 0, row);
        destroyedConnsLabel = createValueLabel("0", Color.web("#F44336"));
        grid.add(createKeyLabel("Destruídas:"), 1, row);
        grid.add(destroyedConnsLabel, 2, row);
        row++;

        // Separador
        grid.add(new Separator(), 0, row, 3, 1);
        row++;

        // Supply Level
        grid.add(createIconLabel("📦"), 0, row);
        supplyLabel = createValueLabel("100%", Color.web("#4CAF50"));
        grid.add(createKeyLabel("Supply Médio:"), 1, row);
        grid.add(supplyLabel, 2, row);
        row++;

        // Conectividade
        grid.add(createIconLabel("📡"), 0, row);
        connectivityLabel = createValueLabel("0%");
        grid.add(createKeyLabel("Conectividade:"), 1, row);
        grid.add(connectivityLabel, 2, row);
        row++;

        // Hub Principal
        grid.add(createIconLabel("🎯"), 0, row);
        hubLabel = createValueLabel("N/A", Color.web("#00E5FF"));
        grid.add(createKeyLabel("Hub Principal:"), 1, row);
        grid.add(hubLabel, 2, row);

        pane.getChildren().addAll(title, grid);
    }

    /**
     * Atualiza todas as estatísticas
     */
    public void update() {
        var stats = graphService.getNetworkStatistics();

        oblastsLabel.setText(String.valueOf(stats.totalOblasts()));
        destroyedOblastsLabel.setText(String.valueOf(stats.destroyedOblasts()));
        frontlineLabel.setText(String.valueOf(stats.frontlineOblasts()));
        connectionsLabel.setText(String.valueOf(stats.totalConnections()));
        destroyedConnsLabel.setText(String.valueOf(stats.destroyedConnections()));

        // Supply com cor dinâmica
        double supply = stats.avgSupplyLevel();
        supplyLabel.setText(String.format("%.0f%%", supply));
        if (supply < 30) {
            supplyLabel.setTextFill(Color.web("#F44336"));
        } else if (supply < 70) {
            supplyLabel.setTextFill(Color.web("#FF9800"));
        } else {
            supplyLabel.setTextFill(Color.web("#4CAF50"));
        }

        // Conectividade
        connectivityLabel.setText(String.format("%.1f%%", stats.connectivity()));

        // Hub
        hubLabel.setText(stats.mainHub());
    }

    private Label createIconLabel(String icon) {
        Label label = new Label(icon);
        label.setFont(Font.font(16));
        return label;
    }

    private Label createKeyLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.LIGHTGRAY);
        label.setFont(Font.font(11));
        return label;
    }

    private Label createValueLabel(String text) {
        return createValueLabel(text, Color.WHITE);
    }

    private Label createValueLabel(String text, Color color) {
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        return label;
    }

    public VBox getPane() {
        return pane;
    }
}