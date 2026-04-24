package com.zoffcc.applications.undereat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import androidx.core.content.ContextCompat;

public class RotatingLocationOverlay extends MyLocationNewOverlay {

    private static final String TAG = "RotatingLocationOverlay";

    private final MapView mMapView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private GeoPoint currentVisualPos;
    private float currentVisualBearing = 0f;
    protected boolean enableAutoStop = true;
    private static final int TOTAL_STEPS = 20;
    private static final int STEP_DURATION_MS = 1000 / TOTAL_STEPS; // Exactly 50ms

    public RotatingLocationOverlay(Context c, MapView mapView) {
        super(createCustomProvider(c), mapView);
        this.mMapView = mapView;

        // 1. Customize Icons
        // Person Icon (Static)
        // Bitmap personBitmap = getBitmapFromVector(c, org.osmdroid.library.R.drawable.person, Color.parseColor("#1976D2")); // Blueish person
        // setPersonIcon(personBitmap);
        // Direction Icon (Arrow)

        @SuppressLint("UseCompatLoadingForDrawables") Bitmap location_arrow_2 = tintImageWithBorder(
                ((BitmapDrawable) c.getResources().getDrawable(
                        R.drawable.round_navigation_color_48)).getBitmap(),
                Color.parseColor("#3142f0"));
        // Bitmap arrowBitmap = getBitmapFromVector(c, org.osmdroid.library.R.drawable.twotone_navigation_black_48, Color.parseColor("#2196F3")); // Bright blue arrow
        setDirectionIcon(location_arrow_2);
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
        Log.i(TAG, "onSingleTapConfirmed:");
        return false;
    }

    public void setEnableAutoStop(boolean value){
        this.enableAutoStop=value;
    }
    public boolean getEnableAutoStop(){
        return this.enableAutoStop;
    }


    @Override
    public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
        final boolean isSingleFingerDrag = (event.getAction() == MotionEvent.ACTION_MOVE)
                                           && (event.getPointerCount() == 1);
        if (event.getAction() == MotionEvent.ACTION_DOWN && enableAutoStop) {
            this.disableFollowLocation();
        } else if (isSingleFingerDrag && isFollowLocationEnabled()) {
            return true;  // prevent the pan
        }

        return super.onTouchEvent(event, mapView);
    }

    private Bitmap getBitmapFromVector(Context context, int drawableId, int tintColor) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) return null;

        drawable.mutate();
        drawable.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static GpsMyLocationProvider createCustomProvider(Context c) {
        GpsMyLocationProvider provider = new GpsMyLocationProvider(c);
        provider.setLocationUpdateMinTime(0);
        provider.setLocationUpdateMinDistance(0);
        return provider;
    }


    @Override
    public void onLocationChanged(final Location location, org.osmdroid.views.overlay.mylocation.IMyLocationProvider source) {
        if (location == null) return;

        if (currentVisualPos == null) {
            currentVisualPos = new GeoPoint(location.getLatitude(), location.getLongitude());
            currentVisualBearing = location.hasBearing() ? location.getBearing() : 0f;
            super.onLocationChanged(location, source);
            return;
        }

        // Position Deltas
        final double startLat = currentVisualPos.getLatitude();
        final double startLon = currentVisualPos.getLongitude();
        final double deltaLat = (location.getLatitude() - startLat) / TOTAL_STEPS;
        final double deltaLon = (location.getLongitude() - startLon) / TOTAL_STEPS;

        // Bearing Deltas - Only interpolate if NEW location has a valid bearing
        final boolean incomingHasBearing = location.hasBearing() && location.getSpeed() > 0.2;
        final float startBearing = currentVisualBearing;
        float bearingDiff = 0f;

        if (incomingHasBearing) {
            float targetBearing = location.getBearing();
            bearingDiff = targetBearing - startBearing;
            if (bearingDiff > 180) bearingDiff -= 360;
            else if (bearingDiff < -180) bearingDiff += 360;
        }
        final float deltaBearing = bearingDiff / TOTAL_STEPS;

        handler.removeCallbacksAndMessages(null);

        handler.post(new Runnable() {
            int step = 1;

            @Override
            public void run() {
                double nextLat = startLat + (deltaLat * step);
                double nextLon = startLon + (deltaLon * step);

                // Only update visual bearing if we are in "moving" mode
                if (incomingHasBearing) {
                    currentVisualBearing = (startBearing + (deltaBearing * step) + 360) % 360;
                }

                currentVisualPos = new GeoPoint(nextLat, nextLon);

                // Create interpolated location
                Location interpolatedLoc = new Location(location);
                interpolatedLoc.setLatitude(nextLat);
                interpolatedLoc.setLongitude(nextLon);

                if (incomingHasBearing) {
                    interpolatedLoc.setBearing(currentVisualBearing);
                    // Explicitly set bearing to true so OSM knows to use the direction icon
                    // and apply rotation
                } else {
                    // If no bearing, OSM defaults to "person" icon
                    interpolatedLoc.removeBearing();
                }

                RotatingLocationOverlay.super.onLocationChanged(interpolatedLoc, source);

                if (isFollowLocationEnabled()) {
                    mMapView.getController().setCenter(currentVisualPos);
                    if (incomingHasBearing) {
                        mMapView.setMapOrientation(-currentVisualBearing);
                    }
                }

                if (step < TOTAL_STEPS) {
                    step++;
                    handler.postDelayed(this, STEP_DURATION_MS);
                }
            }
        });
    }

    @Override
    protected void drawMyLocation(Canvas canvas, Projection pj, Location lastFix) {
        super.drawMyLocation(canvas, pj, lastFix);
    }

    public static Bitmap tintImageWithBorder(Bitmap bitmap, int tintColor) {
        int borderSize = 4; // Thickness of the hard border
        int borderColor = 0xFF000000; // Solid black

        // 1. Calculate the new dimensions
        int newWidth = bitmap.getWidth() + (borderSize * 2);
        int newHeight = bitmap.getHeight() + (borderSize * 2);
        Bitmap bitmapResult = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapResult);

        // 2. Prepare the silhouette paint
        // ExtractAlpha creates a bitmap that only contains the transparency of the original
        Bitmap alphaBitmap = bitmap.extractAlpha();
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(borderColor);

        // 3. Draw the silhouette at offsets to create a hard border
        // This covers Top, Bottom, Left, Right, and Diagonals
        for (int x = -borderSize; x <= borderSize; x++) {
            for (int y = -borderSize; y <= borderSize; y++) {
                // Only draw if we are on the edge of the border radius
                if (x * x + y * y <= borderSize * borderSize) {
                    canvas.drawBitmap(alphaBitmap, borderSize + x, borderSize + y, borderPaint);
                }
            }
        }

        // 4. Draw the tinted image on top in the center
        Paint tintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tintPaint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, borderSize, borderSize, tintPaint);

        // Cleanup
        alphaBitmap.recycle();

        return bitmapResult;
    }
}
