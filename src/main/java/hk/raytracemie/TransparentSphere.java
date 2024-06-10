package hk.raytracemie;

public class TransparentSphere extends Sphere {

    private final double refractiveIndex;

    public TransparentSphere(Vector3 center, double radius, Color color, double refractiveIndex) {
        super(center, radius, color);
        this.refractiveIndex = refractiveIndex;
    }

    public RayIntersect.RayHitStatus hit(Ray ray, double tMin, double tMax, RayIntersect record) {
        if (super.hit(ray, tMin, tMax, record) == RayIntersect.RayHitStatus.HIT) {
            Vector3 normal = record.normal.clone();
            Vector3 incomingDirection = ray.direction.clone();
            double cosThetaI = -incomingDirection.dot(normal);
            double eta = 1.0 / refractiveIndex;

            if (cosThetaI < 0) {
                // Ray is exiting the sphere
                cosThetaI = -cosThetaI;
                normal = normal.negate();
                eta = refractiveIndex;
            }

            double cosThetaT2 = 1.0 - eta * eta * (1.0 - cosThetaI * cosThetaI);

            if (cosThetaT2 > 0) {
                // Refraction at the entry/exit point
                Vector3 refractedDirection = incomingDirection.multiply(eta)
                        .add(normal.multiply(eta * cosThetaI - Math.sqrt(cosThetaT2)))
                        .normalize();
                Ray refractedRay = new Ray(record.point, refractedDirection);
                RayIntersect exitRecord = new RayIntersect();

                if (super.hit(refractedRay, tMin, tMax, exitRecord) == RayIntersect.RayHitStatus.HIT) {
                    Vector3 exitNormal = exitRecord.normal;
                    double cosThetaI2 = -refractedRay.direction.dot(exitNormal);
                    double eta2 = refractiveIndex;

                    if (cosThetaI2 < 0) {
                        // Ray is exiting the sphere again
                        cosThetaI2 = -cosThetaI2;
                        exitNormal = exitNormal.negate();
                        eta2 = 1.0 / refractiveIndex;
                    }

                    double cosThetaT22 = 1.0 - eta2 * eta2 * (1.0 - cosThetaI2 * cosThetaI2);

                    if (cosThetaT22 > 0) {
                        // Refraction at the exit point
                        Vector3 exitRefractedDirection = refractedRay.direction.multiply(eta2)
                                .add(exitNormal.multiply(eta2 * cosThetaI2 - Math.sqrt(cosThetaT22)))
                                .normalize();
                        record.refractedRays[0] = new Ray(exitRecord.point, exitRefractedDirection);
                        return RayIntersect.RayHitStatus.HIT;
                    } else {
                        // Total internal reflection at the exit point
                        return RayIntersect.RayHitStatus.INTERNAL_REFLECTION;
                    }
                } else {
                    return RayIntersect.RayHitStatus.MISSED; // Ray missed the sphere on exit
                }
            } else {
                // Total internal reflection at the entry point
                return RayIntersect.RayHitStatus.EXTERNAL_REFLECTION;
            }
        }
        return RayIntersect.RayHitStatus.MISSED;
    }
}
