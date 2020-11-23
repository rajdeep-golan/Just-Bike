package com.just.justbikev2.Admin;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Common.DirectionJSONParser;
import com.just.justbikev2.Common.LatLngInterpolater;
import com.just.justbikev2.Common.MarkerAnimation;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.R;
import com.just.justbikev2.Remote.IGeoCoordinates;
import com.just.justbikev2.Remote.IGoogleAPI;
import com.just.justbikev2.Remote.RetrofitClientAdmin;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderNav extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private LocationRequest locationRequest;
  private LocationCallback locationCallback;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private Location currentlocation;

  private IGeoCoordinates geoServices;

  private Marker bikeMarker, locationMarkerCust;
  private Order order;

  String contactNo = "+918877772277";
  private static int CALL_CODE = 34;

  private boolean isInit = false;
  private Location prevLocation;

  private Handler handler;
  private int next, index;
  private LatLng start, end;
  private float v;
  private double lat, lng;

  private Polyline blackPolyline, greyPolyline, redPolyline;
  private PolylineOptions polylineOptions, blackPolylineOptions;
  private List<LatLng> polyLineList;
  private IGoogleAPI googleAPI;
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  @BindView(R.id.detailsLayout)
  CardView detailsLayout;

  @BindView(R.id.hideBtn)
  ImageButton hideBtn;

  @BindView(R.id.mapsBikeImage)
  ImageView mapsBikeImage;

  @BindView(R.id.mapsBikeName)
  TextView mapsBikeName;

  @BindView(R.id.exit)
  ImageButton exit;

  @BindView(R.id.call)
  ImageButton call;

  @BindView(R.id.mapsEndTime)
  TextView mapsEndTime;

  @BindView(R.id.addressMap)
  TextView addressMap;

  @BindView(R.id.startTrip)
  ImageButton startTrip;

  @BindView(R.id.startDone)
  TextView startDone;

  @OnClick(R.id.startTrip)
  void onStartT5ripClick() {
    String data = Paper.book().read("orderData");
    Paper.book().write(Common.TRIP_START, data);
    startTrip.setEnabled(false);
    startDone.setText("Done5");
//        drawPath(data);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
      @Override
      public void onSuccess(Location location) {
        Map<String, Object> update_data = new HashMap<>();
        update_data.put("currentLat", currentlocation.getLatitude());
        update_data.put("currentLng", currentlocation.getLongitude());

        FirebaseDatabase.getInstance().getReference("Requests").child(orderId).updateChildren(update_data)
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(OrderNav.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
            drawPath(data);
            //     LatLng newLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
            //     drawRoute(newLocation, Common.currentRequest.getAddress());

          }
        });


      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(OrderNav.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_nav);
    ButterKnife.bind(this);

    if (getIntent() != null)
      orderId = getIntent().getStringExtra("orderId");
    buildLocationRequest();
    buildLocationCallback();
    geoServices = Common.getGeoCodeServices();

    googleAPI = RetrofitClientAdmin.getInstance().create(IGoogleAPI.class);


    exit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    call.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callUs();
      }
    });
    hideBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (detailsLayout.getVisibility() == View.VISIBLE) {
          hideBtn.setImageResource(R.drawable.ic_code_white_24dp);
          detailsLayout.setVisibility(View.INVISIBLE);
        } else {
          detailsLayout.setVisibility(View.VISIBLE);
          hideBtn.setImageResource(R.drawable.ic_compare_arrows_white_24dp);

        }


      }
    });
    Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      .withListener(new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
          // Obtain the SupportMapFragment and get notified when the map is ready to be used.

          SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
          mapFragment.getMapAsync(OrderNav.this);
          fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(OrderNav.this);
          if (ActivityCompat.checkSelfPermission(OrderNav.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrderNav.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
          }
          fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
          Toast.makeText(OrderNav.this, "Grant Location Permissions before using app.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

        }
      }).check();


  }

  private void updateLocation(Location currentlocation) {
    Map<String, Object> update_data = new HashMap<>();
    update_data.put("currentLat", currentlocation.getLatitude());
    update_data.put("currentLng", currentlocation.getLongitude());

    //   String data = Paper.book().read(Common.TRIP_START);
//        if(!TextUtils.isEmpty(data)) {
//            Order order_update = new Gson().fromJson(data, new TypeToken<Order>() {
//            }.getType());
//

    LatLng newLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
    if (order != null) {
      FirebaseDatabase.getInstance().getReference("Requests").child(orderId).updateChildren(update_data)
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(OrderNav.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          //     LatLng newLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
          //     drawRoute(newLocation, Common.currentRequest.getAddress());

        }
      });
    }
  }
