package hk.raytracemie;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class RenderMieTransparency extends Renderer {

    public RenderMieTransparency() {
        super();
    }

    @Override
    public void generateSimulation() {
        double startParticleSize = NanoParticle.radiusInMeters;
        double endParticleSize = startParticleSize;
        double startRefractiveIndexReal = NanoParticle.refractiveIndex.getReal();
        double endRefractiveIndexReal = startRefractiveIndexReal + 0.00000;
        double refractiveIndexImag = NanoParticle.refractiveIndex.getImaginary();

//        Vector3 startPosition = new Vector3(0, 0, 0);
//        Vector3 endPosition = startPosition.clone();

        Vector3 startPosition = new Vector3(-startParticleSize / 2.5, -startParticleSize / 2, 0);
        Vector3 endPosition = new Vector3(startParticleSize / 2.5, startParticleSize / 2, 0);

        DecimalFormat df = new DecimalFormat("0.000000E0");

        for (int frame = 0; frame < numFrames; frame++) {
            double t = (double) frame / (numFrames - 1);
            double particleSizeInMeters = startParticleSize + t * (endParticleSize - startParticleSize);
            double refractiveIndexReal = startRefractiveIndexReal + t * (endRefractiveIndexReal - startRefractiveIndexReal);

            // Interpolate the sphere's position
            Vector3 currentPosition = startPosition.multiply(1 - t).add(endPosition.multiply(t));

            OrthoCamera camera = new OrthoCamera(
                    new Vector3(0, 0, 10),
                    new Vector3(0, 0, 0),
                    new Vector3(0, 1, 0),
                    particleSizeInMeters * 4,
                    imageWidth,
                    imageHeight
            );

            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

            MieTransparentSphere mieTransparentSphere = new MieTransparentSphere(
                    currentPosition,
                    particleSizeInMeters,
                    new Color(1.0, 1.0, 1.0),
                    refractiveIndexReal,
                    refractiveIndexImag
            );

            Plane backgroundPlane = new Plane(
                    new Vector3(0, 0, -1),
                    new Vector3(0, 0, 1),
                    particleSizeInMeters * 1e8,
                    new Color(1.0, 1.0, 1.0)
            );

            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    double u = (double) x / (imageWidth - 1);
                    double v = (double) y / (imageHeight - 1);
                    Ray ray = camera.getRay(u, v);

                    RayIntersect record = new RayIntersect();
                    switch (mieTransparentSphere.hit(ray, 0, Double.MAX_VALUE, record)) {
                        case HIT:
                            Color finalColor = new Color(0.0, 0.0, 0.0);
                            if (record.refractedRays[0] != null) {
                                // Sample the procedural pattern at the refracted position
                                Vector3 refractedPointRed = backgroundPlane.intersect(record.refractedRays[0]);
                                if (refractedPointRed != null) {
                                    Color backgroundColorRed = ProceduralPatternMaker.samplePerlinNoisePattern(refractedPointRed, particleSizeInMeters);
                                    backgroundColorRed.g = 0;
                                    backgroundColorRed.b = 0;
                                    finalColor.add(backgroundColorRed);
                                }
                                Vector3 refractedPointGreen = backgroundPlane.intersect(record.refractedRays[1]);
                                if (refractedPointGreen != null) {
                                    Color backgroundColorGreen = ProceduralPatternMaker.samplePerlinNoisePattern(refractedPointGreen, particleSizeInMeters);
                                    backgroundColorGreen.r = 0;
                                    backgroundColorGreen.b = 0;
                                    finalColor.add(backgroundColorGreen);
                                }
                                Vector3 refractedPointBlue = backgroundPlane.intersect(record.refractedRays[2]);
                                if (refractedPointBlue != null) {
                                    Color backgroundColorBlue = ProceduralPatternMaker.samplePerlinNoisePattern(refractedPointBlue, particleSizeInMeters);
                                    backgroundColorBlue.r = 0;
                                    backgroundColorBlue.g = 0;
                                    finalColor.add(backgroundColorBlue);
                                }
                            } else {
                                finalColor = new Color(0, 0, 0);
                            }
                            image.setRGB(x, imageHeight - 1 - y, finalColor.toInt());
                            break;

                        case INTERNAL_REFLECTION:
                            image.setRGB(x, imageHeight - 1 - y, 0xff00ff);
                            break;

                        default:
                        case MISSED:
                            Vector3 backgroundPoint = backgroundPlane.intersect(ray);
                            Color backgroundColor = ProceduralPatternMaker.samplePerlinNoisePattern(backgroundPoint, particleSizeInMeters);
                            image.setRGB(x, imageHeight - 1 - y, backgroundColor.toInt());
                            break;
                    }
                }
            }

            String particleSizeText = df.format(particleSizeInMeters * 1e6);
            String refractiveIndexText = df.format(refractiveIndexReal);
            addTextOverlay(image, particleSizeText, refractiveIndexText, new DirectionalLight(new Vector3(-1, 0, 0), new Color(1.0, 1.0, 1.0)));

            String outputImageFileName = String.format("mie_transparent_output_%03d.png", frame);
            saveImage(image, outputImageFileName);
        }

        if (numFrames > 10) {
            createVideo("mie_transparent_output", numFrames);
        }
    }
}
