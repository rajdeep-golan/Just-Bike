package com.just.justbikev2;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.just.justbikev2.Admin.OrderNav;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Common.DirectionJSONParser;
import com.just.justbikev2.Common.LatLngInterpolater;
import com.just.justbikev2.Common.MarkerAnimation;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.Model.Request;
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
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderNavClient extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

  private GoogleMap mMap;
  private LocationRequest locationRequest;
  private LocationCallback locationCallback;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private Location currentlocation;

  private IGeoCoordinates geoServices;

  private Marker bikeMarker, locationMarkerCust;
  private Order order;

  String contactNo = "+918585858586";
  private static int CALL_CODE = 34;

  private boolean isInit = false;
  private Location prevLocation;

  private Handler handler;
  private int next, index;
  private LatLng start, end;
  private float v;
  private double lat, lng;

  private Polyline blackPolyline, greyPolyline;
  private PolylineOptions polylineOptions, blackPolylineOptions;
  private List<LatLng> polyLineList;
  private IGoogleAPI googleAPI;
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  private DatabaseReference executiveRef;
  //Move Marker
  private Handler handlerNew;
  private int nextB, indexB;
  private LatLng startB, endB;
  private float vB;
  private double latB, lngB;
  private boolean isInitB = false;

  private String isFromAdmin="0";
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }
    setContentView(R.layout.activity_order_nav);
    ButterKnife.bind(this);

    buildLocationRequest();
    buildLocationCallback();
    geoServices = Common.getGeoCodeServices();

    googleAPI = RetrofitClientAdmin.getInstance().create(IGoogleAPI.class);
    if (getIntent() != null) {
      orderId = getIntent().getStringExtra("orderId");
      isFromAdmin = getIntent().getStringExtra("isFromAdmin");
      if(isFromAdmin!=null && isFromAdmin.equals("1"))
        contactNo = Common.currentRequest.getPhone();
    }
    subscribeJBMovement();

    setUpOrderDetails();

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
          detailsLayout.setVisibility(View.GONE);
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
          mapFragment.getMapAsync(OrderNavClient.this::onMapReady);
          fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(OrderNavClient.this);
          if (ActivityCompat.checkSelfPermission(OrderNavClient.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrderNavClient.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
          }
          fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
          Toast.makeText(OrderNavClient.this, "Grant Location Permissions before using app.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

        }
      }).check();


    //  updateLocation(currentlocation);

  }

  private void subscribeJBMovement() {
    executiveRef = FirebaseDatabase.getInstance().getReference("Requests").child(orderId);
    executiveRef.addValueEventListener(this);

  }


  //    private void updateLocation(Location currentlocation) {
