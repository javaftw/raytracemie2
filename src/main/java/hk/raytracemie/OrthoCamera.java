package hk.raytracemie;

public class OrthoCamera extends Camera {

    public OrthoCamera(Vector3 position, Vector3 lookAt, Vector3 up, double planeSize, int imageWidth, int imageHeight) {
        super(position, lookAt, up, 0, imageWidth, imageHeight);
        // Override the field of view setting in the base Camera class
        double viewportHeight = planeSize;
        double viewportWidth = (double) imageWidth / imageHeight * viewportHeight;

        w = position.subtract(lookAt).normalize();
        u = up.cross(w).normalize();
        v = w.cross(u);

        origin = position;
        horizontal = u.multiply(viewportWidth);
        vertical = v.multiply(viewportHeight);
        lowerLeftCorner = origin.subtract(horizontal.multiply(0.5)).subtract(vertical.multiply(0.5));
    }

    @Override
    public Ray getRay(double s, double t) {
        Vector3 rayOrigin = lowerLeftCorner.add(horizontal.multiply(s)).add(vertical.multiply(t));
        return new Ray(rayOrigin, new Vector3(0, 0, -1));
    }
}
