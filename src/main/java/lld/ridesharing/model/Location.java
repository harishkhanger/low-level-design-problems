package lld.ridesharing.model;

public record Location (double x, double y) {
    public double distanceTo(Location other){
        double dx = x-other.x, dy = y-other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
