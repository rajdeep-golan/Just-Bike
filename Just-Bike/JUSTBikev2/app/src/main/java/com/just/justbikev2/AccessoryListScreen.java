package com.just.justbikev2;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.AccessoryModel;
import com.just.justbikev2.Model.CouponCode;
import com.just.justbikev2.Model.MyResponse;
import com.just.justbikev2.Model.Notification;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.Model.PriceHike;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.Model.Sender;
import com.just.justbikev2.Model.Token;
import com.just.justbikev2.Model.Upi;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.Model.Vehicle;
import com.just.justbikev2.Remote.APIService;
import com.just.justbikev2.Remote.IGoogleServices;
import com.just.justbikev2.ViewHolder.AccessoryListHolder;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessoryListScreen extends AppCompatActivity implements  PaymentResultListener , OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks , LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    RecyclerView accessoriesRecyclerView;
    String bikeId = "";
    TextView total;
    FirebaseRecyclerAdapter<AccessoryModel, AccessoryListHolder> adapter;

    int amt = 0;

    DatabaseReference user_table;

    ImageButton bookBtn;

    String bikeCost;
    HashMap<String, Integer> costMap;
    APIService apiService;
    IGoogleServices mGoogleServices;
    AutocompleteSupportFragment places_fragment;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
    PlacesClient placesClient;
    private Location mlocation;

    String bikeName, bikeImage;
    String startDateString ="", endDateString ="";

    RadioButton thirty,hundred;

    DatabaseReference databaseReference;
    DatabaseReference contacts;
    Map<String, String> namePhoneMap = new HashMap<String, String>();
    private final int CONTACTS_CODE = 124;


    private final static int LOCATION_REQUEST_CODE = 23;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int FASTEST_INTERVAL = 3000;
    public static final int DISPLACEMENT = 10;
    public static final int PLAY_SERVICES_REQUEST = 93;

    private LocationRequest mlocationRequest;
    private GoogleApiClient mgoogleApiClient;

    private CheckBox walletCheckBox;

    RadioButton helmet0 , helmet1 , helmet2;

    String cityDeliveryCost;

  Locale locale = new Locale("en", "in");
  NumberFormat format = NumberFormat.getCurrencyInstance(locale);

  int daysTotal;
  int startDateDigit = 0;
  int startMonthDigit = 0;
  int endDateDigit = 0;
  int endMonthDigit=0;
  int walletBalance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.accessories_layout);


        //Preloading for Payment
        Checkout.preload(getApplicationContext());
        apiService = Common.getFCMService();
        mGoogleServices = Common.getGoogleMapsAPI();

        reverseGeocoder = new Geocoder(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
        contacts = FirebaseDatabase.getInstance().getReference("Contacts");
        accessoriesRecyclerView = findViewById(R.id.accessoriesRecyclerView);
        total = findViewById(R.id.totalValue);
        bookBtn = findViewById(R.id.bookBtn);
        user_table = FirebaseDatabase.getInstance().getReference("User");

        accessoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        accessoriesRecyclerView.setHasFixedSize(true);

        locale = new Locale("en", "in");
         format = NumberFormat.getCurrencyInstance(locale);

        initPlaces();

      upiId="rajput.dbs@dbs";   // Kolkata
      cityDeliveryCost = "100";
        DatabaseReference upi = FirebaseDatabase.getInstance().getReference().child("Upi");
        upi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postData : dataSnapshot.getChildren()){
                    Upi upiModel = postData.getValue(Upi.class);
                    if(Common.cityId.equals(upiModel.getCityId())) {
                      upiId = upiModel.getUpiId();
                      cityDeliveryCost = upiModel.getDeliveryCost();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            bikeId = intent.getStringExtra("bikeId");
            bikeCost = intent.getStringExtra("bikeCost");
            bikeName = intent.getStringExtra("bikeName");
            bikeImage = intent.getStringExtra("bikeImage");
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");
             String noOfDays = intent.getStringExtra("noOfDays");
            startDateString = intent.getStringExtra("startDateString");
            endDateString = intent.getStringExtra("endDateString");

            String dateStart[] = startDate.split("/");
            String dateEnd[] = endDate.split("/");



          if(dateStart.length>1 ) {

            if(noOfDays!=null)    //It is from Schedule a vehicle , so it is in format MM/DD/YYYY
            {
              startDateDigit = Integer.parseInt(dateStart[1]);
              startMonthDigit = Integer.parseInt(dateStart[0]);
              endDateDigit = Integer.parseInt(dateEnd[1]);
              endMonthDigit = Integer.parseInt(dateEnd[0]);
            }else {
              startDateDigit = Integer.parseInt(dateStart[0]);
              startMonthDigit = Integer.parseInt(dateStart[1]);
              endDateDigit = Integer.parseInt(dateEnd[0]);
              endMonthDigit = Integer.parseInt(dateEnd[1]);
            }
            Date date1 = new GregorianCalendar(Integer.parseInt(dateStart[2].substring(0, 4)), startMonthDigit, startDateDigit).getTime();
            Date date2 = new GregorianCalendar(Integer.parseInt(dateEnd[2].substring(0, 4)), endMonthDigit, endDateDigit).getTime();
            long diff = date2.getTime() - date1.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            daysTotal = (int) hours / 24;

            if (daysTotal == 0)   //So that even for 6 or 12 hours (or hours < 24 ) we we charge the extra amount per day
              daysTotal = 1;
          }
          else if(noOfDays!=null)
            daysTotal = noOfDays.equals("0")?1:Integer.parseInt(noOfDays);
          else if(noOfDays == null)
            daysTotal =1;
          //Checking for Price Hike
          DatabaseReference priceHike = FirebaseDatabase.getInstance().getReference("PriceHike");
          priceHike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              for(DataSnapshot postData : dataSnapshot.getChildren()){
                PriceHike price = postData.getValue(PriceHike.class); // Always in DD/MM/YYY format
                if(startDateDigit >= Integer.parseInt(price.getStartDate().substring(0,price.getStartDate().indexOf('/')))
                  && startMonthDigit >= Integer.parseInt(price.getStartDate().substring(3,5))
                    && endDateDigit <= Integer.parseInt(price.getEndDate().substring(0,price.getEndDate().indexOf('/')))
                  && endMonthDigit >= Integer.parseInt(price.getEndDate().substring(3,5)))
                {
                  amt+=(Integer.parseInt(price.getPrice())* daysTotal);
                  total.setText(format.format(amt));
                  break;
                }

              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });


            Order order = new Order(bikeId, bikeName, 1, bikeCost, "0", bikeImage);
            orderList = new ArrayList<>();
            orderList.add(order);
            //int startSearch,endSearch=intent.getExtra ; see if these 2 are !=null , then hide spinner and display date

            //  if (!bikeId.isEmpty()) {
            if (Common.isConnectedToInternet(this)) {
                loadAccessories();
                if (adapter != null)
                    adapter.startListening();
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //Runtime permission
        if (ActivityCompat.checkSelfPermission(AccessoryListScreen.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(AccessoryListScreen.this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_CODE);
//        else
//            getContactList();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleAPIClient();
                createLocationRequest();
            }
        }

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.currentUser==null)
                {
                    Toast.makeText(AccessoryListScreen.this, "Login to continue", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AccessoryListScreen.this,SignInAcitivity.class);
                    startActivity(i);
                }
//                else if(Common.currentUser.getVerified()==null || (Common.currentUser.getVerified()!=null && !Common.currentUser.getVerified().equals("1"))) {
//                    Toast.makeText(AccessoryListScreen.this, "Profile not verified! Please contact  8585858586 for instant help", Toast.LENGTH_LONG).show();
//                    Intent i = new Intent(AccessoryListScreen.this,Articles.class);
//                    i.putExtra("updateProfile","1");
//                    startActivity(i);
//                }
                else
                  openPickUpDropDialog();

            }
        });

        costMap = new HashMap<>();


        amt = Integer.parseInt(bikeCost);

        total.setText(format.format(amt));

      helmet0 = findViewById(R.id.helmet0);
      helmet1 = findViewById(R.id.helmet1);
      helmet2 = findViewById(R.id.helmet2);

      setHelmetOnClicks();


    }
    int noOfHelmets = 2;

  private void setHelmetOnClicks() {
      helmet2.setChecked(true);
    helmet0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          noOfHelmets=0;
        }
      }
    });
    helmet1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          noOfHelmets=1;
        }
      }
    });
    helmet2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
           noOfHelmets = 2;
        }
      }
    });
  }

