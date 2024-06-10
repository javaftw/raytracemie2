package hk.raytracemie;

public class Plane {
    public Vector3 position;
    public Vector3 normal;
    public double size;
    public Color color;

    public Plane(Vector3 position, Vector3 normal, double size, Color color) {
        this.position = position;
        this.normal = normal.normalize();
        this.size = size;
        this.color = color;
    }

    public Vector3 intersect(Ray ray) {
        double denom = normal.dot(ray.direction);
        if (Math.abs(denom) > 1e-6) { // Ensure the ray is not parallel to the plane
            double t = position.subtract(ray.origin).dot(normal) / denom;
            Vector3 hitPoint = ray.at(t);
            Vector3 localHitPoint = hitPoint.subtract(position);
            // Check if the hit point is within the bounds of the plane
            if (Math.abs(localHitPoint.x) <= size / 2 && Math.abs(localHitPoint.y) <= size / 2) {
                return hitPoint;
            }
        }
        return null;
    }
}
