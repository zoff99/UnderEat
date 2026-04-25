package com.zoffcc.applications.undereat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.zoffcc.applications.sorm.Restaurant;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import static com.zoffcc.applications.undereat.Edit_formKt.geo_coord_longdb_to_double;
import static com.zoffcc.applications.undereat.corefuncs.DEMO_SHOWCASE_DEBUG_ONLY;
import static com.zoffcc.applications.undereat.corefuncs.orma;

public class MapActivity extends AppCompatActivity implements SensorEventListener
{
    private static final String TAG = "MapActivity";

    private static final GeoPoint MAP_DEFAULT_CENTER = new GeoPoint(48.20800970787025f, 16.36652915417636f);
    private static final double MAP_DEFAULT_ZOOM_LEVEL = 17.0d;

    static MapView map = null;
    static IMapController mapController = null;
    static RotatingLocationOverlay mLocationOverlay = null;
    static RotationGestureOverlay mRotationGestureOverlay = null;
    static List<Restaurant> restaurants = new ArrayList<>();

    float[] rMat = new float[9];
    float[] orientation = new float[3];
    static int mAzimuth_map = 0;
    static boolean has_azimuth_map = false;
    String where = "NW";
    private final float[] mLastAccelerometer = new float[3];
    private final float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    static private SensorManager sensorManager;
    static private Sensor accelerometer;
    static private Sensor magnetometer;
    static private Sensor rotationsense;
    static boolean haveSensor = false;
    static boolean haveSensor2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        org.osmdroid.config.Configuration.getInstance().
                load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // HINT: just set the HTTP User Agent explicitly here
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        int animationSpeedDefault = 600; // 1000;
        int animationSpeedShort = 400; // 500;
        org.osmdroid.config.Configuration.getInstance().setAnimationSpeedDefault(animationSpeedDefault); //used in animateTo() calls
        org.osmdroid.config.Configuration.getInstance().setAnimationSpeedShort(animationSpeedShort); //during zoom animation

        try
        {
            Log.i(TAG, "base_path_for_OSM:" + org.osmdroid.config.Configuration.getInstance().getOsmdroidBasePath());
        }
        catch(Exception ignored)
        {
        }

        Log.i(TAG, "OSM:isMapViewHardwareAccelerated:" + org.osmdroid.config.Configuration.getInstance().isMapViewHardwareAccelerated());

        setContentView(R.layout.map_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setFlingEnabled(true);
        map.setTilesScaledToDpi(true);
        map.setMinZoomLevel(null);
        map.setBuiltInZoomControls(true);

        mapController = map.getController();
        mapController.setCenter(new GeoPoint(MAP_DEFAULT_CENTER));
        if (DEMO_SHOWCASE_DEBUG_ONLY)
        {
            mapController.setZoom(10);
        }
        else
        {
            mapController.setZoom(MAP_DEFAULT_ZOOM_LEVEL);
        }

        mLocationOverlay = new RotatingLocationOverlay(this, map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setEnableAutoStop(true);
        mLocationOverlay.setDrawAccuracyEnabled(true);
        mLocationOverlay.setEnabled(true);
        // mLocationOverlay.setOptionsMenuEnabled(false);
        map.getOverlays().add(mLocationOverlay);

        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        // mRotationGestureOverlay.setOptionsMenuEnabled(false);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);

        try
        {
            // HINT: categories start with "1" !!
            int max_categories = orma.selectFromCategory().toList().size();
            final List<Integer> category_colors = get_poi_colors(max_categories);

            for (int cur_category = 1; cur_category <= max_categories; cur_category++)
            {
                ArrayList<OverlayItem> items = new ArrayList<>();
                int found_restaurants = 0;
                for (Restaurant restaurant : restaurants)
                {
                    if (restaurant.category_id == cur_category)
                    {
                        Log.i(TAG, "r: " + geo_coord_longdb_to_double(restaurant.lat) + " " +
                                   geo_coord_longdb_to_double(restaurant.lon));
                        String res_name = "";
                        try
                        {
                            res_name = restaurant.name;
                        }
                        catch(Exception ignored)
                        {
                        }
                        items.add(new OverlayItem("" + res_name, "", new GeoPoint(geo_coord_longdb_to_double(restaurant.lat),
                                                                       geo_coord_longdb_to_double(restaurant.lon))));
                        found_restaurants++;
                    }
                }
                if (found_restaurants > 0)
                {
                    Drawable rawDrawable = ContextCompat.getDrawable(this, R.drawable.outline_location_on_24);
                    Drawable wrappedDrawable = DrawableCompat.wrap(rawDrawable).mutate();
                    try
                    {
                        DrawableCompat.setTint(wrappedDrawable, category_colors.get(cur_category - 1));
                    }
                    catch(Exception e)
                    {
                        DrawableCompat.setTint(wrappedDrawable, Color.BLUE);
                    }
                    DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN);

                    @SuppressLint("UseCompatLoadingForDrawables") final ItemizedIconOverlay<OverlayItem> mOverlay
                            = new ItemizedIconOverlay<>(
                            items,
                            wrappedDrawable,
                            null, this);
                    map.getOverlays().add(mOverlay);
                    FastTextOverlay textOverlay = new FastTextOverlay(items);
                    map.getOverlays().add(textOverlay);
                }
            }
        }
        catch(Exception ignored)
        {
        }


        NorthingOverlay northing_ov = new NorthingOverlay(this, map);
        map.getOverlays().add(northing_ov);
        /*
        set_northing_callback(new NorthingOverlay.NorthingCallback() {
            @Override
            public void update_is_northing(boolean value)
            {
                Log.i(TAG, "**CLICK**");
            }
        });
        */
        Log.i(TAG, "onCreate finished");
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float pxToDp(float px) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return px / density;
    }

