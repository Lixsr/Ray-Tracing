import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

// Ray Tracer class
public class RayTracer extends JPanel {
    // Scene settings
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final double VIEWPORT_SIZE = 1;
    private static final double PROJECTION_PLANE_D = 1;
    // private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static SceneObject[] scene;
    private static Color[][] canvas;
    /// Camera parameters
    /// z-axis is the camera's line of sight
    /// x-axis is the horizontal direction
    private static Point3D cameraPosition = new Point3D(9, 2, 4);
    private static double yaw = -80; // Horizontal rotation (left/right)
    private static double pitch = 15; // Vertical rotation (up/down)
    // Initialize the lights
    static Light[] lights = new Light[] {
        new Light(Light.LightType.AMBIENT, 0.2, null, null),
        new Light(Light.LightType.POINT, 0.6, new Point3D(4, 1, 0), null),
        new Light(Light.LightType.DIRECTIONAL, 0.2, null, new Point3D(1, 4, 4))
    };

    static {
        ArrayList<SceneObject> sceneList = new ArrayList<>();
        Random rand = new Random();
        double specular = rand.nextInt(1000);
        // double reflection = rand.nextDouble();
        double refractiveIndex = rand.nextDouble();
        // Randomly select refraction between 0 and 0.4
        double transparency = rand.nextDouble();

        double reflection = rand.nextDouble();
        // Ground sphere
        
        sceneList.add(new Sphere(new Point3D(0, -1000, 0), 1000, new Color(120, 120, 180), 100, 0, 0.1, 1));
        sceneList.add(new Sphere(new Point3D(0 - 1 , 1, 6 + .4), 1, Color.RED, 100, 0, 0, 1));
        sceneList.add(new Sphere(new Point3D(2  , 1, 6 + 0.7), 1, Color.WHITE, specular, 0.96, 0.2, 0.1));
        sceneList.add(new Sphere(new Point3D(4 + 0.5, 1, 6 + 0.8), 1, Color.WHITE, specular, 0.96, 0, 0.5));
        sceneList.add(new Cylinder(new Point3D(2, 2, 5), 1, 3, new Color(128, 0, 128), 300, 0.6, 0.2, 0.8));
        // Random small spheres 
        for (int a = -5; a < 5; a++) {
            for (int b = -5; b < 5; b++) {
                specular = rand.nextInt(1000);
                // Randomly select transparency between 0.8 and 1
                refractiveIndex = 0.6 + (rand.nextDouble() * 0.2);
                // Randomly select refraction between 0 and 0.4
                transparency = rand.nextDouble() * 0.4;
                reflection = rand.nextDouble();

                Point3D center = new Point3D(a + 0.9 * rand.nextDouble(), 0.2, b + 0.9 * rand.nextDouble());
                // sceneList.add(new Sphere(center, 0.2, randomColor, specular, reflection, transparency, refractiveIndex));

                Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                sceneList.add(new Sphere(center, 0.2, color, specular, reflection, transparency, refractiveIndex));

            }
        }
        scene = sceneList.toArray(new SceneObject[0]);
    }



    // // Initialize the scene
    // static {
    //     scene = new SceneObject[] {
    //             new Sphere(new Point3D(0, -1, 3), 1, Color.RED, 500, 0.2, 0, 1), // Shiny red sphere
    //             new Sphere(new Point3D(2, 0, 4), 1, Color.BLUE, 500, 0.3, 0, 1), // Shiny blue sphere
    //             new Sphere(new Point3D(-2, 0, 4), 1, Color.GREEN, 10, 0.4, 0, 1), // Somewhat shiny green sphere
    //             new Sphere(new Point3D(0, -5001, 0), 5000, new Color(255, 255, 0), 1000, 0.5, 0, 1), // Very shiny yellow
    //             new Sphere(new Point3D(0, 1, 6), 1, new Color(255, 255, 0), 100, 0.5, 0, 1), // Very shiny yellow
    //                                                                                            // sphere
    //             new Cylinder(new Point3D(0, 1, 5), 1, 3, new Color(128, 0, 128), 300, 0.6, 0, 1), // Purple cylinder
    //     };
    // }
    // static {
    //     // Initialize the scene as a list to allow dynamic addition of objects
    //     ArrayList<SceneObject> sceneList = new ArrayList<>();
    //     sceneList.add(new Sphere(new Point3D(0, -1, 3), 1, Color.RED, 500, 0.2, 0, 1)); // Shiny red sphere
    //     sceneList.add(new Sphere(new Point3D(2, 0, 4), 1, Color.BLUE, 500, 0.3, 0, 1)); // Shiny blue sphere
    //     sceneList.add(new Sphere(new Point3D(-2, 0, 4), 1, Color.GREEN, 10, 0.4, 0, 1)); // Somewhat shiny green sphere
    //     sceneList.add(new Sphere(new Point3D(0, -5001, 0), 5000, new Color(255, 255, 0), 1000, 0.5, 0, 1)); // Very shiny yellow sphere
    //     sceneList.add(new Sphere(new Point3D(0, 1, 6), 1, new Color(255, 255, 0), 100, 0.5, 0, 1)); // Very shiny yellow sphere
    //     sceneList.add(new Cylinder(new Point3D(0, 1, 5), 1, 3, new Color(128, 0, 128), 300, 0.6, 0, 1)); // Purple cylinder
        
