package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.BikeImagesAdapter;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Model.Banner;
import com.just.justbikev2.Model.BikationModel;
import com.just.justbikev2.ViewHolder.BikationVH;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class Bikation extends AppCompatActivity {
  RecyclerView bikationRV;
  FirebaseRecyclerAdapter<BikationModel, BikationVH> adapter;
  RadioButton inCity , outstation;
  String cityId="0";
  String cityOrOut;   //0 : in city , 1 : outstation ; this is appended to cityId with '_'. So it is cityId_cityOrOut Eg: 0_1 => city = 0 ,i.e, Kolkata , cityOrOut = 1 , i.e. Outstation, so it is Outstation in Kolkata
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
         setContentView(R.layout.activity_bikation);
        if(getIntent()!=null)
          cityId = getIntent().getStringExtra("cityId");
    cityOrOut = cityId + "_1";
    inCity = findViewById(R.id.inCity);
    outstation = findViewById(R.id.outStation);
    outstation.setChecked(true);
    outstation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
          cityOrOut = cityId + "_1";
        loadPlaces();
      }
    });
    inCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
          cityOrOut = cityId + "_0";
        loadPlaces();
      }
    });
    bikationRV = findViewById(R.id.bikationRV);
    bikationRV.setHasFixedSize(true);
    bikationRV.setLayoutManager(new LinearLayoutManager(this));

    loadPlaces();

    }

  private String[] imageUrls=new String[4];

  private void loadPlaces() {
    Query query = FirebaseDatabase.getInstance().getReference()
                  .child("Bikation")
                  .orderByChild("cityId").equalTo(cityOrOut)
                  .limitToLast(100);
    FirebaseRecyclerOptions<BikationModel> options = new FirebaseRecyclerOptions.Builder<BikationModel>()
                                                .setQuery(query,BikationModel.class).build();
    adapter = new FirebaseRecyclerAdapter<BikationModel, BikationVH>(options) {
      @Override
      protected void onBindViewHolder(@NonNull BikationVH holder, int position, @NonNull BikationModel model) {
        BikeImagesAdapter imageAdapter;
        holder.bikationLoc.setText(model.getLocation());

        Locale locale=new Locale("en","in");
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        String cost = format.format(Integer.parseInt(model.getBudget()));
        holder.amount.setText(cost.substring(0,cost.length()-3));

        holder.placeDetailsBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent i  = new Intent(Bikation.this,BikationDetails.class);
            i.putExtra("Place",model);
            startActivity(i);
          }
        });
        holder.days.setText(model.getTimeToSpend());
        holder.bikationName.setText(model.getName());

        imageUrls = new String[]{
                 model.getImage1(), model.getImage2(), model.getImage3(),model.getImage4()
        };
        holder.mSlider.removeAllSliders();
        for (int i=0;i<4;i++) {
          TextSliderView textSliderView = new TextSliderView(getApplicationContext());
          textSliderView.image(imageUrls[i])
            .setProgressBarVisible(true);
          textSliderView.bundle(new Bundle());
          holder.mSlider.addSlider(textSliderView);
        }
        holder.mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        holder.mSlider.startAutoCycle();
        holder.mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        holder.mSlider.setDuration(3000);

      }

      @NonNull
      @Override
      public BikationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.bikation_card,parent,false);
        return new BikationVH(view);
      }
    };
    adapter.startListening();
    adapter.notifyDataSetChanged();
    bikationRV.setAdapter(adapter);
  }
  @Override
  protected void onStart() {
    super.onStart();
    if(adapter!=null)
      adapter.startListening();
  }
}
