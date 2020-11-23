package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.glide.slider.library.SliderLayout;
import com.just.justbikev2.R;

public class BikationVH extends RecyclerView.ViewHolder {

 public TextView bikationName , amount , days;
 public ImageButton placeDetailsBtn;
 public   SliderLayout mSlider;
 public TextView bikationLoc;
  public BikationVH(@NonNull View itemView) {
    super(itemView);
    bikationName = itemView.findViewById(R.id.bikationName);
    mSlider = itemView.findViewById(R.id.slider);
  amount = itemView.findViewById(R.id.amount);
    days = itemView.findViewById(R.id.days);
    placeDetailsBtn = itemView.findViewById(R.id.placeDetailsBtn);
    bikationLoc= itemView.findViewById(R.id.bikationLoc);


  }
}
