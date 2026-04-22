package com.zoffcc.applications.undereat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;

import com.zoffcc.applications.sorm.Restaurant;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.zoffcc.applications.undereat.Edit_formKt.geo_coord_longdb_to_double;

public class MapActivity extends AppCompatActivity
{
    private static final String TAG = "MapActivity";

    // HINT: set default map center to vienna city center
    private static final GeoPoint MAP_DEFAULT_CENTER = new GeoPoint(48.20800970787025f, 16.36652915417636f);
    private static final double MAP_DEFAULT_ZOOM_LEVEL = 17.0d;

    static MapView map = null;
    static IMapController mapController = null;
    static MyLocationNewOverlay mLocationOverlay = null;
    static RotationGestureOverlay mRotationGestureOverlay = null;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    static List<Restaurant> restaurants = new ArrayList<>();
    ImageButton back_on_screen_button = null;

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


        setContentView(R.layout.map_activity);

        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissionsIfNecessary(permissions);

        back_on_screen_button = findViewById(R.id.back_on_screen_button);
        back_on_screen_button.setOnClickListener(v -> {
            // Triggers the exact same behavior as the physical back button
            Log.i(TAG, "XXXXXXXXXX");
            onBackPressed();
        });

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setFlingEnabled(true);
        map.setTilesScaledToDpi(true);
        map.setMinZoomLevel(null);
        map.setBuiltInZoomControls(true);

        mapController = map.getController();
        mapController.setCenter(new GeoPoint(MAP_DEFAULT_CENTER));
        mapController.setZoom(MAP_DEFAULT_ZOOM_LEVEL);

        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        mRotationGestureOverlay.setOptionsMenuEnabled(false);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        ArrayList<OverlayItem> items = new ArrayList<>();
        try
        {
            for (Restaurant restaurant : restaurants)
            {
                Log.i(TAG, "r: " + geo_coord_longdb_to_double(restaurant.lat) + " " + geo_coord_longdb_to_double(restaurant.lon));
                items.add(new OverlayItem("", "",
                                          new GeoPoint(geo_coord_longdb_to_double(restaurant.lat), geo_coord_longdb_to_double(restaurant.lon)))
                );
            }
        }
        catch(Exception ignored)
        {
        }
        final ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items,
                                                                                    null,
                                                                                    this);
        map.getOverlays().add(mOverlay);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        try
        {
            // map.getOverlayManager().remove(4);
        }
        catch(Exception e)
        {
        }

        map.onPause();
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
        catch(Exception e)
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
        catch(Exception e)
        {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
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
}
