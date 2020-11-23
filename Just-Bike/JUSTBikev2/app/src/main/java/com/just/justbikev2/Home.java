package com.just.justbikev2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.just.justbikev2.Adapter.CitySpinnerAdapter;
import com.just.justbikev2.Adapter.FeedbackAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.Model.Advertisement;
import com.just.justbikev2.Model.Banner;
import com.just.justbikev2.Model.Category;
import com.just.justbikev2.Model.Feedback;
import com.just.justbikev2.Model.Token;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.Service.MyLocation;
import com.just.justbikev2.ViewHolder.AdvertisementListHolder;
import com.just.justbikev2.ViewHolder.MenuViewHolder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener
  , NavigationView.OnNavigationItemSelectedListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

  FirebaseDatabase database;
  DatabaseReference category;
  TextView fullName;

  TextView startTime, endTime, startDate, endDate;
  int startHour, startMin, endHour, endMin;

  ImageButton findBikeBtn;
  CounterFab fab;

  Button articles;
  TextView toggleNav, wallet;

  ImageView back_ground;
  Spinner citySpinner;

  private ShimmerFrameLayout mShimmerViewContainer;
  private ShimmerFrameLayout categoryShimmerViewContainer;

  RecyclerView recyclerView;
  RecyclerView recyclerViewMAdvertisement;
  RecyclerView.LayoutManager layoutManager;
  FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

  SwipeRefreshLayout swipeRefreshLayout;

  CircleImageView profilePhoto;
  VideoView videoView;
  String[] countryNames = {"Kolkata", "Seattle", "Medan", "Bangkok", "Colombo"
    , "Sydney", "Nairobi", "Kathmandu", "Gangtok", "Shillong"
    , "Guwahati", "Siliguri", "Jamshedpur", "Ranchi", "Deoghar"
    , "Dhanbad", "Jaipur", "Kota", "Imphal", "Bhuvneswar", "Puri"};
  int flags[] = {R.drawable.kolkata, R.drawable.seattle, R.drawable.medan, R.drawable.bangkok
    , R.drawable.colombo, R.drawable.sydney, R.drawable.nairobi, R.drawable.kathmandu
    , R.drawable.gangtok, R.drawable.shillong, R.drawable.guwahati, R.drawable.siliguri
    , R.drawable.jsr, R.drawable.ranchi, R.drawable.deogarh, R.drawable.dhanbad
    , R.drawable.jaipur, R.drawable.kota, R.drawable.imphal, R.drawable.bbsr
    , R.drawable.puri};


  //Banner
  HashMap<String, String> imageList;
  SliderLayout mSlider;
  FirebaseRecyclerAdapter<Advertisement, AdvertisementListHolder> adAdapter;

  //    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
  Long startDateSelected;
  Long endDateSelected;

  LocationRequest locationRequest;
  private FusedLocationProviderClient fusedLocationProviderClient;
  static Home instance;

  public static Home getInstance() {
    return instance;
  }
  private void updateLocation() {
    buildLocationRequest();
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
  }

  public void updateDatabaseLocation(double lat , double lng){
    Home.this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
        Map<String,Object> putMap = new HashMap<>();
        putMap.put("lat",lat);
        putMap.put("lng",lng);
        if(Common.currentUser!=null)
          user.child(Common.currentUser.getPhone()).updateChildren(putMap);
        else
          user.push().updateChildren(putMap);
      }
    });

  }
  private PendingIntent getPendingIntent() {
    Intent intent = new Intent(this , MyLocation.class);
    intent.setAction(MyLocation.ACTION_PROCESS_UPDATE);
    return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
  }

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
//
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//        .setDefaultFontPath("fonts/restaurant_font.otf")
//        .setFontAttrId(R.attr.fontPath)
//        .build());
    setContentView(R.layout.activity_home);

    instance = this;
    if(Common.currentUser==null || Common.currentUser.getPhone().equals("") || Common.currentUser.getPhone() == null)
    {
      Toast.makeText(Home.this, "Please Login to Continue", Toast.LENGTH_SHORT).show();
      Intent i = new Intent(Home.this,SignInAcitivity.class);
      startActivity(i);
    }
    else{
    Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
      @Override
      public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
      }

      @Override
      public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
        Toast.makeText(Home.this, "Allow Permissions to proceed.", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

      }
    });
    updateLocation();


    mShimmerViewContainer = findViewById(R.id.shimmer);
    categoryShimmerViewContainer = findViewById(R.id.shimmerCategory);

    categoryShimmerViewContainer.startShimmer();
    mShimmerViewContainer.startShimmer();

    back_ground = findViewById(R.id.back_ground);
