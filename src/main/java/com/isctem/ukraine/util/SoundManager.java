package com.isctem.ukraine.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerenciador de Sons - Carrega arquivos MP3 reais
 */
public class SoundManager {

    private static SoundManager instance;
    private final Map<SoundType, MediaPlayer> sounds;
    private MediaPlayer trainMovingPlayer;
    private boolean soundEnabled = true;
    private double volume = 0.6;

    public enum SoundType {
        TRAIN_MOVING("train-moving.mp3", true),
        TRAIN_HORN("train-horn.mp3", false),
        EXPLOSION("explosion.mp3", false),
        ALERT("alert.mp3", false),
        ARRIVAL("arrival.mp3", false);

        final String filename;
        final boolean loop;

        SoundType(String filename, boolean loop) {
            this.filename = filename;
            this.loop = loop;
        }
    }

    private SoundManager() {
        sounds = new HashMap<>();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        System.out.println("🎵 Carregando sons...");

        for (SoundType type : SoundType.values()) {
            try {
                URL resource = getClass().getResource("/sounds/" + type.filename);

                if (resource != null) {
                    Media media = new Media(resource.toString());
                    MediaPlayer player = new MediaPlayer(media);
                    player.setVolume(volume);

                    if (type == SoundType.TRAIN_MOVING) {
                        player.setCycleCount(MediaPlayer.INDEFINITE);
                        trainMovingPlayer = player;
                        System.out.println("✅ Som do trem carregado (loop)");
                    } else {
                        sounds.put(type, player);
                        System.out.println("✅ Som carregado: " + type.filename);
                    }
                } else {
                    System.out.println("⚠️ Arquivo não encontrado: /sounds/" + type.filename);
                    System.out.println("   Certifique-se que o arquivo está em: src/main/resources/sounds/");
                }
            } catch (Exception e) {
                System.out.println("❌ Erro ao carregar " + type.filename + ": " + e.getMessage());
            }
        }

        if (sounds.isEmpty() && trainMovingPlayer == null) {
            System.out.println("⚠️ NENHUM SOM CARREGADO!");
            System.out.println("   Coloque os arquivos MP3 em: src/main/resources/sounds/");
            System.out.println("   Arquivos necessários:");
            for (SoundType type : SoundType.values()) {
                System.out.println("   - " + type.filename);
            }
        } else {
            System.out.println("🎵 Sistema de áudio pronto! " +
                    (sounds.size() + (trainMovingPlayer != null ? 1 : 0)) + " sons carregados");
        }
    }

    // ========== REPRODUÇÃO ==========

    /**
     * Toca um som uma vez
     */
    public void play(SoundType type) {
        if (!soundEnabled) return;

        if (type == SoundType.TRAIN_MOVING) {
            startTrainSound();
            return;
        }

        MediaPlayer player = sounds.get(type);
        if (player != null) {
            try {
                player.stop();
                player.seek(Duration.ZERO);
                player.play();
                System.out.println("🔊 Tocando: " + type.filename);
            } catch (Exception e) {
                System.out.println("⚠️ Erro ao tocar som: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Som não disponível: " + type.name());
        }
    }

    /**
     * Toca som com delay
     */
    public void playWithDelay(SoundType type, double delaySeconds) {
        if (!soundEnabled) return;

        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(Duration.seconds(delaySeconds));
        pause.setOnFinished(e -> play(type));
        pause.play();
    }

    /**
     * Inicia som contínuo do trem (loop)
     */
    public void startTrainSound() {
        if (!soundEnabled || trainMovingPlayer == null) return;

        try {
            if (trainMovingPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                trainMovingPlayer.seek(Duration.ZERO);
                trainMovingPlayer.play();
                System.out.println("🚂 Som do trem iniciado (loop)");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Erro ao iniciar som do trem: " + e.getMessage());
        }
    }

    /**
     * Para som do trem
     */
    public void stopTrainSound() {
        if (trainMovingPlayer != null) {
            try {
                trainMovingPlayer.stop();
                System.out.println("🛑 Som do trem parado");
            } catch (Exception e) {
                System.out.println("⚠️ Erro ao parar som do trem: " + e.getMessage());
            }
        }
    }

    /**
     * Para um som específico
     */
    public void stop(SoundType type) {
        if (type == SoundType.TRAIN_MOVING) {
            stopTrainSound();
        } else {
            MediaPlayer player = sounds.get(type);
            if (player != null) {
                player.stop();
            }
        }
    }

    /**
     * Para TODOS os sons
     */
    public void stopAll() {
        sounds.values().forEach(player -> {
            try {
                player.stop();
            } catch (Exception e) {
                // Ignorar erros ao parar
            }
        });
        stopTrainSound();
        System.out.println("🔇 Todos os sons parados");
    }

    // ========== CONTROLES ==========

    /**
     * Ajusta volume (0.0 a 1.0)
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0, Math.min(1, volume));

        sounds.values().forEach(player -> {
            try {
                player.setVolume(this.volume);
            } catch (Exception e) {
                // Ignorar
            }
        });

        if (trainMovingPlayer != null) {
            try {
                trainMovingPlayer.setVolume(this.volume * 0.7); // Trem um pouco mais baixo
            } catch (Exception e) {
                // Ignorar
            }
        }

        System.out.println("🔊 Volume ajustado: " + (int)(this.volume * 100) + "%");
    }

    /**
     * Liga/Desliga som
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAll();
        }
        System.out.println(enabled ? "🔊 Som ligado" : "🔇 Som desligado");
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Testa se os sons foram carregados
     */
    public boolean hasSounds() {
        return !sounds.isEmpty() || trainMovingPlayer != null;
    }

    /**
     * Retorna informações de debug
     */
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Sistema de Som:\n");
        info.append("  Status: ").append(soundEnabled ? "Ligado" : "Desligado").append("\n");
        info.append("  Volume: ").append((int)(volume * 100)).append("%\n");
        info.append("  Sons carregados: ").append(sounds.size()).append("\n");
        info.append("  Trem: ").append(trainMovingPlayer != null ? "OK" : "Não carregado").append("\n");

        for (SoundType type : SoundType.values()) {
            if (type == SoundType.TRAIN_MOVING) {
                info.append("    ✓ ").append(type.filename).append(" (loop)\n");
            } else if (sounds.containsKey(type)) {
                info.append("    ✓ ").append(type.filename).append("\n");
            } else {
                info.append("    ✗ ").append(type.filename).append(" (faltando)\n");
            }
        }

        return info.toString();
    }
}