package com.isctem.ukraine.ui;

import javafx.animation.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import com.isctem.ukraine.model.*;
import com.isctem.ukraine.service.GraphService;
import com.isctem.ukraine.util.SoundManager;

import java.util.*;

/**
 * Visualização ÉPICA com Comboio Realista + Sons + Efeitos Especiais
 */
public class GraphVisualizationPane {

    private final VBox mainContainer;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GraphService graphService;
    private final SoundManager soundManager;

    private Route currentRoute;
    private final Map<String, NodePosition> nodePositions;

    private Image ukraineMap;

    // Animação avançada
    private Timeline trainAnimation;
    private Train currentTrain;
    private List<Particle> particles = new ArrayList<>();
    private Timeline particleAnimation;

    private double trainSpeed = 1.0;
    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    private static final double WIDTH = 1300;
    private static final double HEIGHT = 900;
    private static final double NODE_RADIUS = 8;

    private static final double MAP_MIN_LON = 22.0;
    private static final double MAP_MAX_LON = 40.5;
    private static final double MAP_MIN_LAT = 44.0;
    private static final double MAP_MAX_LAT = 52.5;

    public GraphVisualizationPane(GraphService graphService) {
        this.graphService = graphService;
        this.soundManager = SoundManager.getInstance();
        this.nodePositions = new HashMap<>();

        this.canvas = new Canvas(WIDTH, HEIGHT);
        this.gc = canvas.getGraphicsContext2D();

        this.mainContainer = new VBox(10);
        this.mainContainer.setStyle("-fx-background-color: #2b2b2b;");

        loadUkraineMap();
        calculateNodePositions();
        setupUI();
        drawGraph();
        startParticleSystem();
    }

    private void loadUkraineMap() {
        try {
            ukraineMap = new Image(
                    getClass().getResourceAsStream("/images/ukraine-map.png"),
                    WIDTH, HEIGHT, true, true
            );
            System.out.println("✅ Mapa carregado!");
        } catch (Exception e) {
            System.out.println("⚠️ Usando mapa padrão");
            ukraineMap = null;
        }
    }

    private void setupUI() {
        HBox controlBar = createControls();
        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setStyle("-fx-background-color: #1a1a1a;");

        setupInteractivity();
        mainContainer.getChildren().addAll(controlBar, canvasContainer);
    }

