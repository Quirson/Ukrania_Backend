package com.isctem.ukraine.model;

import java.util.Objects;

/**
 * Representa um Oblast (província) da Ucrânia.
 * Cada Oblast é um nó no grafo da rede ferroviária.
 */
public class Oblast {
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final boolean isFrontline;
    private final int population;
    private final String region;

    // Dados adicionais para visualização
    private boolean isDestroyed;
    private int supplyLevel;
    private String status;

    /**
     * Construtor completo para Oblast
     */
    public Oblast(String id, String name, double latitude, double longitude,
                  boolean isFrontline, int population, String region) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFrontline = isFrontline;
        this.population = population;
        this.region = region;
        this.isDestroyed = false;
        this.supplyLevel = 100;
        this.status = "OPERATIONAL";
    }

    /**
     * Construtor simplificado
     */
    public Oblast(String id, String name, double latitude, double longitude) {
        this(id, name, latitude, longitude, false, 0, "Unknown");
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isFrontline() {
        return isFrontline;
    }

    public int getPopulation() {
        return population;
    }

    public String getRegion() {
        return region;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public int getSupplyLevel() {
        return supplyLevel;
    }

    public String getStatus() {
        return status;
    }

    // Setters para simulação
    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
        if (destroyed) {
            this.status = "DESTROYED";
            this.supplyLevel = 0;
        }
    }

    public void setSupplyLevel(int supplyLevel) {
        this.supplyLevel = Math.max(0, Math.min(100, supplyLevel));
        updateStatus();
    }

    public void decreaseSupply(int amount) {
        this.supplyLevel = Math.max(0, this.supplyLevel - amount);
        updateStatus();
    }

    public void increaseSupply(int amount) {
        this.supplyLevel = Math.min(100, this.supplyLevel + amount);
        updateStatus();
    }

    private void updateStatus() {
        if (isDestroyed) {
            status = "DESTROYED";
        } else if (supplyLevel < 20) {
            status = "CRITICAL";
        } else if (supplyLevel < 50) {
            status = "LOW";
        } else if (supplyLevel < 80) {
            status = "MODERATE";
        } else {
            status = "OPERATIONAL";
        }
    }

    /**
     * Calcula distância euclidiana até outro Oblast (em km)
     */
    public double distanceTo(Oblast other) {
        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(other.latitude);
        double lon2 = Math.toRadians(other.longitude);

        // Fórmula de Haversine
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // Raio da Terra em km
    }

    /**
     * Retorna cor baseada no status (para visualização)
     */
    public String getStatusColor() {
        return switch (status) {
            case "DESTROYED" -> "#e74c3c";
            case "CRITICAL" -> "#e67e22";
            case "LOW" -> "#f39c12";
            case "MODERATE" -> "#3498db";
            case "OPERATIONAL" -> "#2ecc71";
            default -> "#95a5a6";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Oblast oblast = (Oblast) o;
        return Objects.equals(id, oblast.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Oblast{id='%s', name='%s', status='%s', supply=%d%%}",
                id, name, status, supplyLevel);
    }

    /**
     * Retorna representação detalhada
     */
    public String toDetailedString() {
        return String.format("""
            Oblast: %s (%s)
            Coordinates: (%.4f, %.4f)
            Population: %,d
            Region: %s
            Frontline: %s
            Status: %s
            Supply Level: %d%%
            """,
                name, id, latitude, longitude, population, region,
                isFrontline ? "Yes" : "No", status, supplyLevel);
    }
}