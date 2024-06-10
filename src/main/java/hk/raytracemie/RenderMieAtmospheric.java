package hk.raytracemie;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class RenderMieAtmospheric extends Renderer {

    public RenderMieAtmospheric() {
        super();
    }

    @Override
    public void generateSimulation() {

        double startParticleSize = NanoParticle.radiusInMeters;
        double endParticleSize = startParticleSize + 1.50e-8;

        double startRefractiveIndex = NanoParticle.refractiveIndex.getReal();
        double endRefractiveIndex = startRefractiveIndex + 0;

        double particleSizeInMeters = startParticleSize;
        double zoomScale = 5 * startParticleSize;
        Vector3 startCameraPosition = new Vector3(1,1,1).multiply(zoomScale);
        //Vector3 endCameraPosition = new Vector3(zoomScale * 2 * particleSizeInMeters, zoomScale * 0.75 * particleSizeInMeters, zoomScale * 0.5 * particleSizeInMeters);
        Vector3 endCameraPosition = startCameraPosition.clone().add(new Vector3(0, 0, 0));

        DirectionalLight light = new DirectionalLight(
                new Vector3(-1, 0, 0),
                new Color(1.0, 1.0, 1.0)
        );

        DecimalFormat df = new DecimalFormat("0.000000E0");

        double atmosphereDepth = 2 * zoomScale;
        Atmosphere atmosphere = new Atmosphere(0.015, 1.0e5, atmosphereDepth / 40);

        for (int frame = 0; frame < numFrames; frame++) {
            double t = (double) frame / (numFrames - 1);

            // Interpolate particle size
            particleSizeInMeters = startParticleSize + t * (endParticleSize - startParticleSize);

            // Interpolate refractive index
            double refractiveIndex = startRefractiveIndex + t * (endRefractiveIndex - startRefractiveIndex);

            // Interpolate camera position
            Vector3 cameraPosition = startCameraPosition.multiply(1 - t).add(endCameraPosition.multiply(t));

            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

            Camera camera = new Camera(
                    cameraPosition,
                    new Vector3(0, 0, 0),
                    new Vector3(0, 1, 0),
                    60,
                    imageWidth,
                    imageHeight
            );

            MieSphere mieSphere = new MieSphere(
                    new Vector3(0, 0, 0),
                    particleSizeInMeters,
                    new Color(1.0, 1.0, 1.0),
                    refractiveIndex,
                    NanoParticle.refractiveIndex.getImaginary(),
                    light
            );

            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    double u = (double) x / (imageWidth - 1);
                    double v = (double) y / (imageHeight - 1);
                    Ray ray = camera.getRay(u, v);

                    RayIntersect record = new RayIntersect();
                    if (mieSphere.hit(ray, record)) {
                        Color pixelColor = record.modifiedColor;
                        image.setRGB(x, imageHeight - 1 - y, pixelColor.toInt());
                    } else {
                        Color atmosphereColor = atmosphere.sampleAtmosphere(ray, mieSphere, light, atmosphereDepth);
                        image.setRGB(x, imageHeight - 1 - y, atmosphereColor.toInt());
                    }
                }
            }

            String particleSizeText = df.format(particleSizeInMeters * 2);
            String refractiveIndexText = df.format(refractiveIndex);
            addTextOverlay(image, particleSizeText, refractiveIndexText, light);

            String outputImageFileName = String.format("output_%03d.png", frame);
            saveImage(image, outputImageFileName);
        }

        // Invoke FFmpeg to create a video from the image sequence if it is longer than 10 frames
        if (numFrames > 10) {
            createVideo("output", numFrames);
        }
    }
}
