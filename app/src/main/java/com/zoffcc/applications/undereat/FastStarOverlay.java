package com.zoffcc.applications.undereat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

import static com.zoffcc.applications.undereat.MapActivity.dpToPx;
import static com.zoffcc.applications.undereat.corefuncs.DEMO_SHOWCASE_DEBUG_ONLY;

public class FastStarOverlay extends Overlay {
    private final List<OverlayItem> items;
    private final Paint starFillPaint;
    private final Paint starOutlinePaint;
    private final float starSize = dpToPx(12); // Radius of the star
    private final float starSpacing = dpToPx(28); // Distance between star centers

    public FastStarOverlay(List<OverlayItem> items) {
        this.items = items;

        starFillPaint = new Paint();
        starFillPaint.setColor(Color.YELLOW);
        starFillPaint.setStyle(Paint.Style.FILL);
        starFillPaint.setAntiAlias(true);

        starOutlinePaint = new Paint();
        starOutlinePaint.setColor(Color.BLACK);
        starOutlinePaint.setStyle(Paint.Style.STROKE);
        starOutlinePaint.setStrokeWidth(dpToPx(1.5f));
        starOutlinePaint.setStrokeJoin(Paint.Join.ROUND);
        starOutlinePaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        int minZoomShowStars = 17;
        if (DEMO_SHOWCASE_DEBUG_ONLY) minZoomShowStars = 5;
        if (shadow || mapView.getZoomLevelDouble() < minZoomShowStars) return;

        Projection projection = mapView.getProjection();
        Point mPoint = new Point();
        Rect screenRect = projection.getScreenRect();
        float mapRotation = mapView.getMapOrientation();

        for (OverlayItem item : items) {
            projection.toPixels(item.getPoint(), mPoint);

            if (screenRect.contains(mPoint.x, mPoint.y)) {
                canvas.save();
                canvas.rotate(-mapRotation, mPoint.x, mPoint.y);

                // Position stars above the marker (adjust Y offset as needed)
                float startX = mPoint.x - (starSpacing * 2);
                float y = mPoint.y - dpToPx(110);

                // 1. Get the star rating from item.getTitle() converted to float
                float rating = 0f;
                try {
                    rating = Float.parseFloat(item.getTitle());
                } catch (Exception e) {
                    rating = 0f;
                }

                if (rating != 0f)
                {
                    // 2. Draw the stars with partial fill support
                    for (int i = 0; i < 5; i++)
                    {
                        // Calculate fill level for this specific star (0.0 to 1.0)
                        float fillLevel = Math.max(0, Math.min(1, rating - i));
                        drawStar(canvas, startX + (i * starSpacing), y, fillLevel, i, rating);
                    }
                }

                canvas.restore();
            }
        }
    }

    private void drawStar(Canvas canvas, float cx, float cy, float fillLevel, int star_num, float rating) {

        if (rating == 1.0f)
        {
            if (star_num == 2)
            {
                // Draw Red Triangle
                Path triangle = new Path();
                triangle.moveTo(cx, cy - starSize); // Top
                triangle.lineTo(cx + starSize, cy + starSize); // Bottom Right
                triangle.lineTo(cx - starSize, cy + starSize); // Bottom Left
                triangle.close();

                Paint trianglePaint = new Paint(starFillPaint);
                trianglePaint.setColor(Color.RED);
                canvas.drawPath(triangle, trianglePaint);
                canvas.drawPath(triangle, starOutlinePaint);

                // Draw Black "!"
                Paint textPaint = new Paint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(starSize * 1.2f);
                textPaint.setFakeBoldText(true);
                // Adjust Y slightly to center the symbol visually
                canvas.drawText("!", cx, cy + (starSize * 0.5f), textPaint);
            }
        }
        else
        {

            Path path = new Path();
            double outerRadius = starSize;
            double innerRadius = starSize * 0.5;
            double angle = Math.PI / 5;

            for (int i = 0; i < 10; i++)
            {
                double r = (i % 2 == 0) ? outerRadius : innerRadius;
                float x = (float) (cx + Math.cos(i * angle - Math.PI / 2) * r);
                float y = (float) (cy + Math.sin(i * angle - Math.PI / 2) * r);
                if (i == 0)
                    path.moveTo(x, y);
                else
                    path.lineTo(x, y);
            }
            path.close();

            // Draw the background/empty star border first
            canvas.drawPath(path, starOutlinePaint);

            if (fillLevel > 0)
            {
                canvas.save();
                if (fillLevel < 1)
                {
                    // Clip the canvas so only the left portion of the star is filled
                    float clipWidth = (float) (starSize * 2 * fillLevel);
                    canvas.clipRect(cx - starSize, cy - starSize, (cx - starSize) + clipWidth, cy + starSize);
                }
                canvas.drawPath(path, starFillPaint);
                canvas.restore();

                // Redraw outline on top to ensure the black border isn't clipped
                canvas.drawPath(path, starOutlinePaint);
            }
        }
    }
}

