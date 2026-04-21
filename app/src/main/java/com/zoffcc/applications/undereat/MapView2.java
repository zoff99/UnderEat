package com.zoffcc.applications.undereat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import org.osmdroid.views.MapView;

public class MapView2 extends MapView
{
    private static final String TAG = "MapView2";

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        Log.i(TAG, "onKeyDown:" + keyCode);
        return false;
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        Log.i(TAG, "onKeyUp:" + keyCode);
        return false;
    }

    public MapView2(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MapView2(Context context)
    {
        super(context);
    }
}
