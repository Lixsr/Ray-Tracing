# Ray Tracer in Java

This is a simple ray tracing program written in Java. It renders a scene with three spheres (red, green, and blue) using basic ray tracing techniques. The program uses Java's `javax.swing` library to display the rendered image in a window.

---

## Features
- **3D Sphere Rendering**: Renders three spheres in a 3D space.
- **Ray-Sphere Intersection**: Uses mathematical formulas to compute intersections between rays and spheres.
- **Basic Lighting**: Each sphere is colored with a solid color (no shading or shadows).
- **Interactive Display**: Displays the rendered image in a window using Java's `JFrame`.

---

## Prerequisites
To run this program, you need:
- **Java Development Kit (JDK)**: Ensure you have JDK 8 or later installed.
- **IDE or Command Line**: You can use an IDE like IntelliJ IDEA, Eclipse, or run the program from the command line.

---

## How to Run the Program

### 1. Clone or Download the Code
- Clone this repository or download the `RayTracer.java` file to your local machine.

### 2. Compile the Program
Open a terminal or command prompt and navigate to the directory containing the `RayTracer.java` file. Then, compile the program using the following command:

```bash
javac RayTracer.java
```

This will generate a `RayTracer.class` file.

### 3. Run the Program
After compiling, run the program using the following command:

```bash
java RayTracer
```

### 4. View the Output
A window will open displaying the rendered image of three spheres (red, green, and blue) on a white background.

---

## Code Structure
The program consists of the following classes:

### 1. `Point3D`
Represents a 3D point or vector. It includes methods for vector math:
- `subtract`: Subtracts two vectors.
- `multiply`: Multiplies a vector by a scalar.
- `dot`: Computes the dot product of two vectors.
- `length`: Computes the length of a vector.
- `normalize`: Normalizes a vector to unit length.

### 2. `Sphere`
Represents a sphere in 3D space. It has:
- `center`: The center of the sphere (a `Point3D`).
- `radius`: The radius of the sphere.
- `color`: The color of the sphere.

### 3. `RayTracer`
The main class that handles rendering and display. It includes:
- **Scene Setup**: Defines the three spheres and their properties.
- **Ray Tracing Logic**:
  - `canvasToViewport`: Maps canvas coordinates to viewport coordinates.
  - `intersectRaySphere`: Computes the intersection of a ray with a sphere.
  - `traceRay`: Traces a ray and determines the color of the closest sphere.
- **Rendering**:
  - `render`: Renders the scene by tracing rays for each pixel.
  - `paintComponent`: Draws the rendered image on the screen.
- **Main Method**: Initializes the program and displays the rendered image in a window.

---

## Example Output
The program will render three spheres:
- A **red sphere** at `(0, 1, 3)`.
- A **blue sphere** at `(2, 0, 4)`.
- A **green sphere** at `(-2, 0, 4)`.

The rendered image will look like this:

```
+--------------------------------+
|                                |
|             RED                |
|                                |
|      BLUE          GREEN       |
|                                |
+--------------------------------+
```

---

## Customization
You can modify the program to:
- Add more spheres to the scene.
- Change the colors, positions, or sizes of the spheres.
- Implement shading, reflections, or shadows for more realistic rendering.

---

## Troubleshooting
- **Java Not Found**: Ensure Java is installed and added to your system's PATH.
  - Check Java version: `java -version`
- **Compilation Errors**: Ensure the `RayTracer.java` file is in the correct directory and has no syntax errors.
- **No Display**: If the window does not open, ensure your system supports Java's GUI libraries.

---

## License
This project is open-source and available under the MIT License. Feel free to use, modify, and distribute it.

---

## Author
[Your Name]

---

Let me know if you need further assistance!