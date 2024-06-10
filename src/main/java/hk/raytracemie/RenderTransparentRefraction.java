package hk.raytracemie;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class RenderTransparentRefraction extends Renderer {

    DecimalFormat df = new DecimalFormat("0.000000E0");

    public RenderTransparentRefraction() {
        super();
    }

    @Override
    public void generateSimulation() {
        double startParticleSize = NanoParticle.radiusInMeters;
        double endParticleSize = NanoParticle.radiusInMeters;
        double startRefractiveIndexReal = NanoParticle.refractiveIndex.getReal();
        double endRefractiveIndexReal = NanoParticle.refractiveIndex.getReal();// + 2.00001;

        // Define the start and end positions of the sphere on the yz-plane
        Vector3 startPosition = new Vector3(-startParticleSize / 2.5, -startParticleSize / 2, 0);
        Vector3 endPosition = new Vector3(startParticleSize / 2.5, startParticleSize / 2, 0);

        for (int frame = 0; frame < numFrames; frame++) {
            double t = (double) frame / (numFrames - 1);
            double particleSizeInMeters = startParticleSize + t * (endParticleSize - startParticleSize);
            double refractiveIndex = startRefractiveIndexReal + t * (endRefractiveIndexReal - startRefractiveIndexReal);

            // Interpolate the sphere's position
            Vector3 currentPosition = startPosition.multiply(1 - t).add(endPosition.multiply(t));

            OrthoCamera camera = new OrthoCamera(
                    new Vector3(0, 0, 10),
                    new Vector3(0, 0, 0),
                    new Vector3(0, 1, 0),
                    particleSizeInMeters * 3,
                    imageWidth,
                    imageHeight
            );

            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

            TransparentSphere transparentSphere = new TransparentSphere(
                    currentPosition,
                    particleSizeInMeters,
                    new Color(1.0, 1.0, 1.0),
                    refractiveIndex
            );

            Plane backgroundPlane = new Plane(
                    new Vector3(0, 0, -1),
                    new Vector3(0, 0, 1),
                    particleSizeInMeters * 100, // Ensure this covers the entire viewable area
                    new Color(1.0, 1.0, 1.0) // Not used, but included for completeness
            );

            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    double u = (double) x / (imageWidth - 1);
                    double v = (double) y / (imageHeight - 1);
                    Ray ray = camera.getRay(u, v);

                    RayIntersect record = new RayIntersect();
                    switch (transparentSphere.hit(ray, particleSizeInMeters / 1000, Double.MAX_VALUE, record)) {
                        case HIT:
                            if (record.refractedRays[0] != null) {
                                // Sample the procedural pattern at the refracted position
                                Vector3 refractedPoint = backgroundPlane.intersect(record.refractedRays[0]);
                                if (refractedPoint != null) {
                                    Color refractedColor = ProceduralPatternMaker.sampleProceduralGridPattern(refractedPoint, particleSizeInMeters);
                                    image.setRGB(x, imageHeight - 1 - y, refractedColor.multiply(0.95).toInt());
                                } else {
                                    image.setRGB(x, imageHeight - 1 - y, 0);
                                }
                            } else {
                                image.setRGB(x, imageHeight - 1 - y, 0xffff00);
                            }
                            break;

                        case INTERNAL_REFLECTION:
                            image.setRGB(x, imageHeight - 1 - y, 0xff00ff);
                            break;

                        case EXTERNAL_REFLECTION:
                            image.setRGB(x, imageHeight - 1 - y, 0xd0d0f0);
                            break;

                        default:
                        case MISSED:
                            // Sample the procedural pattern at the original ray direction
                            Vector3 backgroundPoint = backgroundPlane.intersect(ray);
                            Color backgroundColor = ProceduralPatternMaker.sampleProceduralGridPattern(backgroundPoint, particleSizeInMeters);
                            image.setRGB(x, imageHeight - 1 - y, backgroundColor.toInt());
                            break;
                    }
                }
            }

            String particleSizeText = df.format(particleSizeInMeters * 1e6);
            String refractiveIndexText = df.format(refractiveIndex);
            addTextOverlay(image, particleSizeText, refractiveIndexText, new DirectionalLight(new Vector3(-1, 0, 0), new Color(1.0, 1.0, 1.0)));

            String outputImageFileName = String.format("transparent_output_%03d.png", frame);
            saveImage(image, outputImageFileName);
        }

        if (numFrames > 10) {
            createVideo("transparent_output", numFrames);
        }
    }
}
