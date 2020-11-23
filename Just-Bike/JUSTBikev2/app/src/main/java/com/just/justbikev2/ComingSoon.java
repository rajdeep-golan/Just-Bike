package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Model.UpcomingBikes;
import com.just.justbikev2.Model.UpcomingBooking;
import com.just.justbikev2.Service.MyLocation;
import com.just.justbikev2.ViewHolder.UpcomingBikesVH;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComingSoon extends AppCompatActivity {

  @BindView(R.id.comingSoonBikes)
  RecyclerView comingSoonBikes;

  DatabaseReference upComingBikes;
  DatabaseReference upcomingBookings;

  FirebaseRecyclerAdapter<UpcomingBikes, UpcomingBikesVH> adapter;

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
    setContentView(R.layout.activity_coming_soon);



    ButterKnife.bind(this);
    upComingBikes = FirebaseDatabase.getInstance().getReference("ComingSoon");
    upcomingBookings = FirebaseDatabase.getInstance().getReference("UpcomingBooking");


    comingSoonBikes.setLayoutManager(new LinearLayoutManager(this));
    comingSoonBikes.setHasFixedSize(true);
    Query query = FirebaseDatabase.getInstance().getReference().child("ComingSoon")
      .limitToLast(50);
    FirebaseRecyclerOptions<UpcomingBikes> options = new FirebaseRecyclerOptions.Builder<UpcomingBikes>()
      .setQuery(query, UpcomingBikes.class).build();
    adapter = new FirebaseRecyclerAdapter<UpcomingBikes, UpcomingBikesVH>(options) {
      @Override
      protected void onBindViewHolder(@NonNull UpcomingBikesVH holder, int position, @NonNull UpcomingBikes model) {
        Locale locale = new Locale("en", "in");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        String cost = format.format(Integer.parseInt(model.getExpectedCost()) / 24);
        holder.expectedCost.setText(cost.substring(0, cost.length() - 3));
        Picasso.get().load(model.getBikeImageSmall()).placeholder(R.drawable.biker).into(holder.bikeImageSmall);
        Picasso.get().load(model.getNewBikeImageBig()).placeholder(R.drawable.biker).into(holder.newBikeImageBig);
        holder.expectedDate.setText(model.getExpectedDate());
        holder.newBikeName.setText(model.getNewBikeName());
        holder.newBikeCompanyName.setText(model.getNewBikeCompanyName());
        holder.newBikedetailsBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            final BottomSheetDialog bt = new BottomSheetDialog(ComingSoon.this, R.style.BottomSheetDialogTheme);
            View view = LayoutInflater.from(ComingSoon.this).inflate(R.layout.new_bike_details_bottom, null);
            view.findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                UpcomingBooking upcomingBooking = new UpcomingBooking(Common.currentUser.getPhone(), model.getNewBikeCompanyName() + " " + model.getNewBikeName());
                upcomingBookings.child(Common.currentUser.getPhone() + "+" + System.currentTimeMillis()).setValue(upcomingBooking);
                Toast.makeText(ComingSoon.this, "Thank you. You would be notified once it's ready!", Toast.LENGTH_SHORT).show();
              }
            });
            TextView detailsTV = view.findViewById(R.id.detailsTV);
            detailsTV.setText(model.getDetails());
            TextView bikeName = view.findViewById(R.id.new_bike_name);
            bikeName.setText(model.getNewBikeCompanyName() + " " + model.getNewBikeName());

            bt.setContentView(view);
            bt.show();
          }
        });
      }

      @NonNull
      @Override
      public UpcomingBikesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_bikes_card, null);
        return new UpcomingBikesVH(v);
      }
    };

    adapter.notifyDataSetChanged();
    comingSoonBikes.setAdapter(adapter);
    adapter.startListening();

  }


  @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
}
