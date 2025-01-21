public class Point3D {
    double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D subtract(Point3D other) {
        return new Point3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Point3D add(Point3D other) {
        return new Point3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    

    public Point3D multiply(double scalar) {
        return new Point3D(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public double dot(Point3D other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double length() {
        return Math.sqrt(this.dot(this));
    }

    public Point3D normalize() {
        double length = this.length();
        return new Point3D(this.x / length, this.y / length, this.z / length);
    }
}
