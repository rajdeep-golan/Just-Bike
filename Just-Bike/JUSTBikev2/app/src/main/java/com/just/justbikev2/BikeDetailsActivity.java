package com.just.justbikev2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.BikeImagesAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.Model.RatingVehicle;
import com.just.justbikev2.Model.Vehicle;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class BikeDetailsActivity extends AppCompatActivity implements View.OnClickListener , RatingDialogListener,
        AdapterView.OnItemSelectedListener , com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener,
  com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {


    Vehicle vehicle;
    TextView cost, year ,cc,weight ,model , details, bhp ,tSize ,mileage;
    ImageView bikeImage , bikeGif,companyIcon;
    ImageButton book_btn;
    ImageButton showComments;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String bikeId = "";
    CounterFab add_to_cart;
    FloatingActionButton rating_btn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference , ratingReference;
    TextView kilometersValue;
 //   ViewPager viewPager;

    RatingBar ratingBar;
    int amt =0 ;

  BikeImagesAdapter adapter;

    int isFromTime = 0;
    int position = 0;   // Position of package_image selected from spinner

    HashMap<String, Integer> costMap;
    Spinner spinner;
    private String[] imageUrls=new String[4];

    Intent intent;

    RecyclerView bikeImages;

TextView costTV;
String startDateStr , endDateStr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Paper.init(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.bike_detail_new);
        bikeImage=findViewById(R.id.bikeImage);
        bikeGif = findViewById(R.id.bikeGif);
        tSize = findViewById(R.id.tyreValue);
        bhp = findViewById(R.id.bhpValue);
        companyIcon = findViewById(R.id.companyIcon);
        cost=findViewById(R.id.viewDocs);
        kilometersValue = findViewById(R.id.kilometersValue);
        cc=findViewById(R.id.ccValue);
        year=findViewById(R.id.yearValue);
        weight=findViewById(R.id.weightValue);
        details = findViewById(R.id.detailsValue);
          book_btn = findViewById(R.id.book_btn);
        add_to_cart = findViewById(R.id.addToCart_btn);
        rating_btn = findViewById(R.id.rating_btn);
        ratingBar = findViewById(R.id.ratingBarBike);
        collapsingToolbarLayout = findViewById(R.id.collapse);
        showComments = findViewById(R.id.showCommentsBtn);
        spinner = findViewById(R.id.perTimingSpinner);
        costTV = findViewById(R.id.name);
        mileage= findViewById(R.id.mileageValue);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cost_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);

        bikeImages = findViewById(R.id.rvBikeImages);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference  = firebaseDatabase.getReference("Vehicles");
        ratingReference = firebaseDatabase.getReference("Rating");

      //  viewPager = findViewById(R.id.view_pager);
         intent = getIntent();
           if (intent != null) {
             startDateStr = intent.getStringExtra("startDate");
             endDateStr = intent.getStringExtra("endDate");
               bikeId = intent.getStringExtra("BikeId");
               if (bikeId!=null && !bikeId.isEmpty()) {
                   if (Common.isConnectedToInternet(this)) {
                       getBikeInfo();
                       getRating();
                   }
                   else {
                       Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                       return;
                   }
               }else{
                 Toast.makeText(this, "Details download failed. Try again!", Toast.LENGTH_SHORT).show();
                 finish();
               }
           }


           book_btn.setOnClickListener(this);
           showComments.setOnClickListener(this);
        add_to_cart.setOnClickListener(this);

        add_to_cart.setCount(new Database(this).getCartCount());
        rating_btn.setOnClickListener(this);



//        images.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                SetOfImages sOI= dataSnapshot.child("01").getValue(SetOfImages.class);
//                imageUrls[1]=sOI.image2;
//                    imageUrls[0]=sOI.image1;
//                ViewPager viewPager = findViewById(R.id.view_pager);
//                SliderAdapter adapter = new SliderAdapter(BikeDetailsActivity.this, imageUrls);
//                viewPager.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



