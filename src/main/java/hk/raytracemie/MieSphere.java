package hk.raytracemie;

public class MieSphere extends Sphere {

    private final MieScattering redScattering;
    private final MieScattering greenScattering;
    private final MieScattering blueScattering;

    private double redScatteringEfficiency;
    private double greenScatteringEfficiency;
    private double blueScatteringEfficiency;

    private double redAbsorptionEfficiency;
    private double greenAbsorptionEfficiency;
    private double blueAbsorptionEfficiency;
    private final double tMin, tMax; 
    
    private final DirectionalLight dLight;

    public MieSphere(Vector3 center, double radius, Color color, double refractiveIndexReal, double refractiveIndexImag, DirectionalLight light) {
        super(center, radius, color);
        this.dLight = light;
        this.tMin =  radius / 100;
        this.tMax = 10;
        this.redScattering = new MieScattering(radius, DirectionalLight.redWavelength, refractiveIndexReal, refractiveIndexImag);
        this.greenScattering = new MieScattering(radius, DirectionalLight.greenWavelength, refractiveIndexReal, refractiveIndexImag);
        this.blueScattering = new MieScattering(radius, DirectionalLight.blueWavelength, refractiveIndexReal, refractiveIndexImag);

        // Calculate scattering and absorption efficiencies
        this.redScatteringEfficiency = redScattering.calculateScatteringEfficiency();
        this.greenScatteringEfficiency = greenScattering.calculateScatteringEfficiency();
        this.blueScatteringEfficiency = blueScattering.calculateScatteringEfficiency();

        this.redAbsorptionEfficiency = redScattering.calculateAbsorptionEfficiency();
        this.greenAbsorptionEfficiency = greenScattering.calculateAbsorptionEfficiency();
        this.blueAbsorptionEfficiency = blueScattering.calculateAbsorptionEfficiency();
    }

    public boolean hit(Ray ray, RayIntersect record) {
        if (super.hit(ray, tMin, tMax, record) == RayIntersect.RayHitStatus.HIT) {
            if (dLight != null) {
                Vector3 incomingDirection = dLight.direction.negate().normalize();
                double cosTheta = record.normal.dot(incomingDirection);

                // Calculate phase functions for RGB components
                double phaseFunctionRed = redScattering.calculatePhaseFunction(cosTheta);
                double phaseFunctionGreen = greenScattering.calculatePhaseFunction(cosTheta);
                double phaseFunctionBlue = blueScattering.calculatePhaseFunction(cosTheta);

                // Calculate scattered color based on light intensities and efficiencies
                double redIntensity = dLight.getRedIntensity() * phaseFunctionRed * redScatteringEfficiency * (1 - redAbsorptionEfficiency);
                double greenIntensity = dLight.getGreenIntensity() * phaseFunctionGreen * greenScatteringEfficiency * (1 - greenAbsorptionEfficiency);
                double blueIntensity = dLight.getBlueIntensity() * phaseFunctionBlue * blueScatteringEfficiency * (1 - blueAbsorptionEfficiency);

                Color scatteredColor = new Color(
                        this.color.r * redIntensity,
                        this.color.g * greenIntensity,
                        this.color.b * blueIntensity
                );

                // Calculate illumination using Phong reflection model
                double diffuseFactor = Math.max(0, record.normal.dot(dLight.direction));
                Color diffuse = dLight.color.multiply(diffuseFactor);

                // Combine scattered color and illumination
                Color finalColor = scatteredColor;
                //finalColor.add(diffuse);

                record.modifiedColor = finalColor;
            }
            return true;
        }
        return false;
    }

    public Color getSurfaceLight(Vector3 samplePoint) {
        Vector3 direction = samplePoint.subtract(this.center).normalize();
        Ray ray = new Ray(center, direction);
        RayIntersect record = new RayIntersect();

        if (hit(ray, record)) {
            return record.modifiedColor;
        } else {
            return new Color(0.0, 0.0, 0.0); // No light if the ray doesn't hit the sphere
        }
    }
}
