package com.just.justbikev2;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.BikationModel;
import com.just.justbikev2.Model.Upi;

public class PickupPoints extends AppCompatActivity implements
   OnMapReadyCallback{
  TextView name,location;
  ImageView transparentImageView;
  ScrollView mainScrollView;
  SupportMapFragment mapFragment;
  String cityChoice, pickupPoint;
  double latitudeOfJB, longitudeOfJB ;
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
    setContentView(R.layout.activity_pickup_points);

    bindViews();


    cityChoice="Kolkata";   // Kolkata
    pickupPoint= "Purbalok, Infront of Maruti Suzuki Showroom ,Santoshpur, Kolkata, West Bengal 700099";
    DatabaseReference upi = FirebaseDatabase.getInstance().getReference().child("Upi");
    upi.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for(DataSnapshot postData : dataSnapshot.getChildren()){
          Upi upiModel = postData.getValue(Upi.class);
          if(Common.cityId.equals(upiModel.getCityId())) {
            pickupPoint = upiModel.getAddressOfPickUp();
            cityChoice = upiModel.getCity();

            latitudeOfJB = upiModel.getLat();
            longitudeOfJB = upiModel.getLng();
          }
        }
        loadData();

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(PickupPoints.this, "No proper internet connection. Loading failed!", Toast.LENGTH_SHORT).show();
      }
    });

  }

  private void loadData() {
    name.setText(cityChoice);
    location.setText(pickupPoint);
    mapFragment.getMapAsync((OnMapReadyCallback) this);

  }

  private void bindViews() {
     mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.mapFrag);
    name = findViewById(R.id.name);
    location = findViewById(R.id.location);

    mainScrollView = findViewById(R.id.scrollView);
    transparentImageView = findViewById(R.id.transparent_image);
    transparentImageView.setOnTouchListener(new View.OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
          case MotionEvent.ACTION_DOWN:
            // Disallow ScrollView to intercept touch events.
            mainScrollView.requestDisallowInterceptTouchEvent(true);
            // Disable touch on transparent view
            return false;

          case MotionEvent.ACTION_UP:
            // Allow ScrollView to intercept touch events.
            mainScrollView.requestDisallowInterceptTouchEvent(false);
            return true;

          case MotionEvent.ACTION_MOVE:
            mainScrollView.requestDisallowInterceptTouchEvent(true);
            return false;

          default:
            return true;
        }
      }
    });
  }
  private GoogleMap mMap;
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    LatLng pickupLocation = new LatLng(latitudeOfJB,longitudeOfJB);
    mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Just Bike"));
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation,12));
    mMap.getUiSettings().setZoomControlsEnabled(true);

  }
}