//        Glide.with(getApplicationContext()).asGif()
//                .load(R.drawable.red_scooty_big).fitCenter()
//                .into(back_ground);
    citySpinner = findViewById(R.id.citySpinner);
    CitySpinnerAdapter customAdapter = new CitySpinnerAdapter(getApplicationContext(), flags, countryNames);
    citySpinner.setAdapter(customAdapter);
    citySpinnerPosition = 0;
    Common.cityId = String.valueOf(citySpinnerPosition);

    citySpinner.setOnItemSelectedListener(this);
    swipeRefreshLayout = findViewById(R.id.swipeLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
      R.color.orange_btn,
      R.color.com_facebook_blue,
      R.color.green);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        if (Common.isConnectedToInternet(getBaseContext())) {
          getAdvertiseMentList();
          loadMenu();
          updateCurrentUser();
          adAdapter.startListening();
//                    mShimmerViewContainer.stopShimmer();
          mShimmerViewContainer.hideShimmer();
          mShimmerViewContainer.setVisibility(View.INVISIBLE);
          adapter.startListening();
        } else {
          Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
          return;
        }
      }
    });

    swipeRefreshLayout.post(new Runnable() {
      @Override
      public void run() {
        if (Common.isConnectedToInternet(getBaseContext())) {
          loadMenu();
          adapter.startListening();
        } else {
          Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
          return;
        }
      }
    });

    Toolbar toolbar = findViewById(R.id.toolbar);
    videoView = findViewById(R.id.videoView);

    playBgVideo();

    toolbar.setTitle("Just Bike");
    setSupportActionBar(toolbar);

    Paper.init(this);

    //Init Firebase
    database = FirebaseDatabase.getInstance();
    category = database.getReference("Category");
    fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(Home.this, ViewCartOrder.class);
        startActivity(i);
      }
    });

    fab.setCount(new Database(this).getCartCount());

    startTime = findViewById(R.id.startTime);
    startDate = findViewById(R.id.startDate);
    endTime = findViewById(R.id.endTime);
    endDate = findViewById(R.id.endDate);

    articles = findViewById(R.id.viewArticles);
    setupBannerSlider();

    findBikeBtn = findViewById(R.id.finbikeBtn);
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.setDrawerLockMode(Integer.valueOf(0));
    articles.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(Home.this, Articles.class);
        startActivity(i);
      }
    });
    toggleNav = findViewById(R.id.toggleNav);
    toggleNav.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!drawer.isDrawerOpen(GravityCompat.START))
          drawer.openDrawer(GravityCompat.START);
        else
          drawer.openDrawer(GravityCompat.END);

      }
    });
    NavigationView navigationView = findViewById(R.id.nav_view);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    navigationView.setNavigationItemSelectedListener(this);

    //Setting Name for user
    View headerView = navigationView.getHeaderView(0);
    fullName = headerView.findViewById(R.id.phone);
    wallet = headerView.findViewById(R.id.wallet);
    CardView jbidaCard = headerView.findViewById(R.id.jbidaCard);
    jbidaCard.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(Home.this, JbidaWallet.class);
        startActivity(i);
      }
    });
    profilePhoto = headerView.findViewById(R.id.profilePhoto);

    setUserInfo();

    //Loading menu
    recyclerView = findViewById(R.id.recyclerViewMenuHome);
    //layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    // recyclerView.setLayoutManager(layoutManager);
    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.fall_down_animation);
    recyclerView.setLayoutAnimation(controller);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);

    recyclerViewMAdvertisement = findViewById(R.id.recyclerViewMAdvertisement);
    recyclerViewMAdvertisement.setLayoutManager(new GridLayoutManager(this, 3));
    recyclerViewMAdvertisement.setLayoutAnimation(controller);


    fab.setCount(new Database(this).getCartCount());
    if (Common.isConnectedToInternet(this)) {
      loadMenu();
      getAdvertiseMentList();

    } else {
      Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
      return;
    }

    //Loading intent for notification of order : Client side
