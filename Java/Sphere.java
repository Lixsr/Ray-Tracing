import java.awt.Color;

class Sphere extends SceneObject {
    double radius;

    Sphere(Point3D center, double radius, Color color, double specular, double reflective) {
        super(center, color, specular, reflective);
        this.radius = radius;
    }
}