//        Intent intent = getIntent();
//        if (intent != null) {
//
//            if (intent.getSerializableExtra("bikeModel") != null)
//                vehicle = (Vehicle) intent.getSerializableExtra("bikeModel");
//           Picasso.get().load(vehicle.getBikeImageSmall()).into(bikeImage);
//            bikeName.setText(vehicle.getName());
//            cost.setText(vehicle.getCost()+"");
//            cc.setText(vehicle.getCc()+"");
//            year.setText(vehicle.getYear()+"");
//            weight.setText(vehicle.getWeight()+"");
//            details.setText(vehicle.getDetails());
//
//        }

        bikeImages.setHasFixedSize(true);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bikeImages.setLayoutManager(layoutManager);



        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(bikeImages);




        bikeImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View v = snapHelper.findSnapView(layoutManager);
                int pos = layoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHolder = bikeImages.findViewHolderForAdapterPosition(pos);
                CardView rl1 = viewHolder.itemView.findViewById(R.id.bikeLL);

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    rl1.animate().setDuration(350).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                }else{
                    rl1.animate().setDuration(350).scaleX(0.75f).scaleY(0.75f).setInterpolator(new AccelerateInterpolator()).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }

    private void loadImages() {

        adapter = new BikeImagesAdapter(imageUrls,this);
        adapter.notifyDataSetChanged();
        bikeImages.setAdapter(adapter);
    }





    private void getRating() {
        Query rate = ratingReference.orderByChild("vehicleId").equalTo(bikeId);
        rate.addListenerForSingleValueEvent(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postData : dataSnapshot.getChildren()){
                    RatingVehicle curRate = postData.getValue(RatingVehicle.class);
                    sum+=Integer.parseInt(curRate.getRating());
                    count++;
                }
                if(count!=0) {
                    float avg = (float)sum / count;
                    ratingBar.setRating(avg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    String bikeOrScooty;

    private void getBikeInfo() {
        databaseReference.child(bikeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 vehicle =dataSnapshot.getValue(Vehicle.class);
               // Picasso.get().load(vehicle.getBikeImageSmall()).into(bikeImage);
                if(vehicle!=null) {
                    imageUrls = new String[]{
                            vehicle.getImage1(), vehicle.getImage2(), vehicle.getImage3(), vehicle.getImage4()
                    };
                    loadImages();

                    bikeOrScooty = vehicle.getParId().substring(0,vehicle.getParId().indexOf('_'));

                 //   SliderAdapter adapter = new SliderAdapter(BikeDetailsActivity.this, imageUrls);
                  //  viewPager.setAdapter(adapter);
                    collapsingToolbarLayout.setTitle(vehicle.getName());

                    //Including the Rupee Symbol
                    amt= Integer.parseInt(vehicle.getCost());
//                    Locale locale=new Locale("en","in");
//                    NumberFormat format=NumberFormat.getCurrencyInstance(locale);
                    setCostHashMap();
                    setKMHashMap();

                    String costTime;
                    if(intent.getStringExtra("bikeCostTime")!=null) {
                        costTime = intent.getStringExtra("bikeCostTime");
                        cost.setText(costTime);
                        spinner.setVisibility(View.INVISIBLE);
                        isFromTime =1;
                    }
                    else
                        cost.setText(amt+"");
                    cc.setText(vehicle.getCc() + "");
                    year.setText(vehicle.getYear() + "");
                    weight.setText(vehicle.getWeight() + "");
                    details.setText(vehicle.getDetails());
                    bhp.setText(vehicle.getBhp());
                    tSize.setText(vehicle.gettSize());
                    Glide.with(getApplicationContext()).asGif()
                        .load(vehicle.getGif())
                        .into(bikeGif);
                    Picasso.get().load(vehicle.getCompany()).into(companyIcon);
                    mileage.setText(vehicle.getMileage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(5)
                .setTitle("Rate this vehicle")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setDefaultComment("This vehicle is pretty cool!")
                .setStarColor(R.color.star_yellow)
                .setNoteDescriptionTextColor(R.color.btn_red_bg )
                .setTitleTextColor(R.color.btn_red_bg)
                .setDescriptionTextColor(R.color.black)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.btn_red_bg)
                .setCommentTextColor(R.color.black)
                .setCommentBackgroundColor(R.color.grey)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(BikeDetailsActivity.this)
                .show();
    }
    @Override
    public void onClick(View v) {

        Intent i;
        switch (v.getId()){
            case R.id.showCommentsBtn:
                 i = new Intent(this,ShowComments.class);
                 i.putExtra("vehicleId",bikeId);
                 i.putExtra("vehicleName",collapsingToolbarLayout.getTitle().toString());
                 startActivity(i);
                 break;
            case R.id.book_btn:
                getInputDate();

                break;
            case R.id.rating_btn:
                showDialog();
                break;
            case R.id.addToCart_btn:
                new Database(this).addToCart(new Order(
                        bikeId, vehicle.getName() , 1,vehicle.getCost()+"" , "0",vehicle.getImage1())
                );
                add_to_cart.setCount(new Database(this).getCartCount());

                Toast.makeText(this,vehicle.getName()+" added to Package",Toast.LENGTH_SHORT).show();

                break;
        }
    }

    int startHour,startMin;
    private void getInputDate() {
        if(isFromTime!=1){
            //Start date - > start time - > display journey Time ( Proceed? can edit endDate and end time ) -> accessories .
            startDateChose();

        }else   //It is from Find Vehicles , hence it already contains the Start date and end date .
        {
            Intent i= new Intent(this,AccessoryListScreen.class);
            i.putExtra("bikeCost",cost.getText().toString());
            i.putExtra("bikeId",bikeId);
            i.putExtra("bikeImage",imageUrls[0]);
            i.putExtra("bikeName",collapsingToolbarLayout.getTitle().toString());
            i.putExtra("startDate",startDateStr);
            i.putExtra("endDate",endDateStr);
            startActivity(i);
        }

    }

    String startDateString , startTimeString,endDateString; //start date selected by user from
    int startYear , startMonth , startDay;
  List<Pair<Integer,Integer>> dates;
  private void startDateChose() {
    Calendar now = Calendar.getInstance();
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
      BikeDetailsActivity.this,
      now.get(Calendar.YEAR), // Initial year selection
      now.get(Calendar.MONTH), // Initial month selection
      now.get(Calendar.DAY_OF_MONTH) // Inital day selection
    );
    dpd.setAccentColor(getResources().getColor(R.color.btn_red_bg));
    dpd.setTitle("Your Journey Start Date");
    //Past dates are not allowed
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
    dpd.setMinDate(calendar);



    dates = new ArrayList<>();  //for (mon,day) toadd in disable dates for calender
    databaseReference.child(bikeId).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()){
          Vehicle chosenVeh = dataSnapshot.getValue(Vehicle.class);
          List<List<Integer>> bookingSlots = chosenVeh.getBookingSlot();
          if(bookingSlots!=null) {
            for (int i = 0; i < bookingSlots.size(); i++) {
              int startDateTime = bookingSlots.get(i).get(0);
              int endDateTime = bookingSlots.get(i).get(1);
              startDateTime-=10000000;
              endDateTime-=10000000;
              int sMon = 3, sDay = 21, eMon = 10, eDay = 28;
              String startDate = String.valueOf(startDateTime);
              String endDate = String.valueOf(endDateTime);

              if (startDate.length() == 8) {
                sDay = Integer.parseInt(startDate.substring(2, 4));
                sMon = Integer.parseInt(startDate.substring(0, 2));
                dates.add(new Pair<>(sMon, sDay));
              } else if (startDate.length() == 7) {
                sDay = Integer.parseInt(startDate.substring(1, 3));
                sMon = Integer.parseInt("0" + startDate.substring(0, 1));
                dates.add(new Pair<>(sMon, sDay));
              }
              if (endDate.length() == 8) {
                eDay = Integer.parseInt(endDate.substring(2, 4));
                eMon = Integer.parseInt(endDate.substring(0, 2));
                dates.add(new Pair<>(eMon, eDay));
              } else if (endDate.length() == 7) {
                eDay = Integer.parseInt(endDate.substring(1, 3));
                eMon = Integer.parseInt("0" + endDate.substring(0, 1));
                dates.add(new Pair<>(eMon, eDay));
              }
              int noOfDays = 0, flag = 0;
              for (int j = 0; j <= eMon - sMon; j++) {
                int curMon = sMon + j;
                if ((curMon == 1) || (curMon == 3) || (curMon == 5) || (curMon == 7) ||
                  (curMon == 8) || (curMon == 10) || (curMon == 12))
                  noOfDays = 31;
                else if (curMon == 2)
                  noOfDays = 28;
                else
                  noOfDays = 30;
                for (int k = 1; k <= noOfDays; k++) {
                  if (curMon == sMon) {
                    if (curMon == eMon) {
                      flag = 1;
                      if (k > sDay && k < eDay)
                        dates.add(new Pair<>(curMon, k));
                    } else {
                      if (k > sDay)
                        dates.add(new Pair<>(curMon, k));
                    }

                  } else if (curMon == eMon && flag != 1) {
                    if (k < eDay)
                      dates.add(new Pair<>(curMon, k));
                  } else
                    dates.add(new Pair<>(curMon, k));

                }
              }
            }

            List<Calendar> disableDatesList = new ArrayList<>();
            //Disabling dates that have been already booked
            for (int i = 0; i < dates.size(); i++) {
              //Get List of booked Range , loop in that range and extract MM,DD
              Calendar calendar1 = Calendar.getInstance();
              int month = dates.get(i).first;
              int day = dates.get(i).second;
              calendar1.set(Calendar.getInstance().get(Calendar.YEAR), month - 1, day);
              disableDatesList.add(calendar1);

            }
            Calendar[] disableDates = new Calendar[disableDatesList.size()];
            disableDates = disableDatesList.toArray(disableDates);

            dpd.setDisabledDays(disableDates);
          }

        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
      }
    });

    dpd.show(getSupportFragmentManager(), "Datepickerdialog");

    }

    public void displayTime(){
        // from selection of time via spinner , calculate endTime - > display in bottomOverlay ( with book btn ) - > take to accessory screen


        final BottomSheetDialog bt=new BottomSheetDialog(BikeDetailsActivity.this,R.style.BottomSheetDialogTheme);
        View view= LayoutInflater.from(BikeDetailsActivity.this).inflate(R.layout.time_confirm_lay,null);
        view.findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(BikeDetailsActivity.this,AccessoryListScreen.class);
                i.putExtra("bikeCost",cost.getText().toString());
                i.putExtra("bikeId",bikeId);
                i.putExtra("bikeImage",imageUrls[0]);
                i.putExtra("bikeName",collapsingToolbarLayout.getTitle().toString());
                i.putExtra("startDate",startDateString+" , "+startTimeString);
                i.putExtra("endDate",endDateString+" , "+endTimeString);

                startActivity(i);
                bt.dismiss();
            }
        });
        ImageView imageView=view.findViewById(R.id.my_image);
        Picasso.get().load(imageUrls[0]).into(imageView);
        TextView name=view.findViewById(R.id.txt_prname);
        name.setText(collapsingToolbarLayout.getTitle());

        TextView price=view.findViewById(R.id.txt_prprice);
        Locale locale=new Locale("en","in");
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        String costString =  cost.getText().toString();
        if(costString.contains("mo"))
          price.setText(format.format(Integer.parseInt(costString.substring(0,costString.indexOf('/')))));
        else
          price.setText(format.format(Integer.parseInt(cost.getText().toString())));
        view.findViewById(R.id.startJourney).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDateChose();
                bt.dismiss();

            }
        });

        view.findViewById(R.id.endJourney).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              endDateChose();
              bt.dismiss();


            }
        });
        TextView endTimeTV = view.findViewById(R.id.endTime);
        endTimeTV.setText(endTimeTV.getText().toString()+endDateString+" , "+endTimeString);
        TextView startTimeTV = view.findViewById(R.id.startTime);
        startTimeTV.setText(startTimeTV.getText().toString()+startDateString+" , "+startTimeString);
        bt.setContentView(view);
        bt.show();




    }
    int sTime ;
    private void startTimeChose() {
        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        int minsCur = rightNow.get(Calendar.MINUTE);
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this,startHour,startMin,0,false);
        timePickerDialog.setAccentColor(getResources().getColor(R.color.btn_red_bg));

        if(monthCal==rightNow.get(Calendar.MONTH) && dayCal==rightNow.get(Calendar.DAY_OF_MONTH))
            timePickerDialog.setMinTime((hours+2)%24,minsCur,0);

        timePickerDialog.setThemeDark(true);
        timePickerDialog.setTitle("Your Journey Start Time");
        timePickerDialog.dismissOnPause(true); timePickerDialog.show(getSupportFragmentManager(), "StartTimeickerdialog");
    }
    String endTimeString ;
    int endHour, endMin;
    private void endTimeChose() {
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this, startHour,startMin,0,false);
        timePickerDialog.setAccentColor(getResources().getColor(R.color.btn_red_bg));

        String[] startDateNo =startDateString.split("/") ;
        String[] endDateNo = endDateString.split("/");
        timePickerDialog.setThemeDark(true);
        timePickerDialog.setTitle("Your Journey End Time");
        timePickerDialog.dismissOnPause(true);
        if(position == 1 && sTime>=1800 && !startDateNo[0].equals(endDateNo[0])) { //That means not for the same date
            timePickerDialog.setMinTime(0, startMin, 0);  //Since then next day starts
            timePickerDialog.setMaxTime((startHour + 6) % 24, startMin, 0);
        }
        else if(position == 1 && sTime>=1800 ) {    //Means 31st and 31st
            timePickerDialog.setMinTime(startHour, startMin, 0);
            timePickerDialog.setMaxTime(23, 30, 0);   //Maximum tonight
        }else if(position == 1 && sTime<1800 ) {    //Means 31st and 31st
            timePickerDialog.setMinTime(startHour, startMin, 0);
            timePickerDialog.setMaxTime((startHour+6)%24, startMin, 0);   //Maximum tonight
        }
        else if(position == 2 && sTime>=1200 && !startDateNo[0].equals(endDateNo[0])) {
            timePickerDialog.setMinTime(0, startMin, 0);  //Since then next day starts
            timePickerDialog.setMaxTime((startHour + 12) % 24, startMin, 0);
        }
        else if(position == 2 && sTime>=1200 ){
            timePickerDialog.setMinTime(startHour, startMin, 0);
            timePickerDialog.setMaxTime(23, 30, 0);   //Maximum that night
        }else if(position == 2 && sTime<1200 ){
            timePickerDialog.setMinTime(startHour, startMin, 0);
            timePickerDialog.setMaxTime((startHour+12)%24, startMin, 0);   //Maximum that night
        }
        else if (startDateNo[0].equals(endDateNo[0])  && startDateNo[1].equals(endDateNo[1])) {
            timePickerDialog.setMinTime(startHour, startMin, 0);
            timePickerDialog.setMaxTime(23, 30, 0);
        }
        else
             timePickerDialog.setMaxTime(startHour,startMin,0);     //Since day has already changed
        timePickerDialog.show(getSupportFragmentManager(), "EndTimeickerdialog");

    }

    int monthCal,dayCal;
  int endMonthCal , endDateCaldayCal;

  private void endDateChose() {
      Calendar now = Calendar.getInstance();
      com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
        BikeDetailsActivity.this,
        now.get(Calendar.YEAR), // Initial year selection
        now.get(Calendar.MONTH), // Initial month selection
        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
      );
      dpd.setAccentColor(getResources().getColor(R.color.btn_red_bg));
      dpd.setTitle("Your Journey Start Date");
      //Only Max 1 day allowed
      Calendar minCal = Calendar.getInstance();
      minCal.set(startYear,monthCal,dayCal);
      dpd.setMinDate(minCal);

    endMonthCal=monthCal ;
    endDateCaldayCal=dayCal;
      Calendar maxCal = Calendar.getInstance();
      //Depending of choice of spinner
      if(position == 0) {
        maxCal.set(startYear, monthCal, dayCal + 1);
        endMonthCal=monthCal;
        endDateCaldayCal=dayCal + 1;
      }
      else if(position == 1) {
        if (sTime>=1800)        // evening after 6 => 6hours package_image mein day change ho jaega. Time will be MOD 24
        {  maxCal.set(startYear, monthCal, dayCal + 1);
          endDateCaldayCal= dayCal+ 1;
        }
        else
          maxCal.set(startYear, monthCal, dayCal);
      }
      else if(position == 2){
        if (sTime>=1200){
          maxCal.set(startYear, monthCal, dayCal + 1);
          endDateCaldayCal=dayCal + 1;
        }
        else
          maxCal.set(startYear, monthCal, dayCal);
      }
      else if(position == 3){
        maxCal.set(startYear,monthCal,dayCal+7);
        endDateCaldayCal= dayCal+ 7;
      }
      else if(position == 4){
        maxCal.set(startYear , monthCal , dayCal+14);
        endDateCaldayCal= dayCal+ 14;
      }
      else if(position == 5){
        maxCal.set(startYear,monthCal+1,dayCal);
        endMonthCal=monthCal+1;
      }
      else if(position == 6){
        maxCal.set(startYear+1,monthCal , dayCal);
      }
      dpd.setMaxDate(maxCal);
      dates = new ArrayList<>();  //for (mon,day) to add in disable dates for calender
      databaseReference.child(bikeId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if(dataSnapshot.exists()){
            Vehicle chosenVeh = dataSnapshot.getValue(Vehicle.class);
            List<List<Integer>> bookingSlots = chosenVeh.getBookingSlot();
            if(bookingSlots!=null) {
              for (int i = 0; i < bookingSlots.size(); i++) {
                int startDateTime = bookingSlots.get(i).get(0);
                int endDateTime = bookingSlots.get(i).get(1);
                startDateTime-=10000000;
                endDateTime-=10000000;
                int sMon = 3, sDay = 21, eMon = 10, eDay = 28;
                String startDate = String.valueOf(startDateTime);
                String endDate = String.valueOf(endDateTime);

                if (startDate.length() == 8) {
                  sDay = Integer.parseInt(startDate.substring(2, 4));
                  sMon = Integer.parseInt(startDate.substring(0, 2));
                  dates.add(new Pair<>(sMon, sDay));
                } else if (startDate.length() == 7) {
                  sDay = Integer.parseInt(startDate.substring(1, 3));
                  sMon = Integer.parseInt("0" + startDate.substring(0, 1));
                  dates.add(new Pair<>(sMon, sDay));
                }
                if (endDate.length() == 8) {
                  eDay = Integer.parseInt(endDate.substring(2, 4));
                  eMon = Integer.parseInt(endDate.substring(0, 2));
                  dates.add(new Pair<>(eMon, eDay));
                } else if (endDate.length() == 7) {
                  eDay = Integer.parseInt(endDate.substring(1, 3));
                  eMon = Integer.parseInt("0" + endDate.substring(0, 1));
                  dates.add(new Pair<>(eMon, eDay));
                }

                // startDateOfBookingSlot  between ( > ) ChosenStartDate &  ( < ) choesnEndDate , then we make startDateOfBookingSlot  the maximum date possible
                Date date1 = new GregorianCalendar(startYear, sMon-1, sDay).getTime();    //Of bookingSlot
                Date date2 = new GregorianCalendar(startYear, monthCal, dayCal).getTime();    // Chosen start date
                Date date3 = new GregorianCalendar(startYear, endMonthCal, endDateCaldayCal).getTime();    //chosen end date
                long diff1 = date2.getTime() - date1.getTime();   //   Should be negative,i.e startDateOfBookingSlot is greater
                long diff2 = date3.getTime() - date1.getTime();   // Should be positive ,i.e startDateOfBookingSlot is less

                if(diff1 <= 0 && diff2 >= 0)
                {
                  maxCal.set(startYear,sMon-1 , sDay);
                  dpd.setMaxDate(maxCal);
                }
                int noOfDays = 0, flag = 0;
                for (int j = 0; j <= eMon - sMon; j++) {
                  int curMon = sMon + j;
                  if ((curMon == 1) || (curMon == 3) || (curMon == 5) || (curMon == 7) ||
                    (curMon == 8) || (curMon == 10) || (curMon == 12))
                    noOfDays = 31;
                  else if (curMon == 2)
                    noOfDays = 28;
                  else
                    noOfDays = 30;
                  for (int k = 1; k <= noOfDays; k++) {
                    if (curMon == sMon) {
                      if (curMon == eMon) {
                        flag = 1;
                        if (k > sDay && k < eDay)
                          dates.add(new Pair<>(curMon, k));
                      } else {
                        if (k > sDay)
                          dates.add(new Pair<>(curMon, k));
                      }

                    } else if (curMon == eMon && flag != 1) {
                      if (k < eDay)
                        dates.add(new Pair<>(curMon, k));
                    } else
                      dates.add(new Pair<>(curMon, k));

                  }
                }
              }

              List<Calendar> disableDatesList = new ArrayList<>();
              //Disabling dates that have been already booked
              for (int i = 0; i < dates.size(); i++) {
                //Get List of booked Range , loop in that range and extract MM,DD
                Calendar calendar1 = Calendar.getInstance();
                int month = dates.get(i).first;
                int day = dates.get(i).second;
                calendar1.set(Calendar.getInstance().get(Calendar.YEAR), month - 1, day);
                disableDatesList.add(calendar1);

              }
              Calendar[] disableDates = new Calendar[disableDatesList.size()];
              disableDates = disableDatesList.toArray(disableDates);

              dpd.setDisabledDays(disableDates);
            }

          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
          Toast.makeText(BikeDetailsActivity.this, "Unable to find pre-booking dates", Toast.LENGTH_SHORT).show();
        }
      });

      dpd.show(getSupportFragmentManager(), "EndDatepickerdialog");
    }


    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comment) {
        RatingVehicle ratingVehicle = new RatingVehicle(Common.currentUser.getPhone() , Common.currentUser.getName() ,
                 String.valueOf(value),comment,bikeId );

        //Using Firebase generated Keys , so that chats can rate multiple vehicles
        ratingReference.push().setValue(ratingVehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(BikeDetailsActivity.this, "Thank you for your response", Toast.LENGTH_SHORT).show();
            }
        });
        /* Below Commented code let's user rate only 1 vehicle , i.e. 1 comment per user
        ratingReference.child(Common.currentUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()){
                    //Update previous value
                    ratingReference.child(Common.currentUser.getPhone()).removeValue();
                    ratingReference.child(Common.currentUser.getPhone()).setValue(ratingVehicle);
                }
                else
                    ratingReference.child(Common.currentUser.getPhone()).setValue(ratingVehicle);
                Toast.makeText(BikeDetailsActivity.this, "Thank you for your response", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
        getRating();
    }

  @Override
  protected void onResume() {
    super.onResume();
    if(isFromTime != 1 && costMap!=null) {
      cost.setText(costMap.get(selected).toString());
      if ((150 * 365) == kmMap.get(selected)) {
        cost.setText(String.valueOf(costMap.get(selected)/12)+"/mo");
        kilometersValue.setText(String.valueOf(kmMap.get(selected)/12) + "/mo");
      } else {
        String kmDaySuffix = kmMap.get(selected)==kms? kmMap.get(selected).toString()+"/day":kmMap.get(selected).toString();
        kilometersValue.setText(kmDaySuffix);
      }
    }
  }

  public void setCostHashMap(){
        costMap = new HashMap<>();
        if(bikeOrScooty.equals("01")) {
            costMap.put("1 Day", amt);
            costMap.put("6 hours", amt - 200);
            costMap.put("12 hours", amt - 100);
            costMap.put("1 Week", (amt - 150) * 7);
            costMap.put("2 Weeks", (amt - 250) * 14);
            costMap.put("1 Month", (amt-600) * 30);
            costMap.put("1 Year", (amt-700) * 365);
        }
        else{
        costMap.put("1 Day",amt);
        costMap.put("6 hours",amt-200);
        costMap.put("12 hours",amt-100);
        costMap.put("1 Week",(amt-100)*7);
        costMap.put("2 Weeks",(amt-200)*14);
        costMap.put("1 Month",(amt-300)*30);
        costMap.put("1 Year",(amt-400)*365);
     }
    }
    int kms=250;
    HashMap<String,Integer> kmMap;
    public void setKMHashMap(){
        kmMap = new HashMap<>();
        if(bikeOrScooty.equals("01")) {
            kmMap.put("1 Day", kms);
            kmMap.put("6 hours", kms - 100);
            kmMap.put("12 hours", kms - 50);
            kmMap.put("1 Week", kms * 7);
            kmMap.put("2 Weeks", 3000);
            kmMap.put("1 Month", 150 * 30);
            kmMap.put("1 Year", 150 * 365);
        }
        else
        {
            kms=160;
            kmMap.put("1 Day", kms);
            kmMap.put("6 hours", kms - 60);
            kmMap.put("12 hours", kms - 30);
            kmMap.put("1 Week", kms * 7);
            kmMap.put("2 Weeks", 3000);
            kmMap.put("1 Month", 150 * 30);
            kmMap.put("1 Year", 150 * 365);
        }
    }
  String selected;
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        position = pos;
         selected = parent.getItemAtPosition(pos).toString();
//        Locale locale=new Locale("en","in");
//        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        if(isFromTime != 1 && costMap!=null) {
            cost.setText(costMap.get(selected).toString());
            if ((150 * 365) == kmMap.get(selected)) {
                cost.setText(String.valueOf(costMap.get(selected)/12)+"/mo");
                kilometersValue.setText(String.valueOf(kmMap.get(selected)/12) + "/mo");
            } else {
                String kmDaySuffix = kmMap.get(selected)==kms? kmMap.get(selected).toString()+"/day":kmMap.get(selected).toString();
                kilometersValue.setText(kmDaySuffix);
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        if(view.getTag().equals("EndTimeickerdialog")) {
            endHour = hourOfDay;
            if (minute % 30 != 0) {
                int minuteFloor = minute - (minute % 30);
                minute = minuteFloor + (minute == minuteFloor + 1 ? 30 : 0);
                if (minute == 60)
                    minute = 0;
            }
            endMin = minute;
            endTimeString = (endHour < 10 ? "0" + endHour : endHour) + ":" + (endMin < 10 ? "0" + endMin : endMin);
            displayTime();
        }
        else if(view.getTag().equals("StartTimeickerdialog")){
            startHour = hourOfDay;
            if (minute%30!=0) {
                int minuteFloor = minute - (minute % 30);
                minute = minuteFloor + (minute == minuteFloor + 1 ? 30 : 0);
                if (minute == 60)
                    minute = 0;
            }
            startMin = minute;
            startTimeString=(startHour<10?"0"+startHour:startHour)+":"+(startMin<10?"0"+startMin:startMin);
            sTime = Integer.parseInt(startTimeString.substring(0, 2) + startTimeString.substring(2 + 1));
            endDateChose();
        }
    }
  @Override
  public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    //   String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
    if(view.getTag().equals("Datepickerdialog")) {
      monthCal = monthOfYear;
      dayCal = dayOfMonth;
      startYear = year;
      startMonth = monthOfYear ;
      startDay = dayOfMonth;
      startDateString =dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
      startTimeChose();
    }
    else if(view.getTag().equals("EndDatepickerdialog")){
      endDateString =dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
      endTimeChose();
    }
  }

}
