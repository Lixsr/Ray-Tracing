# Ray Tracer in Java

This is a simple ray tracing program written in Java. It renders a scene with three spheres (red, green, and blue) using basic ray tracing techniques. The program uses Java's `javax.swing` library to display the rendered image in a window.

---

## Features

- **Sphere Rendering**: Renders a 2D projection of a 3D scene.
- **Ray-Sphere Intersection**: Uses mathematical formulas to compute intersections between rays and spheres.
- **Interactive Display**: Displays the rendered image in a window using Java's `JFrame`.

---

## Prerequisites

To run this program, you need:

- **Java Development Kit (JDK)**: Ensure you have JDK 8 or later installed.

---

## How to Run the Program

### 1. Clone or Download the Code

- Clone this repository or download the `Ray-Tracing` folder to your local machine.

### 2. Run the Program on Different Platforms

#### Step 1: Open a Terminal or Command Prompt

- **Windows**: Open Command Prompt or PowerShell by searching for it in the Start menu.
- **Mac**: Open the Terminal application (found in Applications > Utilities).
- **Linux**: Open a terminal using your preferred method.

#### Step 2: Navigate to the Program Directory

Use the `cd` command to navigate to the directory containing the `RayTracer.java` file. For example:

```bash
cd path/to/your/directory
```

#### Step 3: Run the Program

Run the program directly using the following command:

```bash
java RayTracer.java
```

**Note**: Ensure that Java is properly installed and added to your system's PATH. You can verify this by running:

```bash
java -version
```

If Java is not installed or configured, refer to your operating system's instructions for setting it up.

### 4. View the Output

A window will open displaying the rendered image of three spheres (red, green, and blue) on a white background.

---

## Code Structure

The program consists of the following classes:

---

### 1. `Point3D`

Represents a 3D point or vector with `x`, `y`, and `z` coordinates. Includes methods for vector operations such as addition, subtraction, dot product, scaling, normalization, and calculating distance. Used for geometric calculations in 3D space.

---

### 2. `Sphere`

Represents a spherical object in 3D space, inheriting from `SceneObject`. Defines properties such as radius, color, specular highlights, reflection, transparency, and refractive index. Used to define and manipulate spheres within the scene for ray tracing.

---

### 3. `Cylinder`

Represents a cylindrical object in 3D space, inheriting from `SceneObject`. Includes properties such as radius, height, color, specular highlights, reflection, transparency, and refractive index. Used to define and manipulate cylinders in the scene for ray tracing.

---

### 4. `SceneObject`

An abstract class representing a 3D object in the scene, with properties such as position (`center`), color, and material characteristics like specular, reflective, transparency, and refractive index. This class serves as a base for specific geometric objects like spheres and cylinders.

---

### 5. `Light`

Represents a light source in the scene with three possible types: `AMBIENT`, `POINT`, and `DIRECTIONAL`. It includes properties like light intensity, position (for point lights), and direction (for directional lights), providing the necessary data to simulate lighting effects in the scene.

---

### 5. Scene Initialization

This code dynamically initializes the scene with various objects, including spheres and a cylinder. It creates a list of scene objects (`SceneObject[]`), with properties such as random colors, specular, transparency, reflection, and refractive index. Several objects are added, including a ground sphere, multiple spheres with varying attributes, and random small spheres. The objects are stored in `sceneList`, which is then converted into an array of `SceneObject` objects.

---

### 6. `rotateVector` Method

This method rotates a 3D vector (`Point3D`) based on given yaw and pitch angles. It first applies pitch rotation around the X-axis and then yaw rotation around the Y-axis. The resulting rotated vector is normalized and returned as a new `Point3D`. Angles are converted from degrees to radians before applying trigonometric functions to compute the new coordinates.

---

### 7. `canvasToViewport` Method

This method converts canvas coordinates (`x`, `y`) to viewport coordinates. It scales the `x` and `y` values based on the viewport size and the canvas dimensions. The `z` coordinate is set to a constant value representing the projection plane distance. This conversion helps map pixel positions on the canvas to the 3D viewport for rendering.

---

### 8. `blendColors` Method

This method blends two colors (`color1` and `color2`) based on a given weight. It calculates the weighted average of the red, green, and blue components of both colors, producing a new color that combines the two. The result is capped to ensure that each RGB component stays within the valid color range (0-255).

---

### 9. `closestIntersection` Method

This method finds the closest intersection between a ray and objects in the scene. It checks each object (either a sphere or a cylinder) for intersections within the specified `tMin` and `tMax` range. The method returns the object that the ray intersects first, along with the corresponding intersection distance (`t`).

---

### 10. `intersectRayCylinder` Method

This method calculates the intersections of a ray with a cylinder. It checks for intersections with both the side and the caps of the cylinder. The function returns the closest two intersection distances (`t1` and `t2`) along the ray. If there are no intersections, it returns `null`.

---

### 11. `intersectRaySphere` Method

This method calculates the intersections of a ray with a sphere. It solves the quadratic equation derived from the ray-sphere intersection. The function returns the two possible intersection distances (`t1` and `t2`). If there is no intersection (discriminant < 0), it returns `Double.POSITIVE_INFINITY` for both values.

---

### 12. `traceRay` Method

The `traceRay` method recursively traces a ray through the scene, calculating intersections with objects and determining the final color based on lighting, reflection, and refraction. It considers the object's material properties (specular, reflection, transparency) and combines the local color with reflected and refracted contributions, applying a recursive depth limit. If no intersection is found, it returns the background color.

---

### 13. `computeLighting` Method

The `computeLighting` method calculates the lighting intensity at a point on a surface. It iterates through all light sources in the scene, handling ambient, point, and directional lights. For each light, it checks for shadowing by casting a ray towards the light source. The method also computes diffuse and specular contributions based on the surface normal and viewer direction. The result is the total lighting intensity affecting the point, factoring in reflections if applicable.

---
---
---

### 3. `RayTracer`

- **Rendering**:
  - `render`: Renders the scene by tracing rays for each pixel.
  - `paintComponent`: Draws the rendered image on the screen.
- **Main Method**: Initializes the program and displays the rendered image in a window.

---
