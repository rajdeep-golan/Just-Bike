package com.just.justbikev2.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Common.DirectionJSONParser;
import com.just.justbikev2.R;
import com.just.justbikev2.Remote.IGeoCoordinates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener , LocationListener {

    private GoogleMap mMap;
    private static int PLAY_SERVICES_RESOLUTION_REQUEST =1000;
    private final static int LOCATION_PERMISSION_REQUEST =1001;

    private Location mlocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private static int UPDATE_INTERVAL=1000;
    private static int FATEST_INTERVAL=5000;
    private static int DISPLACEMENT=10;

    private IGeoCoordinates geoServices;

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
        setContentView(R.layout.activity_tracking_order);

        geoServices = Common.getGeoCodeServices();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestRuntimePermission();
        else{
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }
        displayLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    Marker locationMarker,locationMarkerBike;
    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestRuntimePermission();
        else
        {
            mlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(mlocation !=null)
            {
                double latitude = mlocation.getLatitude();
                double longitude = mlocation.getLongitude();

                //Adding Marker to current location and moving camera
                LatLng newLocation = new LatLng(latitude,longitude);

                if(locationMarker==null) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_bike_top);
                    bitmap = Common.scaleBitmap(bitmap,100,100);
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Your Location")
                            .position(newLocation);

                    locationMarker = mMap.addMarker(markerOptions);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0f));

                //Adding marker for order and drawing route
                drawRoute(newLocation,Common.currentRequest.getAddress());

            }

        }
    }

    private void drawRoute(LatLng newLocation, String address) {
        geoServices.getCode(address,"AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());

//                    JSONArray routes = jsonObject.getJSONArray("routes");
//                    JSONObject object = routes.getJSONObject(0);
//                    JSONArray legs = object.getJSONArray("legs");
//                    JSONObject legsObjects = legs.getJSONObject(0);
//
////get the distance
//                    JSONObject distance = legsObjects.getJSONObject("distance");
//                    String distanceCal = distance.getString("text");
//
////get the time
//                    JSONObject time = legsObjects.getJSONObject("duration");
//                    String duration = time.getString("text");
//                    Toast.makeText(TrackingOrder.this, distanceCal+","+duration, Toast.LENGTH_SHORT).show();

                    String lat = ((JSONArray)jsonObject.get("results"))
                                    .getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location")
                                    .get("lat").toString();

                    String lng = ((JSONArray)jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lng").toString();
                    LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                    if(orderLocation!=null) {
                        if (locationMarkerBike == null) {
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_loc);
//                        bitmap = Common.scaleBitmap(bitmap, 70, 70);

                            locationMarkerBike = mMap.addMarker(new MarkerOptions()
                                    .position(orderLocation).title(Common.currentRequest.getName() +","+Common.currentRequest.getPhone()));
                        }
                    }
                    else
                        Toast.makeText(TrackingOrder.this, "Location Not Found!", Toast.LENGTH_SHORT).show();
                    //drawing Route
                    geoServices.getDirections(newLocation.latitude+","+newLocation.longitude,orderLocation.latitude+","+orderLocation.longitude,
                            "AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE").enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            new ParserTask().execute(response.body().toString());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private  void  createLocationRequest() {
       locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    private boolean checkPlayServices() {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if(resultCode!=ConnectionResult.SUCCESS)
            {
                if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                    GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
                else {
                    Toast.makeText(this, "Your Device does not supoort Google Services", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return false;
            }
            return true;
    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                                                                 Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION
                                                                     } , LOCATION_PERMISSION_REQUEST);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            displayLocation();
            startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,(com.google.android.gms.location.LocationListener)this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        if(googleApiClient!=null)
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
     if(googleApiClient!=null)
         googleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onLocationChanged(Location location) {

        mlocation = location;
        displayLocation();
    }
    android.app.AlertDialog waitingDialog;
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String , String>>>>{
        ProgressDialog dialog = new ProgressDialog(TrackingOrder.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please Wait..");
            dialog.show();

        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String,String>>> routes =null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes = parser.parse(jsonObject);

            }catch (JSONException e){
                e.printStackTrace();
            }
            return routes;

        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            dialog.dismiss();
            ArrayList points = null;
            PolylineOptions lineOptions = null;

            for(int i=0;i<lists.size();i++){
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for(int j=0;j<path.size();j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat,lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(R.color.Green);
                lineOptions.geodesic(true);
            }
            mMap.addPolyline(lineOptions);
        }
    }
}
