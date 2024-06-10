/*
 */
package hk.raytracemie;

/**
 *
 * @author User
 */
public class ProceduralPatternMaker {

    public ProceduralPatternMaker() {
    }

    public static Color sampleProceduralPattern(Vector3 point, double scale) {
        // Simple procedural pattern: color based on sine functions
        double r = 0.5 * (1 + Math.sin(point.x * scale));
        double g = 0.5 * (1 + Math.sin(point.y * scale));
        double b = 0.5 * (1 + Math.sin(point.z * scale));
        return new Color(r, g, b);
    }

    public static Color samplePerlinNoisePattern(Vector3 point, double scale) {
        scale *= 1e13;
        double noiseValue = PerlinNoise.noise(point.x * scale, point.y * scale, point.z * scale);
        double r = 0.5 * (1 + Math.sin(noiseValue * Math.PI));
        double g = 0.5 * (1 + Math.sin(noiseValue * Math.PI + 2.0));
        double b = 0.5 * (1 + Math.sin(noiseValue * Math.PI + 4.0));
        return new Color(r, g, b);
    }

    public static Color sampleCheckerboardPattern(Vector3 point, double scale) {
        int check = (int) (Math.floor(point.x * scale) + Math.floor(point.y * scale) + Math.floor(point.z * scale)) % 2;
        return check == 0 ? new Color(1.0, 1.0, 1.0) : new Color(0.0, 0.0, 0.0);
    }


    public static Color sampleVoronoiPattern(Vector3 point, double scale) {
        double internalScale = 9.372585e7;
        double[] distances = Voronoi.distances(point.x * scale * internalScale, point.y * scale * internalScale, point.z * scale * internalScale);
        double r = distances[0]/25;
        double g = distances[1]/23;
        double b = distances[2]/21;
        return new Color(r, g, b);
    }

    public static Color sampleProceduralGridPattern(Vector3 point, double scale) {
        double innerScale = 2.0;
        int tileX = (int) Math.floor(point.x / scale * innerScale);
        int tileY = (int) Math.floor(point.y / scale * innerScale);

        if (tileY % 2 != 0) {
            tileX += 1;
        }

        int tileIndex = Math.floorMod(tileX, 5);
        switch (tileIndex) {
            case 0:
                return new Color(0.80, 0.6, 0.0); // Orange
            case 1:
                return new Color(1.0, 1.0, 1.0); // White
            case 2:
                return new Color(1.0, 0.0, 0.0); // Red
            case 3:
                return new Color(0.0, 1.0, 0.0); // Green
            case 4:
                return new Color(0.0, 0.0, 1.0); // Blue
            default:
                return new Color(0.5,0.5,0.5); // Default to white if something goes wrong
        }
    }

}