double latitudeOfJB=22.498345 , longitudeOfJB=88.399769;

  public OnMapReadyCallback onMapReadyCallback1(){
  return new OnMapReadyCallback() {
    @Override
    public void onMapReady(GoogleMap googleMap) {
      mMap = googleMap;
      DatabaseReference upi = FirebaseDatabase.getInstance().getReference().child("Upi");
      upi.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for(DataSnapshot postData : dataSnapshot.getChildren()){
            Upi upiModel = postData.getValue(Upi.class);
            if(Common.cityId.equals(upiModel.getCityId())) {
              hubAddress.setText("Address: "+upiModel.getAddressOfPickUp());
              latitudeOfJB = upiModel.getLat();
              longitudeOfJB = upiModel.getLng();
              LatLng pickupLocation = new LatLng(latitudeOfJB,longitudeOfJB);
              mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Just Bike"));
              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation,8));
              mMap.getUiSettings().setZoomControlsEnabled(true);
            }
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });


    }
  };
}
int deliveryCost = 0;
int askForDeliveryLocation = 1; // 1: JB will deliver ,so need clients location , 0: clients comes to nearest HUB, so don't ask for location
int selfPick = 0 , selfDrop = 0;
TextView hubAddress;
  private void openPickUpDropDialog(){
    AlertDialog.Builder alertdialog = new AlertDialog.Builder(AccessoryListScreen.this,R.style.RedDialogTheme);
    alertdialog.setTitle("Delivery Options");
    LayoutInflater inflater = LayoutInflater.from(this);
    View layout_home = inflater.inflate(R.layout.delivery_option_dialog,null);
    alertdialog.setView(layout_home);

    NestedScrollView mainScrollView = layout_home.findViewById(R.id.scrollView);

    CheckBox selfPickupCheckbox = layout_home.findViewById(R.id.selfPickupCheckbox);
    CheckBox pickupCheckbox = layout_home.findViewById(R.id.pickupCheckbox);
    CheckBox selfDropCheckbox = layout_home.findViewById(R.id.selfDropCheckbox);
    CheckBox dropCheckbox = layout_home.findViewById(R.id.dropCheckbox);
    ImageView transparentImageView = layout_home.findViewById(R.id.transparent_image);
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
    pickupCheckbox.setText(pickupCheckbox.getText().toString()+cityDeliveryCost);
    dropCheckbox.setText(dropCheckbox.getText().toString()+cityDeliveryCost);
    hubAddress = layout_home.findViewById(R.id.hubAddress);

    pickupCheckbox.setChecked(true);
    dropCheckbox.setChecked(true);
    askForDeliveryLocation=1;
    int costForDeliveryInt = Integer.parseInt(cityDeliveryCost);
    deliveryCost = costForDeliveryInt*2;  //For both pickup and delivery

    dropCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          selfDropCheckbox.setChecked(false);
          selfDrop=0;
          deliveryCost+=costForDeliveryInt;
        }
        else {
          selfDropCheckbox.setChecked(true);
          selfDrop=1;
        }
      }
    });
    selfDropCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          dropCheckbox.setChecked(false);
          selfDrop=1;
          deliveryCost-=costForDeliveryInt;
        }
        else {
          dropCheckbox.setChecked(true);
          selfDrop=0;
        }
      }
    });
    pickupCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          selfPickupCheckbox.setChecked(false);
          selfPick=0;
          deliveryCost+=costForDeliveryInt;
        }
        else {
          selfPickupCheckbox.setChecked(true);
          selfPick=1;
        }
      }
    });
    selfPickupCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          pickupCheckbox.setChecked(false);
          selfPick=1;
          deliveryCost-=costForDeliveryInt;
        }
        else {
          pickupCheckbox.setChecked(true);
          selfPick=0;
        }
      }
    });
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.hubMap);
    mapFragment.getMapAsync(onMapReadyCallback1());

    alertdialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(selfDrop == 1 && selfPick == 1)
          askForDeliveryLocation = 0;
        openTermsAndCondition();
        dialog.dismiss();
        if ( getSupportFragmentManager().findFragmentById(R.id.hubMap) != null)
          getSupportFragmentManager().beginTransaction()
            .remove(getSupportFragmentManager().findFragmentById(R.id.hubMap))
            .commit();
      }
    });
    alertdialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if ( getSupportFragmentManager().findFragmentById(R.id.hubMap) != null)
          getSupportFragmentManager().beginTransaction()
            .remove(getSupportFragmentManager().findFragmentById(R.id.hubMap))
            .commit();
      }
    });
    alertdialog.show();
  }

    private void openTermsAndCondition() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(AccessoryListScreen.this,R.style.RedDialogTheme);
        alertdialog.setTitle("Terms and Conditions");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.t_and_c_card,null);
        alertdialog.setView(layout_home);
        alertdialog.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(AccessoryListScreen.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(AccessoryListScreen.this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_CODE);
                else {
                 //   getContactList();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AccessoryListScreen.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        }, LOCATION_REQUEST_CODE);
                    } else {
                        intiMaps();
                    }
                  showAlertDialog();
                }
                dialog.dismiss();
            }
        });
        alertdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(AccessoryListScreen.this, "You need to agree to Terms and Conditions before booking.", Toast.LENGTH_LONG).show();
            }
        });
        alertdialog.show();
    }

    LatLng orderLocation;
    private GoogleMap mMap;

    private void createLocationRequest() {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mlocationRequest.setSmallestDisplacement(DISPLACEMENT);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private synchronized void buildGoogleAPIClient() {
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mgoogleApiClient.connect();
    }

    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        // Loop Through All The Numbers
        while (phones.moveToNext()) {

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // Cleanup the phone number
            phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
            phoneNumber = phoneNumber.replace('/', ' ');
            phoneNumber = phoneNumber.replace('.', ' ');
            phoneNumber = phoneNumber.replace('#', ' ');
            phoneNumber = phoneNumber.replace('$', ' ');
            phoneNumber = phoneNumber.replace('[', ' ');
            phoneNumber = phoneNumber.replace(']', ' ');

            // Enter Into Hash Map ; PhoneNumber : Key , Name : Value
            namePhoneMap.put(phoneNumber, name);

        }
//
//        // Get The Contents of Hash Map in Log
//        for (Map.Entry<String, String> entry : namePhoneMap.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//        }
        if (Common.currentUser == null) {
            Toast.makeText(this, "Please Login to Continue", Toast.LENGTH_LONG).show();
            Intent i = new Intent(AccessoryListScreen.this, SignInAcitivity.class);
            startActivity(i);
        } else
            contacts.child(Common.currentUser.getPhone()).setValue(namePhoneMap);

        chooseEmergencyContact();
        phones.close();
    }

  private void chooseEmergencyContact() {
    AlertDialog.Builder contactsdialog = new AlertDialog.Builder(AccessoryListScreen.this,R.style.RedDialogTheme);
    contactsdialog.setTitle("Emergency Contact");
    LayoutInflater inflater = LayoutInflater.from(this);
    View layout_home = inflater.inflate(R.layout.emergency_contact_dialog,null);
    ImageView searchBtn= layout_home.findViewById(R.id.searchBtn);
    EditText searchET= layout_home.findViewById(R.id.searchET);

    searchBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Toast.makeText(AccessoryListScreen.this, "Could not search , Please type the number", Toast.LENGTH_SHORT).show();
          searchET.setText("");
      }
    });

    contactsdialog.setView(layout_home);
    contactsdialog.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    contactsdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        Toast.makeText(AccessoryListScreen.this, "Emergency Contact not chosen", Toast.LENGTH_LONG).show();
      }
    });
    contactsdialog.show();
  }

  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intiMaps();
                    if (checkPlayServices()) {
                        buildGoogleAPIClient();
                        createLocationRequest();
                    }
                } else
                    Toast.makeText(this, "Allow location permission before booking Vehicle", Toast.LENGTH_SHORT).show();
                break;
            case CONTACTS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getContactList();
                else
                    Toast.makeText(this, "Please choose emergency contact before booking Vehicle", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
            else {
                Toast.makeText(this, "This device does not support LOCATION!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private Geocoder reverseGeocoder;
    Marker tempMarker;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
      mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    if (latLng != null) {
                        android.location.Address address = reverseGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                        String streetAddress = address.getAddressLine(0);
                        if(tempMarker!=null)
                            tempMarker.remove();
                        tempMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(streetAddress));
                        editText.setText(streetAddress);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mlocation != null) {
            Log.d("LOCATION:", "Your Location" + mlocation.getLatitude() + "," + mlocation.getLongitude());
        } else
            Log.d("LOCATION:", "Could not get your location");
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mlocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mgoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mlocation = location;
        displayLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void intiMaps() {
        if (mlocation != null) {
            mGoogleServices.getAddress(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false&key=AIzaSyD6qCL_yTdaA7SIS-ptw8bEXYbcslixF7U",
                    mlocation.getLatitude(), mlocation.getLongitude())).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
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
                        orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        if(tempMarker!=null)
                            tempMarker.remove();

//                      LatLng pickupLocation = new LatLng(latitudeOfJB,longitudeOfJB);
//                      tempMarker=  mMap.addMarker(new MarkerOptions().position(pickupLocation).draggable(true).title("Your Location"));
//                      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation,8));

                        tempMarker = mMap.addMarker(new MarkerOptions()
                                .position(orderLocation).draggable(true).title("Your Location"));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(orderLocation,12));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(AccessoryListScreen.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFrag);
        if(mapFragment!=null)
           mapFragment.getMapAsync(this);
    }

    int c = 1;
    private void loadAccessories() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Accessory")
                .limitToLast(100);

        FirebaseRecyclerOptions<AccessoryModel> options =
                new FirebaseRecyclerOptions.Builder<AccessoryModel>()
                        .setQuery(query, AccessoryModel.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<AccessoryModel, AccessoryListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AccessoryListHolder holder, int position, @NonNull AccessoryModel model) {
                holder.name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.accessoryImage);//.transform(new RoundedCornersTransformation(50,0))
              holder.checkBoxAccessory.setOnCheckedChangeListener(null);

              holder.checkBoxAccessory.setChecked(model.isSelected());
                holder.checkBoxAccessory.setClickable(false);
                holder.cost.setText(model.getCost());
              holder.checkBoxAccessory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  //set your object's last status
                  model.setSelected(isChecked);
                }
              });
                costMap.put(model.getName(), Integer.parseInt(model.getCost()));
                holder.accessoryCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Order order = new Order("Accessory " + c++, model.getName(), 1, model.getCost(), "0", model.getImage());

                        Locale locale = new Locale("en", "in");
                        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                        if (holder.checkBoxAccessory.isChecked()) {
                            holder.checkBoxAccessory.setChecked(false);
                            amt -= Integer.parseInt(model.getCost());
                            orderList.remove(order);
                        } else {
                            holder.checkBoxAccessory.setChecked(true);
                            amt += Integer.parseInt(model.getCost());
                            orderList.add(order);
                        }
                        total.setText(format.format(amt));
                    }
                });
            }

            @NonNull
            @Override
            public AccessoryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.accessories_card_view, parent, false);
                return new AccessoryListHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        accessoriesRecyclerView.setAdapter(adapter);
    }

    Place placeSelected;
    private void setupPlaceAutoComplete() {
        places_fragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places);

        places_fragment.setCountry("IN");
        places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                Toast.makeText(AccessoryListScreen.this, "" + place.getAddress(), Toast.LENGTH_SHORT).show();
                String addressFull = place.getAddress();
                editText.setText(addressFull);
                if (shipToThis.isChecked())
                    shipToThis.setChecked(false);
                else if (homeAddress.isChecked())
                    homeAddress.setChecked(false);
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(AccessoryListScreen.this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPlaces() {
        Places.initialize(this, getString(R.string.google_maps_api));
        placesClient = Places.createClient(this);

    }

    EditText editText,buildingNo;
    EditText etComment;
    EditText etPurpose;
    RadioButton shipToThis;
    RadioButton homeAddress;
    ImageButton addressBtn;
    String addressPay, commentPay,purposePay;
    TextView etInCityRide , etOutstationRide;

    private static View add_address;

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccessoryListScreen.this,R.style.RedDialogTheme);
        alertDialog.setTitle("One Last Step");
        LayoutInflater inflater = this.getLayoutInflater();
        try {
//                if(add_address!=null) {
//                    ViewGroup parent = (ViewGroup) add_address.getParent();
//                    if (parent != null)
//                        parent.removeView(add_address);
//                }
            add_address = inflater.inflate(R.layout.add_address_comment_card, null);
            editText = add_address.findViewById(R.id.eTAddress);
            setupPlaceAutoComplete();

            etComment = add_address.findViewById(R.id.editTextComment);
          buildingNo = add_address.findViewById(R.id.buildingNo);
            addressBtn = add_address.findViewById(R.id.addComment);
            etPurpose = add_address.findViewById(R.id.eTPurpose);
            etOutstationRide = add_address.findViewById(R.id.etOutstationRide);
            etInCityRide = add_address.findViewById(R.id.etInCityRide);

            shipToThis = add_address.findViewById(R.id.shipToThisAdd);
            homeAddress = add_address.findViewById(R.id.shipToHomeAdd);
            shipToThis.setClickable(true);
            NestedScrollView mainScrollView =add_address.findViewById(R.id.main_scrollview);
            ImageView transparentImageView = add_address.findViewById(R.id.transparent_image);
            ImageView transparentImageViewAddress = add_address.findViewById(R.id.transparentImageViewAddress);
            ImageView transparentImageViewComment = add_address.findViewById(R.id.transparentImageViewComment);
            ImageView transparentImageViewPurpose = add_address.findViewById(R.id.transparentImageViewPurpose);

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

            transparentImageViewAddress.setOnTouchListener(new View.OnTouchListener() {

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
            transparentImageViewComment.setOnTouchListener(new View.OnTouchListener() {

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
            transparentImageViewPurpose.setOnTouchListener(new View.OnTouchListener() {

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


            etInCityRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etPurpose.setText(etInCityRide.getText());
                }
            });
            etOutstationRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etPurpose.setText(etOutstationRide.getText());
                }
            });


//            //Setting default address as home address
//          if (Common.currentUser == null && Common.currentUser.getHomeAddress() == null || TextUtils.isEmpty(Common.currentUser.getHomeAddress()))
//            Toast.makeText(AccessoryListScreen.this, "Please add your home address", Toast.LENGTH_SHORT).show();
//
//          else {
//            addressPay = Common.currentUser.getHomeAddress();
//            editText.setText(addressPay);
//          }

            homeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (Common.currentUser == null && Common.currentUser.getHomeAddress() == null || TextUtils.isEmpty(Common.currentUser.getHomeAddress()))
                            Toast.makeText(AccessoryListScreen.this, "Please add your home address", Toast.LENGTH_SHORT).show();

                        else {
                            addressPay = Common.currentUser.getHomeAddress();
                            editText.setText(addressPay);
                        }
                    }
                }
            });

            shipToThis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked && mlocation != null) {
                        mGoogleServices.getAddress(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false&key=AIzaSyD6qCL_yTdaA7SIS-ptw8bEXYbcslixF7U",
                                mlocation.getLatitude(), mlocation.getLongitude())).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    JSONArray resultArray = jsonObject.getJSONArray("results");
                                    JSONObject firstObj = resultArray.getJSONObject(0);
                                    addressPay = firstObj.getString("formatted_address");
                                    editText.setText(addressPay);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Toast.makeText(AccessoryListScreen.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if(mlocation == null) {
                        Toast.makeText(AccessoryListScreen.this, "Please turn on Location!", Toast.LENGTH_LONG).show();
                        shipToThis.setChecked(false);
                        shipToThis.setClickable(false);
                    }
                }
            });


            alertDialog.setView(add_address);
            alertDialog.setIcon(R.drawable.ic_my_location_black_24dp);
            alertDialog.setCancelable(false);
            setFinishOnTouchOutside(false);

            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if ( getSupportFragmentManager().findFragmentById(R.id.places) != null)
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().findFragmentById(R.id.places))
                                .commit();
                    if ( getSupportFragmentManager().findFragmentById(R.id.mapFrag) != null)
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().findFragmentById(R.id.mapFrag))
                                .commit();
                }
            });

            alertDialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(placeSelected!=null && placeSelected.getLatLng()!=null){
                        //Set lat lng for user
                        Common.currentUser.setLat(placeSelected.getLatLng().latitude);
                        Common.currentUser.setLng(placeSelected.getLatLng().longitude);
                    }

                    if (!shipToThis.isChecked() && !homeAddress.isChecked())
                        addressPay = editText.getText().toString(); //Change it to if(shipaddress!=null) addressPay = shipaddress.getAddress().toString() ; else getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_fragment)).commit();


                    commentPay = etComment.getText().toString();
                    purposePay = etPurpose.getText().toString();

                    if (editText.getText().toString() == null || editText.getText().toString().equals(""))
                        Toast.makeText(AccessoryListScreen.this, "Please enter a address!", Toast.LENGTH_SHORT).show();
                    else if (etPurpose.getText() == null || etPurpose.getText().toString().equals(""))
                        Toast.makeText(AccessoryListScreen.this, "Please enter Purpose of your trip", Toast.LENGTH_LONG).show();
                    else if(buildingNo.getText() == null || buildingNo.getText().toString().equals(""))
                      Toast.makeText(AccessoryListScreen.this, "Please enter Building / Flat / House Number", Toast.LENGTH_SHORT).show();
                    else if (Common.currentUser == null) {
                        Toast.makeText(getApplicationContext(), "Please Login before Booking!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(AccessoryListScreen.this , SignInAcitivity.class);
                        startActivity(i);
                    }
                     else
                            showPaymentMethods();

//                if(addressET != null) {
//                    getFragmentManager().beginTransaction().remove(addressET).commit();
//                }


//              Below Code is for PayPal use

//                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(totalAmount),"USD","Just Bike App Order",PayPalPayment.PAYMENT_INTENT_SALE);
//                Intent i = new Intent(getApplicationContext() , PaymentActivity.class);
//                i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
//                i.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
//                startActivityForResult(i,PAYMENT_REQUEST_CODE);
//
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_fragment))
//                        .commit();
//                if(!editText.getText().toString().equals("") ) {
//                    Request request = new Request(Common.currentUser.getPhone(), editText.getText().toString(), Common.currentUser.getName(), orderList,amount.getText().toString(), etComment.getText().toString(),"0");
//                    String orderNo = String.valueOf(System.currentTimeMillis());
//                    databaseReference.child(orderNo).setValue(request);
//                    new Database(getBaseContext()).cleanCart();
//                    Toast.makeText(ViewCartOrder.this, "Thank You! , Order Received", Toast.LENGTH_SHORT).show();
//                    finish();
//                    sendNotification(orderNo);
//
//
//                }
//                else
//                    Toast.makeText(ViewCartOrder.this, "Please enter Address", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            //addressBtn.setOnClickListener(v -> editAddress());
//         addressBtn.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent intent = new Intent(getApplicationContext(), ViewCartOrder.class);
//                 Bundle bundle = new Bundle();
//
//                 bundle.putString(SimplePlacePicker.API_KEY,"AIzaSyA0HsdqTPwDI5DFKPAEsvh18ua-XqujNoE");
//                 bundle.putString(SimplePlacePicker.COUNTRY,"India");
//                 bundle.putString(SimplePlacePicker.LANGUAGE,"en");
//                 // bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,supportedAreas);
//
//                 intent.putExtras(bundle);
//                 startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
//             }
//         });


            alertDialog.show();

        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
    }
    TextView walletAmt,totalAmt,couponAmt;
    EditText couponCode;
    ImageButton couponBtn;
    int walletFlag=0;
    int couponCost = 0;
    int paying30Per = 0;  // 0 : 100% , 1 : 30%
    private void showPaymentMethods() {
        final BottomSheetDialog bt = new BottomSheetDialog(AccessoryListScreen.this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(AccessoryListScreen.this).inflate(R.layout.payment_options_bottom, null);
        walletCheckBox = view.findViewById(R.id.walletCheckBox);
        walletAmt= view.findViewById(R.id.walletAmt);
        totalAmt = view.findViewById(R.id.amt);
        couponCode = view.findViewById(R.id.couponCode);
        couponBtn = view.findViewById(R.id.couponBtn);
        couponAmt = view.findViewById(R.id.couponAmt);
      thirty = view.findViewById(R.id.thirty);
      hundred = view.findViewById(R.id.hundred);

      ImageView upiIV = view.findViewById(R.id.upiIV);
      Glide.with(this).asGif().load(R.drawable.upi_gif).into(upiIV);
      TextView deliveryAmt = view.findViewById(R.id.deliveryAmt);
        DatabaseReference couponns = FirebaseDatabase.getInstance().getReference().child("Coupon");
      deliveryAmt.setText(format.format(deliveryCost));
        couponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponns.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String coupon= couponCode.getText().toString();
                        for(DataSnapshot postData : dataSnapshot.getChildren())
                        {
                            CouponCode c = postData.getValue(CouponCode.class);
                            if(c.getCode().equals(coupon)) {
                                couponCost = Integer.parseInt(c.getAmount());
                                couponAmt.setText(format.format(couponCost));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

      hundred.setChecked(true);
      thirty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if(isChecked){
            paying30Per = 1;
          }else
            paying30Per = 0;
        }
      });

      hundred.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if(isChecked){
            paying30Per = 0;
          }else
            paying30Per=1;
        }
      });
        bt.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                totalAmt.setText("Amount : "+format.format(amt));
            }
        });
        totalAmt.setText("Amount : "+format.format(amt));
        String walletValue = format.format(Integer.parseInt(Common.currentUser.getWallet()));
        if(walletValue.length()>3)
            walletAmt.setText(walletValue.substring(0,walletValue.length()-3));

        walletCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if( amt - Integer.parseInt(Common.currentUser.getWallet())>=0 )
                    {
                        String walletValue = format.format(0);
                        if(walletValue.length()>3)
                            walletAmt.setText(walletValue.substring(0,walletValue.length()-3));
                        totalAmt.setText("Amount : " + format.format(amt - Integer.parseInt(Common.currentUser.getWallet())));
                    }else {
                        totalAmt.setText("Amount : " + format.format(1));
                        String walletValue = format.format(Integer.parseInt(Common.currentUser.getWallet())-amt+1);
                        if(walletValue.length()>3)
                            walletAmt.setText(walletValue.substring(0,walletValue.length()-3));

                    }


                }
                else {
                    walletAmt.setText(walletValue.substring(0,walletValue.length()-3));
                    totalAmt.setText("Amount : " + format.format(amt));
                }
            }
        });
        view.findViewById(R.id.transferBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!walletCheckBox.isChecked())
                    walletFlag=0;
                else if(walletCheckBox.isChecked())
                    walletFlag = 1;
                startPayment();
                bt.dismiss();
            }
        });
        view.findViewById(R.id.upiBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!walletCheckBox.isChecked())
                    walletFlag=0;
                else if(walletCheckBox.isChecked())
                    walletFlag = 1;
             //   triPay();
                payUsingUPI();
                bt.dismiss();
            }
        });
        bt.setContentView(view);
        bt.show();

    }
  private final int UPI_PAYMENT = 342;
    private String upiId;
    private void payUsingUPI() {
        walletAmount = amt-couponCost;
        if(walletFlag==1){
            walletAmount -= Integer.parseInt(Common.currentUser.getWallet());
            if (walletAmount <= 0) {
                walletAmount = 1;
                walletFlag=2;
            }
        }
        if(paying30Per == 1)  //if 30% chosen
        {
          walletAmount *= 0.3;
          outsandingAmt = String.valueOf((int)((walletAmount)*0.7));
        }
        else
          outsandingAmt = "0";

      walletAmount+=deliveryCost;
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", "Just Bike")
                .appendQueryParameter("tn", bikeName)
                .appendQueryParameter("am", String.valueOf(walletAmount))
                .appendQueryParameter("cu", "INR")
                .build();
        Intent upiPaymentIntent = new Intent(Intent.ACTION_VIEW);
        upiPaymentIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPaymentIntent, "Pay with");

        if (chooser.resolveActivity(getPackageManager()) != null)
            startActivityForResult(chooser, UPI_PAYMENT);
        else {
            Toast.makeText(this, "No UPI app found! Please download any OR choose other payment options", Toast.LENGTH_LONG).show();
        }

        }

    private void upiPaymentDataOperation(ArrayList<String> dataList) {
        if (Common.isConnectedToInternet(this)) {
            String str = dataList.get(0);
            String paymentCancel = "";
            if (str == null)
                str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase()))
                        status = equalStr[1].toLowerCase();
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) ||
                            equalStr[0].toLowerCase().equals("txnRef".toLowerCase()))
                        approvalRefNo = equalStr[1];
                    else
                        paymentCancel = "Payment Cancelled by You";
                    }
            }
            if (status.equals("success")) {
                upiPaymentSuccessful();
                Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show();
            } else if (paymentCancel.equals("Payment Cancelled by You")) {
                Toast.makeText(this, "Payment Cancelled by You", Toast.LENGTH_SHORT).show();
            }
        }
    }
