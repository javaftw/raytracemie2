package hk.raytracemie;

public class MieTransparentSphere extends Sphere {

    private final MieScattering redScattering;
    private final MieScattering greenScattering;
    private final MieScattering blueScattering;

    private double redScatteringEfficiency;
    private double greenScatteringEfficiency;
    private double blueScatteringEfficiency;

    private double redAbsorptionEfficiency;
    private double greenAbsorptionEfficiency;
    private double blueAbsorptionEfficiency;

    public MieTransparentSphere(Vector3 center, double radius, Color color, double refractiveIndexReal, double refractiveIndexImag) {
        super(center, radius, color);
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

    @Override
    public RayIntersect.RayHitStatus hit(Ray ray, double tMin, double tMax, RayIntersect record) {
        RayIntersect.RayHitStatus rayStatus = super.hit(ray, tMin, tMax, record);
        if (rayStatus == RayIntersect.RayHitStatus.HIT) {
            Vector3 incomingDirection = ray.direction.clone();
            Vector3 normal = record.normal.clone();
            double cosTheta = -incomingDirection.dot(normal);

            // Calculate phase functions for RGB components
            double phaseFunctionRed = redScattering.calculatePhaseFunction(cosTheta);
            double phaseFunctionGreen = greenScattering.calculatePhaseFunction(cosTheta);
            double phaseFunctionBlue = blueScattering.calculatePhaseFunction(cosTheta);

            // Calculate scattered color based on light intensities and efficiencies
            double redIntensity = phaseFunctionRed * redScatteringEfficiency * (1 - redAbsorptionEfficiency);
            double greenIntensity = phaseFunctionGreen * greenScatteringEfficiency * (1 - greenAbsorptionEfficiency);
            double blueIntensity = phaseFunctionBlue * blueScatteringEfficiency * (1 - blueAbsorptionEfficiency);

            // Calculate the refracted directions for RGB components
            double etaRed = 1.0 / redScattering.getRefractiveIndex().getReal();
            double etaGreen = 1.0 / greenScattering.getRefractiveIndex().getReal();
            double etaBlue = 1.0 / blueScattering.getRefractiveIndex().getReal();

            if (cosTheta < 0) {
                etaRed = redScattering.getRefractiveIndex().getReal();
                etaGreen = greenScattering.getRefractiveIndex().getReal();
                etaBlue = blueScattering.getRefractiveIndex().getReal();
                normal = normal.clone().negate();
                cosTheta = -cosTheta;
            }

            // Calculate refraction for red wavelength
            double cosThetaT2Red = 1.0 - etaRed * etaRed * (1.0 - cosTheta * cosTheta);
            if (cosThetaT2Red > 0) {
                Vector3 refractedDirectionRed = incomingDirection.clone().multiply(etaRed)
                        .add(normal.clone().multiply(etaRed * cosTheta - Math.sqrt(cosThetaT2Red))).normalize();
                record.refractedRays[0] = new Ray(record.point, refractedDirectionRed);
            }

            // Calculate refraction for green wavelength
            double cosThetaT2Green = 1.0 - etaGreen * etaGreen * (1.0 - cosTheta * cosTheta);
            if (cosThetaT2Green > 0) {
                Vector3 refractedDirectionGreen = incomingDirection.clone().multiply(etaGreen)
                        .add(normal.clone().multiply(etaGreen * cosTheta - Math.sqrt(cosThetaT2Green))).normalize();
                record.refractedRays[1] = new Ray(record.point, refractedDirectionGreen);
            }

            // Calculate refraction for blue wavelength
            double cosThetaT2Blue = 1.0 - etaBlue * etaBlue * (1.0 - cosTheta * cosTheta);
            if (cosThetaT2Blue > 0) {
                Vector3 refractedDirectionBlue = incomingDirection.clone().multiply(etaBlue)
                        .add(normal.clone().multiply(etaBlue * cosTheta - Math.sqrt(cosThetaT2Blue))).normalize();
                record.refractedRays[2] = new Ray(record.point, refractedDirectionBlue);
            }

            // Combine the scattered and refracted light intensities
            Color scatteredColor = new Color(
                    this.color.r * redIntensity,
                    this.color.g * greenIntensity,
                    this.color.b * blueIntensity
            );

            record.modifiedColor = scatteredColor;
        }
        return rayStatus;
    }
}
