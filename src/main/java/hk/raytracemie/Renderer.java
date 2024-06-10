package hk.raytracemie;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;

public abstract class Renderer {
    protected int imageWidth = 600;
    protected int imageHeight = imageWidth;
    protected int numFrames = 30;

    public Renderer() {}

    protected void saveImage(BufferedImage image, String filename) {
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            System.err.println("Error: Unable to save the image.");
            e.printStackTrace();
        }
    }

    protected void createVideo(String imagePrefix, int numFrames) {
        String outputVideoFileName = imagePrefix + ".mp4";
        try {
            File check = new File(outputVideoFileName);
            if (check.exists()) {
                check.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:\\Users\\User\\Downloads\\ffmpeg-master-latest-win64-gpl\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe",
                    "-r", "30",
                    "-f", "image2",
                    "-i", imagePrefix + "_%03d.png",
                    "-vcodec", "libx264",
                    "-crf", "25",
                    "-pix_fmt", "yuv420p",
                    outputVideoFileName
            );
            processBuilder.inheritIO(); // To see FFmpeg output in console
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void addTextOverlay(BufferedImage image, String particleSizeText, String refractiveIndexText, DirectionalLight light) {
        DecimalFormat df = new DecimalFormat("0.000000E0");
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(java.awt.Color.WHITE);

        g2d.drawString("Particle diameter (m): " + particleSizeText, 5, 15);
        g2d.drawString("Refractive Index: " + refractiveIndexText, 5, 30);
        g2d.drawString("Wavelengths (m):", 5, 45);
        g2d.drawString("Red: " + df.format(light.redWavelength) + " Intensity: " + light.getRedIntensity(), 5, 60);
        g2d.drawString("Green: " + df.format(light.greenWavelength) + " Intensity: " + light.getGreenIntensity(), 5, 75);
        g2d.drawString("Blue: " + df.format(light.blueWavelength) + " Intensity: " + light.getBlueIntensity(), 5, 90);
        g2d.dispose();
    }

    public abstract void generateSimulation();
}
