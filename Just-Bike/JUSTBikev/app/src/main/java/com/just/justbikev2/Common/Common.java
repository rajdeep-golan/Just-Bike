package com.just.justbikev2.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.Remote.APIService;
import com.just.justbikev2.Remote.IGeoCoordinates;
import com.just.justbikev2.Remote.IGoogleServices;
import com.just.justbikev2.Remote.RetrofitClientAdmin;
import com.just.justbikev2.Remote.RetrofitClientClient;

import java.util.ArrayList;
import java.util.List;

public class Common {
  public static final String USER_PHONE = "userPhone";
  public static UserModel currentUser;
  public static Request currentRequest;
  public static String cityId;

  public static String baseUrl = "https://maps.googleapis.com/";
  public static String baseUrlMessaging = "https://fcm.googleapis.com/";
  public static String TRIP_START = "trip";

  public static String UserKey = "User";
  public static String PasswordKey = "Password";
  public static final int PICK_IMAGE_REQ = 21;

  public static String getDeviceName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    String name = Build.DEVICE;
    if (model!=null && model.startsWith(manufacturer)) {
      return capitalize(model+(name!=null?name:""));
    } else {
      return capitalize(manufacturer) + " " + model+(name!=null?" "+name:"");
    }
  }
  private static String capitalize(String s) {
    // Making the device name as Starting letter captial
    if (s == null || s.length() == 0) {
      return "";
    }
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }


    public static APIService getFCMService(){
        return RetrofitClientClient.getClient(baseUrlMessaging).create(APIService.class);
    }
    public static IGoogleServices getGoogleMapsAPI(){
        return RetrofitClientClient.getGoogleClient(baseUrl).create(IGoogleServices.class);
    }

    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++)
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
            }
        }
        return false;
    }
    public static String convertCodeToStatus(String status){
    if(status!=null) {
      if (status.equals("0"))
        return "Placed!";
      else if (status.equals("1"))
        return "Still time to reach you!";
      else if (status.equals("2"))
        return "Bike Sent!";
      else if (status.equals("3"))
        return "Journey ON!";
      else if (status.equals("4"))
        return "Journey Over!";
      else    // if(status.equals("5"))
        return "Cancelled";
    }
    else
      return "-";
    }

    public static IGeoCoordinates getGeoCodeServices(){
        return RetrofitClientAdmin.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap , int newWidth , int newHeight){

        Bitmap scaledBimap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX = newWidth / (float)bitmap.getWidth();
        float scaleY = newHeight / (float)bitmap.getHeight();
        float pivotX = 0 ,pivotY = 0;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBimap);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBimap;
    }

    public static String getOrderId() {
        //RazorpayClient razorpayClient = new RazorpayClient("key_id", "key_secret");

        return "";
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if(begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float)Math.toDegrees(Math.atan(lng/lat));
        else if(begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float)((90-Math.toDegrees(Math.atan(lng/lat)))+90);
        else if(begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float)((Math.toDegrees(Math.atan(lng/lat)))+180);
        else if(begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float)((90-Math.toDegrees(Math.atan(lng/lat)))+270);
        return -1;
    }

    public static List<LatLng> decodePoly(String encode) {
        List poly = new ArrayList();
        int index =0 , length = encode.length();
        int lat = 0 , lng =0;
        while(index<length){
            int b , shift = 0,result = 0;
            do{
                b=encode.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift += 5;

            }while(b >= 0x20);
            int dlat = ((result & 1 ) != 0 ? ~(result >> 1):(result >>1));
            lat+=dlat;

            shift = 0;
            result = 0;
            do{
                b=encode.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift += 5;

            }while(b >= 0x20);
            int dlng = ((result & 1 ) != 0 ? ~(result >> 1):(result >>1));
            lng+=dlng;

            LatLng p = new LatLng((double)lat/1E5,(double)lng/1E5);
            poly.add(p);
        }
        return poly;

    }
}