int discountAmt=0;
    private void upiPaymentSuccessful() {
        if (Common.currentUser != null) {
            Map<String, Object> passMap = new HashMap<>();
            if(walletFlag == 2) {
                int walletValue = Integer.parseInt(Common.currentUser.getWallet())-amt+1;
                passMap.put("wallet", String.valueOf(walletValue) );
            }
            else
                passMap.put("wallet", "0");
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
            users.child(Common.currentUser.getPhone()).updateChildren(passMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AccessoryListScreen.this, "Total Amount : " + walletAmount, Toast.LENGTH_SHORT).show();
                    updateCurrentUser();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccessoryListScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            Request request;
          DatabaseReference vehiclesRef = FirebaseDatabase.getInstance().getReference("Vehicles");
          List<Integer> listDate = new ArrayList<>();
          listDate.add(Integer.parseInt(startDateString));
          listDate.add(Integer.parseInt(endDateString));
          vehiclesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              for(DataSnapshot postData : dataSnapshot.getChildren()) {
                Vehicle v = postData.getValue(Vehicle.class);
                List<List<Integer>> bookingSlot = new ArrayList<>();
                if (v.getName().equals(orderList.get(0).getProductName()) && postData.getKey()!=null) {
                  if (v.getBookingSlot() != null) {
                    bookingSlot = v.getBookingSlot();
                    int pushCount = 0;

                    if (bookingSlot != null)
                      pushCount = bookingSlot.size();
                    vehiclesRef.child(postData.getKey()).child("bookingSlot").child(pushCount + "").setValue(listDate);

                  } else {
                    int pushCount = 0;
                    if (v.getName().equals(orderList.get(0).getProductName())) {
                      if (bookingSlot != null)
                        pushCount = bookingSlot.size();
                      vehiclesRef.child(postData.getKey()).child("bookingSlot").child(pushCount + "").setValue(listDate);
                    }
                  }
                }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
          discountAmt = amt - walletAmount;
          if (startDate != null) {   //when it is from Bike Details Activity
                request = new Request(Common.currentUser.getPhone(), editText.getText().toString(),purposePay,
                        Common.currentUser.getName(),
                        orderList, String.valueOf(amt),
                        commentPay, "0", "1",
                        "latlng", outsandingAmt,String.valueOf(discountAmt), "UPI/PAYTM",
                        startDate, endDate,noOfHelmets,selfPick,selfDrop);
            } else {
                request = new Request(Common.currentUser.getPhone(), editText.getText().toString(),purposePay,
                        Common.currentUser.getName(),
                        orderList, String.valueOf(amt),
                        commentPay, "0", "1",
                        "latlng", outsandingAmt,String.valueOf(discountAmt), "UPI/PAYTM",
                  startDateString, endDateString,noOfHelmets,selfPick,selfDrop);
            }
            String orderNo = String.valueOf(System.currentTimeMillis());
          databaseReference.child(orderNo).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              Toast.makeText(AccessoryListScreen.this, "Success!", Toast.LENGTH_SHORT).show();
            }
          });
            Toast.makeText(AccessoryListScreen.this, "Thank You! , Order Received", Toast.LENGTH_SHORT).show();
          sendNotification(orderNo);
          Common.currentRequest = request;
            Intent i = new Intent(this, OrderConfirmation.class);
            i.putExtra("orderNo", orderNo);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if (resultCode == RESULT_OK || resultCode == 11) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void sendNotification(String orderNo) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postDat : dataSnapshot.getChildren()) {
                    Token serverToken = postDat.getValue(Token.class);

                    //Creating RAW payload to send
                    Notification notification = new Notification("You have new order" + orderNo, "Just Bike");
                    Sender content = new Sender(serverToken.getToken(), notification);
                    apiService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(AccessoryListScreen.this, "Vehicle Booked Successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else
                                    Toast.makeText(AccessoryListScreen.this, "Failed to send Notication!!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("Error!", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Sending it via users
      DatabaseReference staff = FirebaseDatabase.getInstance().getReference("User");
      Query dataStaff = staff.orderByChild("isServerToken").equalTo(true);
      dataStaff.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for (DataSnapshot postDat : dataSnapshot.getChildren()) {
            UserModel serverToken = postDat.getValue(UserModel.class);

            //Creating RAW payload to send
            Notification notification = new Notification("You have new order" + orderNo, "Just Bike");
            Sender content = new Sender(serverToken.getPhone(), notification);
            apiService.sendNotification(content).enqueue(new Callback<MyResponse>() {
              @Override
              public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                  if (response.body().success == 1) {
                    Toast.makeText(AccessoryListScreen.this, "Vehicle Booked Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                  } else
                    Toast.makeText(AccessoryListScreen.this, "Failed to send Notication!!", Toast.LENGTH_SHORT).show();
                }

              }

              @Override
              public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.e("Error!", t.getMessage());
              }
            });
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
    }

    private void updateCurrentUser() {
        user_table.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                    UserModel userModel = dataSnapshot.child(Common.currentUser.getPhone()).getValue(UserModel.class);
                    Common.currentUser = userModel;
                    user_table.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }

    ArrayList<Order> orderList;
    String outsandingAmt="0";
    String startDate, endDate;

    @Override
    public void onPaymentSuccess(String s) {
        if (Common.currentUser != null) {

            Map<String, Object> passMap = new HashMap<>();
            if(walletFlag == 2) {
                int walletValue = Integer.parseInt(Common.currentUser.getWallet())-amt+1;
                passMap.put("wallet", String.valueOf(walletValue) );
            }
            else
                passMap.put("wallet", "0");
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
            users.child(Common.currentUser.getPhone()).updateChildren(passMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AccessoryListScreen.this, "Total Amount : " + walletAmount, Toast.LENGTH_SHORT).show();
                    updateCurrentUser();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccessoryListScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            Request request;

            DatabaseReference vehiclesRef = FirebaseDatabase.getInstance().getReference("Vehicles");
            List<Integer> listDate = new ArrayList<>();
            listDate.add(Integer.parseInt(startDateString));
          listDate.add(Integer.parseInt(endDateString));
            vehiclesRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postData : dataSnapshot.getChildren()){
                  Vehicle v = postData.getValue(Vehicle.class);
                  List<List<Integer>> bookingSlot = v.getBookingSlot();
                  int pushCount=0;
                  if(v.getName().equals( orderList.get(0).getProductName())){
                    if(bookingSlot!=null)
                      pushCount = bookingSlot.size();
                    vehiclesRef.child(postData.getKey()).child("bookingSlot").child(pushCount+"").setValue(listDate);
                  }
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AccessoryListScreen.this, "Update failed", Toast.LENGTH_SHORT).show();
              }
            });
           discountAmt = amt - walletAmount;
            if (startDate != null) {   //when it is from Bike Details Activity
                request = new Request(Common.currentUser.getPhone(), editText.getText().toString(),purposePay,
                        Common.currentUser.getName(),
                        orderList, String.valueOf(amt),
                        commentPay, "0", "1",
                        "latlng", outsandingAmt,String.valueOf(discountAmt), "App/Razorpay",
                        startDate, endDate,noOfHelmets,selfPick,selfDrop);
            } else {
                request = new Request(Common.currentUser.getPhone(), editText.getText().toString(),purposePay,
                        Common.currentUser.getName(),
                        orderList, String.valueOf(amt),
                        commentPay, "0", "1",
                        "latlng", outsandingAmt,String.valueOf(discountAmt), "App/Razorpay",
                  startDateString, endDateString,noOfHelmets,selfPick,selfDrop);
            }
            String orderNo = String.valueOf(System.currentTimeMillis());
            databaseReference.child(orderNo).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AccessoryListScreen.this, "Sucesss!", Toast.LENGTH_SHORT).show();
              }
            });
            Common.currentRequest = request;
            Toast.makeText(AccessoryListScreen.this, "Thank You! , Order Received", Toast.LENGTH_SHORT).show();
            finish();
            sendNotification(orderNo);

            Intent i = new Intent(this, OrderConfirmation.class);
            i.putExtra("orderNo", orderNo);
            startActivity(i);
            finish();
        } else
            Toast.makeText(this, "Please Login before Booking!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Error during payment , Try again!", Toast.LENGTH_SHORT).show();
    }

    String razorpayAmt;
    int walletAmount;
    public void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_vyPSpJi7gF28TF");

        checkout.setImage(R.drawable.just_black_logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Just Bike");

            options.put("description", "Secured Payment Gateway");
            options.put("image", R.drawable.biker);
            // options.put("order_id", Common.getOrderId());
            options.put("currency", "INR");
             walletAmount  = amt-couponCost;
            if(walletFlag==1){
                walletAmount -= Integer.parseInt(Common.currentUser.getWallet());
                if (walletAmount <= 0){
                    walletAmount = 1;
                    walletFlag=2;
                }
            }
            if(paying30Per == 1) {
              razorpayAmt = String.valueOf((int)((walletAmount+deliveryCost) * 0.3 * 100));
              outsandingAmt = String.valueOf((int)(walletAmount+deliveryCost)*0.7);
            }
            else {   //paying30Per==0 , i.e. 100%
              razorpayAmt = String.valueOf((int) (walletAmount + deliveryCost) * 100);
              outsandingAmt = "0";
            }
            options.put("amount", razorpayAmt);

            checkout.open(activity, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
        if(mgoogleApiClient!=null)
            mgoogleApiClient.connect();
    }

        @Override
        protected void onStop() {
            super.onStop();
            if (mgoogleApiClient!=null && mgoogleApiClient.isConnected()) {
                mgoogleApiClient.disconnect();
            }
        }


    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
}
