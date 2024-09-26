public class Vector {
    private double x, y;
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Point start, Point end) {
        this.x = end.getX() - start.getX();
        this.y = end.getY() - start.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector add(Vector other) {
        return new Vector(x + other.getX(), y + other.getY());
    }

    public Vector subtract(Vector other) {
        return new Vector(x - other.getX(), y - other.getY());
    }

    public Vector scale(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    public double dotProduct(Vector other) {
        return x * other.getX() + y * other.getY();
    }

    public double angleBetween(Vector other) {
        return Math.acos(dotProduct(other) / (getMagnitude() * other.getMagnitude()));
    }

    public Vector normalize() {
        return scale(1 / getMagnitude());
    }

    public double crossProduct(Vector other) {
        return x * other.getY() - y * other.getX();
    }

    public Vector rotate (double angle) {
        double newX = x * Math.cos(angle) - y * Math.sin(angle);
        double newY = x * Math.sin(angle) + y * Math.cos(angle);
        return new Vector(newX, newY);
    }
}
