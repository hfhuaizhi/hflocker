package com.hfhuaizhi.hflocker.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.annotation.Keep;

import java.lang.reflect.Field;

@Keep
public class DensityUtil {

    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;
    private static boolean isPad = false;

    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static void init(Context context) {
        isPad = isPad(context);
    }

    public static boolean isPad() {
        return isPad;
    }

    private static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static float getScreenDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static float getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getFixedHeight(Context context) {
        return Math.max(getScreenHeight(context), getScreenWidth(context));
    }

    public static float getFixedWidth(Context context) {
        return Math.min(getScreenHeight(context), getScreenWidth(context));
    }

    public static boolean isHorizontalScreen(Context context) {
        Configuration config = context.getResources().getConfiguration();
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return isPad ? (config.orientation != Configuration.ORIENTATION_PORTRAIT) : (dm.widthPixels > dm.heightPixels);
    }

    public static float getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int dip2px(Context context, float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density + 0.5F);
    }

    public static int dip2px(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density + 0.5F);
    }

    public static int px2dip(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5F);
    }

    public static int sp2px(Context context, float sp) {
        float density = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * density + 0.5F);
    }

    public static int px2sp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / density + 0.5F);
    }

    public static void setCustomDensity(Activity activity, final Application application) {
        DisplayMetrics applicationDisplayMetrics = application.getResources().getDisplayMetrics();


        if (sNoncompatDensity == 0) {
            sNoncompatDensity = applicationDisplayMetrics.density;
            sNoncompatScaledDensity = applicationDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }


        final float targetDensity = applicationDisplayMetrics.widthPixels / 360.0f;
        final float targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        //        final int targetDensityDpi = (int) (160 * targetDensity);


        applicationDisplayMetrics.density = targetDensity;
        applicationDisplayMetrics.scaledDensity = targetScaledDensity;
        //        applicationDisplayMetrics.densityDpi = targetDensityDpi;


        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        //        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }
}
