import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import random

# Vector and Ray classes
class Vec3:
    def __init__(self, x=0, y=0, z=0):
        self.x = x
        self.y = y
        self.z = z

    def __add__(self, other):
        return Vec3(self.x + other.x, self.y + other.y, self.z + other.z)

    def __sub__(self, other):
        return Vec3(self.x - other.x, self.y - other.y, self.z - other.z)

    def __mul__(self, other):
        if isinstance(other, Vec3):
            return Vec3(self.x * other.x, self.y * other.y, self.z * other.z)
        else:
            return Vec3(self.x * other, self.y * other, self.z * other)

    def __truediv__(self, other):
        return Vec3(self.x / other, self.y / other, self.z / other)

    def dot(self, other):
        return self.x * other.x + self.y * other.y + self.z * other.z

    def cross(self, other):
        return Vec3(
            self.y * other.z - self.z * other.y,
            self.z * other.x - self.x * other.z,
            self.x * other.y - self.y * other.x
        )

    def length(self):
        return np.sqrt(self.dot(self))

    def unit_vector(self):
        return self / self.length()

    def reflect(self, normal):
        return self - normal * 2.0 * self.dot(normal)

    def refract(self, normal, eta_ratio):
        cos_theta = min(-self.dot(normal), 1.0)
        r_out_perp = (self + normal * cos_theta) * eta_ratio
        r_out_parallel = normal * -np.sqrt(abs(1.0 - r_out_perp.length() ** 2))
        return r_out_perp + r_out_parallel

    @staticmethod
    def random():
        return Vec3(random.random(), random.random(), random.random())

    @staticmethod
    def random_range(min_val, max_val):
        return Vec3(random.uniform(min_val, max_val), random.uniform(min_val, max_val), random.uniform(min_val, max_val))

    def __neg__(self):
        return Vec3(-self.x, -self.y, -self.z)


class Ray:
    def __init__(self, origin, direction):
        self.origin = origin
        self.direction = direction

    def point_at_parameter(self, t):
        return self.origin + self.direction * t

# Sphere class
class Sphere:
    def __init__(self, center, radius, material):
        self.center = center
        self.radius = radius
        self.material = material

    def hit(self, ray, t_min, t_max):
        oc = ray.origin - self.center
        a = ray.direction.dot(ray.direction)
        b = 2.0 * oc.dot(ray.direction)
        c = oc.dot(oc) - self.radius * self.radius
        discriminant = b * b - 4 * a * c

        if discriminant > 0:
            temp = (-b - np.sqrt(discriminant)) / (2.0 * a)
            if t_min < temp < t_max:
                return temp, self

            temp = (-b + np.sqrt(discriminant)) / (2.0 * a)
            if t_min < temp < t_max:
                return temp, self

        return None, None

# Material classes
class Material:
    def scatter(self, ray, hit_record):
        pass

class Lambertian(Material):
    def __init__(self, albedo):
        self.albedo = albedo

    def scatter(self, ray, hit_record):
        scatter_direction = hit_record['normal'] + Vec3.random_in_unit_sphere()
        scattered = Ray(hit_record['p'], scatter_direction)
        attenuation = self.albedo
        return scattered, attenuation

class Metal(Material):
    def __init__(self, albedo, fuzz):
        self.albedo = albedo
        self.fuzz = min(fuzz, 1.0)

    def scatter(self, ray, hit_record):
        reflected = ray.direction.unit_vector().reflect(hit_record['normal'])
        scattered = Ray(hit_record['p'], reflected + Vec3.random_in_unit_sphere() * self.fuzz)
        attenuation = self.albedo
        return scattered, attenuation

class Dielectric(Material):
    def __init__(self, refractive_index):
        self.refractive_index = refractive_index

    def scatter(self, ray, hit_record):
        attenuation = Vec3(1.0, 1.0, 1.0)
        eta_ratio = 1.0 / self.refractive_index if hit_record['front_face'] else self.refractive_index

        unit_direction = ray.direction.unit_vector()
        cos_theta = min(-unit_direction.dot(hit_record['normal']), 1.0)
        sin_theta = np.sqrt(1.0 - cos_theta * cos_theta)

        cannot_refract = eta_ratio * sin_theta > 1.0
        if cannot_refract or self.reflectance(cos_theta, eta_ratio) > random.random():
            direction = unit_direction.reflect(hit_record['normal'])
        else:
            direction = unit_direction.refract(hit_record['normal'], eta_ratio)

        scattered = Ray(hit_record['p'], direction)
        return scattered, attenuation

    def reflectance(self, cosine, ref_idx):
        r0 = (1 - ref_idx) / (1 + ref_idx)
        r0 = r0 * r0
        return r0 + (1 - r0) * (1 - cosine) ** 5

