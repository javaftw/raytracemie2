package hk.raytracemie;

import java.util.Random;

public class Voronoi {
    private static final int NUM_POINTS = 16;
    private static final Vector3[] points = new Vector3[NUM_POINTS];

    static {
        Random random = new Random();
        for (int i = 0; i < NUM_POINTS; i++) {
            points[i] = new Vector3(random.nextDouble(), random.nextDouble(), random.nextDouble());
        }
    }

    public static double[] distances(double x, double y, double z) {
        double[] distances = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            distances[i] = points[i].distance(new Vector3(x, y, z));
        }
        java.util.Arrays.sort(distances);
        return distances;
    }
}