    //     // Initialize random number generator
    //     Random rand = new Random();
    //     // Now append new objects in a loop
    //     for (int i = 0; i < 5; i++) {
            
    //         int r = rand.nextInt(256);
    //         int g = rand.nextInt(256);
    //         int b = rand.nextInt(256);
    //         Color randomColor = new Color(r, g, b);

    //         // Randomly select radius (either 0.1, 0.2, or 0.3)
    //         double radius = 0.1 + (rand.nextInt(3) / 10.0);

    //         double specular = rand.nextInt(1000);

    //         // Randomly select refraction between 0.8 and 1
    //         double refraction = 0.8 + (rand.nextDouble() * 0.2);

    //         // Randomly select transparency between 0 and 0.4
    //         double transparency = rand.nextDouble() * 0.4;

    //         // Create a new Sphere with random attributes and append it to the scene
    //         sceneList.add(new Sphere(new Point3D(i, i, i + 3), radius, randomColor, specular, refraction, 0, transparency));
    //     }

    //     // Convert the list back to an array if necessary
    //     scene = sceneList.toArray(new SceneObject[0]);
    // }

    
    // static {
    //     ArrayList<SceneObject> sceneList = new ArrayList<>();
    //     Random rand = new Random();
    //     Color randomColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    //     double specular = rand.nextInt(1000);
    //     // Randomly select transparency between 0.8 and 1
    //     double refractiveIndex = 0.2 + (rand.nextDouble() * 0.7);
    //     // Randomly select refraction between 0 and 0.4
    //     double transparency = rand.nextDouble() * 0.9;

    //     double reflection = rand.nextDouble();
    //     // Ground sphere
    //     // Ground sphere
    //     sceneList.add(new Sphere(new Point3D(0, -1000, 0), 1000, new Color(120, 120, 180), 100, 0, 0.1, 1));

    //     // Random small spheres
    //     for (int a = -11; a < 11; a++) {
    //         for (int b = -11; b < 11; b++) {
    //             specular = rand.nextInt(1000);
    //             // Randomly select transparency between 0.8 and 1
    //             refractiveIndex = 0.2 + (rand.nextDouble() * 0.6);
    //             // Randomly select refraction between 0 and 0.4
    //             transparency = rand.nextDouble() * 0.9;
    //             reflection = rand.nextDouble();
    //             double chooseMat = rand.nextDouble();
    //             Point3D center = new Point3D(a + 0.9 * rand.nextDouble(), 0.2, b + 0.9 * rand.nextDouble());

    //             if (center.distance(new Point3D(4, 0.2, 0)) > 0.9) {
    //                 if (chooseMat < 0.8) { // Diffuse
    //                     Color albedo = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    //                     sceneList.add(new Sphere(center, 0.2, albedo, specular, reflection, transparency, refractiveIndex));
    //                 } else if (chooseMat < 0.95) { // Metal
    //                     Color albedo = new Color(0.5f * (1 + rand.nextFloat()), 0.5f * (1 + rand.nextFloat()), 0.5f * (1 + rand.nextFloat()));
    //                     sceneList.add(new Sphere(center, 0.2, albedo, specular, reflection, transparency, refractiveIndex));
    //                 } else { // Glass
    //                     Color albedo = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    //                     sceneList.add(new Sphere(center, 0.2, albedo, specular, reflection, transparency, refractiveIndex));
    //                 }
    //             }
    //         }
    //     }