//        Map<String,Object> update_data = new HashMap<>();
//        update_data.put("currentLat",currentlocation.getLatitude());
//        update_data.put("currentLng",currentlocation.getLongitude());
//        if(order!=null){
//            FirebaseDatabase.getInstance().getReference("Requests").child(orderId).updateChildren(update_data)
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(OrderNavClient.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
  private Location updateJBLocation(Location currentlocation) {
    if (order != null) {
      FirebaseDatabase.getInstance().getReference("Requests").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if (dataSnapshot.exists()) {
            Request request = dataSnapshot.getValue(Request.class);
            Common.currentRequest = request;
            if (request != null) {
 //             latOfJustBike = request.currentLat;
//                            lngOfJustBike = request.currentLat;
//                            currentlocation.setLatitude(latOfJustBike);
//                            currentlocation.setLongitude(lngOfJustBike);

              LatLng bikeLocation = new LatLng(request.currentLat, request.currentLng);

              if (bikeMarker == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.biker_assasin);
                bitmap = Common.scaleBitmap(bitmap, 60, 100);
                bikeMarker = mMap.addMarker(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                  .position(bikeLocation).title("Just Bike"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation, 18));

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

                moveMarkerAnimation(bikeMarker, from, to);

                prevLocation = currentlocation;

              }
              if (!isInit) {
                isInit = true;
                prevLocation = currentlocation;
              }
              //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation, 15));

              LatLng newLocation = new LatLng(request.currentLat, request.currentLng);
              drawRoute(newLocation, Common.currentRequest.getAddress());


            }
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
          Toast.makeText(OrderNavClient.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
      });
    }
    return currentlocation;

  }


  private double latOfJustBike, lngOfJustBike;

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
          polylineOptions.geodesic(true);
          greyPolyline = mMap.addPolyline(polylineOptions);

          blackPolylineOptions = new PolylineOptions();
          blackPolylineOptions.color(Color.BLACK);
          blackPolylineOptions.width(8);
          blackPolylineOptions.startCap(new SquareCap());
          blackPolylineOptions.jointType(JointType.ROUND);
          blackPolylineOptions.addAll(polyLineList);
          blackPolylineOptions.geodesic(true);
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
              if (index < polyLineList.size() - 2) {
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
                  if (start != null && end != null ) {
                    v = valueAnimator.getAnimatedFraction();
                    lng = v * end.longitude + (1 - v) * start.longitude;
                    lat = v * end.latitude + (1 - v) * start.latitude;
                    LatLng newPos = new LatLng(lat, lng);

                    bikeMarker.setPosition(newPos);
                    bikeMarker.setAnchor(0.5f, 0.5f);
                    bikeMarker.setRotation(Common.getBearing(start, newPos));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(bikeMarker.getPosition()));
                  }
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
            Toast.makeText(OrderNavClient.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
    String data = Paper.book().read("orderData");
    String endTime = Paper.book().read("EndTime");
    String address = Paper.book().read("address");
    String phoneNumber = Paper.book().read("phoneNumber");

    if (!TextUtils.isEmpty(data)) {
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
 //       Uncomment the below code for Admin to call the user
//        if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber))
//          contactNo = phoneNumber;
      }
    } else
      Toast.makeText(this, "Order is null", Toast.LENGTH_SHORT).show();

  }

  private void drawRoute(LatLng newLocation, String address) {
    geoServices.getCode(address, "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        try {

          JSONObject jsonObject = new JSONObject(response.body());
          if(!((JSONArray)jsonObject.get("results")).isNull(0)) {
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
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_loc);
//                        bitmap = Common.scaleBitmap(bitmap, 70, 70);

                locationMarkerCust = mMap.addMarker(new MarkerOptions()
                  .position(orderLocation).title("You"));     // Common.currentRequest.getName() +","+Common.currentRequest.getPhone()
              }
            } else
              Toast.makeText(OrderNavClient.this, "Location Not Found!", Toast.LENGTH_SHORT).show();
            //drawing Route
            geoServices.getDirections(newLocation.latitude + "," + newLocation.longitude, orderLocation.latitude + "," + orderLocation.longitude,
              "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
              @Override
              public void onResponse(Call<String> call, Response<String> response) {
                new OrderNavClient.ParserTask().execute(response.body().toString());
              }

              @Override
              public void onFailure(Call<String> call, Throwable t) {

              }
            });
          }
        }
        catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {

      }
    });
  }

  android.app.AlertDialog waitingDialog;

  @Override
  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    //Save position
    String from = new StringBuilder().append(Common.currentRequest.currentLat)
      .append(",")
      .append(Common.currentRequest.currentLng).toString();
    //Update position
    Common.currentRequest = dataSnapshot.getValue(Request.class);
    String to = new StringBuilder().append(Common.currentRequest.currentLat)
      .append(",")
      .append(Common.currentRequest.currentLng).toString();
    ;
    if (dataSnapshot.exists()) {
      if (isInitB)
        moveMarkerAnimation(bikeMarker, from, to);
      else
        isInitB = true;
    }
  }

  @Override
  public void onCancelled(@NonNull DatabaseError databaseError) {

  }

  public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    ProgressDialog dialog = new ProgressDialog(OrderNavClient.this);

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog.setMessage("Please Wait..");
      if(!((Activity) OrderNavClient.this).isFinishing())
         dialog.show();

    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
      JSONObject jsonObject;
      List<List<HashMap<String, String>>> routes = null;
      try {
        jsonObject = new JSONObject(strings[0]);
        if(jsonObject.get("status").equals("ZERO_RESULTS"));
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
      ArrayList points  = new ArrayList<>();
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
        if(locationResult!=null) {
          currentlocation = locationResult.getLastLocation();
          LatLng bikeLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());

          if (bikeMarker == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.biker_assasin);
            bitmap = Common.scaleBitmap(bitmap, 60, 100);
            bikeMarker = mMap.addMarker(new MarkerOptions()
              .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
              .position(bikeLocation).title("Just Bike"));

          } else {
            bikeMarker.setPosition(bikeLocation);
          }
          mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLocation, 15));
          currentlocation = updateJBLocation(currentlocation);
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
    if(Common.currentRequest!=null)
      drawRoutes(Common.currentRequest);
  }

  private void drawRoutes(Request currentRequest) {
    geoServices.getCode(currentRequest.getAddress(), "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        try {

          JSONObject jsonObject = new JSONObject(response.body());
          if(!((JSONArray)jsonObject.get("results")).isNull(0)) {
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
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_loc);
//                        bitmap = Common.scaleBitmap(bitmap, 70, 70);

                locationMarkerCust = mMap.addMarker(new MarkerOptions()
                  .position(orderLocation).title("You"));     // Common.currentRequest.getName() +","+Common.currentRequest.getPhone()
              }
            } else
              Toast.makeText(OrderNavClient.this, "Location Not Found!", Toast.LENGTH_SHORT).show();
            LatLng locationBike = new LatLng(currentRequest.currentLat, currentRequest.currentLng);

            if (bikeMarker == null) {
              Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.biker_assasin);
              bitmap = Common.scaleBitmap(bitmap, 60, 100);
              bikeMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(locationBike).title("Just Bike"));
              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationBike, 15));


            } else {
              bikeMarker.setPosition(locationBike);
              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationBike, 15));
            }
            //drawing Route
            if(locationBike.latitude>0 && locationBike.latitude > 0) {
              geoServices.getDirections(locationBike.latitude + "," + locationBike.longitude, orderLocation.latitude + "," + orderLocation.longitude,
                "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                  new OrderNavClient.ParserTask().execute(response.body().toString());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                  Toast.makeText(OrderNavClient.this, "Error!", Toast.LENGTH_SHORT).show();
                }
              });
            }
          }
        } catch (JSONException e) {
          e.printStackTrace();

        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
      }
    });

  }

  @Override
  protected void onDestroy() {
    if (fusedLocationProviderClient != null)
      fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    if (compositeDisposable != null)
      compositeDisposable.clear();
    if(executiveRef!=null)
      executiveRef.removeEventListener(this);
    isInitB = false;
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (fusedLocationProviderClient != null) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
      }else //Request granted!
      fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
  }
}