    private HBox createControls() {
        HBox controls = new HBox(15);
        controls.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 10;");

        Label speedLabel = new Label("🚂 Velocidade:");
        speedLabel.setTextFill(Color.LIGHTGRAY);

        Slider speedSlider = new Slider(0.5, 3.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setPrefWidth(150);

        Label speedValue = new Label("1.0x");
        speedValue.setTextFill(Color.web("#00E5FF"));
        speedValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        speedSlider.valueProperty().addListener((obs, old, newVal) -> {
            trainSpeed = newVal.doubleValue();
            speedValue.setText(String.format("%.1fx", trainSpeed));
            if (trainAnimation != null) {
                trainAnimation.setRate(trainSpeed);
            }
        });

        // Volume control
        Label volLabel = new Label("🔊");
        volLabel.setTextFill(Color.LIGHTGRAY);

        Slider volumeSlider = new Slider(0, 1.0, 0.7);
        volumeSlider.setPrefWidth(100);
        volumeSlider.valueProperty().addListener((obs, old, newVal) -> {
            soundManager.setVolume(newVal.doubleValue());
        });

        CheckBox soundToggle = new CheckBox("Som");
        soundToggle.setSelected(true);
        soundToggle.setTextFill(Color.LIGHTGRAY);
        soundToggle.setOnAction(e -> soundManager.setSoundEnabled(soundToggle.isSelected()));

        Button playBtn = createButton("▶️", "#4CAF50", this::startTrainAnimation);
        Button pauseBtn = createButton("⏸️", "#FF9800", this::pauseTrainAnimation);
        Button stopBtn = createButton("⏹️", "#F44336", this::stopTrainAnimation);
        Button hornBtn = createButton("📯", "#9C27B0", () -> soundManager.play(SoundManager.SoundType.TRAIN_HORN));

        Button zoomIn = createButton("🔍+", "#607D8B", () -> {
            scale *= 1.2;
            drawGraph();
        });
        Button zoomOut = createButton("🔍-", "#607D8B", () -> {
            scale *= 0.8;
            drawGraph();
        });
        Button reset = createButton("🔄", "#607D8B", () -> {
            scale = 1.0;
            offsetX = 0;
            offsetY = 0;
            drawGraph();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        controls.getChildren().addAll(
                speedLabel, speedSlider, speedValue,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                volLabel, volumeSlider, soundToggle,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                playBtn, pauseBtn, stopBtn, hornBtn,
                spacer,
                zoomIn, zoomOut, reset
        );

        return controls;
    }

    private Button createButton(String text, String color, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px;", color));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void calculateNodePositions() {
        for (Oblast oblast : graphService.getGraph().getAllOblasts()) {
            double normX = (oblast.getLongitude() - MAP_MIN_LON) / (MAP_MAX_LON - MAP_MIN_LON);
            double normY = 1.0 - (oblast.getLatitude() - MAP_MIN_LAT) / (MAP_MAX_LAT - MAP_MIN_LAT);

            double x = 100 + normX * (WIDTH - 200);
            double y = 50 + normY * (HEIGHT - 100);

            nodePositions.put(oblast.getId(), new NodePosition(x, y));
        }
    }

    private void startParticleSystem() {
        particleAnimation = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            particles.removeIf(p -> !p.isAlive());
            particles.forEach(Particle::update);
        }));
        particleAnimation.setCycleCount(Timeline.INDEFINITE);
        particleAnimation.play();
    }

