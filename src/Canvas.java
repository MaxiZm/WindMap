import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Canvas extends JPanel {

    private ArrayList<WindStation> windStations;

    private ArrayList<FlyingWindPoint> flyingWindPoints = new ArrayList<>();

    private Point lastClick = null;
    private Point mousePos = null;

    private final int FPS = 60;

    private int width, height;

    private final int FNUM = 200;
    private final int R = 2;

    public Canvas(WindStation[] windStatio) {
        windStations = new ArrayList<>();
        windStations.addAll(Arrays.asList(windStatio));

        this.setFocusable(true);
        this.requestFocus();

        Timer timer = new Timer(1000 / FPS, e -> {
            Thread th1 = new Thread(() -> {
                update();
            });
            Thread th2 = new Thread(() -> {
                repaint();
            });
            th1.start();
            th2.start();
        });
        timer.start();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                super.mouseClicked(evt);
                mouseClickedHandler(evt.getX(), evt.getY());
            }
        });

        addMouseMotionListener(new java.awt.event.MouseAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                super.mouseMoved(evt);
                mousePos = new Point(evt.getX(), evt.getY());
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                super.componentResized(evt);
                width = getWidth();
                height = getHeight();
            }
        });

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                super.keyPressed(evt);
                if (evt.getKeyChar() == 'r' || evt.getKeyChar() == 'к') {
                    windStations.clear();
                    flyingWindPoints.clear();
                } else if (evt.getKeyChar() == 'c' || evt.getKeyChar() == 'с') {
                    flyingWindPoints.clear();
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Add background gradient
        Graphics2D g2d = (Graphics2D) g;
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create a gradient background from dark blue to black
        GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 50), 0, getHeight(), Color.BLACK);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        draw(g2d);
    }

    public void mouseClickedHandler(double x, double y) {
        if (lastClick != null) {
            windStations.add(new WindStation(lastClick, new Vector(x - lastClick.getX(), y - lastClick.getY()).scale((double) 1 / 50)));
            lastClick = null;
        } else {
            lastClick = new Point(x, y);
        }
    }

    private void draw(Graphics2D g2d) {
        for (WindStation windStation : windStations) {
            int wx, wy;
            wx = (int) windStation.getPos().getX();
            wy = (int) windStation.getPos().getY();

            // Get wind intensity
            double intensity = windStation.getWind().getMagnitude();

            // Map intensity to a color (blue for low intensity, red for high)
            float hue = (float) (0.7 - Math.min(intensity / 10.0, 1.0) * 0.7);
            Color windColor = Color.getHSBColor(hue, 1.0f, 1.0f);

            // Draw the wind station circle
            g2d.setColor(windColor);
            g2d.fillOval(wx - 10, wy - 10, 20, 20);

            // Draw the wind direction line
            g2d.setColor(windColor);
            g2d.setStroke(new BasicStroke(2));
            int lineX = wx + (int) (windStation.getWind().getX() * 50);
            int lineY = wy + (int) (windStation.getWind().getY() * 50);
            g2d.drawLine(wx, wy, lineX, lineY);
        }

        for (FlyingWindPoint flyingWindPoint : flyingWindPoints) {
            int wy, wx;
            wx = (int) flyingWindPoint.getPos().getX();
            wy = (int) flyingWindPoint.getPos().getY();

            // Get speed magnitude
            double speed = flyingWindPoint.getSpeed().getMagnitude();

            // Map speed to a color (blue for low speed, red for high)
            float hue = (float) (0.7 - Math.min(speed / 10.0, 1.0) * 0.7);
            Color pointColor = Color.getHSBColor(hue, 1.0f, 1.0f);

            // Draw the point
            g2d.setColor(pointColor);
            g2d.fillOval(wx - R, wy - R, R * 2, R * 2);

            // Draw the trajectory with fading effect
            for (int i = 0; i < flyingWindPoint.getTrajectory().size() - 1; i++) {
                int alpha = 255 - 255 * i / flyingWindPoint.getTrajectory().size();
                Color trajectoryColor = new Color(pointColor.getRed(), pointColor.getGreen(), pointColor.getBlue(), alpha);
                g2d.setColor(trajectoryColor);
                Point p1 = flyingWindPoint.getTrajectory().get(i);
                Point p2 = flyingWindPoint.getTrajectory().get(i + 1);
                g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
            }
        }

        if (lastClick != null && mousePos != null) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawLine((int) lastClick.getX(), (int) lastClick.getY(), (int) mousePos.getX(), (int) mousePos.getY());
        }
    }

    private void update() {
        if (flyingWindPoints.size() < FNUM) {
            Point pos = new Point((int) (Math.random() * width), (int) (Math.random() * height));
            flyingWindPoints.add(new FlyingWindPoint(pos, new Vector(0, 0), (int) (Math.random() * 350 + 50)));
        }

        for (int i = 0; i < flyingWindPoints.size(); i++) {
            if (flyingWindPoints.get(i).getLifetime() <= 0) {
                flyingWindPoints.remove(i);
                i--;
                continue;
            }

            Vector speed = new Vector(0, 0);
            for (WindStation windStation : windStations) {
                double distance = windStation.getPos().distance(flyingWindPoints.get(i).getPos());
                speed = speed.add(windStation.getWind().scale(
                        10000 / Math.pow(distance, 0.75)
                ));
            }

            flyingWindPoints.get(i).setSpeed(speed);
            flyingWindPoints.get(i).update(FPS);
        }
    }

    public void open(String[] args) {
        JFrame frame = new JFrame("WindMap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        this.width = 800;
        this.height = 600;
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(true);
    }
}
