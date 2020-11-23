package com.just.justbikev2.Common;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerAnimation  {
    public static void animateMarkerToGB(final Marker marker , LatLng finalPosition , LatLngInterpolater interpolater){
         LatLng startPos = marker.getPosition();
        Handler handler = new Handler();
        long start = SystemClock.uptimeMillis();
        Interpolator interpolator = new AccelerateDecelerateInterpolator();

        float duration = 3000;  // 3 sec

        handler.post(new Runnable() {
            long elapsed;
            float v,t;

            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / duration;
                v = interpolator.getInterpolation(t);
                marker.setPosition(interpolater.interpolate(v,startPos , finalPosition));

                //Repeating till Process is complete
                if(t<1){
                    handler.postDelayed(this,16);   //Post again in 16ms
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void animateMarkerToHC(final Marker marker , LatLng finalPosition , LatLngInterpolater interpolater){
        LatLng startLocation = marker.getPosition();
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                LatLng newPos = interpolater.interpolate(v,startLocation,finalPosition);
                marker.setPosition(newPos);
            }
        });
        valueAnimator.setFloatValues(0,1);
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void animateMarkerToICS(final Marker marker , LatLng finalPosition , LatLngInterpolater interpolater){
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return interpolater.interpolate(fraction,startValue,endValue);
            }
        };
        Property<Marker , LatLng> property = Property.of(Marker.class,LatLng.class,"position");
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(marker , property,typeEvaluator,finalPosition);
        objectAnimator.setDuration(3000);
        objectAnimator.start();
    }
}
