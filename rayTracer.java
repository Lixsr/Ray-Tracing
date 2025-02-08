import java.io.FileWriter;
import java.io.IOException;

public class RayTracer {

    // Image dimensions
    private static final int IMAGE_WIDTH = 800;
    private static final int IMAGE_HEIGHT = 400;

    // Camera and scene setup
    private static final Vec3 LOWER_LEFT_CORNER = new Vec3(-2.0, -1.0, -1.0);
    private static final Vec3 HORIZONTAL = new Vec3(4.0, 0.0, 0.0);
    private static final Vec3 VERTICAL = new Vec3(0.0, 2.0, 0.0);
    private static final Vec3 ORIGIN = new Vec3(0.0, 0.0, 0.0);

    // Maximum recursion depth for rays
    private static final int MAX_DEPTH = 50;

    public static void main(String[] args) {
        // Render the scene
        String ppmContent = renderScene();

        // Save the rendered image to a PPM file
        try (FileWriter writer = new FileWriter("output.ppm")) {
            writer.write(ppmContent);
            System.out.println("Rendered image saved to output.ppm");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String renderScene() {
        StringBuilder ppmBuilder = new StringBuilder();
        ppmBuilder.append("P3\n").append(IMAGE_WIDTH).append(" ").append(IMAGE_HEIGHT).append("\n255\n");

        for (int j = IMAGE_HEIGHT - 1; j >= 0; j--) {
            for (int i = 0; i < IMAGE_WIDTH; i++) {
                double u = (double) i / IMAGE_WIDTH;
                double v = (double) j / IMAGE_HEIGHT;
                Ray ray = new Ray(ORIGIN, LOWER_LEFT_CORNER.add(HORIZONTAL.scale(u)).add(VERTICAL.scale(v)));
                Vec3 color = color(ray, MAX_DEPTH);
                int ir = (int) (255.99 * color.x());
                int ig = (int) (255.99 * color.y());
                int ib = (int) (255.99 * color.z());
                ppmBuilder.append(ir).append(" ").append(ig).append(" ").append(ib).append("\n");
            }
        }

        return ppmBuilder.toString();
    }

    private static Vec3 color(Ray ray, int depth) {
        HitRecord rec = new HitRecord();
        if (depth <= 0) {
            return new Vec3(0, 0, 0); // Terminate recursion
        }
        if (hitSphere(new Vec3(0, 0, -1), 0.5, ray, 0.001, Double.MAX_VALUE, rec)) {
            Ray scattered = new Ray(new Vec3(0, 0, 0), new Vec3(0, 0, 0));
            Vec3 attenuation = new Vec3(0, 0, 0);
            if (rec.material.scatter(ray, rec, attenuation, scattered)) {
                return attenuation.multiply(color(scattered, depth - 1));
            }
            return new Vec3(0, 0, 0);
        }
        Vec3 unitDirection = ray.direction().unitVector();
        double t = 0.5 * (unitDirection.y() + 1.0);
        return new Vec3(1.0, 1.0, 1.0).scale(1.0 - t).add(new Vec3(0.5, 0.7, 1.0).scale(t)); // Background gradient
    }

    private static boolean hitSphere(Vec3 center, double radius, Ray ray, double tMin, double tMax, HitRecord rec) {
        Vec3 oc = ray.origin().subtract(center);
        double a = ray.direction().dot(ray.direction());
        double b = oc.dot(ray.direction());
        double c = oc.dot(oc) - radius * radius;
        double discriminant = b * b - a * c;

        if (discriminant > 0) {
            double temp = (-b - Math.sqrt(discriminant)) / a;
            if (temp < tMax && temp > tMin) {
                rec.t = temp;
                rec.p = ray.pointAtParameter(rec.t);
                rec.normal = rec.p.subtract(center).scale(1.0 / radius);
                rec.material = new Metal(new Vec3(0.8, 0.6, 0.2), 0.3); // Example material
                return true;
            }
            temp = (-b + Math.sqrt(discriminant)) / a;
            if (temp < tMax && temp > tMin) {
                rec.t = temp;
                rec.p = ray.pointAtParameter(rec.t);
                rec.normal = rec.p.subtract(center).scale(1.0 / radius);
                rec.material = new Dielectric(1.5); // Example material
                return true;
            }
        }
        return false;
    }
}

// Vector class
class Vec3 {
    private final double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 subtract(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 scale(double t) {
        return new Vec3(x * t, y * t, z * t);
    }

    public Vec3 multiply(Vec3 v) {
        return new Vec3(x * v.x, y * v.y, z * v.z);
    }

    public double dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 unitVector() {
        double length = Math.sqrt(x * x + y * y + z * z);
        return new Vec3(x / length, y / length, z / length);
    }

    public Vec3 reflect(Vec3 normal) {
        return this.subtract(normal.scale(2 * this.dot(normal)));
    }

    public Vec3 refract(Vec3 normal, double etaiOverEtat) {
        double cosTheta = Math.min(this.scale(-1).dot(normal), 1.0);
        Vec3 rOutPerp = this.add(normal.scale(cosTheta)).scale(etaiOverEtat);
        Vec3 rOutParallel = normal.scale(-Math.sqrt(Math.abs(1.0 - rOutPerp.dot(rOutPerp))));
        return rOutPerp.add(rOutParallel);
    }
}

// Ray class
class Ray {
    private final Vec3 origin;
    private final Vec3 direction;

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vec3 origin() { return origin; }
    public Vec3 direction() { return direction; }

    public Vec3 pointAtParameter(double t) {
        return origin.add(direction.scale(t));
    }
}

// Hit record class
class HitRecord {
    double t;
    Vec3 p;
    Vec3 normal;
    Material material;
}

// Material interface
interface Material {
    boolean scatter(Ray rayIn, HitRecord rec, Vec3 attenuation, Ray scattered);
}

// Lambertian material (diffuse)
class Lambertian implements Material {
    private final Vec3 albedo;

    public Lambertian(Vec3 albedo) {
        this.albedo = albedo;
    }

    @Override
    public boolean scatter(Ray rayIn, HitRecord rec, Vec3 attenuation, Ray scattered) {
        Vec3 scatterDirection = rec.normal.add(randomInUnitSphere());
        scattered.origin = rec.p;
        scattered.direction = scatterDirection;
        attenuation.x = albedo.x();
        attenuation.y = albedo.y();
        attenuation.z = albedo.z();
        return true;
    }

    private Vec3 randomInUnitSphere() {
        Vec3 p;
        do {
            p = new Vec3(Math.random(), Math.random(), Math.random()).scale(2).subtract(new Vec3(1, 1, 1));
        } while (p.dot(p) >= 1.0);
        return p;
    }
}

// Metal material (reflective)
class Metal implements Material {
    private final Vec3 albedo;
    private final double fuzz;

    public Metal(Vec3 albedo, double fuzz) {
        this.albedo = albedo;
        this.fuzz = fuzz < 1 ? fuzz : 1;
    }

    @Override
    public boolean scatter(Ray rayIn, HitRecord rec, Vec3 attenuation, Ray scattered) {
        Vec3 reflected = rayIn.direction().unitVector().reflect(rec.normal);
        scattered.origin = rec.p;
        scattered.direction = reflected.add(randomInUnitSphere().scale(fuzz));
        attenuation.x = albedo.x();
        attenuation.y = albedo.y();
        attenuation.z = albedo.z();
        return scattered.direction.dot(rec.normal) > 0;
    }

    private Vec3 randomInUnitSphere() {
        Vec3 p;
        do {
            p = new Vec3(Math.random(), Math.random(), Math.random()).scale(2).subtract(new Vec3(1, 1, 1));
        } while (p.dot(p) >= 1.0);
        return p;
    }
}

// Dielectric material (transparent/refractive)
class Dielectric implements Material {
    private final double refIdx;

    public Dielectric(double refIdx) {
        this.refIdx = refIdx;
    }

    @Override
    public boolean scatter(Ray rayIn, HitRecord rec, Vec3 attenuation, Ray scattered) {
        Vec3 outwardNormal;
        double niOverNt;
        attenuation.x = 1.0;
        attenuation.y = 1.0;
        attenuation.z = 1.0;
        double cosine;
        if (rayIn.direction().dot(rec.normal) > 0) {
            outwardNormal = rec.normal.scale(-1);
            niOverNt = refIdx;
            cosine = refIdx * rayIn.direction().dot(rec.normal) / rayIn.direction().length();
        } else {
            outwardNormal = rec.normal;
            niOverNt = 1.0 / refIdx;
            cosine = -rayIn.direction().dot(rec.normal) / rayIn.direction().length();
        }

        Vec3 refracted = rayIn.direction().refract(outwardNormal, niOverNt);
        double reflectProb = refracted != null ? schlick(cosine, refIdx) : 1.0;

        if (Math.random() < reflectProb) {
            Vec3 reflected = rayIn.direction().reflect(rec.normal);
            scattered.origin = rec.p;
            scattered.direction = reflected;
        } else {
            scattered.origin = rec.p;
            scattered.direction = refracted;
        }
        return true;
    }

    private double schlick(double cosine, double refIdx) {
        double r0 = (1 - refIdx) / (1 + refIdx);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow(1 - cosine, 5);
    }
}