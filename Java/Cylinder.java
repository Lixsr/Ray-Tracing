import java.awt.Color;

class Cylinder extends SceneObject {
    double radius;
    double height;

    Cylinder(Point3D center, double radius, double height, Color color, double specular, double reflective) {
        super(center, color, specular, reflective);
        this.radius = radius;
        this.height = height;
    }
}