//    }

  private void moveMarkerAnimation(Marker bikeMarker, String from, String to) {
    compositeDisposable.add(googleAPI.getDirections("driving",
      "less_driving", from, to,
      getString(R.string.google_maps_api)).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Throwable {
          JSONObject jsonObject = new JSONObject(s);

          JSONArray routes = jsonObject.getJSONArray("routes");
          for (int i = 0; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            JSONObject poly = route.getJSONObject("overview_polyline");
            String polyline = poly.getString("points");
            polyLineList = Common.decodePoly(polyline);
          }
          polylineOptions = new PolylineOptions();
          polylineOptions.color(Color.GRAY);
          polylineOptions.width(8);
          polylineOptions.startCap(new SquareCap());
          polylineOptions.jointType(JointType.ROUND);
          polylineOptions.addAll(polyLineList);
          //     polylineOptions.geodesic(true);
          greyPolyline = mMap.addPolyline(polylineOptions);

          blackPolylineOptions = new PolylineOptions();
          blackPolylineOptions.color(Color.BLACK);
          blackPolylineOptions.width(8);
          blackPolylineOptions.startCap(new SquareCap());
          blackPolylineOptions.jointType(JointType.ROUND);
          blackPolylineOptions.addAll(polyLineList);
          //   blackPolylineOptions.geodesic(true);
          blackPolyline = mMap.addPolyline(blackPolylineOptions);

          //Animator
          ValueAnimator animator = ValueAnimator.ofInt(0, 100);
          animator.setDuration(2000);
          animator.setInterpolator(new LinearInterpolator());
          animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
              List<LatLng> points = greyPolyline.getPoints();
              int percentValue = (int) animator.getAnimatedValue();
              int size = points.size();
              int newPoints = (int) (size * (percentValue / 100.0f));
              List<LatLng> p = points.subList(0, newPoints);
              blackPolyline.setPoints(p);
            }
          });
          animator.start();

          //Moving bike
          handler = new Handler();
          index = -1;
          next = 1;
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;
                start = polyLineList.get(index);
                end = polyLineList.get(next);
              }
              ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
              valueAnimator.setDuration(1500);
              valueAnimator.setInterpolator(new LinearInterpolator());
              valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                  v = valueAnimator.getAnimatedFraction();
                  lng = v * end.longitude + (1 - v) * start.longitude;
                  lat = v * end.latitude + (1 - v) * start.latitude;
                  LatLng newPos = new LatLng(lat, lng);

                  bikeMarker.setPosition(newPos);
                  bikeMarker.setAnchor(0.5f, 0.5f);
                  bikeMarker.setRotation(Common.getBearing(start, newPos));

                  mMap.moveCamera(CameraUpdateFactory.newLatLng(bikeMarker.getPosition()));
                }
              });
              valueAnimator.start();

              if (index < polyLineList.size() - 2)    // Reach Destination
              {
                handler.postDelayed(this, 1500);
              }
            }
          }, 1500);
        }
      }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Throwable {
          if (throwable != null)
            Toast.makeText(OrderNav.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }));
  }

  private void askForCallPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_CODE);
  }

  public void callUs() {
    Intent callIntent = new Intent(Intent.ACTION_CALL);
    callIntent.setData(Uri.parse("tel:" + contactNo));

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
      askForCallPermission();
    else
      startActivity(callIntent);

  }

  @SuppressLint("MissingPermission")
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == CALL_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contactNo));
        startActivity(callIntent);
      } else
        Toast.makeText(this, "Please accept calling permission before making a Call", Toast.LENGTH_SHORT).show();
    }

  }

  String orderId;

  private void setUpOrderDetails() {
    Paper.init(this);
    String data = "";
    String endTime = Paper.book().read("EndTime");
    String address = Paper.book().read("address");
    String phoneNumber = Paper.book().read("phoneNumber");
    Request request = Common.currentRequest;
    ;
    if (TextUtils.isEmpty(Paper.book().read(Common.TRIP_START))) {
      startTrip.setEnabled(true);
    } else {
      startTrip.setEnabled(false);
      data = Paper.book().read(Common.TRIP_START);
    }
    if (!TextUtils.isEmpty(data)) {
      drawPath(data);
      order = new Gson().fromJson(data, new TypeToken<Order>() {
      }.getType());
      if (order != null) {
        mapsBikeName.setText(order.getProductName());
        if (order.getImage() != null)
          Picasso.get().load(order.getImage()).into(mapsBikeImage);
        mapsEndTime.setText(endTime);
        if (address.lastIndexOf("West") > 1)
          addressMap.setText(address.substring(0, address.lastIndexOf("West") - 2));   //For West Bengal and -1 for , (comma)
        else
          addressMap.setText(address);
        if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber))
          contactNo = phoneNumber;
      }
    } else if (request != null) {
      mapsBikeName.setText(request.getOrderList().get(0).getProductName());
      if (request.getOrderList().get(0).getImage() != null)
        Picasso.get().load(request.getOrderList().get(0).getImage()).into(mapsBikeImage);
      mapsEndTime.setText(endTime);
      if (address.lastIndexOf("West") > 1)
        addressMap.setText(address.substring(0, address.lastIndexOf("West") - 2));   //For West Bengal and -1 for , (comma)
      else
        addressMap.setText(address);
      if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber))
        contactNo = phoneNumber;
    } else
      Toast.makeText(this, "Order is null", Toast.LENGTH_SHORT).show();

  }

  private void drawPath(String data) {

    if (fusedLocationProviderClient != null) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      fusedLocationProviderClient.getLastLocation()
        .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                }
                              }
        ).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Toast.makeText(OrderNav.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }).addOnCompleteListener(new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
          if (task != null && task.getResult() != null) {
            currentlocation = task.getResult();
            LatLng bikeLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());

            if (bikeMarker == null) {
              Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps_icon);
              bitmap = Common.scaleBitmap(bitmap, 150, 100);
              bikeMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(bikeLocation).title("You"));

              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation, 18));

            }
