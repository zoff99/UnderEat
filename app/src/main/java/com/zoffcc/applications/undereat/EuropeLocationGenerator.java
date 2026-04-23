package com.zoffcc.applications.undereat;

import java.util.Random;

public class EuropeLocationGenerator {
    // Rough bounding box for Mainland Europe
    private static final double MIN_LAT = 36.0;
    private static final double MAX_LAT = 70.0;
    private static final double MIN_LON = -10.0;
    private static final double MAX_LON = 40.0;

    public static void main(String[] args) {
        double[] coordinates = generateRandomEuropeLocation();
        System.out.printf("Random Location: Latitude %.6f, Longitude %.6f%n",
                          coordinates[0], coordinates[1]);
    }

    /**
     * Generates a random coordinate within the European bounding box.
     * Note: This may include some sea areas. For 100% mainland accuracy,
     * use a GeoJSON library to check if the point is within a "Europe" polygon.
     */
    public static double[] generateRandomEuropeLocation() {
        Random random = new Random();

        double lat = MIN_LAT + (MAX_LAT - MIN_LAT) * random.nextDouble();
        double lon = MIN_LON + (MAX_LON - MIN_LON) * random.nextDouble();

        return new double[]{lat, lon};
    }
}