# Camera class
class Camera:
    def __init__(self):
        self.aspect_ratio = 16.0 / 9.0
        self.image_width = 1200
        self.samples_per_pixel = 500
        self.max_depth = 50
        self.vfov = 20
        self.lookfrom = Vec3(13, 2, 3)
        self.lookat = Vec3(0, 0, 0)
        self.vup = Vec3(0, 1, 0)
        self.defocus_angle = 0.6
        self.focus_dist = 10.0

        self.image_height = int(self.image_width / self.aspect_ratio)
        self.center = self.lookfrom

        # Camera basis vectors
        w = (self.lookfrom - self.lookat).unit_vector()
        u = self.vup.cross(w).unit_vector()
        v = w.cross(u)

        # Viewport dimensions
        theta = np.deg2rad(self.vfov)
        h = np.tan(theta / 2)
        viewport_height = 2 * h * self.focus_dist
        viewport_width = viewport_height * (self.image_width / self.image_height)

        # Horizontal and vertical viewport vectors
        self.viewport_u = u * viewport_width
        self.viewport_v = -v * viewport_height

        # Pixel delta vectors
        self.pixel_delta_u = self.viewport_u / self.image_width
        self.pixel_delta_v = self.viewport_v / self.image_height

        # Upper-left pixel location
        self.viewport_upper_left = (
            self.center - (w * self.focus_dist) - self.viewport_u / 2 - self.viewport_v / 2
        )
        self.pixel00_loc = self.viewport_upper_left + (self.pixel_delta_u + self.pixel_delta_v) * 0.5

    import numpy as np
    from numba import njit, prange
    from PIL import Image

    # Assuming Vec3, Sphere, Lambertian, Metal, Dielectric, Camera, and color are defined elsewhere

    @njit(parallel=True)
    def render(width, height):
        nx = width
        ny = height
        world = [
            Sphere(Vec3(0, 0, -1), 0.5, Lambertian(Vec3(0.8, 0.3, 0.3))),
            Sphere(Vec3(0, -100.5, -1), 100, Lambertian(Vec3(0.8, 0.8, 0.0))),
            Sphere(Vec3(1, 0, -1), 0.5, Metal(Vec3(0.8, 0.6, 0.2), 0.3)),
            Sphere(Vec3(-1, 0, -1), 0.5, Dielectric(1.5))
        ]
        cam = Camera()
        image = np.zeros((ny, nx, 3), dtype=np.uint8)

        for j in range(ny):  # Parallelize this loop
            for i in range(nx):
                u = i / nx
                v = (ny - j - 1) / ny  # Flip y-axis
                ray = cam.get_ray(u, v)
                col = color(ray, world)
                ir = int(255.99 * col.x)
                ig = int(255.99 * col.y)
                ib = int(255.99 * col.z)
                image[j, i] = [ir, ig, ib]

        return image

    def get_ray(self, i, j):
        pixel_center = self.pixel00_loc + (self.pixel_delta_u * i) + (self.pixel_delta_v * j)
        ray_direction = pixel_center - self.center
        return Ray(self.center, ray_direction)

    def ray_color(self, ray, world, depth):
      if depth <= 0:
          return Vec3(0, 0, 0)

      closest_t = float('inf')
      closest_sphere = None
      hit_record = {}  # Store hit record

      # Iterate through spheres in the world
      for sphere in world:
          temp_t, temp_sphere = sphere.hit(ray, 0.001, closest_t)
          if temp_sphere is not None:
              closest_t = temp_t
              closest_sphere = temp_sphere

      # If a sphere was hit, calculate color
      if closest_sphere is not None:
          hit_record['t'] = closest_t
          hit_record['p'] = ray.point_at_parameter(closest_t)
          hit_record['normal'] = (hit_record['p'] - closest_sphere.center).unit_vector()
          hit_record['front_face'] = ray.direction.dot(hit_record['normal']) < 0
          if not hit_record['front_face']:
              hit_record['normal'] = hit_record['normal'] * -1

          scattered, attenuation = closest_sphere.material.scatter(ray, hit_record)
          if scattered is not None:
              return attenuation * self.ray_color(scattered, world, depth - 1)
          return Vec3(0, 0, 0)

      unit_direction = ray.direction.unit_vector()
      t = 0.5 * (unit_direction.y + 1.0)
      return Vec3(1.0, 1.0, 1.0) * (1.0 - t) + Vec3(0.5, 0.7, 1.0) * t  # Background gradient

# Utility functions
def random_double(min_val=0.0, max_val=1.0):
    return random.uniform(min_val, max_val)

def random_in_unit_sphere():
    while True:
        p = Vec3.random_range(-1, 1)
        if p.length() < 1.0:
            return p
from tqdm import tqdm
# Main function
def main():
    world = []

    ground_material = Lambertian(Vec3(0.5, 0.5, 0.5))
    world.append(Sphere(Vec3(0, -1000, 0), 1000, ground_material))

    # for a in range(-11, 11):
    #     for b in range(-11, 11):
    #         choose_mat = random_double()
    #         center = Vec3(a + 0.9 * random_double(), 0.2, b + 0.9 * random_double())

    #         if (center - Vec3(4, 0.2, 0)).length() > 0.9:
    #             if choose_mat < 0.8:
    #                 # Diffuse
    #                 albedo = Vec3.random() * Vec3.random()
    #                 sphere_material = Lambertian(albedo)
    #             elif choose_mat < 0.95:
    #                 # Metal
    #                 albedo = Vec3.random_range(0.5, 1)
    #                 fuzz = random_double(0, 0.5)
    #                 sphere_material = Metal(albedo, fuzz)
    #             else:
    #                 # Glass
    #                 sphere_material = Dielectric(1.5)
    #             world.append(Sphere(center, 0.2, sphere_material))

    # material1 = Dielectric(1.5)
    # world.append(Sphere(Vec3(0, 1, 0), 1.0, material1))

    # material2 = Lambertian(Vec3(0.4, 0.2, 0.1))
    # world.append(Sphere(Vec3(-4, 1, 0), 1.0, material2))

    # material3 = Metal(Vec3(0.7, 0.6, 0.5), 0.0)
    # world.append(Sphere(Vec3(4, 1, 0), 1.0, material3))

    cam = Camera()

    cam.render(world)


if __name__ == "__main__":
    main()