package com.combintech.baojili.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.reflect.Field;

/**
 * Created by Hendry Setiadi
 */

public class CustomSwipeToRefresh extends SwipeRefreshLayout implements ViewTreeObserver
        .OnGlobalLayoutListener {

    private static float MAX_SWIPE_DISTANCE_FACTOR = 0.6f;
    private static int DEFAULT_REFRESH_TRIGGER_DISTANCE = 300;

    private int refreshTriggerDistance = DEFAULT_REFRESH_TRIGGER_DISTANCE;

    ViewTreeObserver vto;

    public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        // Calculate the trigger distance.
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        Float mDistanceToTriggerSync = Math.min(
                ((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                refreshTriggerDistance * metrics.density);
        if (Build.VERSION.SDK_INT >= 21) {
            this.setDistanceToTriggerSync(Math.round(mDistanceToTriggerSync));
        } else {
            try {
                // Set the internal trigger distance using reflection.
                Field field = SwipeRefreshLayout.class.getDeclaredField("mDistanceToTriggerSync");
                field.setAccessible(true);
                field.setFloat(this, mDistanceToTriggerSync);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Only needs to be done once so remove listener.
        ViewTreeObserver obs = getViewTreeObserver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            obs.removeOnGlobalLayoutListener(this);
        } else {
            //noinspection deprecation
            obs.removeGlobalOnLayoutListener(this);
        }
    }

    private int getRefreshTriggerDistance() {
        return refreshTriggerDistance;
    }

    private void setRefreshTriggerDistance(int refreshTriggerDistance) {
        this.refreshTriggerDistance = refreshTriggerDistance;
    }
}
