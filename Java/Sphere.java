import java.awt.Color;

class Sphere extends SceneObject {
    double radius;

    Sphere(Point3D center, double radius, Color color, double specular, double reflective, double transparency, double refractiveIndex) {
        super(center, color, specular, reflective, transparency, refractiveIndex);
        this.radius = radius;
    }
}