    public void drawGraph() {
        gc.setFill(Color.web("#0a0a0a"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.save();
        gc.translate(offsetX, offsetY);
        gc.scale(scale, scale);

        // Mapa de fundo
        if (ukraineMap != null) {
            gc.setGlobalAlpha(0.35);
            gc.drawImage(ukraineMap, 0, 0, WIDTH, HEIGHT);
            gc.setGlobalAlpha(1.0);
        } else {
            drawMapPlaceholder();
        }

        drawGrid();
        drawConnections();

        if (currentRoute != null) {
            drawRoute(currentRoute);
        }

        drawNodes();
        drawParticles();

        if (currentTrain != null) {
            drawTrain();
        }

        gc.restore();

        drawLegend();
        drawZoomInfo();
    }

    private void drawMapPlaceholder() {
        // Gradient background (cores da Ucrânia)
        gc.setFill(Color.web("#005BBB", 0.15));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.web("#FFD500", 0.1));
        gc.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

        gc.setStroke(Color.web("#005BBB"));
        gc.setLineWidth(3);
        gc.strokeRect(100, 50, WIDTH - 200, HEIGHT - 100);

        gc.setFill(Color.web("#FFD500", 0.2));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        gc.fillText("УКРАЇНА", 400, 450);
    }

    private void drawGrid() {
        gc.setStroke(Color.web("#2a2a2a", 0.5));
        gc.setLineWidth(0.5);

        for (int i = 0; i < WIDTH; i += 50) {
            gc.strokeLine(i, 0, i, HEIGHT);
        }
        for (int i = 0; i < HEIGHT; i += 50) {
            gc.strokeLine(0, i, WIDTH, i);
        }
    }

    private void drawConnections() {
        Graph graph = graphService.getGraph();
        Set<String> drawn = new HashSet<>();

        for (Connection conn : graph.getAllConnections()) {
            String key = conn.getFrom().getId() + "-" + conn.getTo().getId();
            String rev = conn.getTo().getId() + "-" + conn.getFrom().getId();

            if (drawn.contains(key) || drawn.contains(rev)) continue;

            NodePosition from = nodePositions.get(conn.getFrom().getId());
            NodePosition to = nodePositions.get(conn.getTo().getId());

            if (from == null || to == null) continue;

            if (conn.isDestroyed()) {
                // Linha destruída com efeito
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.setLineDashes(8, 8);
                gc.strokeLine(from.x, from.y, to.x, to.y);
                gc.setLineDashes(0);

                // X vermelho no meio
                double midX = (from.x + to.x) / 2;
                double midY = (from.y + to.y) / 2;
                gc.setStroke(Color.web("#FF0000"));
                gc.setLineWidth(4);
                gc.strokeLine(midX - 10, midY - 10, midX + 10, midY + 10);
                gc.strokeLine(midX - 10, midY + 10, midX + 10, midY - 10);
            } else {
                gc.setStroke(Color.web("#666666", 0.6));
                gc.setLineWidth(2);
                gc.strokeLine(from.x, from.y, to.x, to.y);
            }

            drawn.add(key);
        }
    }

    private void drawRoute(Route route) {
        List<Oblast> path = route.getPath();

        // Glow effect na rota
        gc.setStroke(Color.web("#00E5FF", 0.3));
        gc.setLineWidth(12);
        for (int i = 0; i < path.size() - 1; i++) {
            NodePosition from = nodePositions.get(path.get(i).getId());
            NodePosition to = nodePositions.get(path.get(i + 1).getId());
            if (from != null && to != null) {
                gc.strokeLine(from.x, from.y, to.x, to.y);
            }
        }

        // Linha principal
        gc.setStroke(Color.web("#00E5FF"));
        gc.setLineWidth(4);
        for (int i = 0; i < path.size() - 1; i++) {
            NodePosition from = nodePositions.get(path.get(i).getId());
            NodePosition to = nodePositions.get(path.get(i + 1).getId());
            if (from != null && to != null) {
                gc.strokeLine(from.x, from.y, to.x, to.y);
                drawArrow(from.x, from.y, to.x, to.y);
            }
        }

        // Numerar
        for (int i = 0; i < path.size(); i++) {
            NodePosition pos = nodePositions.get(path.get(i).getId());
            if (pos != null) {
                gc.setFill(Color.web("#00E5FF"));
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                gc.fillText(String.valueOf(i + 1), pos.x + 15, pos.y - 10);
            }
        }
    }

    private void drawArrow(double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double len = 15;
        double angleOffset = Math.PI / 6;

        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        gc.setFill(Color.web("#00E5FF"));
        double[] xPoints = {
                midX,
                midX - len * Math.cos(angle - angleOffset),
                midX - len * Math.cos(angle + angleOffset)
        };
        double[] yPoints = {
                midY,
                midY - len * Math.sin(angle - angleOffset),
                midY - len * Math.sin(angle + angleOffset)
        };
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawNodes() {
        for (Oblast oblast : graphService.getGraph().getAllOblasts()) {
            NodePosition pos = nodePositions.get(oblast.getId());
            if (pos == null) continue;

            Color nodeColor;
            if (oblast.isDestroyed()) {
                nodeColor = Color.web("#B71C1C");
                // Pulsar se destruído
                double pulse = (Math.sin(System.currentTimeMillis() / 200.0) + 1) / 2;
                gc.setGlobalAlpha(0.5 + pulse * 0.5);
            } else if (oblast.isFrontline()) {
                nodeColor = Color.web("#FF6F00");
            } else if (oblast.getSupplyLevel() < 30) {
                nodeColor = Color.web("#FBC02D");
            } else {
                nodeColor = Color.web("#4CAF50");
            }

            // Glow
            gc.setFill(nodeColor);
            gc.setGlobalAlpha(0.3);
            gc.fillOval(pos.x - NODE_RADIUS - 5, pos.y - NODE_RADIUS - 5,
                    (NODE_RADIUS + 5) * 2, (NODE_RADIUS + 5) * 2);

            // Nó principal
            gc.setGlobalAlpha(1.0);
            gc.setFill(nodeColor);
            gc.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Borda
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Nome
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            gc.fillText(oblast.getName(), pos.x + NODE_RADIUS + 5, pos.y + 4);

            if (oblast.isFrontline()) {
                gc.setFont(Font.font(14));
                gc.fillText("⚠️", pos.x - 7, pos.y + 5);
            }

            gc.setGlobalAlpha(1.0);
        }
    }

    private void drawTrain() {
        if (currentTrain == null) return;

        double x = currentTrain.x;
        double y = currentTrain.y;
        double angle = currentTrain.angle;

        gc.save();
        gc.translate(x, y);
        gc.rotate(Math.toDegrees(angle));

        // Sombra
        gc.setFill(Color.web("#000000", 0.4));
        gc.fillOval(-25, -23, 50, 46);

        // Fumaça atrás
        if (Math.random() < 0.3) {
            particles.add(new Particle(x - Math.cos(angle) * 20, y - Math.sin(angle) * 20));
        }

        // Corpo do comboio
        gc.setFill(Color.web("#D32F2F"));
        gc.fillRoundRect(-25, -15, 50, 30, 10, 10);

        // Janelas
        gc.setFill(Color.web("#FFEB3B"));
        gc.fillRect(-15, -8, 8, 8);
        gc.fillRect(7, -8, 8, 8);

        // Rodas
        gc.setFill(Color.web("#424242"));
        gc.fillOval(-20, 8, 12, 12);
        gc.fillOval(8, 8, 12, 12);

        // Chaminé
        gc.setFill(Color.web("#212121"));
        gc.fillRect(-8, -20, 6, 10);

        // Luz frontal
        gc.setFill(Color.web("#FFEB3B", 0.8));
        gc.fillOval(20, -5, 10, 10);

        // Borda brilhante
        gc.setStroke(Color.web("#FFD600"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(-25, -15, 50, 30, 10, 10);

        gc.restore();

        // Nome do comboio
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.fillText("🚂 Supply Train", x - 40, y - 35);
    }

    private void drawParticles() {
        for (Particle p : particles) {
            gc.setFill(Color.web("#888888", p.alpha));
            gc.fillOval(p.x - p.size / 2, p.y - p.size / 2, p.size, p.size);
        }
    }

    private void drawLegend() {
        double x = 20;
        double y = HEIGHT - 160;

        gc.setFill(Color.web("#1a1a1a", 0.95));
        gc.fillRoundRect(x, y, 220, 140, 10, 10);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.fillText("LEGENDA", x + 10, y + 20);

        gc.setFont(Font.font(10));

        String[] labels = {"✅ Operacional", "⚠️ Frontline", "🟡 Crítico", "💥 Destruído", "🚂 Comboio", "💣 Caminho Bloqueado"};
        String[] colors = {"#4CAF50", "#FF6F00", "#FBC02D", "#B71C1C", "#D32F2F", "#FF0000"};

        for (int i = 0; i < labels.length; i++) {
            gc.setFill(Color.web(colors[i]));
            gc.fillOval(x + 10, y + 35 + i * 18, 10, 10);
            gc.setFill(Color.LIGHTGRAY);
            gc.fillText(labels[i], x + 28, y + 44 + i * 18);
        }
    }

    private void drawZoomInfo() {
        gc.setFill(Color.web("#1a1a1a", 0.9));
        gc.fillRoundRect(WIDTH - 160, 20, 140, 50, 5, 5);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font(11));
        gc.fillText(String.format("Zoom: %.1fx", scale), WIDTH - 150, 40);
        gc.fillText("🖱️ Scroll ou botões", WIDTH - 150, 55);
    }

    // ========== ANIMAÇÃO DO COMBOIO ==========

    public void startTrainAnimation() {
        if (currentRoute == null || currentRoute.getPath().size() < 2) {
            System.out.println("⚠️ Selecione uma rota primeiro!");
            soundManager.play(SoundManager.SoundType.ALERT);
            return;
        }

        stopTrainAnimation();

        List<Oblast> path = currentRoute.getPath();

        // DETECÇÃO DE CICLOS
        Set<String> visited = new HashSet<>();
        boolean hasCycle = false;
        String cycleCity = null;

        for (Oblast oblast : path) {
            if (visited.contains(oblast.getId())) {
                hasCycle = true;
                cycleCity = oblast.getName();
                break;
            }
            visited.add(oblast.getId());
        }

        if (hasCycle) {
            System.out.println("🔄 CICLO DETECTADO em: " + cycleCity + "!");
            soundManager.play(SoundManager.SoundType.EXPLOSION);
            soundManager.playWithDelay(SoundManager.SoundType.ALERT, 1.0);
            // Criar explosão visual massiva
            String finalCycleCity = cycleCity;
            NodePosition pos = nodePositions.get(
                    path.stream()
                            .filter(o -> o.getName().equals(finalCycleCity))
                            .findFirst()
                            .map(Oblast::getId)
                            .orElse("")
            );

            if (pos != null) {
                for (int j = 0; j < 50; j++) {
                    particles.add(new Particle(pos.x, pos.y, Color.web("#FF1744")));
                }
            }

            showAlert("💥 CICLO DETECTADO!",
                    "O comboio passou por " + cycleCity + " mais de uma vez!\n\n" +
                            "Isso causa:\n" +
                            "• Desperdício de combustível\n" +
                            "• Atraso na entrega\n" +
                            "• Risco de emboscada\n\n" +
                            "Use algoritmo de caminho mínimo (Dijkstra)!");
            return;
        }

        // Verificar cidades destruídas
        boolean hasDestroyed = path.stream().anyMatch(Oblast::isDestroyed);
        if (hasDestroyed) {
            System.out.println("💥 ALERTA: Rota contém cidades destruídas!");
            soundManager.play(SoundManager.SoundType.EXPLOSION);
            showAlert("⚠️ ROTA BLOQUEADA!", "A rota contém cidades destruídas!\nO comboio não pode passar.");
            return;
        }

        // Verificar conexões destruídas
        for (int i = 0; i < path.size() - 1; i++) {
            Connection conn = graphService.getGraph().getConnection(
                    path.get(i).getId(), path.get(i + 1).getId()
            );
            if (conn != null && conn.isDestroyed()) {
                System.out.println("💥 EXPLOSÃO: Conexão destruída detectada!");
                soundManager.play(SoundManager.SoundType.EXPLOSION);

                NodePosition pos = nodePositions.get(path.get(i).getId());
                if (pos != null) {
                    for (int j = 0; j < 30; j++) {
                        particles.add(new Particle(pos.x, pos.y, Color.web("#FF5722")));
                    }
                }

                showAlert("💣 EXPLOSÃO!", "Linha férrea destruída entre\n" +
                        path.get(i).getName() + " e " + path.get(i + 1).getName());
                return;
            }
        }

        // Buzina de início
        soundManager.play(SoundManager.SoundType.TRAIN_HORN);

        // Som de movimento
        soundManager.playWithDelay(SoundManager.SoundType.TRAIN_HORN, 0.5);
        javafx.animation.PauseTransition startDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.0));
        startDelay.setOnFinished(e -> soundManager.startTrainSound());
        startDelay.play();

        // Animação
        trainAnimation = new Timeline();

        for (int i = 0; i < path.size() - 1; i++) {
            final int step = i;
            NodePosition from = nodePositions.get(path.get(step).getId());
            NodePosition to = nodePositions.get(path.get(step + 1).getId());

            if (from == null || to == null) continue;

            KeyFrame frame = new KeyFrame(
                    Duration.seconds((step + 1) * 2.5),
                    e -> animateTrainSegment(from, to)
            );

            trainAnimation.getKeyFrames().add(frame);
        }

        trainAnimation.setRate(trainSpeed);
        trainAnimation.setOnFinished(e -> {
            System.out.println("✅ Comboio chegou ao destino!");
            soundManager.stopTrainSound();
            soundManager.play(SoundManager.SoundType.ARRIVAL);
            currentTrain = null;
            drawGraph();
        });

        trainAnimation.play();
        System.out.println("🚂 Viagem iniciada!");
    }

    private void animateTrainSegment(NodePosition from, NodePosition to) {
        Timeline segment = new Timeline();
        double angle = Math.atan2(to.y - from.y, to.x - from.x);

        for (int i = 0; i <= 30; i++) {
            final double progress = i / 30.0;

            KeyFrame kf = new KeyFrame(
                    Duration.millis(i * 80),
                    e -> {
                        double x = from.x + (to.x - from.x) * progress;
                        double y = from.y + (to.y - from.y) * progress;
                        currentTrain = new Train(x, y, angle);
                        drawGraph();
                    }
            );

            segment.getKeyFrames().add(kf);
        }

        segment.play();
    }

    public void pauseTrainAnimation() {
        if (trainAnimation != null) {
            trainAnimation.pause();
            soundManager.stop(SoundManager.SoundType.TRAIN_MOVING);
        }
    }

    public void stopTrainAnimation() {
        if (trainAnimation != null) {
            trainAnimation.stop();
            soundManager.stopAll();
            currentTrain = null;
            drawGraph();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void setupInteractivity() {
        canvas.setOnScroll(event -> {
            scale *= event.getDeltaY() > 0 ? 1.1 : 0.9;
            scale = Math.max(0.5, Math.min(scale, 3.0));
            drawGraph();
        });

        final double[] lastXY = {0, 0};
        canvas.setOnMousePressed(e -> {
            lastXY[0] = e.getX();
            lastXY[1] = e.getY();
        });
        canvas.setOnMouseDragged(e -> {
            offsetX += e.getX() - lastXY[0];
            offsetY += e.getY() - lastXY[1];
            lastXY[0] = e.getX();
            lastXY[1] = e.getY();
            drawGraph();
        });

        canvas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                double x = (event.getX() - offsetX) / scale;
                double y = (event.getY() - offsetY) / scale;

                for (Map.Entry<String, NodePosition> entry : nodePositions.entrySet()) {
                    NodePosition pos = entry.getValue();
                    double dist = Math.sqrt(Math.pow(x - pos.x, 2) + Math.pow(y - pos.y, 2));

                    if (dist <= NODE_RADIUS * 2) {
                        Oblast oblast = graphService.getGraph().getOblast(entry.getKey());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(oblast.getName());
                        alert.setHeaderText(null);
                        alert.setContentText(oblast.toDetailedString());
                        alert.show();
                        break;
                    }
                }
            }
        });
    }

    public void displayRoute(Route route) {
        this.currentRoute = route;
        stopTrainAnimation();
        drawGraph();
    }

    public void clearHighlights() {
        this.currentRoute = null;
        stopTrainAnimation();
        drawGraph();
    }

    public void refresh() {
        calculateNodePositions();
        drawGraph();
    }

    public VBox getPane() {
        return mainContainer;
    }

    // Classes auxiliares
    private record NodePosition(double x, double y) {
    }

    private static class Train {
        double x, y, angle;

        Train(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }

    private static class Particle {
        double x, y, vx, vy, size, alpha, life;
        Color color;

        Particle(double x, double y) {
            this(x, y, Color.web("#888888"));
        }

        Particle(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.vx = (Math.random() - 0.5) * 2;
            this.vy = -Math.random() * 3 - 1;
            this.size = Math.random() * 5 + 3;
            this.alpha = 1.0;
            this.life = 1.0;
            this.color = color;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.1; // Gravidade
            life -= 0.02;
            alpha = Math.max(0, life);
        }

        boolean isAlive() {
            return life > 0;
        }
    }
}