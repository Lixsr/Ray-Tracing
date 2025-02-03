import java.awt.Color;

abstract class SceneObject {
    Point3D center;
    Color color;
    double specular;
    double reflective;

    SceneObject(Point3D center, Color color, double specular, double reflective) {
        this.center = center;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
    }
}