//        Intent serivce = new Intent(Home.this, ListenOrder.class);
//        startService(serivce);

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
    calendar.clear();
    Long today = MaterialDatePicker.todayInUtcMilliseconds();
    calendar.setTimeInMillis(today);
    calendar.set(Calendar.MONTH, Calendar.MONTH);
    CalendarConstraints.Builder calenderConstraints = new CalendarConstraints.Builder();
    MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
    builder.setTitleText("Select Start & End date");
    builder.setCalendarConstraints(limitRange().build());

    CalendarConstraints.DateValidator dateValidatorMin = DateValidatorPointForward.from(System.currentTimeMillis());
    calenderConstraints.setValidator(dateValidatorMin);
    MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
    endDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Dialog dialogFrg = materialDatePicker.getDialog();
        if (dialogFrg != null && dialogFrg.isShowing()) {
        } else {
          materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        }
      }
    });
    startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Dialog dialogFrg = materialDatePicker.getDialog();
        if (dialogFrg != null && dialogFrg.isShowing()) {
        } else {
          materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        }
      }
    });
    materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
      @Override
      public void onPositiveButtonClick(Pair<Long, Long> selection) {
        startDateSelected = selection.first;
        endDateSelected = selection.second;
        Date currentTime = Calendar.getInstance().getTime();
        String todayDate = currentTime.toString().substring(4, 10);
        String[] todayDates = todayDate.split(" ");
        curTime = currentTime.toString().substring(11, 13);

        startDateIntentString = materialDatePicker.getHeaderText();

        String[] dates = materialDatePicker.getHeaderText().split(" ");
        startDate.setText(dates[0] + " " + dates[1]);//or selection
        if (dates.length > 5)      //It means it is 2021 (since while development , it is 2020 ) so we get "30," in dates[4]
          dates[4] = dates[4].substring(0, dates[4].length() - 1);
        endDate.setText(dates[3] + " " + dates[4]);//or selection

        try {
          // checking valid integer using parseInt() method
          Integer.parseInt(dates[0]);     //It is 22 Jun (if it is Jun 22 , it will goto catch) , i.e digit is index 0
          if (todayDates[1].equals(dates[0]) &&            //Since today's date is Jun 22 always , so digit is index 1
            todayDates[0].equals(dates[1]))            //22==22 and Jun==Jun?
            isTodayDate = true;

        } catch (NumberFormatException e) {         //It is  Jun 22
          if (todayDates[0].equals(dates[0]) &&            //Since today's date is Jun 22 always , so digit is index 1
            todayDates[1].equals(dates[1]))            //22==22 and Jun==Jun?
            isTodayDate = true;
        } finally {
          if (startTime.getText().toString().equals("Start Time") && endTime.getText().toString().equals("End Time  "))
            startTimeChose();
        }
      }
    });


    startTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startTimeChose();
      }
    });


    endTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        endTimeChoose();
      }
    });

    findBikeBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (startTime.getText().toString().equals("Start Time") || endTime.getText().toString().equals("End Time  ") || startDate.getText().toString().equals("Start Date")) {
          Toast.makeText(Home.this, "Please select both Date and Time", Toast.LENGTH_SHORT).show();
        } else {
          Intent i = new Intent(Home.this, BikeListScreen.class);
          i.putExtra("startTime", startTime.getText().toString());
          i.putExtra("startDate", startDateIntentString);
          i.putExtra("endTime", endTime.getText().toString());
          //      i.putExtra("endtDate",endDate.getText().toString());
          //   if (materialDatePicker != null && materialDatePicker.getHeaderText() != null)
          // i.putExtra("CategoryId",adapter.getRef(i).getKey());
          //      i.putExtra("Available From Date", materialDatePicker.getHeaderText());
          // if (time != null && !time.isEmpty())
          //       i.putExtra("Available From Time", time);
          startActivity(i);
        }
      }
    });
    updateToken(FirebaseInstanceId.getInstance().getToken());

    loadFeedbacks();

  }
  }




  private void setUserInfo() {
    if (Common.currentUser != null && !Common.currentUser.getName().isEmpty()) {
      fullName.setText(Common.currentUser.getName());
      if (Common.currentUser.getProfilePic() != null)
        Picasso.get().load(Uri.parse(Common.currentUser.getProfilePic())).into(profilePhoto);
      Locale locale = new Locale("en", "in");
      NumberFormat format = NumberFormat.getCurrencyInstance(locale);
      String walletAmt = "0";
      if (Common.currentUser.getWallet() != null)
        walletAmt = format.format(Integer.parseInt(Common.currentUser.getWallet()));
      if (walletAmt.length() > 3)
        wallet.setText(walletAmt.substring(0, walletAmt.length() - 3));

    }
  }

  int feedbackCount = 0;
  private FeedbackAdapter feedbackAdapter;
  private RecyclerView feedbackRV;

  private void loadFeedbacks() {
    feedbackRV = findViewById(R.id.feedbackRV);
    feedbackRV.setLayoutManager(new LinearLayoutManager(this));
    List<Feedback> feedbacks = new ArrayList<>();
    DatabaseReference feedbackReference = FirebaseDatabase.getInstance().getReference().child("Feedback");
    feedbackReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot postData : dataSnapshot.getChildren()) {
          Feedback feedback = postData.getValue(Feedback.class);
          feedbacks.add(feedback);
          feedbackAdapter = new FeedbackAdapter(feedbacks, Home.this);
          feedbackAdapter.notifyDataSetChanged();
          feedbackRV.setAdapter(feedbackAdapter);

        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(Home.this, "Unable to load feedbacks", Toast.LENGTH_SHORT).show();
      }
    });
  }


  private void playBgVideo() {
    Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_red);
    if(videoView!=null) {
      videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {

          mp.setVolume(0, 0);
          mp.setLooping(true);
        }
      });
      videoView.setVideoURI(video);
      videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
          if (isFinishing()) {
            return;
          }
        }
      });

      videoView.start();
    }
  }

  boolean isTodayDate = false;
  String curTime;
  String startDateIntentString;

  private void getAdvertiseMentList() {
    Query query = FirebaseDatabase.getInstance()
      .getReference()
      .child("Advertisement")
      .limitToLast(50);

    FirebaseRecyclerOptions<Advertisement> options =
      new FirebaseRecyclerOptions.Builder<Advertisement>()
        .setQuery(query, Advertisement.class)
        .build();

    adAdapter = new FirebaseRecyclerAdapter<Advertisement, AdvertisementListHolder>(options) {

      @Override
      protected void onBindViewHolder(@NonNull AdvertisementListHolder adViewHolder, final int i, @NonNull Advertisement category) {

        adViewHolder.gifText.setText(category.getGifText());

        Glide.with(getApplicationContext()).asGif()
          .load(category.getGifImage()).centerCrop().transform(new CircleCrop())
          .into(adViewHolder.gifImage);
        adViewHolder.gifImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (category.getGifText() != null) {
              if (category.getGifText().equals("Upcoming")) {
                Intent i = new Intent(Home.this, ComingSoon.class);
                startActivity(i);
              } else if (category.getGifText().equals("Bikation")) {
                Intent i = new Intent(Home.this, Bikation.class);
                i.putExtra("cityId", String.valueOf(citySpinnerPosition));
                startActivity(i);
              } else {
                Intent i = new Intent(Home.this, AdvertisementDetails.class);
                startActivity(i);
              }

            }
          }
        });


      }

      // "https://firebasestorage.googleapis.com/v0/b/just-bike-6a869.appspot.com/o/rsz_wahyu-setiawan-qwjfwjllnqc-unsplash2x.png?alt=media&token=73cd650b-5b50-4901-a67e-3b00dc93f549"