////                        if(isInit && prevLocation!=null){
//
//                            String from = new StringBuilder().append(prevLocation.getLatitude())
//                                    .append(",")
//                                    .append(prevLocation.getLongitude()).toString();
//                            String to = new StringBuilder().append(bikeLocation.latitude).append(",").append(bikeLocation.longitude).toString();
//                            LatLng prevLocationLatLng = new LatLng(prevLocation.getLatitude(),prevLocation.getLongitude());
//                            MarkerAnimation.animateMarkerToGB(bikeMarker,bikeLocation,new LatLngInterpolater.Spherical());
//                            bikeMarker.setRotation(Common.getBearing(prevLocationLatLng,bikeLocation));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLng(bikeLocation));
//
//                      //      moveMarkerAnimation(bikeMarker,from,to);
//
//                            prevLocation = currentlocation;

            //  }

//                        if(!isInit){
//                            isInit = true;
//                            prevLocation = currentlocation;
//                        }
            //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation, 15));
            LatLng newLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
            drawRoute(newLocation, Common.currentRequest.getAddress());

            // updateLocation(currentlocation);


          }
        }
      });
    }

    //Add marker for customer
//        geoServices.getCode(Common.currentRequest.getAddress(),"AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                try {
//
//                    JSONObject jsonObject = new JSONObject(response.body());
//
////                    JSONArray routes = jsonObject.getJSONArray("routes");
////                    JSONObject object = routes.getJSONObject(0);
////                    JSONArray legs = object.getJSONArray("legs");
////                    JSONObject legsObjects = legs.getJSONObject(0);
////
//////get the distance
////                    JSONObject distance = legsObjects.getJSONObject("distance");
////                    String distanceCal = distance.getString("text");
////
//////get the time
////                    JSONObject time = legsObjects.getJSONObject("duration");
////                    String duration = time.getString("text");
////                    Toast.makeText(TrackingOrder.this, distanceCal+","+duration, Toast.LENGTH_SHORT).show();
//
//                    String lat = ((JSONArray)jsonObject.get("results"))
//                            .getJSONObject(0)
//                            .getJSONObject("geometry")
//                            .getJSONObject("location")
//                            .get("lat").toString();
//
//                    String lng = ((JSONArray)jsonObject.get("results"))
//                            .getJSONObject(0)
//                            .getJSONObject("geometry")
//                            .getJSONObject("location")
//                            .get("lng").toString();
//                    LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
//                    if(orderLocation!=null) {
//                        if (locationMarkerCust == null) {
////                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_loc);
////                        bitmap = Common.scaleBitmap(bitmap, 70, 70);
//
//                            locationMarkerCust = mMap.addMarker(new MarkerOptions()
//                                    .position(orderLocation).title(Common.currentRequest.getName() +","+Common.currentRequest.getPhone()));
//
//
//                        }
//                    }
//                    else
//                        Toast.makeText(OrderNav.this, "Location Not Found!", Toast.LENGTH_SHORT).show();
//                    //drawing Route
////                    geoServices.getDirections(newLocation.latitude+","+newLocation.longitude,orderLocation.latitude+","+orderLocation.longitude,
////                            "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
////                        @Override
////                        public void onResponse(Call<String> call, Response<String> response) {
////                            new ParserTask().execute(response.body().toString());
////                        }
////
////                        @Override
////                        public void onFailure(Call<String> call, Throwable t) {
////
////                        }
////                    });
//                }catch (JSONException e){
//                    e.printStackTrace();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//            }
//        });
//
//
////        if(fusedLocationProviderClient!=null) {
////            fusedLocationProviderClient.getLastLocation()
////                    .addOnSuccessListener(new OnSuccessListener<Location>() {
////                                              @Override
////                                              public void onSuccess(Location location) {
////                                                  String from = new StringBuilder().append(location.getLatitude())
////                                                          .append(",")
////                                                          .append(location.getLongitude()).toString();
////                                                  String to = new StringBuilder().append(Common.currentRequest.currentLat).append(",").append(Common.currentRequest.currentLng).toString();
////                                                  compositeDisposable.add(googleAPI.getDirections("driving",
////                                                          "less_driving",from,to,
////                                                          getString(R.string.google_api_key)).subscribeOn(Schedulers.io())
////                                                          .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
////                                                              @Override
////                                                              public void accept(String s) throws Throwable {
////                                                                  try {
////                                                                      JSONObject jsonObject = new JSONObject(s);
////
////                                                                      JSONArray routes = jsonObject.getJSONArray("routes");
////                                                                      for (int i = 0; i < routes.length(); i++) {
////                                                                          JSONObject route = routes.getJSONObject(i);
////                                                                          JSONObject poly = route.getJSONObject("overview_polyline");
////                                                                          String polyline = poly.getString("points");
////                                                                          polyLineList = Common.decodePoly(polyline);
////                                                                      }
////                                                                      polylineOptions = new PolylineOptions();
////                                                                      polylineOptions.color(Color.RED);
////                                                                      polylineOptions.width(8);
////                                                                      polylineOptions.startCap(new SquareCap());
////                                                                      polylineOptions.jointType(JointType.ROUND);
////                                                                      polylineOptions.addAll(polyLineList);
////                                                                      //     polylineOptions.geodesic(true);
////                                                                      redPolyline = mMap.addPolyline(polylineOptions);
////                                                                      JSONObject object = routes.getJSONObject(0);
////                                                                      JSONArray legs = object.getJSONArray("legs");
////                                                                      JSONObject legsObjects = legs.getJSONObject(0);
////
//////get the distance
////                                                                      JSONObject distance = legsObjects.getJSONObject("distance");
////                                                                      String distanceCal = distance.getString("text");
////
//////get the time
////                                                                      JSONObject time = legsObjects.getJSONObject("duration");
////                                                                      String duration = time.getString("text");
////                                                                      Toast.makeText(OrderNav.this, distanceCal+","+duration, Toast.LENGTH_SHORT).show();
////                                                                  }
////                                                                  catch (Exception e){
////                                                                      Toast.makeText(OrderNav.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
////                                                                  }
////
////
////
////
////
//
//
////
////                        if (bikeMarker == null) {
////                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vector_bike_top);
////                            bitmap = Common.scaleBitmap(bitmap, 100, 100);
////                            bikeMarker = mMap.addMarker(new MarkerOptions()
////                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
////                                    .position(bikeLocation).title("You"));
////
////                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation,18));
////
////                        }
////                        if(isInit && prevLocation!=null){
////
////                            String from = new StringBuilder().append(prevLocation.getLatitude())
////                                    .append(",")
////                                    .append(prevLocation.getLongitude()).toString();
////                            String to = new StringBuilder().append(bikeLocation.latitude).append(",").append(bikeLocation.longitude).toString();
////                            LatLng prevLocationLatLng = new LatLng(prevLocation.getLatitude(),prevLocation.getLongitude());
////                            MarkerAnimation.animateMarkerToGB(bikeMarker,bikeLocation,new LatLngInterpolater.Spherical());
////                            bikeMarker.setRotation(Common.getBearing(prevLocationLatLng,bikeLocation));
////                            mMap.animateCamera(CameraUpdateFactory.newLatLng(bikeLocation));
////
////                            moveMarkerAnimation(bikeMarker,from,to);
////
////                            prevLocation = currentlocation;
////
////                        }
////
////                        if(!isInit){
////                            isInit = true;
////                            prevLocation = currentlocation;
////                        }
////                        //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation, 15));
////                        LatLng newLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
////                        drawRoute(newLocation, Common.currentRequest.getAddress());
////
////                        updateLocation(currentlocation);
////
//
//                                                              }
//
//                                                          }, new Consumer<Throwable>() {
//                                                              @Override
//                                                              public void accept(Throwable throwable) throws Throwable {
//                                                                  if(throwable!=null)
//                                                                      Toast.makeText(OrderNav.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                                                              }};
//
//                                              }
//                                              }
//
//                    ).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(OrderNav.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task<Location> task) {
//                    if (task != null &&  task.getResult()!=null) {
//                        currentlocation = task.getResult();
//                        LatLng bikeLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
//
//                }
//            }});
//        }
  }

  private void drawRoute(LatLng newLocation, String address) {

    geoServices.getCode(address, "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        try {

          JSONObject jsonObject = new JSONObject(response.body());

          String lat = ((JSONArray) jsonObject.get("results"))
            .getJSONObject(0)
            .getJSONObject("geometry")
            .getJSONObject("location")
            .get("lat").toString();

          String lng = ((JSONArray) jsonObject.get("results"))
            .getJSONObject(0)
            .getJSONObject("geometry")
            .getJSONObject("location")
            .get("lng").toString();
          LatLng orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
          if (orderLocation != null) {
            if (locationMarkerCust == null) {

              locationMarkerCust = mMap.addMarker(new MarkerOptions()
                .position(orderLocation).title(Common.currentRequest.getName() + "," + Common.currentRequest.getPhone()));
            }
          } else
            Toast.makeText(OrderNav.this, "Location Not Found!", Toast.LENGTH_SHORT).show();
          //drawing Route
          geoServices.getDirections(newLocation.latitude + "," + newLocation.longitude, orderLocation.latitude + "," + orderLocation.longitude,
            "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
              new ParserTask().execute(response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
          });
        } catch (JSONException e) {
          e.printStackTrace();

        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {

      }
    });
  }

  android.app.AlertDialog waitingDialog;

  public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    ProgressDialog dialog = new ProgressDialog(OrderNav.this);

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog.setMessage("Please Wait..");
      if (!((Activity) OrderNav.this).isFinishing()) {
        //show dialog
        dialog.show();

      }

    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
      JSONObject jsonObject;
      List<List<HashMap<String, String>>> routes = null;
      try {
        jsonObject = new JSONObject(strings[0]);
        DirectionJSONParser parser = new DirectionJSONParser();

        routes = parser.parse(jsonObject);

      } catch (JSONException e) {
        e.printStackTrace();
      }
      return routes;

    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
      dialog.dismiss();
      ArrayList points = null;
      PolylineOptions lineOptions = null;

      for (int i = 0; i < lists.size(); i++) {
        points = new ArrayList();
        lineOptions = new PolylineOptions();

        List<HashMap<String, String>> path = lists.get(i);

        for (int j = 0; j < path.size(); j++) {
          HashMap<String, String> point = path.get(j);
          double lat = Double.parseDouble(point.get("lat"));
          double lng = Double.parseDouble(point.get("lng"));

          LatLng position = new LatLng(lat, lng);
          points.add(position);
        }
        lineOptions.addAll(points);
        lineOptions.width(8);
        lineOptions.color(R.color.Red);
        lineOptions.geodesic(true);
      }
      if (lineOptions != null)
        mMap.addPolyline(lineOptions);
    }
  }

  private void buildLocationCallback() {
    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        currentlocation = locationResult.getLastLocation();


        LatLng bikeLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());

        updateLocation(locationResult.getLastLocation());
        if (bikeMarker == null) {
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps_icon);
          bitmap = Common.scaleBitmap(bitmap, 150, 100);
          bikeMarker = mMap.addMarker(new MarkerOptions()
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            .position(bikeLocation).title("You"));
          mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation, 15));

        }

        if (isInit && prevLocation != null) {

          String from = new StringBuilder().append(prevLocation.getLatitude())
            .append(",")
            .append(prevLocation.getLongitude()).toString();
          String to = new StringBuilder().append(bikeLocation.latitude).append(",").append(bikeLocation.longitude).toString();
          LatLng prevLocationLatLng = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
          MarkerAnimation.animateMarkerToGB(bikeMarker, bikeLocation, new LatLngInterpolater.Spherical());
          bikeMarker.setRotation(Common.getBearing(prevLocationLatLng, bikeLocation));
          mMap.animateCamera(CameraUpdateFactory.newLatLng(bikeLocation));

          if (!from.equals(to))
            moveMarkerAnimation(bikeMarker, from, to);

          prevLocation = currentlocation;

        }
        if (!isInit) {
          isInit = true;
          prevLocation = currentlocation;
        }
      }
    };

  }


  private void buildLocationRequest() {
    locationRequest = new LocationRequest();
    locationRequest.setInterval(15000);
    locationRequest.setFastestInterval(10000);
    locationRequest.setSmallestDisplacement(20f);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    setUpOrderDetails();

  }

  @Override
  protected void onDestroy() {
    if (fusedLocationProviderClient != null)
      fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    if (compositeDisposable != null)
      compositeDisposable.clear();
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (fusedLocationProviderClient != null) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    }
}
