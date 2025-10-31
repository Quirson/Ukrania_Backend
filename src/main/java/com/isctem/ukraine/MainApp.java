package com.isctem.ukraine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.isctem.ukraine.ui.MainViewController;

import java.util.Objects;

/**
 * Aplicação Principal - Ukraine Logistics System
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Criar controller principal
            MainViewController mainView = new MainViewController();

            // Criar cena
            Scene scene = new Scene(mainView.getView(), 1600, 900);

            // Aplicar CSS (tema escuro moderno)
            scene.getStylesheets().add(
                    getClass().getResource("/css/dark-theme.css").toExternalForm()
            );

            // Configurar stage
            primaryStage.setTitle("🇺🇦 Ukraine Military Logistics System - AED2 Project");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);

            // Ícone (opcional)
            try {
                primaryStage.getIcons().add(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ukraine-flag.png")))
                );
            } catch (Exception e) {
                System.out.println("Ícone não encontrado, continuando sem ele...");
            }

            primaryStage.show();

            System.out.println("✅ Aplicação JavaFX iniciada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao iniciar aplicação: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        System.out.println("🛑 Encerrando aplicação...");
    }
}