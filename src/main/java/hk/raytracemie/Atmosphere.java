package hk.raytracemie;

public class Atmosphere {
    private double scatteringCoefficient;
    private double absorptionCoefficient;
    private double samplingInterval;

    public Atmosphere(double scatteringCoefficient, double absorptionCoefficient, double samplingInterval) {
        this.scatteringCoefficient = scatteringCoefficient;
        this.absorptionCoefficient = absorptionCoefficient;
        this.samplingInterval = samplingInterval;
    }

    public Color sampleAtmosphere(Ray ray, MieSphere sphere, DirectionalLight light, double maxDistance) {
        Color accumulatedColor = new Color(0.0, 0.0, 0.0);
        double distance = 0.0;

        while (distance < maxDistance) {
            Vector3 samplePoint = ray.at(distance);
            RayIntersect tempRecord = new RayIntersect();

            // Check if the light is blocked by the sphere
            boolean blocked = sphere.hit(ray, tempRecord);

            if (!blocked) {
                Color lightContribution = new Color(0.0, 0.0, 0.0);
                    Ray lightRay = new Ray(samplePoint, light.direction);
                    RayIntersect lightIntersect = new RayIntersect();
                    if (!sphere.hit(lightRay, lightIntersect)) {
                        //lightContribution.add(light.color);
                    }
                
                
                // Add surface light contribution
                Color surfaceLight = sphere.getSurfaceLight(samplePoint);
                lightContribution.add(surfaceLight);

                accumulatedColor.add(lightContribution.multiply(scatteringCoefficient));
            } else {
                break;
            }

            distance += samplingInterval;
        }

        return accumulatedColor.multiply(Math.exp(-absorptionCoefficient * distance));
    }
}
