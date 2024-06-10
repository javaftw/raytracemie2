package hk.raytracemie;

public class Sphere {
    public Vector3 center;
    public double radius;
    public Color color;

    public Sphere(Vector3 center, double radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    public RayIntersect.RayHitStatus hit(Ray ray, double tMin, double tMax, RayIntersect record) {
        Vector3 oc = ray.origin.subtract(center);
        double a = ray.direction.dot(ray.direction);
        double b = oc.dot(ray.direction);
        double c = oc.dot(oc) - radius * radius;
        double discriminant = b * b - a * c;
        if (discriminant > 0) {
            double temp = (-b - Math.sqrt(discriminant)) / a;
            if (temp < tMax && temp > tMin) {
                record.t = temp;
                record.point = ray.at(temp);
                record.normal = record.point.subtract(center).multiply(1.0 / radius);
                return RayIntersect.RayHitStatus.HIT;
            }
            temp = (-b + Math.sqrt(discriminant)) / a;
            if (temp < tMax && temp > tMin) {
                record.t = temp;
                record.point = ray.at(temp);
                record.normal = record.point.subtract(center).multiply(1.0 / radius);
                return RayIntersect.RayHitStatus.HIT;
            }
        }
        return RayIntersect.RayHitStatus.MISSED;
    }
}