// "https://firebasestorage.googleapis.com/v0/b/just-bike-6a869.appspot.com/o/rsz_1harley-davidson-eetjkc_wz34-unsplash2x.png?alt=media&token=a13dceb7-591f-4a76-aa95-98f412b05dc7"
      @NonNull
      @Override
      public AdvertisementListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advertisement_gif_card, null);
        return new AdvertisementListHolder(view);
      }
    };
    adAdapter.notifyDataSetChanged();
    recyclerViewMAdvertisement.setAdapter(adAdapter);
    swipeRefreshLayout.setRefreshing(false);

    //Animation
    recyclerViewMAdvertisement.getAdapter().notifyDataSetChanged();
    recyclerViewMAdvertisement.scheduleLayoutAnimation();

  }

  /*
         Limit selectable Date range
       */
  private CalendarConstraints.Builder limitRange() {

    CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

    Calendar calendarStart = Calendar.getInstance();
    Calendar calendarEnd = Calendar.getInstance();

    int year = 2020;
    int startMonth = calendarStart.get(Calendar.MONTH);
    int startDate = calendarStart.get(Calendar.DATE);

    //Update this to change maxDate (currently it is 31st Dec 2021 )
    int endMonth = 12;
    int endDate = 31;
    calendarStart.set(year, startMonth, startDate);
    calendarEnd.set(2021, endMonth - 1, endDate + 1);

    long minDate = calendarStart.getTimeInMillis();
    long maxDate = calendarEnd.getTimeInMillis();


    constraintsBuilderRange.setStart(minDate);
    //Change here for last date disabling
    constraintsBuilderRange.setEnd(maxDate);
    constraintsBuilderRange.setValidator(new RangeValidator(minDate, maxDate));

    return constraintsBuilderRange;
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (countryNames[position].equals("Kolkata") || countryNames[position].equals("Jamshedpur") || countryNames[position].equals("Deoghar") ||
      countryNames[position].equals("Seattle") || countryNames[position].equals("Jaipur") || countryNames[position].equals("Medan") ||
      countryNames[position].equals("Bangkok") || countryNames[position].equals("Sydney") || countryNames[position].equals("Nairobi") ||
      countryNames[position].equals("Colombo") || countryNames[position].equals("Kathmandu")) {
      Toast.makeText(this, "Welcome to " + countryNames[position] + " Just Bike HUB", Toast.LENGTH_LONG).show();
      citySpinnerPosition = position;
      Common.cityId = String.valueOf(position);
    } else {
      Toast.makeText(this, "Currently OFFLINE in this location", Toast.LENGTH_SHORT).show();
      citySpinner.setSelection(0);
      citySpinnerPosition = 0;
      Common.cityId = String.valueOf(0);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    citySpinner.setSelection(0);
    citySpinnerPosition = 0;
    Common.cityId = String.valueOf(0);    //Default Kolkata

  }


  static class RangeValidator implements CalendarConstraints.DateValidator {

    long minDate, maxDate;

    RangeValidator(long minDate, long maxDate) {
      this.minDate = minDate;
      this.maxDate = maxDate;
    }

    RangeValidator(Parcel parcel) {
      minDate = parcel.readLong();
      maxDate = parcel.readLong();
    }

    @Override
    public boolean isValid(long date) {
      return !(minDate > date || maxDate < date);
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeLong(minDate);
      dest.writeLong(maxDate);
    }

    public static final Parcelable.Creator<RangeValidator> CREATOR = new Parcelable.Creator<RangeValidator>() {

      @Override
      public RangeValidator createFromParcel(Parcel parcel) {
        return new RangeValidator(parcel);
      }

      @Override
      public RangeValidator[] newArray(int size) {
        return new RangeValidator[size];
      }
    };


  }

  @Override
  public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
    endHour = hourOfDay;
    if (minute % 30 != 0) {
      int minuteFloor = minute - (minute % 30);
      minute = minuteFloor + (minute == minuteFloor + 1 ? 30 : 0);
      if (minute == 60)
        minute = 0;
    }
    endMin = minute;

//               Calendar calendar = Calendar.getInstance();
//                calendar.set(0,0,0,endHour,endMin);
//                endTime.setText(DateFormat.format("hh:mm",calendar));
    endTime.setText((endHour < 10 ? "0" + endHour : endHour) + ":" + (endMin < 10 ? "0" + endMin : endMin));
  }


  private void endTimeChoose() {
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this, startHour, startMin, 0, false);
    timePickerDialog.setAccentColor(getResources().getColor(R.color.btn_red_bg));
    timePickerDialog.setThemeDark(true);
    timePickerDialog.setTitle("Your Journey End Time");
    timePickerDialog.dismissOnPause(true);
    if (startDateSelected != null && startDateSelected.equals(endDateSelected))
      timePickerDialog.setMinTime(startHour, startMin, 0);  //Since then next day starts

    timePickerDialog.show(getSupportFragmentManager(), "Timeickerdialog");

  }

  private void startTimeChose() {
    TimePickerDialog timePickerDialog = new TimePickerDialog(Home.this, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (isTodayDate && hourOfDay <= Integer.parseInt(curTime) + 2)
          Toast.makeText(Home.this, "Please Select Start Time Greater than 2 hours from Current Time!", Toast.LENGTH_LONG).show();
        else {
          startHour = hourOfDay;
          if (minute % 30 != 0) {
            int minuteFloor = minute - (minute % 30);
            minute = minuteFloor + (minute == minuteFloor + 1 ? 30 : 0);
            if (minute == 60)
              minute = 0;
          }
          startMin = minute;
          startTime.setText((startHour < 10 ? "0" + startHour : startHour) + ":" + (startMin < 10 ? "0" + startMin : startMin));
          endTimeChoose();
        }
      }
    }, 24, 0, false);
    timePickerDialog.updateTime(startHour, startMin);
    timePickerDialog.setTitle("Choose Start Time");
    timePickerDialog.show();
  }

  private void setupBannerSlider() {
    mSlider = findViewById(R.id.sliderBanner);
    imageList = new HashMap<>();

    DatabaseReference banners = database.getReference("Banner");
    banners.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot postData : dataSnapshot.getChildren()) {
          Banner banner = postData.getValue(Banner.class);
          imageList.put(banner.getName() + "@@@" + banner.getId(), banner.getImage());
        }
        for (String key : imageList.keySet()) {
          String[] keySplit = key.split("@@@");
          String nameOfBike = keySplit[0];
          String bikeId = keySplit[1];

          TextSliderView textSliderView = new TextSliderView(getApplicationContext());
          textSliderView.image(imageList.get(key))
            .description(nameOfBike)
            .setProgressBarVisible(true);
          textSliderView.bundle(new Bundle());
          textSliderView.getBundle().putString("BikeId", bikeId);
          mSlider.addSlider(textSliderView);
          banners.removeEventListener(this);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
    mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
    mSlider.startAutoCycle();
    mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
    mSlider.setCustomAnimation(new DescriptionAnimation());
    mSlider.setDuration(3000);
  }

  private void updateToken(String tokenRefresh) {
    if (Common.currentUser != null) {
      FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
      DatabaseReference tokens = firebaseDatabase.getReference("Tokens");
      Token token = new Token(tokenRefresh, false);
      tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
  }

  int citySpinnerPosition;
  int shimmerCounter;

  private void loadMenu() {
    Query query = FirebaseDatabase.getInstance()
      .getReference()
      .child("Category")
      .limitToLast(50);

    FirebaseRecyclerOptions<Category> options =
      new FirebaseRecyclerOptions.Builder<Category>()
        .setQuery(query, Category.class)
        .build();
    shimmerCounter = 0;

    adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {

      @Override
      protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, final int i, @NonNull Category category) {

        menuViewHolder.name.setText(category.getName());
        // Picasso.get().load(category.getBikeImageSmall()).fit().into(menuViewHolder.image);
        Glide.with(getApplicationContext()).asGif().load(category.getImage()).into(menuViewHolder.image);
        final Category clickItem = category;
        if (shimmerCounter == 0) {
          categoryShimmerViewContainer.stopShimmer();
          categoryShimmerViewContainer.hideShimmer();
          categoryShimmerViewContainer.setVisibility(View.GONE);

          mShimmerViewContainer.stopShimmer();
          mShimmerViewContainer.hideShimmer();
          mShimmerViewContainer.setVisibility(View.INVISIBLE);

          shimmerCounter = 1;
        }
        menuViewHolder.setItemClickListener(new ItemClickListener() {
          @Override
          public void onClick(View view, int position, boolean isLongClick) {
            Intent intent = new Intent(Home.this, BikeListScreen.class);
            intent.putExtra("CategoryId", adapter.getRef(i).getKey() + "_" + citySpinnerPosition);
            startActivity(intent);
          }
        });
      }

      @NonNull
      @Override
      public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.menu_item, parent, false);

        return new MenuViewHolder(view);
      }
    };
    adapter.notifyDataSetChanged();
    recyclerView.setAdapter(adapter);
    swipeRefreshLayout.setRefreshing(false);

    //Animation
    recyclerView.getAdapter().notifyDataSetChanged();
    recyclerView.scheduleLayoutAnimation();
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.home, menu);
    return true;
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (adapter != null)
      adapter.stopListening();
    if (mSlider != null)
      mSlider.stopAutoCycle();
    if (fusedLocationProviderClient != null)
      fusedLocationProviderClient.removeLocationUpdates(locationCallback);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    } else if (id == R.id.action_refresh) {
      loadMenu();
      getAdvertiseMentList();
      adapter.startListening();
      adAdapter.startListening();
      mShimmerViewContainer.stopShimmer();
      mShimmerViewContainer.hideShimmer();
      mShimmerViewContainer.setVisibility(View.INVISIBLE);
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_franchise) {
      Intent i = new Intent(Home.this, Franchise.class);
      startActivity(i);
    }
    else if (id == R.id.nav_pickup) {
      Intent i = new Intent(Home.this, PickupPoints.class);
      startActivity(i);
    }
    else if (id == R.id.nav_banner) {
      Intent i = new Intent(Home.this, ContactUs.class);
      startActivity(i);

    } else if (id == R.id.nav_home_address) {
      showHomeAddressDialog();
    } else if (id == R.id.nav_gallery) {
      Intent i = new Intent(Home.this, Articles.class);
      startActivity(i);

    }else if (id == R.id.rateUs) {
      Intent i = new Intent(Home.this, RateUsActivity.class);
      startActivity(i);

    } else if (id == R.id.nav_send) {
      Toast.makeText(this, "Get Jobs from JUST Bike", Toast.LENGTH_SHORT).show();
      Intent i = new Intent(Home.this, Careers.class);
      startActivity(i);
    } else if (id == R.id.orders) {
      Intent orderList = new Intent(Home.this, ViewOrders.class);
      startActivity(orderList);

    } else if (id == R.id.logout) {
      //Remove saved id,pwd
      Paper.book().destroy();
      Intent sinIn = new Intent(Home.this, SignInAcitivity.class);
      sinIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(sinIn);
           /* for Firebase AUTH
             AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


             */

    } else if (id == R.id.nav_changePass) {
      showPasswordDialog();

    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public static final int UPDATE_INTERVAL = 5000;
  public static final int FASTEST_INTERVAL = 3000;
  public static final int DISPLACEMENT = 10;

  private void initLocation() {
    buildLocationRequest();
    buildLocationCallback();
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
  }

  private void buildLocationCallback() {
    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        if(locationResult!=null)
          currentlocation = locationResult.getLastLocation();
      }
    };
  }

  private void buildLocationRequest() {
    locationRequest = new LocationRequest();
    locationRequest.setInterval(UPDATE_INTERVAL);
    locationRequest.setFastestInterval(FASTEST_INTERVAL);
    locationRequest.setSmallestDisplacement(DISPLACEMENT);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private Location currentlocation;

  private void showHomeAddressDialog() {
    Dexter.withContext(this)
      .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      .withListener(new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
          Toast.makeText(Home.this, "You must enable this permission to use App", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

        }
      }).check();

    initLocation();
    AlertDialog.Builder alertdialog = new AlertDialog.Builder(Home.this);
    alertdialog.setTitle("Change Your Home Address");
    alertdialog.setMessage("Fill in information");

    LayoutInflater inflater = LayoutInflater.from(this);
    View layout_home = inflater.inflate(R.layout.add_address_card, null);
    EditText etHomeAddress = layout_home.findViewById(R.id.etHomeAddress);
    layout_home.findViewById(R.id.getlocation).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          return;
        }
        fusedLocationProviderClient.getLastLocation()
          .addOnSuccessListener(Home.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
              // Got last known location. In some rare situations this can be null.
              if (location != null) {
                // Logic to handle location object
                //String addressPay = location.getProvider();
                currentlocation = location;
                // addressET.setText(addressPay);
              } else
                Toast.makeText(Home.this, "Pleas turn on your location.", Toast.LENGTH_SHORT).show();
            }
          }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
          @Override
          public void onComplete(@NonNull Task<Location> task) {
            if (task != null && task.getResult() != null) {
              String coordiantes = new StringBuilder().append(task.getResult().getLatitude())
                .append("/")
                .append(task.getResult().getLongitude()).toString();
              Single<String> singleAddress = Single.just(getAddressFromLatLng(task.getResult().getLatitude(), task.getResult().getLongitude()));
              Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                @Override
                public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull String s) {
                  etHomeAddress.setText(s);
                }

                @Override
                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                  Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });
            }
          }
        });
      }
    });
    alertdialog.setView(layout_home);
    alertdialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (!etHomeAddress.getText().toString().equals("")) {
          Common.currentUser.setHomeAddress(etHomeAddress.getText().toString());
          FirebaseDatabase.getInstance().getReference("User")
            .child(Common.currentUser.getPhone())
            .setValue(Common.currentUser)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Home.this, "Home address updated successfully", Toast.LENGTH_SHORT).show();
              }
            });
        } else
          Toast.makeText(Home.this, "Please Enter Information!", Toast.LENGTH_SHORT).show();
      }
    });
    alertdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    alertdialog.show();

  }

  private String getAddressFromLatLng(double latitude, double longitude) {

    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    String result = "";
    try {
      List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
      if (addressList != null && addressList.size() > 0) {
        Address address = addressList.get(0);
        StringBuilder stringBuilder = new StringBuilder(address.getAddressLine(0));
        result = stringBuilder.toString();
      } else
        Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private LocationCallback locationCallback;


  private void showPasswordDialog() {
    AlertDialog.Builder alertdialog = new AlertDialog.Builder(Home.this);
    alertdialog.setTitle("Change Your Password");

    LayoutInflater inflater = LayoutInflater.from(this);
    View alertView = inflater.inflate(R.layout.change_password, null);
    EditText eTPass = alertView.findViewById(R.id.eTPass);
    EditText etConfirmPass = alertView.findViewById(R.id.eTConfirmPass);
    EditText etNewPass = alertView.findViewById(R.id.eTNewPass);

    alertdialog.setView(alertView);
    alertdialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        android.app.AlertDialog waitingDialog = new SpotsDialog.Builder()
          .setContext(Home.this)
          .setTheme(R.style.Custom)
          .build();
        waitingDialog.show();

        if (eTPass.getText().toString().equals(Common.currentUser.getPassword())) {
          if (etConfirmPass.getText().toString().equals(etNewPass.getText().toString())) {
            Map<String, Object> passMap = new HashMap<>();
            passMap.put("password", etConfirmPass.getText().toString());
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
            users.child(Common.currentUser.getPhone()).updateChildren(passMap).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                waitingDialog.dismiss();
                Toast.makeText(Home.this, "Password updated Successfully", Toast.LENGTH_SHORT).show();
              }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
              }
            });
          } else {
            Toast.makeText(Home.this, "New Password doesn't match", Toast.LENGTH_SHORT).show();
            waitingDialog.dismiss();
          }
        } else {
          Toast.makeText(Home.this, "Incorrect Old Password", Toast.LENGTH_SHORT).show();
          waitingDialog.dismiss();
        }

      }
    });
    alertdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    alertdialog.show();

  }

  @Override
  protected void onStart() {
    super.onStart();
    if (adapter != null) adapter.startListening();
    if (adAdapter != null) adAdapter.startListening();
    if (mShimmerViewContainer != null)
      mShimmerViewContainer.startShimmer();
    if (categoryShimmerViewContainer != null)
      categoryShimmerViewContainer.startShimmer();
    if (swipeRefreshLayout != null)
      swipeRefreshLayout.setRefreshing(false);
  }

  @Override
  protected void onResume() {
    if(Common.currentUser==null || Common.currentUser.getPhone().equals("") || Common.currentUser.getPhone() == null)
    {
      Toast.makeText(Home.this, "Please Login to Continue", Toast.LENGTH_SHORT).show();
      Intent i = new Intent(Home.this,SignInAcitivity.class);
      startActivity(i);
    }
    else {
      super.onResume();
      playBgVideo();
      updateCurrentUser();
      fab.setCount(new Database(this).getCartCount());
      if (adapter != null)
        adapter.startListening();
      if (adAdapter != null) adAdapter.startListening();
      if (mShimmerViewContainer != null)
        mShimmerViewContainer.startShimmer();
      if (categoryShimmerViewContainer != null)
        categoryShimmerViewContainer.startShimmer();
      if (swipeRefreshLayout != null)
        swipeRefreshLayout.setRefreshing(false);
      if (mSlider != null)
        mSlider.startAutoCycle();
      initLocation();
      if (fusedLocationProviderClient != null) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
      }
    }
    }
  private void updateCurrentUser() {
      DatabaseReference user_table = FirebaseDatabase.getInstance().getReference().child("User");
    user_table.addListenerForSingleValueEvent((new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (Common.currentUser!=null && dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
          UserModel userModel = dataSnapshot.child(Common.currentUser.getPhone()).getValue(UserModel.class);
          Common.currentUser = userModel;
          setUserInfo();
        }
      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    }));
  }

    @Override
    protected void onPause() {
        super.onPause();
        if(mShimmerViewContainer!=null)
            mShimmerViewContainer.stopShimmer();
        if(categoryShimmerViewContainer!=null)
            categoryShimmerViewContainer.stopShimmer();
        if(swipeRefreshLayout!=null)
            swipeRefreshLayout.setRefreshing(false);
    }
}
