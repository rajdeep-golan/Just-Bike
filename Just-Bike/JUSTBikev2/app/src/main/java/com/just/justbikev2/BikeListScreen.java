package com.just.justbikev2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.BikeListAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.Model.Vehicle;
import com.just.justbikev2.ViewHolder.ViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class BikeListScreen extends AppCompatActivity implements View.OnClickListener, BikeListAdapter.OnClickListener , com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener
, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {
    RecyclerView bikeListRecyclerView;
    RecyclerView bikeListRecyclerView2;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutManager2;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference vehicleList;
    FirebaseRecyclerAdapter<Vehicle, ViewHolder> adapter;

  BikeListAdapter adapter2;
    ArrayList<Vehicle> vehicleSet;

    String categoryId;

    Vehicle vehicleCLicked;

    private ShimmerFrameLayout mShimmerViewContainer;

    String startDate , startTime , endDate, endTime;

    Database localdb;

    //Facebook Share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    String bikeId;


    int refershFlag =0;
    String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    //Creating Target from Picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(bitmap).build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().addPhoto(sharePhoto).build();
                shareDialog.show(sharePhotoContent);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    //Search functionality
    FirebaseRecyclerAdapter<Vehicle, ViewHolder> searchAdapter;
    List<String> suggestedSearch = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    SwipeRefreshLayout swipeRefreshLayout;

    static BikeListScreen instance;
    public static BikeListScreen getInstance(){
      return instance;
    }
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
      setContentView(R.layout.bike_list_recycler);
      if(Common.currentUser==null || Common.currentUser.getPhone().equals("") || Common.currentUser.getPhone() == null)
      {
        Toast.makeText(BikeListScreen.this, "Please Login to Continue", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(BikeListScreen.this,SignInAcitivity.class);
        startActivity(i);
      }
      else{
      instance = this;
      materialSearchBar = findViewById(R.id.serachBar);

      mShimmerViewContainer = findViewById(R.id.shimmer);
      mShimmerViewContainer.startShimmer();

      swipeRefreshLayout = findViewById(R.id.RVBikeList);
      swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
        R.color.orange_btn,
        R.color.com_facebook_blue,
        R.color.green);


      swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          if (Common.isConnectedToInternet(getBaseContext())) {
            if (categoryId != null && !categoryId.isEmpty()) {
              mShimmerViewContainer.startShimmer();
              mShimmerViewContainer.setVisibility(View.VISIBLE);
              loadVehicles();
              loadSuggestList();
              adapter.startListening();


            } else if (startDate != null && !startDate.isEmpty()) {
              if (refershFlag != 1) {
                // adapter.startListening();
                mShimmerViewContainer.startShimmer();
                mShimmerViewContainer.setVisibility(View.VISIBLE);
                loadVehiclesTime();
              } else
                swipeRefreshLayout.setRefreshing(false);
            }

          } else {
            Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            return;
          }
        }
      });
      //Init Facebook
      callbackManager = CallbackManager.Factory.create();
      shareDialog = new ShareDialog(this);
      if (getIntent() != null) {
        categoryId = getIntent().getStringExtra("CategoryId");
        startDate = getIntent().getStringExtra("startDate");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
      }
      // if((categoryId!=null && !categoryId.isEmpty()) ||  (startDate!=null && !startDate.isEmpty())) {

      if (Common.isConnectedToInternet(this)) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        vehicleList = firebaseDatabase.getReference("Vehicles");
        bikeListRecyclerView = findViewById(R.id.bikeRecycler);
        bikeListRecyclerView2 = findViewById(R.id.bikeRecycler2);

        layoutManager = new LinearLayoutManager(this);
        layoutManager2 = new LinearLayoutManager(this);
        bikeListRecyclerView.setLayoutManager(layoutManager);
        bikeListRecyclerView.setHasFixedSize(true);
        bikeListRecyclerView2.setLayoutManager(layoutManager2);
        bikeListRecyclerView2.setHasFixedSize(true);
        localdb = new Database(this);


        if (categoryId != null && !categoryId.isEmpty()) {
          loadVehicles();
          loadSuggestList();
          //Searching
          materialSearchBar.setHint("Search...");

          materialSearchBar.setCardViewElevation(10);
          materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              int c = 0;
              List<String> suggest = new ArrayList<>();
              for (String search : suggestedSearch) {
                if (c < 1)
                  if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                    suggest.add(search);
                    c++;
                  }
              }
              materialSearchBar.setLastSuggestions(suggest);
              if (!materialSearchBar.getText().isEmpty() || !materialSearchBar.getText().equals("")) {
                showSearchItems(s.toString());
                searchAdapter.startListening();
              }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
          });
          materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
              //When searchBar is closed , restore to original data
              if (!enabled)
                bikeListRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
              //When search finished , show resultant searched items
              showSearchItems(text);
              searchAdapter.startListening();
              materialSearchBar.clearSuggestions();

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
          });

        } else if (startDate != null && !startDate.isEmpty()) {
          mShimmerViewContainer.startShimmer();
          mShimmerViewContainer.setVisibility(View.VISIBLE);
          loadVehiclesTime();
          if (materialSearchBar != null)
            materialSearchBar.setVisibility(View.GONE);

        }

      } else {
        Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        return;
      }
    }

    }
    int hours;
    int noOfDays;
    Date d1 = null;
    Date d2 = null;
  int startSearchInt ;
  int endSearchInt;

  String  stringStartDate , stringEndDate;
  private void loadVehiclesTime() {
        refershFlag=1;


        bikeListRecyclerView2.setVisibility(View.VISIBLE);

        vehicleSet = new ArrayList<>();

        HashMap<String,Integer>  monthCount = new HashMap<>();
        for(int i =0 ;i<12;i++)
            monthCount.put(months[i],i+1);

        String[] dateString = startDate.split(" ");         //startDate.split(" - ");
        String startDateDigit="",endDateDigit="",dateDigit="",monthDigit="",dateDigit1="",monthDigit1="";
        try
        {
            // checking valid integer using parseInt() method
            Integer.parseInt(dateString[1]);
             dateDigit = dateString[1];
             monthDigit = monthCount.get(dateString[0]).toString();
             startDateDigit = (monthDigit.length()==1?"0"+monthDigit:monthDigit)+(dateDigit.length()==1?"0"+dateDigit:dateDigit);

             dateDigit1 = dateString[4];
             monthDigit1 = monthCount.get(dateString[3]).toString();
             endDateDigit = (monthDigit1.length()==1?"0"+monthDigit1:monthDigit1)+(dateDigit1.length()==1?"0"+dateDigit1:dateDigit1);

        }
        catch (NumberFormatException e)
        {
             dateDigit = dateString[0];
             monthDigit = monthCount.get(dateString[1]).toString();
             startDateDigit = (monthDigit.length()==1?"0"+monthDigit:monthDigit)+(dateDigit.length()==1?"0"+dateDigit:dateDigit);

             dateDigit1 = dateString[3];
             monthDigit1 = monthCount.get(dateString[4]).toString();
             endDateDigit = (monthDigit1.length()==1?"0"+monthDigit1:monthDigit1)+(dateDigit1.length()==1?"0"+dateDigit1:dateDigit1);
        }

finally{
            String startSearch = startDateDigit + (startTime.substring(0, 2) + startTime.substring(3));
            String endSearch = endDateDigit + (endTime.substring(0, 2) + endTime.substring(3));

             startSearchInt = 10000000 + Integer.parseInt(startSearch);      //For month in request and database , add 10 to month , in each slot , like for Nov , 21 (11+10) for April 14 (4+10)  //Added 10^7 for 8061930 [Mddhhmm] to make it 18061930 , eaiser for calculation [MMddhhmm]
             endSearchInt = 10000000 + Integer.parseInt(endSearch);

             stringStartDate = (monthDigit.length() == 1 ? "0" + monthDigit : monthDigit) + "/" + (dateDigit.length() == 1 ? "0" + dateDigit : dateDigit) + "/2020  " + startTime;
             stringEndDate = (monthDigit1.length() == 1 ? "0" + monthDigit1 : monthDigit1) + "/" + (dateDigit1.length() == 1 ? "0" + dateDigit1 : dateDigit1) + "/2020 " + endTime;

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

            try {
                d1 = sdf.parse(stringStartDate);
                d2 = sdf.parse(stringEndDate);
              DateTime dateTime1=new DateTime(),dateTime2=new DateTime();
                if(d1!=null && d2!=null) {
                   dateTime1 = new DateTime(d1);
                   dateTime2 = new DateTime(d2);
                }

                noOfDays = Days.daysBetween(dateTime1, dateTime2).getDays();
                int noOfHours = Hours.hoursBetween(dateTime1, dateTime2).getHours();
                hours = noOfHours % 24;
                //  int noOfMins = Minutes.minutesBetween(dateTime1,dateTime2).getMinutes()%3600;


//            Toast.makeText(this, noOfDays+","+noOfHours+","+noOfMins, Toast.LENGTH_LONG).show();


            } catch (ParseException e) {
                e.printStackTrace();
            }
            shimmerCounter=0;

            //Creating the list of vehicles
          vehicleList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              int bookedOrNot = 0;
              for(DataSnapshot postData : dataSnapshot.getChildren()) {
                bikeId = postData.getKey();
                bookedOrNot = 0;
                Vehicle vehicle = postData.getValue(Vehicle.class);
                if (vehicle.getBookingSlot() != null && vehicle.getBookingSlot().size() > 0) {
                  for (int i = 0; i < vehicle.getBookingSlot().size(); i++) {
                    List<Integer> dateTime = vehicle.getBookingSlot().get(i);
                    if( dateTime!=null && ((endSearchInt < dateTime.get(0) && startSearchInt< dateTime.get(0))
                      || (startSearchInt > dateTime.get(1) && endSearchInt > dateTime.get(1))))
                    { // It does not conflict with this booking slot
                    }
                    else
                    {
                      bookedOrNot = 1;
                    }
                  }
                }

                if (bookedOrNot == 0 && vehicle.getCityId()!=null && vehicle.getCityId().equals(Common.cityId))
                  vehicleSet.add(vehicle);
              }
              adapter2 = new BikeListAdapter(BikeListScreen.this,vehicleSet,BikeListScreen.this::onSelect);

              adapter2.notifyDataSetChanged();
              bikeListRecyclerView2.setAdapter(adapter2);
              if(shimmerCounter==0) {
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.hideShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
                shimmerCounter = 1;
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              Toast.makeText(BikeListScreen.this, "No more vehicles available.", Toast.LENGTH_SHORT).show();
            }
          });

        }

    }

    public int getAmountAsPerTime(String cost) {
        int amt = Integer.parseInt(cost);
        int hourPrice =0, dayPrice=0;
        if(0<hours && hours<=6)
            hourPrice = amt - 200;
        else if(6<hours && hours<=12)
            hourPrice = amt -100;
        else if(12<hours && hours<=24)
            hourPrice = amt;
        if(1<=noOfDays && noOfDays<=7)
            dayPrice =amt*noOfDays;
        else if(7<noOfDays && noOfDays<=14)
            dayPrice = (amt-100)*noOfDays;
        else if(14<noOfDays && noOfDays<=30)
            dayPrice = (amt-200)*noOfDays;
        else if(30<noOfDays)
            dayPrice = (amt-300)*noOfDays;

        if(hourPrice+dayPrice <= 0)
          return Integer.parseInt(cost);
        return hourPrice+dayPrice;

    }
    private boolean hasPermissions(){
        int result = ContextCompat.checkSelfPermission(BikeListScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;

    }
    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(BikeListScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

        }
        else
            ActivityCompat.requestPermissions(BikeListScreen.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_CODE);

    }
    private final static int FILE_CODE =22;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case FILE_CODE:
                if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    shareThisBike(iv);
        }
    }