    //     // // Large spheres
    //     // sceneList.add(new Sphere(new Point3D(0, 1, 0), 1.0, Color.BLUE, 500, reflection, transparency, refractiveIndex));
    //     // sceneList.add(new Sphere(new Point3D(-4, 1, 0), 1.0, new Color(102, 51, 26), 500, 1, 0.1, 1));
    //     // sceneList.add(new Sphere(new Point3D(4, 1, 0), 1.0, new Color(179, 153, 128), 500, 1, 0, 1));
    //     // Glass sphere (Dielectric - transparent with refractive index)
    //     sceneList.add(new Sphere(new Point3D(0, 1, 0), 1.0, Color.BLUE, 500, 0, 0.1, 1.5)); 

    //     // Diffuse sphere (Lambertian - non-reflective, opaque)
    //     sceneList.add(new Sphere(new Point3D(-4, 1, 0), 1.0, new Color(102, 51, 26), 500, 1, 0, 1)); 

    //     // Metal sphere (Metal - highly reflective, opaque)
    //     sceneList.add(new Sphere(new Point3D(4, 1, 0), 1.0, new Color(179, 153, 128), 500, 1, 0, 1)); 

    //     scene = sceneList.toArray(new SceneObject[0]);
    // }


    // static {
    //     ArrayList<SceneObject> sceneList = new ArrayList<>();
    //     Random rand = new Random();

    //     // Ground sphere
    //     sceneList.add(new Sphere(new Point3D(0, -1000, 0), 1000, new Lambertian(new Color(128, 128, 128))));

    //     // Random small spheres
    //     for (int a = -11; a < 11; a++) {
    //         for (int b = -11; b < 11; b++) {
    //             double chooseMat = rand.nextDouble();
    //             Point3D center = new Point3D(a + 0.9 * rand.nextDouble(), 0.2, b + 0.9 * rand.nextDouble());

    //             if (center.distance(new Point3D(4, 0.2, 0)) > 0.9) {
    //                 if (chooseMat < 0.8) { // Diffuse
    //                     Color albedo = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    //                     sceneList.add(new Sphere(center, 0.2, new Lambertian(albedo)));
    //                 } else if (chooseMat < 0.95) { // Metal
    //                     Color albedo = new Color(0.5f * (1 + rand.nextFloat()), 0.5f * (1 + rand.nextFloat()), 0.5f * (1 + rand.nextFloat()));
    //                     double fuzz = 0.5 * rand.nextDouble();
    //                     sceneList.add(new Sphere(center, 0.2, new Metal(albedo, fuzz)));
    //                 } else { // Glass
    //                     sceneList.add(new Sphere(center, 0.2, new Dielectric(1.5)));
    //                 }
    //             }
    //         }
    //     }

    //     // Large spheres
    //     sceneList.add(new Sphere(new Point3D(0, 1, 0), 1.0, new Dielectric(1.5)));
    //     sceneList.add(new Sphere(new Point3D(-4, 1, 0), 1.0, new Lambertian(new Color(102, 51, 26))));
    //     sceneList.add(new Sphere(new Point3D(4, 1, 0), 1.0, new Metal(new Color(179, 153, 128), 0.0)));

    //     scene = sceneList.toArray(new SceneObject[0]);
    // }
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

    // Helper Function to reflect a ray around a normal vector
    private static Point3D reflectRay(Point3D R, Point3D N) {
        // R_reflected = 2 * N * (N · R) - R
        return N.multiply(2 * N.dot(R)).subtract(R);
    }

    // Canvas to viewport conversion
    private static Point3D canvasToViewport(int x, int y) {
        return new Point3D(
                x * VIEWPORT_SIZE / WIDTH,
                y * VIEWPORT_SIZE / HEIGHT,
                PROJECTION_PLANE_D);
    }


    public RayTracer() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
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

    private static IntersectionResult closestIntersection(Point3D O, Point3D D, double tMin, double tMax) {
        double closestT = Double.POSITIVE_INFINITY;
        SceneObject closestObject = null;
    
        for (SceneObject obj : scene) {
            double[] tValues = null;
    
            if (obj instanceof Sphere) {
                Sphere sphere = (Sphere) obj;
                tValues = intersectRaySphere(O, D, sphere);
            } else if (obj instanceof Cylinder) {
                Cylinder cylinder = (Cylinder) obj;
                tValues = intersectRayCylinder(O, D, cylinder);
            }
    
            if (tValues == null) {
                continue;
            }
    
            double t1 = tValues[0];
            double t2 = tValues[1];
    
            if (t1 >= tMin && t1 <= tMax && t1 < closestT) {
                closestT = t1;
                closestObject = obj;
            }
    
            if (t2 >= tMin && t2 <= tMax && t2 < closestT) {
                closestT = t2;
                closestObject = obj;
            }
        }

        // Return the closest object and the corresponding t value
        return new IntersectionResult(closestObject, closestT);
    }

