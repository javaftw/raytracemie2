package hk.raytracemie;

public class Camera {
    public Vector3 origin;
    public Vector3 lowerLeftCorner;
    public Vector3 horizontal;
    public Vector3 vertical;
    public Vector3 u, v, w;

    public Camera(Vector3 position, Vector3 lookAt, Vector3 up, double vfov, int imageWidth, int imageHeight) {
        double theta = Math.toRadians(vfov);
        double h = Math.tan(theta / 2);
        double viewportHeight = 2.0 * h;
        double viewportWidth = (double) imageWidth / imageHeight * viewportHeight;

        w = position.subtract(lookAt).normalize();
        u = up.cross(w).normalize();
        v = w.cross(u);

        origin = position;
        horizontal = u.multiply(viewportWidth);
        vertical = v.multiply(viewportHeight);
        lowerLeftCorner = origin.subtract(horizontal.multiply(0.5)).subtract(vertical.multiply(0.5)).subtract(w);
    }

    public Ray getRay(double s, double t) {
        return new Ray(origin, lowerLeftCorner.add(horizontal.multiply(s)).add(vertical.multiply(t)).subtract(origin));
    }
}