ImageView iv;
    private void showSearchItems(CharSequence text) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Vehicles")
                .orderByChild("name").equalTo(text.toString())
                .limitToLast(100);

        FirebaseRecyclerOptions<Vehicle> options =
                new FirebaseRecyclerOptions.Builder<Vehicle>()
                        .setQuery(query, Vehicle.class)
                        .build();

        searchAdapter = new FirebaseRecyclerAdapter<Vehicle, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Vehicle model) {
                holder.bikeName.setText(model.getName());
                Picasso.get().load(model.getImage1()).into(holder.bikeImage);
                BlurTransformation transformation1 = new BlurTransformation(getBaseContext(),25,1);
                Picasso.get().load(model.getImage1()).transform(transformation1).into(holder.bikeImageBlur);
                holder.ccValue.setText(model.getCc()+"");
                holder.mileageValue.setText(model.getMileage()+"");
                holder.colour.setText(model.getColour());
                holder.costValue.setText(model.getCost()+"");
                holder.weightValue.setText(model.getWeight()+"");
                holder.yearValue.setText(model.getYear()+"");

                //Click to share
                holder.shareIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(model.getImage1()).into(target);
                    }
                });
                final Vehicle clickItem = model;
                vehicleCLicked = clickItem;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(BikeListScreen.this,BikeDetailsActivity.class);
                        intent.putExtra("BikeId", searchAdapter.getRef(position).getKey() );
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bike_card_view, parent, false);
                return new ViewHolder(view);
            }
        };
        bikeListRecyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggestList() {
        vehicleList.orderByChild("parId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){

                Vehicle lastSearchVehicle = data.getValue(Vehicle.class);
                suggestedSearch.add(lastSearchVehicle.getName());

            }


                List<String> oneElementSearch = new ArrayList<>();
                if(suggestedSearch!=null && suggestedSearch.size()>0)
                  oneElementSearch.add(suggestedSearch.get(suggestedSearch.size()-1));
                materialSearchBar.setLastSuggestions(oneElementSearch);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    String curBikeCost , curBikeImage , curBikeName;
    int shimmerCounter;
    private void loadVehicles() {

        bikeListRecyclerView2.setVisibility(View.INVISIBLE);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Vehicles")
                .orderByChild("parId").equalTo(categoryId)
                .limitToLast(100);

        FirebaseRecyclerOptions<Vehicle> options =
                new FirebaseRecyclerOptions.Builder<Vehicle>()
                        .setQuery(query, Vehicle.class)
                        .build();

        shimmerCounter=0;

        adapter = new FirebaseRecyclerAdapter<Vehicle, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Vehicle model) {
                holder.bikeName.setText(model.getName());
                Picasso.get().load(model.getCompany()).into(holder.company);

//                Glide.with(getApplicationContext()).asGif()
//                        .load(model.getCompany())
//                        .into(holder.company);
                Picasso.get().load(model.getImage1()).into(holder.bikeImage);    //.transform(new RoundedCornersTransformation(50,0))
                holder.shareIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Build.VERSION.SDK_INT>=23){
                                if(hasPermissions())
                                    shareThisBike(holder.bikeImage);
                                else {
                                    requestPermission();
                                    iv = holder.bikeImage;
                                }
                        }
                        else
                         shareThisBike(holder.bikeImage);

                            //Picasso.get().load(model.getImage1()).into(target);       Uncomment to share via facebook and add Facebook API too



                        //Modify below code to share via Whatsapp
//                        try {
//                            String s = model.getName().toString() + "\n" + model.getDetails().toString();
//
//                            File file = new File(getExternalCacheDir(), "sample.jpg");
//                            FileOutputStream fOut = new FileOutputStream(file);
//                            BitmapDrawable drawable = (BitmapDrawable) holder.bikeImage.getDrawable();
//                            Bitmap bitmap = drawable.getBitmap();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//                            fOut.flush();
//                            fOut.close();
//                            file.setReadable(true, false);
//
//                            Intent intent = new Intent(Intent.ACTION_SEND);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra(Intent.EXTRA_TEXT, s);
//                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                            intent.setType("image/*");
//                            startActivity(Intent.createChooser(intent, "Share Via"));
//                        } catch (Exception e) {
//                            Toast.makeText(BikeListScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
                //Adding to favorite
                if(localdb.isFavorite(adapter.getRef(position).getKey()))
                    holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                //Toggling on click of favorite
                holder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localdb.isFavorite(adapter.getRef(position).getKey())) {
                            localdb.addToFavorites(adapter.getRef(position).getKey());
                            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(BikeListScreen.this, model.getName()+ " Added to Favorites!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            localdb.removeFromFavorites(adapter.getRef(position).getKey());
                            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(BikeListScreen.this, model.getName()+ " Removed from Favorites!", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                if(shimmerCounter==0) {
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.hideShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    shimmerCounter = 1;
                }

                BlurTransformation transformation1 = new BlurTransformation(getBaseContext(),30,3);
                Picasso.get().load(model.getImage1()).fit().transform(transformation1).into(holder.bikeImageBlur);
             //   int opacity = 153;
             //   holder.bikeImageBlur.setBackgroundColor(opacity * 0x1000000);
                holder.ccValue.setText(model.getCc());
                holder.mileageValue.setText(model.getMileage());
                holder.colour.setText(model.getColour());
                holder.costValue.setText(model.getCost());
                holder.weightValue.setText(model.getWeight());
                holder.yearValue.setText(model.getYear());
                final Vehicle clickItem = model;
                vehicleCLicked = clickItem;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View v, int position, boolean isLongClick) {
                        final BottomSheetDialog bt=new BottomSheetDialog(BikeListScreen.this,R.style.BottomSheetDialogTheme);
                        View view= LayoutInflater.from(BikeListScreen.this).inflate(R.layout.bottom_sheet_lay,null);
                        view.findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bikeId = adapter.getRef(position).getKey();
                                curBikeCost = model.getCost();
                                vehicleChosenName = model.getName();
                                vehicleChosenImage = model.getImage1();
                                getInputDate();
                                bt.dismiss();
//                                Intent i= new Intent(BikeListScreen.this,AccessoryListScreen.class);
//                               i.putExtra("bikeCost",model.getCost());
//                                startActivity(i);
//                                bt.dismiss();
                            }
                        });
                        ImageView imageView=view.findViewById(R.id.my_image);
                        curBikeImage = model.getImage1();
                        curBikeName = model.getName();
                        Picasso.get().load(model.getImage1()).into(imageView);
                        TextView name=view.findViewById(R.id.txt_prname);
                        name.setText(model.getName());

                        TextView price=view.findViewById(R.id.txt_prprice);
                        Locale locale=new Locale("en","in");
                        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
                        price.setText(format.format(Integer.parseInt(model.getCost())));

                        view.findViewById(R.id.viewDetails).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BikeListScreen.this,BikeDetailsActivity.class);
                                intent.putExtra("BikeId", adapter.getRef(position).getKey());
                                startActivity(intent);
                                bt.dismiss();
                            }
                        });
                        view.findViewById(R.id.cartLayout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Database(BikeListScreen.this).addToCart(new Order(
                                        adapter.getRef(position).getKey()       //vehicleId
                                        , model.getName() , 1,model.getCost()+"" , "0",model.getImage1())
                                );
                                Toast.makeText(BikeListScreen.this,model.getName()+" added to Package",Toast.LENGTH_SHORT).show();

                            }
                        });
                        bt.setContentView(view);
                        bt.show();