    public static float dpToPx(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return dp * density;
    }

    @NonNull
    private static List<Integer> get_poi_colors(int max_categories)
    {
        List<Integer> category_colors = new ArrayList<>(max_categories);

        category_colors.add(0xFFFF4444); // Red #1
        category_colors.add(0xFFFFBB33); // Orange #2
        category_colors.add(0xFF99CC00); // Green #3
        category_colors.add(0xFF33B5E5); // Blue #4
        category_colors.add(0xFFAA66CC); // Purple #5
        category_colors.add(0xFF0099CC); // Dark Blue #6
        category_colors.add(0xFF669900); // Dark Green #7
        category_colors.add(0xFFFF8800); // Dark Orange #8
        category_colors.add(0xFFCC0000); // Dark Red #9
        category_colors.add(0xFF222222); // Near Black #10
        category_colors.add(0xFFFF4081); // Pink/Magenta #11
        return category_colors;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        map.onResume();
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setEnableAutoStop(false);
        start_sensors(this);
        Log.i(TAG, "onResume");
        // Log.d("LifecycleDebug", "onResume called. Config: " + getResources().getConfiguration().toString());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stop_sensors();
        map.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed");
    }

    void remove_map_overlays()
    {
        try
        {
            mLocationOverlay.disableMyLocation();
        }
        catch(Exception ignored)
        {
        }

        try
        {
            for (Overlay ov : map.getOverlays())
            {
                Log.i(TAG, "OVXXXX:1:" + ov);
            }
            map.getOverlays().clear();
        }
        catch(Exception ignored)
        {
        }
    }

    public synchronized static void set_restaurant_list(final List<Restaurant> r)
    {
        try
        {
            restaurants = r;
            for (Restaurant restaurant : restaurants)
            {
                Log.i(TAG, "r1: " + geo_coord_longdb_to_double(restaurant.lat) + " " + geo_coord_longdb_to_double(restaurant.lon));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth_map = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
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
            mAzimuth_map = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth_map = Math.round(mAzimuth_map);

        if (mAzimuth_map >= 350 || mAzimuth_map <= 10)
            where = "N";
        if (mAzimuth_map < 350 && mAzimuth_map > 280)
            where = "NW";
        if (mAzimuth_map <= 280 && mAzimuth_map > 260)
            where = "W";
        if (mAzimuth_map <= 260 && mAzimuth_map > 190)
            where = "SW";
        if (mAzimuth_map <= 190 && mAzimuth_map > 170)
            where = "S";
        if (mAzimuth_map <= 170 && mAzimuth_map > 100)
            where = "SE";
        if (mAzimuth_map <= 100 && mAzimuth_map > 80)
            where = "E";
        if (mAzimuth_map <= 80 && mAzimuth_map > 10)
            where = "NE";

        // the result is clockwise. E = 90°, N = 0°, W = 275°, S = 180°
        // Log.i(TAG, "azz: " + mAzimuth + "° " + where);
        has_azimuth_map = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    void start_sensors(Context c)
    {
        try
        {
            sensorManager = (SensorManager) c.getSystemService(SENSOR_SERVICE);
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
    }

    void stop_sensors()
    {
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
}
