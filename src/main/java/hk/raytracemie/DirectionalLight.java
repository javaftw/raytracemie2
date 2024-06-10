package hk.raytracemie;

public class DirectionalLight {
    public static final double redWavelength = 6.628e-7;
    public static final double greenWavelength = 5.255e-7;
    public static final double blueWavelength = 4.75e-7;

    public Vector3 direction;
    public Color color;

    public DirectionalLight(Vector3 direction, Color color) {
        this.direction = direction.normalize();
        this.color = color;
    }


    public double getRedIntensity() {
        return this.color.r;
    }

    public double getGreenIntensity() {
        return this.color.g;
    }

    public double getBlueIntensity() {
        return this.color.b;
    }
}
