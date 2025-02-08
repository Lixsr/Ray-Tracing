import java.awt.Color;

abstract class SceneObject {
    Point3D center;
    Color color;
    double specular;
    double reflective;
    double transparency;
    double refractiveIndex;

    SceneObject(Point3D center, Color color, double specular, double reflective, double transparency, double refractiveIndex) {
        this.center = center;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
        this.transparency = transparency;
        
    }
}