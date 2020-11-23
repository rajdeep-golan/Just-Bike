package com.just.justbikev2.Common;

import com.google.android.gms.maps.model.LatLng;

public interface LatLngInterpolater {
     LatLng  interpolate(float fraction , LatLng a , LatLng b);
        class Linear implements LatLngInterpolater{
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude)*fraction +a.latitude;
                double lng = (b.longitude - a.longitude)*fraction +a.longitude;
                return new LatLng(lat,lng);
            }
        }
        class LinearFixed implements LatLngInterpolater{
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude)*fraction +a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if(Math.abs(lngDelta)>180){
                    lngDelta -= Math.signum(lngDelta)*360;
                }
                double lng = lngDelta*fraction + a.longitude;
                return new LatLng(lat,lng);
            }
        }
        class Spherical implements LatLngInterpolater{
            @Override
            public LatLng interpolate(float fraction, LatLng from, LatLng to) {
                double fromLat = Math.toRadians(from.latitude);
                double fromLng = Math.toRadians(from.longitude);
                double toLat = Math.toRadians(to.latitude);
                double toLng = Math.toRadians(to.longitude);
                double cosFromLat = Math.cos(fromLat);
                double costToLat = Math.cos(toLat);

               //Computin Spherical Interpolation coefficients
                double angle = computeAngleBetween(fromLat,fromLng,toLat,toLng);
                double sinAngle=Math.sin(angle);

                if(sinAngle<1E-6)
                    return from;
                double a= Math.sin((1-fraction) * angle)/sinAngle;
                double b= Math.sin((fraction) * angle)/sinAngle;

               //Converts Polar to Vector and interpolate
                double x = a * cosFromLat * Math.cos(fromLng) + b * costToLat * Math.cos(toLng);
                double y = a * cosFromLat * Math.sin(fromLng) + b * costToLat * Math.sin(toLng);
                double z = a * Math.sin(fromLat) + b * Math.sin(toLat);

                //Converts Interpolater back to Polar
                double lat = Math.atan2(z,Math.sqrt(x*x + y*y));
                double lng = Math.atan2(y,x);
                return new LatLng(Math.toDegrees(lat),Math.toDegrees(lng));
            }

            private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
                double dLat = fromLat - toLat;
                double dLng = fromLng - toLng;
                return 2*Math.asin(Math.sqrt(Math.pow(Math.sin(dLat/2),2) + Math.cos(fromLat)*Math.cos(toLat) * Math.pow(Math.sin(dLng/2),2)));
            }
        }
}
