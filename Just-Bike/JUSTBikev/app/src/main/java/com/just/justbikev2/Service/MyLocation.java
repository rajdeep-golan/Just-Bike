package com.just.justbikev2.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.just.justbikev2.ComingSoon;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Home;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyLocation extends BroadcastReceiver {

  public static final String ACTION_PROCESS_UPDATE ="com.just.justbikev2.UPDATE_LOCATION";
  @Override
  public void onReceive(Context context, Intent intent) {
      if(intent!=null){
        final String action  = intent.getAction();
        if(ACTION_PROCESS_UPDATE.equals(action)){
          LocationResult result = LocationResult.extractResult(intent);
          if(result!=null){
            Location location = result.getLastLocation();
            try {
              Home.getInstance().updateDatabaseLocation(location.getLatitude(),location.getLongitude());
            }catch (Exception e){
               //Update Database Location
              DatabaseReference locaUpdate = FirebaseDatabase.getInstance().getReference("UserLocationUpdate");
              DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
              Map<String,Object> putMap = new HashMap<>();
              putMap.put("lat",location.getLatitude());
              putMap.put("lng",location.getLongitude());
              putMap.put("phoneModel",Common.getDeviceName());

              Date date = new Date(System.currentTimeMillis());
              putMap.put("locationTime",String.valueOf(date));
              if(Common.currentUser!=null)
                user.child(Common.currentUser.getPhone()).updateChildren(putMap);
              else
                locaUpdate.push().updateChildren(putMap);
            }
          }
        }
      }
  }

}
