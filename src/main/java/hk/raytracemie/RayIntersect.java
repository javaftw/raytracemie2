package hk.raytracemie;

public class RayIntersect {
    public double t;
    public Vector3 point;
    public Vector3 normal;
    public Color modifiedColor;
    public boolean isScatterPoint;
    public Vector3 scatterDirection;
    public Ray[] refractedRays = new Ray[3];
    
    public static enum RayHitStatus {
        MISSED,
        HIT,
        INTERNAL_REFLECTION,
        EXTERNAL_REFLECTION
    }

    public RayIntersect() {
        this.t = 0.0;
        this.point = new Vector3(0, 0, 0);
        this.normal = new Vector3(0, 0, 0);
        this.modifiedColor = new Color(0, 0, 0);
        this.scatterDirection = new Vector3(0, 0, 0);
        this.isScatterPoint = false;
        
    }
    
    
}