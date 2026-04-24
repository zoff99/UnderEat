package com.zoffcc.applications.undereat;

import java.util.Random;

public class GlobalLocationGenerator
{
    // Europe Boundaries
    private static final double EU_MIN_LAT = 36.0, EU_MAX_LAT = 70.0;
    private static final double EU_MIN_LON = -10.0, EU_MAX_LON = 40.0;

    // California Boundaries
    private static final double CA_MIN_LAT = 32.5, CA_MAX_LAT = 42.0;
    private static final double CA_MIN_LON = -124.4, CA_MAX_LON = -114.1;

    private static final Random random = new Random();

    public static void main(String[] args) {
        // Generate 5 random locations that could be in either place
        for (int i = 0; i < 5; i++) {
            double[] loc = generateLocationFromEither();
            System.out.printf("Point %d: %.6f, %.6f%n", i + 1, loc[0], loc[1]);
        }
    }

    /**
     * Randomly chooses between Europe and California, then generates a point.
     */
    public static double[] generateLocationFromEither() {
        // nextBoolean() effectively gives a 50/50 chance for each region
        if (random.nextBoolean()) {
            return generateRandomLocation(EU_MIN_LAT, EU_MAX_LAT, EU_MIN_LON, EU_MAX_LON);
        } else {
            return generateRandomLocation(CA_MIN_LAT, CA_MAX_LAT, CA_MIN_LON, CA_MAX_LON);
        }
    }

    public static double[] generateRandomLocation(double minLat, double maxLat, double minLon, double maxLon) {
        double lat = minLat + (maxLat - minLat) * random.nextDouble();
        double lon = minLon + (maxLon - minLon) * random.nextDouble();
        return new double[]{lat, lon};
    }
}
