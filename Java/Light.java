public class Light {
    enum LightType { AMBIENT, POINT, DIRECTIONAL }

    LightType type;
    double intensity;
    Point3D position; // Only for point light
    Point3D direction; // Only for directional light

    public Light(LightType type, double intensity, Point3D position, Point3D direction) {
        this.type = type;
        this.intensity = intensity;
        this.position = position;
        this.direction = direction != null ? direction.normalize() : null;
    }
}