import java.awt.Color;

public class Sphere {
    Point3D center;
    double radius;
    Color color;
    double specular;
    double reflective;
    double transparency;  // NEW
    double refractiveIndex; // NEW

    Sphere(Point3D center, double radius, Color color, double specular, double reflective, double transparency, double refractiveIndex) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
        this.transparency = transparency;
        this.refractiveIndex = refractiveIndex;
    }
}
