package com.just.justbikev2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.just.justbikev2.Model.BikationModel;

public class BikationDetails extends AppCompatActivity implements
   OnMapReadyCallback{
  TextView name,location,details,timeToSpend,budget,hotel,preferredVehicle;
  BikationModel model;
  ImageView transparentImageView;
  ScrollView mainScrollView;

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
    setContentView(R.layout.activity_bikation_details);

    bindViews();

    if(getIntent()!=null)
      model = (BikationModel)getIntent().getSerializableExtra("Place");
    loadData();
  }

  private void loadData() {
    name.setText(model.getName());
    location.setText(model.getLocation());
    details.setText(model.getDetails());
    timeToSpend.setText(model.getTimeToSpend());
    budget.setText(model.getBudget());
    hotel.setText(model.getHotelOrCamp());
    preferredVehicle.setText(model.getPreferredVehicle());
  }

  private void bindViews() {
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.mapFrag);
    mapFragment.getMapAsync((OnMapReadyCallback) this);
    name = findViewById(R.id.name);
    location = findViewById(R.id.location);
    details= findViewById(R.id.details);
    timeToSpend = findViewById(R.id.timeToSpend);
    budget = findViewById(R.id.budget);
    hotel= findViewById(R.id.hotel);
    preferredVehicle = findViewById(R.id.preferredVehicle);
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
    mMap.getUiSettings().setZoomControlsEnabled(true);
    LatLng latLng = new LatLng(model.getLat(),model.getLng());
    mMap.addMarker(new MarkerOptions().position(latLng).title(model.getName()));
    mMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

  }
}