//                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP ) {       Uncomment to start transtions , make sure the transition names of the elements to be animated should be same
//                            Pair[] pair = new Pair[2];
//                            pair[0] = new Pair<>(holder.bikeImage, "bikeImage");
//                            pair[1] = new Pair<>(holder.bikeName, "bikeName");
//                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(BikeListScreen.this, pair);
//                            intent.putExtra("BikeId", adapter.getRef(position).getKey());
//                            startActivity(intent, options.toBundle());
//                        }
//                        else
//                        {

                    //    }
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bike_card_view, parent, false);
                return new ViewHolder(view);
            }
        };
      adapter.startListening();
      adapter.notifyDataSetChanged();
        bikeListRecyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
    int startHour,startMin;
    int isFromTime = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    String vehicleChosenImage,vehicleChosenName;
    private void getInputDate() {
        if(isFromTime!=1){
            //Start date - > start time - > display journey Time ( Proceed? can edit endDate and end time ) -> accessories .
            startDateChose();

        }else   //It is from Find Vehicles , hence it already contains the Start date and end date .
        {
            Intent i= new Intent(this,AccessoryListScreen.class);
            i.putExtra("bikeCost",curBikeCost);
            i.putExtra("bikeId",bikeId);
            i.putExtra("bikeImage", vehicleChosenImage);
            i.putExtra("bikeName",vehicleChosenName);
            i.putExtra("startEndDate", startDate);
            i.putExtra("startEndDate", startTime + " - " + endTime);
            startActivity(i);
        }

    }
    String startDateString , startTimeString,endDateString; //start date selected by user from
    int startYear , startMonth , startDay;

    List<Pair<Integer,Integer>> dates;
    private void startDateChose() {
        Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                BikeListScreen.this,
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
        vehicleList.child(bikeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Vehicle chosenVeh = dataSnapshot.getValue(Vehicle.class);
                    List<List<Integer>> bookingSlots = chosenVeh.getBookingSlot();
                    if(bookingSlots!=null) {
                        for (int i = 0; i < bookingSlots.size(); i++) {
                          if (bookingSlots.get(i) != null) {
                            int startDateTime = bookingSlots.get(i).get(0);
                            int endDateTime = bookingSlots.get(i).get(1);
                            startDateTime -= 10000000;
                            endDateTime -= 10000000;
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

    }public void displayTime(){
        // from selection of time via spinner , calculate endTime - > display in bottomOverlay ( with book btn ) - > take to accessory screen


        final BottomSheetDialog bt=new BottomSheetDialog(BikeListScreen.this,R.style.BottomSheetDialogTheme);
        View view= LayoutInflater.from(BikeListScreen.this).inflate(R.layout.time_confirm_lay,null);
        view.findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String[] sDatesArr = startDateString.split("/");
              String startSearch = (sDatesArr[1] + sDatesArr[0])
                                  + (startTimeString.substring(0, 2) + startTimeString.substring(3));
              String[] eDatesArr = endDateString.split("/");
              String endSearch = (eDatesArr[1] + eDatesArr[0])
                                   + (endTimeString.substring(0, 2) + endTimeString.substring(3));

              startSearchInt = 10000000 + Integer.parseInt(startSearch);      //For month in request and database , add 10 to month , in each slot , like for Nov , 21 (11+10) for April 14 (4+10)  //Added 10^7 for 8061930 [Mddhhmm] to make it 18061930 , eaiser for calculation [MMddhhmm]
              endSearchInt = 10000000 + Integer.parseInt(endSearch);

                Intent i= new Intent(BikeListScreen.this,AccessoryListScreen.class);
                i.putExtra("BikeId", bikeId);
                i.putExtra("bikeImage", vehicleChosenImage);
                i.putExtra("bikeName",vehicleChosenName);
                i.putExtra("endDate", endDateString+" , "+endTimeString);
                i.putExtra("startDate", startDateString+" , "+startTimeString);
                i.putExtra("bikeCost",String.valueOf(getAmountAsPerTime(curBikeCost)));
                i.putExtra("startDateString",String.valueOf(startSearchInt));
              i.putExtra("endDateString",String.valueOf(endSearchInt));

                startActivity(i);
                bt.dismiss();
            }
        });
        ImageView imageView=view.findViewById(R.id.my_image);
        Picasso.get().load(curBikeImage).into(imageView);
        TextView name=view.findViewById(R.id.txt_prname);
        name.setText(curBikeName);

        TextView price=view.findViewById(R.id.txt_prprice);
        Locale locale=new Locale("en","in");
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);

    Date date1 ;
    Date date2 ;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    try {
      date1 = sdf.parse(endDateString + " " + endTimeString);
      date2 = sdf.parse(startDateString + " " + startTimeString);
      DateTime dateTime1 = new DateTime(), dateTime2 = new DateTime();
      if (date1 != null && date2 != null) {
        dateTime1 = new DateTime(date1);
        dateTime2 = new DateTime(date2);
      }

      noOfDays = Days.daysBetween(dateTime2, dateTime1).getDays();
      int noOfHours = Hours.hoursBetween(dateTime2, dateTime1).getHours();
      hours = noOfHours % 24;
    }catch (ParseException e) {
      e.printStackTrace();
    }
        price.setText(format.format(getAmountAsPerTime(curBikeCost)));
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
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this,startHour,startMin,0,false);
        timePickerDialog.setAccentColor(getResources().getColor(R.color.btn_red_bg));

        String[] startDateNo =startDateString.split("/") ;
        String[] endDateNo = endDateString.split("/");
        timePickerDialog.setThemeDark(true);
        timePickerDialog.setTitle("Your Journey End Time");
        timePickerDialog.dismissOnPause(true);
        if (startDateNo[0].equals(endDateNo[0])  && startDateNo[1].equals(endDateNo[1])) {
            timePickerDialog.setMinTime(startHour, startMin, 0);
            timePickerDialog.setMaxTime(23, 30, 0);
        }
