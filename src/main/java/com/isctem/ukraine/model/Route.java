package com.isctem.ukraine.model;
import com.isctem.ukraine.model.Location;
public class Route {
    private double totalDistance;
    private int stepCount;
    private Location start;
    private Location end;

    public Route(double totalDistance, int stepCount, Location start, Location end) {
        this.totalDistance = totalDistance;
        this.stepCount = stepCount;
        this.start = start;
        this.end = end;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public int getStepCount() {
        return stepCount;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }
}
