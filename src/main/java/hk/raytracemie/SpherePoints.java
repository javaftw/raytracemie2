/*
 */
package hk.raytracemie;

public class SpherePoints {
    public static Vector3[] generateFibonacciLattice(int numPoints, double radius) {
    Vector3[] points = new Vector3[numPoints];
    double phi = Math.PI * (3.0 - Math.sqrt(5.0)); // Golden angle in radians
    for (int i = 0; i < numPoints; i++) {
        double y = 1 - (i / (double)(numPoints - 1)) * 2; // y goes from 1 to -1
        double radiusAtY = Math.sqrt(1 - y * y); // Radius at y
        double theta = phi * i; // Golden angle increment

        double x = Math.cos(theta) * radiusAtY;
        double z = Math.sin(theta) * radiusAtY;

        points[i] = new Vector3(x * radius, y * radius, z * radius);
    }
    return points;
}


}
