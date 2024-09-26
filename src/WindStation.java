public class WindStation {
    private Point pos;
    private Vector wind;

    public WindStation(Point pos, Vector wind) {
        this.pos = pos;
        this.wind = wind;
    }

    public Point getPos() {
        return pos;
    }

    public Vector getWind() {
        return wind;
    }

    public void setWind(Vector wind) {
        this.wind = wind;
    }
}