//        else
//            timePickerDialog.setMaxTime(startHour,startMin,0);
        timePickerDialog.show(getSupportFragmentManager(), "EndTimeickerdialog");

    }

    int monthCal,dayCal;
    private void endDateChose() {
      Calendar now = Calendar.getInstance();
      com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
        BikeListScreen.this,
        now.get(Calendar.YEAR), // Initial year selection
        now.get(Calendar.MONTH), // Initial month selection
        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
      );
      dpd.setAccentColor(getResources().getColor(R.color.btn_red_bg));
      dpd.setTitle("Your Journey End Date");
      //Only Max 1 day allowed
      Calendar minCal = Calendar.getInstance();
        minCal.set(startYear,monthCal,dayCal);
      dpd.setMinDate(minCal);
      Calendar maxCal = Calendar.getInstance();
      maxCal.set(startYear,monthCal,dayCal+1);
  //    dpd.setMaxDate(maxCal);


      dates = new ArrayList<>();  //for (mon,day) to add in disable dates for calender
      vehicleList.child(bikeId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if(dataSnapshot.exists()){
            Vehicle chosenVeh = dataSnapshot.getValue(Vehicle.class);
            List<List<Integer>> bookingSlots = chosenVeh.getBookingSlot();
            List<Pair<Integer,Integer>> datesGreaterThanSDate = new ArrayList<>();    // Pairs of Day , Month
            if(bookingSlots!=null) {
              for (int i = 0; i < bookingSlots.size(); i++) {
                if(bookingSlots.get(i)!=null){
                int startDateTime = bookingSlots.get(i).get(0);
                int endDateTime = bookingSlots.get(i).get(1);
                startDateTime -= 10000000;
                endDateTime -= 10000000;
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
                Date date1 = new GregorianCalendar(startYear, monthCal, dayCal).getTime();    // Chosen start date
                Date date2 = new GregorianCalendar(startYear, sMon - 1, sDay).getTime();
                long diff = date2.getTime() - date1.getTime();
                if (diff > 0)
                  datesGreaterThanSDate.add(new Pair<>(sDay, sMon - 1));
                if (endDate.length() == 8) {
                  eDay = Integer.parseInt(endDate.substring(2, 4));
                  eMon = Integer.parseInt(endDate.substring(0, 2));
                  dates.add(new Pair<>(eMon, eDay));
                } else if (endDate.length() == 7) {
                  eDay = Integer.parseInt(endDate.substring(1, 3));
                  eMon = Integer.parseInt("0" + endDate.substring(0, 1));
                  dates.add(new Pair<>(eMon, eDay));
                }


//                 date1 = new GregorianCalendar(startYear, monthCal, dayCal).getTime();    // Chosen start date
//                 date2 = new GregorianCalendar(startYear, eMon-1, eDay).getTime();
//                 diff = date2.getTime() - date1.getTime();
//                if(diff>0)// Date is greater , should be added
//                    datesGreaterThanSDate.add(new Pair<>(eDay,eMon-1));


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
            }
              if(datesGreaterThanSDate!=null && datesGreaterThanSDate.size()>0) {
                Pair<Integer,Integer> smallestPairDate = datesGreaterThanSDate.get(0);  //Initially taking it to be the first in list
                for (int i=1;i<datesGreaterThanSDate.size();i++) {
                  Pair<Integer,Integer> p = datesGreaterThanSDate.get(i);
                  Date date1 = new GregorianCalendar(startYear, smallestPairDate.second, smallestPairDate.first).getTime();    // Chosen start date
                  Date date2 = new GregorianCalendar(startYear,  p.second, p.first).getTime();    //Pair of DD , MM
                  long diff = date2.getTime() - date1.getTime();
                  if (diff < 0)    // We need the smallest out of all dates greater than Selected start date
                  {
                    //date2 is smaller
                    smallestPairDate = new Pair<>(p.first,p.second);
                  }
                }
                maxCal.set(startYear,smallestPairDate.second,smallestPairDate.first);
                dpd.setMaxDate(maxCal);
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
          Toast.makeText(BikeListScreen.this, "Unable to find pre-booking dates", Toast.LENGTH_SHORT).show();
        }
      });

      dpd.show(getSupportFragmentManager(), "EndDatepickerdialog");

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

    private void shareThisBike(ImageView bikeImageShare)  {
        bikeImageShare.setDrawingCacheEnabled(true);
        Bitmap shareBitmap = bikeImageShare.getDrawingCache();
        String timestamp = new SimpleDateFormat("yyyyMMdd_hhss").format(new Date());
        String imageName = "just_bike_"+timestamp+"_";
        File file = new File(Environment.getExternalStorageDirectory(),imageName);
        try {
            //Put image inside the file
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            shareBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);

            //Opening to share the file
            Intent intent = new Intent(Intent.ACTION_SEND);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(BikeListScreen.this,"com.just.justbikev2.fileprovider",file));
            }
            else
                intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
            intent.setType("image/*");
            startActivity(intent);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
  protected void onStart() {
      super.onStart();
     if(adapter!=null)
         adapter.startListening();
    }
    @Override
    public void onClick(View v) {

        Toast.makeText(this,"Clicked",Toast.LENGTH_LONG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.startListening();

    }

    @Override
    public void onSelect(int position) {
      Vehicle model = vehicleSet.get(position);

      final BottomSheetDialog bt = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
      View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_lay, null);
      view.findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) { Intent i = new Intent(getApplicationContext(), AccessoryListScreen.class);
      i.putExtra("bikeCost", String.valueOf(getAmountAsPerTime(model.getCost())));
      i.putExtra("bikeId",model.getParId());
      i.putExtra("bikeImage", model.getImage1());
      i.putExtra("bikeName", model.getName());
      i.putExtra("startDate", stringStartDate);
      i.putExtra("endDate", stringEndDate);
      i.putExtra("startDateString",String.valueOf(startSearchInt));
          i.putExtra("endDateString",String.valueOf(endSearchInt));
          i.putExtra("noOfDays",String.valueOf(noOfDays));

      startActivity(i);
      bt.dismiss();
    }
});
  ImageView imageView = view.findViewById(R.id.my_image);
  Picasso.get().load(model.getImage1()).into(imageView);
  TextView name = view.findViewById(R.id.txt_prname);
  name.setText(model.getName());

  TextView price = view.findViewById(R.id.txt_prprice);
  Locale locale = new Locale("en", "in");
  NumberFormat format = NumberFormat.getCurrencyInstance(locale);
  price.setText(format.format(getAmountAsPerTime(model.getCost())));
  bikeId = model.getKeyId();

  view.findViewById(R.id.viewDetails).setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
  Intent intent = new Intent(BikeListScreen.this, BikeDetailsActivity.class);
  intent.putExtra("BikeId", bikeId);
  intent.putExtra("startDate", stringStartDate);
  intent.putExtra("endDate", stringEndDate);
  intent.putExtra("bikeCostTime", String.valueOf(getAmountAsPerTime(model.getCost())));
  startActivity(intent);
  bt.dismiss();
  }
  });
  TextView cartLayout = view.findViewById(R.id.cartTV);
  cartLayout.setText(startDate + " , " + startTime + " - " + endTime);
  bt.setContentView(view);
  bt.show();
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
     //   String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
      if(view.getTag().equals("Datepickerdialog")) {
        monthCal = monthOfYear;
        dayCal = dayOfMonth;
        startYear = year;
        startMonth = monthOfYear;
        startDay = dayOfMonth;
        startDateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        startTimeChose();
      }
      else if(view.getTag().equals("EndDatepickerdialog")){
        endDateString =dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        endTimeChose();
      }
    }
}
