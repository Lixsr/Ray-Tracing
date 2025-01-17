import javax.swing.*;
import java.awt.*;

// Ray Tracer class
public class RayTracer extends JPanel {
    // Scene settings
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final double VIEWPORT_SIZE = 1;
    private static final double PROJECTION_PLANE_D = 1;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static Sphere[] scene;
    private static Color[][] canvas;

    public RayTracer() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    // Initialize the scene
    static {
        scene = new Sphere[] {
            new Sphere(new Point3D(0, -1, 3), 1, Color.RED),
            new Sphere(new Point3D(2, 0, 4), 1, Color.BLUE),
            new Sphere(new Point3D(-2, 0, 4), 1, Color.GREEN)
        };
    }

    // Canvas to viewport conversion
    private static Point3D canvasToViewport(int x, int y) {
        return new Point3D(
            x * VIEWPORT_SIZE / WIDTH,
            y * VIEWPORT_SIZE / HEIGHT,
            PROJECTION_PLANE_D
        );
    }

    // Ray-sphere intersection
    private static double[] intersectRaySphere(Point3D O, Point3D D, Sphere sphere) {
        Point3D CO = O.subtract(sphere.center);
        double a = D.dot(D);
        double b = 2 * CO.dot(D);
        double c = CO.dot(CO) - sphere.radius * sphere.radius;
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
        }

        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
        return new double[] { t1, t2 };
    }

    // Trace a ray
    private static Color traceRay(Point3D O, Point3D D, double tMin, double tMax) {
        double closestT = Double.POSITIVE_INFINITY;
        Sphere closestSphere = null;

        for (Sphere sphere : scene) {
            double[] tValues = intersectRaySphere(O, D, sphere);
            double t1 = tValues[0];
            double t2 = tValues[1];

            if (t1 >= tMin && t1 <= tMax && t1 < closestT) {
                closestT = t1;
                closestSphere = sphere;
            }

            if (t2 >= tMin && t2 <= tMax && t2 < closestT) {
                closestT = t2;
                closestSphere = sphere;
            }
        }

        if (closestSphere == null) {
            return BACKGROUND_COLOR;
        }

        return closestSphere.color;
    }

    // Render the scene
    private static void render() {
        canvas = new Color[HEIGHT][WIDTH];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Point3D D = canvasToViewport(x - WIDTH / 2, HEIGHT / 2 - y).normalize();
                Color color = traceRay(new Point3D(0, 0, 0), D, 1, Double.POSITIVE_INFINITY);
                canvas[y][x] = color;
            }
        }
    }

    // Paint the rendered image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                g.setColor(canvas[y][x]);
                g.fillRect(x, y, 1, 1);
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        render();

        JFrame frame = new JFrame("Ray Tracer");
        RayTracer panel = new RayTracer();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}