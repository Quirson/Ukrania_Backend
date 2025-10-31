package com.isctem.ukraine.ui;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;
import java.net.URI;

/**
 * Tela dos Desenvolvedores - COM FOTOS E CONTATOS
 */
public class AboutDevelopersView {

    private static Stage stage;

    public static void show() {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Pressione ESC para voltar");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(10,10,10,0.95);");

        // Background animado
        Canvas bgCanvas = new Canvas(1920, 920);
        root.getChildren().add(bgCanvas);
        animateMatrixBackground(bgCanvas);

        // Conteúdo principal
        VBox mainContent = createMainContent();
        root.getChildren().add(mainContent);

        // Botão fechar
        Button closeBtn = createCloseButton();
        root.getChildren().add(closeBtn);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        scaleInAnimation(mainContent);
        stage.show();
    }

    private static VBox createMainContent() {
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        content.setStyle("-fx-background-color: rgba(20,20,30,0.85); -fx-background-radius: 25;");

        // Título
        Label title = new Label("EQUIPE DE DESENVOLVIMENTO");
        title.setFont(Font.font("Courier New", FontWeight.EXTRA_BOLD, 48));
        title.setTextFill(Color.web("#00FF00"));
        title.setEffect(new Glow(0.8));

        Label subtitle = new Label("Ukraine Logistics System • ISCTEM • 2025");
        subtitle.setFont(Font.font("Courier New", 18));
        subtitle.setTextFill(Color.web("#00FF00"));
        subtitle.setOpacity(0.8);

        // Container dos desenvolvedores
        HBox devsContainer = new HBox(30);
        devsContainer.setAlignment(Pos.CENTER);
        devsContainer.setPadding(new Insets(20));

        devsContainer.getChildren().addAll(
                createDevCard(
                        "Quirson Fernando Ngale",
                        "Lead Developer & FullStack Engineer",
                        "Java • JavaFX • Web Development • AI/ML\n" +
                                "System Architecture • Database Design",
                        "🌐 quirsonngale.me",
                        "#00E5FF",
                        createPortfolioAction()
                ),
                createDevCard(
                        "Celestino Sitoe",
                        "Front-End & UI/UX Developer",
                        "User Interface Design • JavaFX Components\n" +
                                "User Experience • Graphic Design",
                        "📱 +258 85 068 3589",
                        "#FFD600",
                        createWhatsAppAction("+258850683589")
                ),
                createDevCard(
                        "Stanley Cossa",
                        "Backend & Algorithms Developer",
                        "Data Structures • Algorithm Optimization\n" +
                                "System Performance • Graph Theory",
                        "📱 +258 84 640 4259",
                        "#00FF00",
                        createWhatsAppAction("+258846404259")
                )
        );

        // Info institucional
        VBox infoBox = createInfoBox();
        content.getChildren().addAll(title, subtitle, devsContainer, infoBox);

        return content;
    }

    private static VBox createDevCard(String name, String role, String skills,
                                      String contact, String color, Runnable contactAction) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(350, 450);
        card.setStyle(String.format(
                "-fx-background-color: rgba(30,30,40,0.9); " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 25;", color
        ));
        card.setEffect(new DropShadow(25, Color.web(color, 0.6)));

        // Avatar placeholder (substitua pelas fotos reais)
        StackPane avatar = createAvatarPlaceholder(name, color);

        // Nome
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.web(color));
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);

        // Role
        Label roleLabel = new Label(role);
        roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        roleLabel.setTextFill(Color.web("#00FF00"));
        roleLabel.setWrapText(true);
        roleLabel.setTextAlignment(TextAlignment.CENTER);

        // Skills
        TextArea skillsArea = new TextArea(skills);
        skillsArea.setEditable(false);
        skillsArea.setWrapText(true);
        skillsArea.setPrefHeight(80);
        skillsArea.setStyle(
                "-fx-control-inner-background: rgba(40,40,50,0.8); " +
                        "-fx-text-fill: #CCCCCC; " +
                        "-fx-border-color: #444444; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-font-size: 11px;"
        );

        // Contact Button
        Button contactBtn = new Button(contact);
        contactBtn.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 12px; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-cursor: hand;", color
        ));
        contactBtn.setOnAction(e -> contactAction.run());

        card.getChildren().addAll(avatar, nameLabel, roleLabel, skillsArea, contactBtn);
        setupCardHoverEffects(card, color);

        return card;
    }

    private static StackPane createAvatarPlaceholder(String name, String color) {
        StackPane avatarContainer = new StackPane();

        // Círculo de fundo
        Circle background = new Circle(60);
        background.setFill(Color.web(color));
        background.setEffect(new Glow(0.6));

        // Inicial do nome
        Label initial = new Label(String.valueOf(name.charAt(0)));
        initial.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        initial.setTextFill(Color.BLACK);

        // Texto "Add Photo"
        Label photoText = new Label("📷\nAdd Photo");
        photoText.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        photoText.setTextFill(Color.WHITE);
        photoText.setTextAlignment(TextAlignment.CENTER);
        photoText.setTranslateY(25);

        avatarContainer.getChildren().addAll(background, initial, photoText);
        avatarContainer.setPrefSize(120, 120);

        return avatarContainer;
    }

    private static Runnable createPortfolioAction() {
        return () -> {
            try {
                java.awt.Desktop.getDesktop().browse(
                        new URI("https://quirsonngale.me")
                );
            } catch (Exception e) {
                showAlert("Erro", "Não foi possível abrir o portfolio: " + e.getMessage());
            }
        };
    }

    private static Runnable createWhatsAppAction(String phone) {
        return () -> {
            try {
                String url = "https://wa.me/" + phone.replace("+", "").replace(" ", "");
                java.awt.Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                showAlert("Erro", "Não foi possível abrir WhatsApp: " + e.getMessage());
            }
        };
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private static void setupCardHoverEffects(VBox card, String color) {
        card.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), card);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();

            card.setStyle(card.getStyle().replace("0.9", "1.0")
                    .replace("3", "4"));
        });

        card.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            card.setStyle(card.getStyle().replace("1.0", "0.9")
                    .replace("4", "3"));
        });
    }

    private static VBox createInfoBox() {
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setStyle(
                "-fx-background-color: rgba(0,255,0,0.1); " +
                        "-fx-border-color: #00FF00; " +
                        "-fx-border-width: 2; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 20;"
        );

        Label institution = new Label("\uD83C\uDF93 Instituto Superior de Ciências e Tecnologia de Moçambique");
        institution.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        institution.setTextFill(Color.web("#00FF00"));

        Label course = new Label("Licenciatura em Engenharia Informática • AED II • 2025");
        course.setFont(Font.font("Arial", 12));
        course.setTextFill(Color.web("#00FF00"));
        course.setOpacity(0.9);

        Label tech = new Label("⚙️ Java 21 • JavaFX • JGraphT • Maven • Git");
        tech.setFont(Font.font("Arial", 11));
        tech.setTextFill(Color.web("#FFD600"));

        infoBox.getChildren().addAll(institution, course, tech);
        return infoBox;
    }

    private static Button createCloseButton() {
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: rgba(255,50,50,0.8); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 8 12; " +
                        "-fx-cursor: hand;"
        );
        closeBtn.setEffect(new DropShadow(10, Color.RED));

        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(15, 15, 0, 0));

        closeBtn.setOnAction(e -> stage.close());

        closeBtn.setOnMouseEntered(e -> {
            closeBtn.setStyle(closeBtn.getStyle().replace("0.8", "1.0"));
        });
        closeBtn.setOnMouseExited(e -> {
            closeBtn.setStyle(closeBtn.getStyle().replace("1.0", "0.8"));
        });

        return closeBtn;
    }

    private static void animateMatrixBackground(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(80), e -> {
            gc.setFill(Color.web("#0a0a0a", 0.08));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setFill(Color.web("#00FF00", 0.4));
            gc.setFont(Font.font("Courier New", FontWeight.BOLD, 14));

            for (int i = 0; i < 20; i++) {
                double x = Math.random() * canvas.getWidth();
                double y = Math.random() * canvas.getHeight();
                char c = (char) (Math.random() * 94 + 33);
                gc.fillText(String.valueOf(c), x, y);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private static void scaleInAnimation(VBox content) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), content);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), content);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.play();
    }
}