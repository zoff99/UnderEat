package com.zoffcc.applications.undereat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Service;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import static com.zoffcc.applications.undereat.MainActivityKt.TAG;

public class GPSTracker extends Service implements LocationListener, SensorEventListener
{
    private final Context mContext;
    private double latitude = 0;
    private double longitude = 0;
    private float accuracy = 99999999;
    private double altitude = 0;
    private float bearing = 0;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10;
    private static final long MIN_TIME_BW_UPDATES = 0; // 1000 * 60 * 1;
    protected LocationManager locationManager;
    protected SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Sensor rotationsense;
    boolean haveSensor = false;
    boolean haveSensor2 = false;

    float[] rMat = new float[9];
    float[] orientation = new float[3];
    int mAzimuth = 0;
    String where = "NW";
    private final float[] mLastAccelerometer = new float[3];
    private final float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    public GPSTracker(Context context)
    {
        this.mContext = context;
        startUsingGPS();
    }

    public void startUsingGPS()
    {
        try
        {
            sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null)
            {
                if ((sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) ||
                    (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null))
                {
                    Log.i(TAG, "No Sensor available");
                }
                else
                {
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                    haveSensor = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                    haveSensor2 = sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
                }
            }
            else
            {
                rotationsense = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                haveSensor = sensorManager.registerListener(this, rotationsense, SensorManager.SENSOR_DELAY_UI);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "EE7:" + e.getMessage());
        }

        try
        {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            if (locationManager != null)
            {
                if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                                       MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "EE1:" + e.getMessage());
        }
    }

    public void stopUsingGPS() {
        try
        {
            if (locationManager != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                }
                locationManager.removeUpdates(GPSTracker.this);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "EE2.1:" + e.getMessage());
        }

        try
        {
            sensorManager.unregisterListener(this, accelerometer);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "EE2.2:" + e.getMessage());
        }

        try
        {
            sensorManager.unregisterListener(this, magnetometer);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "EE2.3:" + e.getMessage());
        }

        try
        {
            sensorManager.unregisterListener(this, rotationsense);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "EE2.4:" + e.getMessage());
        }


        try
        {
            sensorManager.unregisterListener(this);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "EE2.5:" + e.getMessage());
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @SuppressWarnings("unused")
    public float getAccuracy() {
        return accuracy;
    }

    @SuppressWarnings("unused")
    public double getAltitude() {
        return altitude;
    }

    @SuppressWarnings("unused")
    public float getBearing() {
        return bearing;
    }

    public double getHeading() {
        return mAzimuth;
    }

    static float computeDistanceAndBearing(double lat1, double lon1, double lat2, double lon2)
    {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        // Convert lat/long to radians
        lat1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lon1 *= Math.PI / 180.0;
        lon2 *= Math.PI / 180.0;

        double a = 6378137.0; // WGS84 major axis
        double b = 6356752.3142; // WGS84 semi-major axis
        double f = (a - b) / a;

        double l = lon2 - lon1;
        double u1 = Math.atan((1.0 - f) * Math.tan(lat1));
        double u2 = Math.atan((1.0 - f) * Math.tan(lat2));

        double cosU1 = Math.cos(u1);
        double cosU2 = Math.cos(u2);
        double sinU1 = Math.sin(u1);
        double sinU2 = Math.sin(u2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;

        double sigma;
        double cosSqAlpha;
        double cos2SM;
        double cosSigma;
        double sinSigma;
        double cosLambda = 0.0;
        double sinLambda = 0.0;

        double lambda = l; // initial guess
        for (int iter = 0; iter < 20; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2;
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = (sinSigma == 0) ? 0.0 :
                    cosU1cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
            cos2SM = (cosSqAlpha == 0) ? 0.0 : cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha;

            double cC = (f / 16.0) * cosSqAlpha * (4.0 + f * (4.0 - 3.0 * cosSqAlpha));

            lambda = l + (1.0 - cC) * f * sinAlpha * (sigma + cC * sinSigma * (cos2SM
                                                                               + cC * cosSigma * (-1.0 + 2.0 * cos2SM * cos2SM)));

            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0e-12) {
                break;
            }
        }

        float finalBearing = (float) Math.atan2(cosU1 * sinLambda,
                                                -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda);
        // convert to degrees (from radians)
        finalBearing = (float) (finalBearing * (180.0 / Math.PI));

        Log.i(TAG, "BBBBBB=" + finalBearing);

        return finalBearing;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        bearing = location.getBearing();
        Log.i(TAG, "onLocationChanged: " + location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: " + provider);
        if (provider.equals(LocationManager.GPS_PROVIDER))
        {
            stopUsingGPS();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled: " + provider);
        if (provider.equals(LocationManager.GPS_PROVIDER))
        {
            startUsingGPS();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged: " + provider + " " + status + " " + extras);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * returns Distance in Meters
     */
    public static double calculateDistance(double lat1, double lon1, double el1,
                                           double lat2, double lon2, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                   + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                     * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";

        Log.i(TAG, "" + mAzimuth + "° " + where);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
