package com.zoffcc.applications.undereat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

import static com.zoffcc.applications.undereat.MapActivity.dpToPx;

public class FastTextOverlay extends Overlay
{
    private final List<OverlayItem> items;
    private final Paint textPaint;
    private final Paint textOutlinePaint;

    public FastTextOverlay(List<OverlayItem> items) {
        this.items = items;
        // Fill paint (The black text)
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(dpToPx(30));
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);

        // Outline paint (The white border)
        textOutlinePaint = new Paint(textPaint); // Copy settings from textPaint
        textOutlinePaint.setColor(Color.WHITE);
        textOutlinePaint.setStyle(Paint.Style.STROKE);
        textOutlinePaint.setStrokeWidth(dpToPx(4)); // Adjust thickness here
        textOutlinePaint.setStrokeJoin(Paint.Join.ROUND);
        textOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        int minZoomShowText = 17;
        if (shadow || mapView.getZoomLevelDouble() < minZoomShowText) return;

        Projection projection = mapView.getProjection();
        Point mPoint = new Point();
        Rect screenRect = projection.getScreenRect();
        float mapRotation = mapView.getMapOrientation();

        for (OverlayItem item : items) {
            projection.toPixels(item.getPoint(), mPoint);

            if (screenRect.contains(mPoint.x, mPoint.y)) {
                canvas.save();
                canvas.rotate(-mapRotation, mPoint.x, mPoint.y);

                float x = mPoint.x;
                float y = mPoint.y - dpToPx(70);

                // Draw outline first
                canvas.drawText(item.getTitle(), x, y, textOutlinePaint);
                // Draw actual text on top
                canvas.drawText(item.getTitle(), x, y, textPaint);

                canvas.restore();
            }
        }
    }
}
