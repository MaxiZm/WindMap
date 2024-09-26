import java.util.ArrayList;

public class FlyingWindPoint {
    private Point pos;
    private Vector speed;
    private ArrayList<Point> Trajectory;
    private int lifetime;

    public FlyingWindPoint(Point pos, Vector speed, int lifetime) {
        this.pos = pos;
        this.speed = speed;
        this.lifetime = lifetime;
        Trajectory = new ArrayList<>();
    }

    public Point getPos() {
        return pos;
    }

    public Vector getSpeed() {
        return speed;
    }

    public void setSpeed(Vector speed) {
        this.speed = speed;
    }

    public int getLifetime() {
        return lifetime;
    }

    public ArrayList<Point> getTrajectory() {
        return Trajectory;
    }

    public void update(int FPS) {
        pos = pos.add(speed.scale(1.0 / FPS));
        Trajectory.add(pos);
        lifetime--;
    }



}
