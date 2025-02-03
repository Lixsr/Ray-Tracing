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
    private static SceneObject[] scene;
    private static Color[][] canvas;

    public RayTracer() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    // Paint the rendered image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Loop through each pixel in the canvas
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                // Set the color for the current pixel
                g.setColor(canvas[y][x]);
                // Draw the pixel as a 1x1 rectangle
                g.fillRect(x, y, 1, 1);
            }
        }
    }

    /// Camera parameters
    private static Point3D cameraPosition = new Point3D(0, 0, 0);
    private static double yaw = 0; // Horizontal rotation (left/right)
    private static double pitch = 0; // Vertical rotation (up/down)

    // Render the scene
    private static void render() {
        canvas = new Color[HEIGHT][WIDTH];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                // Get viewport direction
                Point3D D = canvasToViewport(x - WIDTH / 2, HEIGHT / 2 - y).normalize();

                // Rotate the direction vector based on the camera's yaw and pitch
                D = rotateVector(D, yaw, pitch);

                // Trace ray from the camera position
                Color color = traceRay(cameraPosition, D, 1, Double.POSITIVE_INFINITY, 3); // Recursion depth = 3
                canvas[y][x] = color;
            }
        }
    }

    // Rotate a vector using yaw (horizontal) and pitch (vertical) rotations
    private static Point3D rotateVector(Point3D v, double yaw, double pitch) {
        // Convert angles to radians
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        // Apply pitch rotation (around X-axis)
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);
        double y1 = cosPitch * v.y - sinPitch * v.z;
        double z1 = sinPitch * v.y + cosPitch * v.z;

        // Apply yaw rotation (around Y-axis)
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double x2 = cosYaw * v.x + sinYaw * z1;
        double z2 = -sinYaw * v.x + cosYaw * z1;

        // Return the rotated and normalized vector
        return new Point3D(x2, y1, z2).normalize();
    }

    // Helper Function
    private static Color multiplyColor(Color color, double scalar) {
        int r = (int) (color.getRed() * scalar);
        int g = (int) (color.getGreen() * scalar);
        int b = (int) (color.getBlue() * scalar);
        return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    // Helper Function to blend two colors based on a weight
    private static Color blendColors(Color color1, Color color2, double weight) {
        // Calculate the red component by blending the two colors based on the weight
        int r = (int) (color1.getRed() * (1 - weight) + color2.getRed() * weight);
        // Calculate the green component by blending the two colors based on the weight
        int g = (int) (color1.getGreen() * (1 - weight) + color2.getGreen() * weight);
        // Calculate the blue component by blending the two colors based on the weight
        int b = (int) (color1.getBlue() * (1 - weight) + color2.getBlue() * weight);
        // Return the new blended color, ensuring that each component is within the
        // valid range
        return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    // Helper Function to reflect a ray around a normal vector
    private static Point3D reflectRay(Point3D R, Point3D N) {
        // R_reflected = 2 * N * (N · R) - R
        return N.multiply(2 * N.dot(R)).subtract(R);
    }

    // Initialize the scene
    static {
        scene = new SceneObject[] {
                new Sphere(new Point3D(0, -1, 3), 1, Color.RED, 500, 0.2), // Shiny red sphere
                new Sphere(new Point3D(2, 0, 4), 1, Color.BLUE, 500, 0.3), // Shiny blue sphere
                new Sphere(new Point3D(-2, 0, 4), 1, Color.GREEN, 10, 0.4), // Somewhat shiny green sphere
                new Sphere(new Point3D(0, -5001, 0), 5000, new Color(255, 255, 0), 1000, 0.5), // Very shiny yellow
                                                                                               // sphere
                new Cylinder(new Point3D(0, 1, 5), 1, 3, new Color(128, 0, 128), 300, 0.6) // Purple cylinder
        };
    }


    // Canvas to viewport conversion
    private static Point3D canvasToViewport(int x, int y) {
        return new Point3D(
                x * VIEWPORT_SIZE / WIDTH,
                y * VIEWPORT_SIZE / HEIGHT,
                PROJECTION_PLANE_D);
    }

    private static IntersectionResult closestIntersection(Point3D O, Point3D D, double tMin, double tMax) {
        double closestT = Double.POSITIVE_INFINITY;
        SceneObject closestObject = null;

        // Iterate through all objects in the scene
        for (SceneObject obj : scene) {
            double[] tValues = null;

            // Check if the object is a Sphere
            if (obj instanceof Sphere) {
                Sphere sphere = (Sphere) obj;
                tValues = intersectRaySphere(O, D, sphere);
            }
            // Check if the object is a Cylinder
            else if (obj instanceof Cylinder) {
                Cylinder cylinder = (Cylinder) obj;
                tValues = intersectRayCylinder(O, D, cylinder);
            }

            // If tValues is null, skip this object
            if (tValues == null)
                continue;

            // Check the intersection points
            double t1 = tValues[0];
            double t2 = tValues[1];

            // Check if t1 is within the valid range and closer than the current closest
            if (t1 >= tMin && t1 <= tMax && t1 < closestT) {
                closestT = t1;
                closestObject = obj;
            }

            // Check if t2 is within the valid range and closer than the current closest
            if (t2 >= tMin && t2 <= tMax && t2 < closestT) {
                closestT = t2;
                closestObject = obj;
            }
        }

        // Return the closest object and the corresponding t value
        return new IntersectionResult(closestObject, closestT);
    }

    private static double[] intersectRayCylinder(Point3D O, Point3D D, Cylinder cylinder) {
        // Extract cylinder properties
        Point3D center = cylinder.center;
        double radius = cylinder.radius;
        double height = cylinder.height;

        // Vector from ray origin to cylinder center
        Point3D CO = O.subtract(center);

        // Project the ray direction onto the cylinder's axis (assume axis is along the
        // y-axis)
        double dy = D.y;
        double cy = CO.y;

        // Calculate coefficients for the quadratic equation
        double a = D.x * D.x + D.z * D.z;
        double b = 2 * (D.x * CO.x + D.z * CO.z);
        double c = CO.x * CO.x + CO.z * CO.z - radius * radius;

        // Solve the quadratic equation: at^2 + bt + c = 0
        double discriminant = b * b - 4 * a * c;

        // No intersection if discriminant is negative
        if (discriminant < 0) {
            return null;
        }

        // Calculate the two intersection points
        double t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2 * a);

        // Check if the intersection points are within the cylinder's height
        double y1 = O.y + t1 * D.y;
        double y2 = O.y + t2 * D.y;

        if (y1 < center.y || y1 > center.y + height) {
            t1 = Double.POSITIVE_INFINITY; // Invalid intersection
        }
        if (y2 < center.y || y2 > center.y + height) {
            t2 = Double.POSITIVE_INFINITY; // Invalid intersection
        }

        // Return the valid intersection points
        return new double[] { t1, t2 };
    }

    // Helper class to store the result of the intersection
    private static class IntersectionResult {
        SceneObject object;
        double t;

        IntersectionResult(SceneObject object, double t) {
            this.object = object;
            this.t = t;
        }
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
    private static Color traceRay(Point3D O, Point3D D, double tMin, double tMax, int recursionDepth) {
        // Base case: stop recursion if maximum depth is reached
        if (recursionDepth <= 0) {
            return BACKGROUND_COLOR;
        }

        // Find the closest intersection
        IntersectionResult result = closestIntersection(O, D, tMin, tMax);
        SceneObject closestObject = result.object;
        double closestT = result.t;

        // If no intersection, return the background color
        if (closestObject == null) {
            return BACKGROUND_COLOR;
        }

        // Compute intersection point and normal
        Point3D P = O.add(D.multiply(closestT)); // P = O + t * D
        Point3D N = P.subtract(closestObject.center).normalize(); // N = (P - C) / |P - C|

        // Compute local color (diffuse and specular lighting)
        double lighting = computeLighting(P, N, D.multiply(-1), closestObject.specular);
        Color localColor = multiplyColor(closestObject.color, lighting);

        // If the object is not reflective or recursion limit is reached, return the
        // local color
        double r = closestObject.reflective;
        if (recursionDepth <= 0 || r <= 0) {
            return localColor;
        }

        // Compute reflected color
        Point3D R = reflectRay(D.multiply(-1), N); // R = reflect(-D, N)
        Color reflectedColor = traceRay(P, R, 0.001, Double.POSITIVE_INFINITY, recursionDepth - 1);

        // Blend local color and reflected color based on reflectivity
        return blendColors(localColor, reflectedColor, r);
    }

    // Compute lighting
    private static double computeLighting(Point3D P, Point3D N, Point3D V, double s) {
        double intensity = 0.0;

        for (Light light : lights) {
            if (light.type == Light.LightType.AMBIENT) {
                // Ambient light
                intensity += light.intensity;
            } else {
                Point3D L;
                double tMax;

                if (light.type == Light.LightType.POINT) {
                    // Point light: L is the vector from P to the light source
                    L = light.position.subtract(P);
                    tMax = 1; // t_max is 1 for point lights
                } else {
                    // Directional light: L is the light direction
                    L = light.direction;
                    tMax = Double.POSITIVE_INFINITY; // t_max is infinity for directional lights
                }

                // Shadow check
                IntersectionResult shadowResult = closestIntersection(P, L, 0.001, tMax);
                if (shadowResult.object != null) {
                    // If there's an intersection, skip this light (shadow)
                    continue;
                }

                // Diffuse reflection
                double nDotL = N.dot(L);
                if (nDotL > 0) {
                    intensity += light.intensity * nDotL / (N.length() * L.length());
                }

                // Specular reflection (if specular exponent s is not -1)
                if (s != -1) {
                    Point3D R = reflectRay(L, N); // Use ReflectRay for reflection
                    double rDotV = R.dot(V);
                    if (rDotV > 0) {
                        intensity += light.intensity * Math.pow(rDotV / (R.length() * V.length()), s);
                    }
                }
            }
        }

        return intensity;
    }

    // Initialize the lights
    static Light[] lights = new Light[] {
            new Light(Light.LightType.AMBIENT, 0.2, null, null),
            new Light(Light.LightType.POINT, 0.6, new Point3D(2, 1, 0), null),
            new Light(Light.LightType.DIRECTIONAL, 0.2, null, new Point3D(1, 4, 4))
    };

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