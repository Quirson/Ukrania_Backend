package com.isctem.ukraine.model;

import java.util.Objects;

/**
 * Representa uma conexão ferroviária entre dois Oblasts.
 * Cada conexão é uma aresta no grafo com peso (distância).
 */
public class Connection {
    private final Oblast from;
    private final Oblast to;
    private final double distance;
    private boolean isDestroyed;
    private String railwayType;
    private double condition;

    /**
     * Construtor completo
     */
    public Connection(Oblast from, Oblast to, double distance, String railwayType) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.railwayType = railwayType;
        this.isDestroyed = false;
        this.condition = 100.0;
    }

    /**
     * Construtor simplificado
     */
    public Connection(Oblast from, Oblast to, double distance) {
        this(from, to, distance, "STANDARD");
    }

    // Getters
    public Oblast getFrom() {
        return from;
    }

    public Oblast getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public String getRailwayType() {
        return railwayType;
    }

    public double getCondition() {
        return condition;
    }

    // Setters
    public void setDestroyed(boolean destroyed) {
        this.isDestroyed = destroyed;
        if (destroyed) {
            this.condition = 0;
        }
    }

    public void setCondition(double condition) {
        this.condition = Math.max(0, Math.min(100, condition));
        if (this.condition == 0) {
            this.isDestroyed = true;
        }
    }

    public void damageConnection(double damagePercent) {
        this.condition = Math.max(0, this.condition - damagePercent);
        if (this.condition == 0) {
            this.isDestroyed = true;
        }
    }

    public void repairConnection(double repairPercent) {
        if (!isDestroyed) {
            this.condition = Math.min(100, this.condition + repairPercent);
        }
    }

    /**
     * Retorna o peso efetivo considerando a condição da linha
     */
    public double getEffectiveWeight() {
        if (isDestroyed) {
            return Double.POSITIVE_INFINITY;
        }
        // Quanto pior a condição, maior o "custo" de usar esta linha
        double conditionFactor = 100.0 / Math.max(1, condition);
        return distance * conditionFactor;
    }

    /**
     * Verifica se a conexão é utilizável
     */
    public boolean isUsable() {
        return !isDestroyed && condition > 0;
    }

    /**
     * Retorna o outro Oblast desta conexão
     */
    public Oblast getOther(Oblast oblast) {
        if (oblast.equals(from)) {
            return to;
        } else if (oblast.equals(to)) {
            return from;
        }
        throw new IllegalArgumentException("Oblast não faz parte desta conexão");
    }

    /**
     * Verifica se esta conexão conecta dois Oblasts específicos
     */
    public boolean connects(Oblast o1, Oblast o2) {
        return (from.equals(o1) && to.equals(o2)) ||
                (from.equals(o2) && to.equals(o1));
    }

    /**
     * Retorna cor baseada na condição (para visualização)
     */
    public String getConditionColor() {
        if (isDestroyed) {
            return "#e74c3c";
        } else if (condition < 30) {
            return "#e67e22";
        } else if (condition < 60) {
            return "#f39c12";
        } else if (condition < 90) {
            return "#3498db";
        } else {
            return "#2ecc71";
        }
    }

    /**
     * Retorna espessura da linha baseada na condição
     */
    public int getLineThickness() {
        if (isDestroyed) {
            return 1;
        }
        return (int) (1 + (condition / 25)); // 1 a 5
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return (from.equals(that.from) && to.equals(that.to)) ||
                (from.equals(that.to) && to.equals(that.from));
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to) + Objects.hash(to, from);
    }

    @Override
    public String toString() {
        return String.format("Connection{%s -> %s, %.1fkm, %s, %.0f%%}",
                from.getName(), to.getName(), distance,
                isDestroyed ? "DESTROYED" : "OK", condition);
    }

    /**
     * Retorna representação detalhada
     */
    public String toDetailedString() {
        return String.format("""
            Connection:
            From: %s
            To: %s
            Distance: %.2f km
            Type: %s
            Condition: %.1f%%
            Status: %s
            Effective Weight: %.2f
            """,
                from.getName(), to.getName(), distance, railwayType,
                condition, isDestroyed ? "DESTROYED" : "OPERATIONAL",
                getEffectiveWeight());
    }
}