    private static double[] intersectRayCylinder(Point3D O, Point3D D, Cylinder cylinder) {
        Point3D center = cylinder.center;
        double radius = cylinder.radius;
        double height = cylinder.height;
    
        Point3D CO = O.subtract(center);
    
        double dy = D.y;
        double cy = CO.y;
    
        double a = D.x * D.x + D.z * D.z;
        double b = 2 * (D.x * CO.x + D.z * CO.z);
        double c = CO.x * CO.x + CO.z * CO.z - radius * radius;
    
        double discriminant = b * b - 4 * a * c;
    
        if (discriminant < 0) {
            return null;
        }
    
        double t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2 * a);
    
        double y1 = O.y + t1 * D.y;
        double y2 = O.y + t2 * D.y;
    
        if (y1 < center.y || y1 > center.y + height) {
            t1 = Double.POSITIVE_INFINITY;
        }
        if (y2 < center.y || y2 > center.y + height) {
            t2 = Double.POSITIVE_INFINITY;
        }
    
        // Ensure valid intersections
        if (t1 == Double.POSITIVE_INFINITY && t2 == Double.POSITIVE_INFINITY) {
            return null;
        }
    
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
        if (recursionDepth <= 0) {
            return BACKGROUND_COLOR;
        }
    
        IntersectionResult result = closestIntersection(O, D, tMin, tMax);
        SceneObject closestObject = result.object;
        double closestT = result.t;
    
        if (closestObject == null) {
            return BACKGROUND_COLOR;
        }
    
        Point3D P = O.add(D.multiply(closestT));
        Point3D N = P.subtract(closestObject.center).normalize();
    
        double lighting = computeLighting(P, N, D.multiply(-1), closestObject.specular);
        Color localColor = multiplyColor(closestObject.color, lighting);
    
        double r = closestObject.reflective;
        double transparency = closestObject.transparency;
    
        if (recursionDepth <= 0 || r <= 0 && transparency <= 0) {
            return localColor;
        }
    
        Point3D R = reflectRay(D.multiply(-1), N);
        Color reflectedColor = traceRay(P, R, 0.001, Double.POSITIVE_INFINITY, recursionDepth - 1);
    
        Color refractedColor = BACKGROUND_COLOR;
        if (transparency > 0) {
            Point3D refractedDirection = refractRay(D, N, closestObject);
            if (refractedDirection != null) {
                refractedColor = traceRay(P, refractedDirection, 0.001, Double.POSITIVE_INFINITY, recursionDepth - 1);
            }
        }
    
        Color finalColor = blendColors(localColor, reflectedColor, r);
        finalColor = blendColors(finalColor, refractedColor, transparency);
    
        return finalColor;
    }
    private static Point3D refractRay(Point3D D, Point3D N, SceneObject object) {
        double eta = 1.0 / object.refractiveIndex;
        double cosi = -N.dot(D);
        double k = 1.0 - eta * eta * (1.0 - cosi * cosi);
    
        if (k < 0) {
            return null; // Total internal reflection
        }
    
        Point3D refracted = D.multiply(eta).add(N.multiply(eta * cosi - Math.sqrt(k)));
        return refracted.normalize();
    }

    // Compute lighting
    private static double computeLighting(Point3D P, Point3D N, Point3D V, double s) {
        double intensity = 0.0;
    
        for (Light light : lights) {
            if (light.type == Light.LightType.AMBIENT) {
                intensity += light.intensity;
            } else {
                Point3D L;
                double tMax;
    
                if (light.type == Light.LightType.POINT) {
                    L = light.position.subtract(P);
                    tMax = 1;
                } else {
                    L = light.direction;
                    tMax = Double.POSITIVE_INFINITY;
                }
    
                // Normalize L
                L = L.normalize();
    
                IntersectionResult shadowResult = closestIntersection(P, L, 0.001, tMax);
                if (shadowResult.object != null) {
                    continue;
                }
    
                double nDotL = N.dot(L);
                if (nDotL > 0) {
                    intensity += light.intensity * nDotL;
                }
    
                if (s != -1) {
                    Point3D R = reflectRay(L, N);
                    double rDotV = R.dot(V);
                    if (rDotV > 0) {
                        intensity += light.intensity * Math.pow(rDotV, s);
                    }
                }
            }
        }
    
        return intensity;
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