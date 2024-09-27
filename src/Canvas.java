import javax.annotation.processing.SupportedSourceVersion;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw(g2d);
    }


    public void mouseClickedHandler(double x, double y) {
        if (lastClick != null) {
            windStations.add(new WindStation(lastClick, new Vector(x - lastClick.getX(), y - lastClick.getY()).scale((double) 1 /50)));
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

            g2d.setColor(Color.BLACK);
            g2d.fillOval(wx - 10, wy - 10, 20, 20);
            g2d.setColor(Color.RED);
            g2d.drawLine(wx, wy, wx + (int) windStation.getWind().getX() * 50, wy + (int) windStation.getWind().getY() * 50);
        }

        for (FlyingWindPoint flyingWindPoint : flyingWindPoints) {
            int wy, wx;
            wx = (int) flyingWindPoint.getPos().getX();
            wy = (int) flyingWindPoint.getPos().getY();

            g2d.setColor(Color.BLUE);
            g2d.fillOval(wx - R, wy - R, R*2, R*2);
            // set color dynamically according to speed;
            for (int i = 0; i < flyingWindPoint.getTrajectory().size() - 1; i++) {
                g2d.setColor(new Color(0, 0, 255, 255 - 255 * i / flyingWindPoint.getTrajectory().size()));
                Point p1 = flyingWindPoint.getTrajectory().get(i);
                Point p2 = flyingWindPoint.getTrajectory().get(i + 1);
                g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
            }
        }

        if (lastClick != null && mousePos != null) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine((int) lastClick.getX(), (int) lastClick.getY(), (int) mousePos.getX(), (int) mousePos.getY());

        }

    }

    private void update() {
        if (flyingWindPoints.size() < FNUM) {
            Point pos = new Point((int) (Math.random() * width), (int) (Math.random() * height));

            flyingWindPoints.add(new FlyingWindPoint(pos, new Vector(0, 0), (int)(Math.random() * 350 + 50)));
        }

        for (int i = 0; i < flyingWindPoints.size(); i++) {
            if (flyingWindPoints.get(i).getLifetime() <= 0) {
                flyingWindPoints.remove(i);
                i--;
                continue;
            }


            Vector speed = new Vector(0, 0);
            for (WindStation windStation : windStations) {
                speed = speed.add(windStation.getWind().scale(
                        10000 / Math.pow(windStation.getPos().distance(flyingWindPoints.get(i).getPos()), 0.75)
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
