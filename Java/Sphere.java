import java.awt.*;

// Sphere class
class Sphere {
    Point3D center;
    double radius;
    Color color;
    double specular;
    double reflective;

    Sphere(Point3D center, double radius, Color color, double specular, double reflective) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
    }
}