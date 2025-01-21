import java.awt.*;

// Sphere class
class Sphere {
    Point3D center;
    double radius;
    Color color;
    double specular;

    public Sphere(Point3D center, double radius, Color color, double specular) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
    }
}