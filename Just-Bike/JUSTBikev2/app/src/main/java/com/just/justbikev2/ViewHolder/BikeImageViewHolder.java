package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;

public class BikeImageViewHolder extends RecyclerView.ViewHolder{
    public ImageView imgBike;

    public BikeImageViewHolder(@NonNull View itemView) {
        super(itemView);

        imgBike = itemView.findViewById(R.id.imgBike);
    }
}
