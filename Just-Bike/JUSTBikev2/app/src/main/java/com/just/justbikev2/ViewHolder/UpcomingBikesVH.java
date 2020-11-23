package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;

public class UpcomingBikesVH extends RecyclerView.ViewHolder {
    public ImageView newBikeImageBig,bikeImageSmall;
    public TextView newBikeCompanyName, newBikeName,expectedCost,expectedDate;
    public ImageButton newBikedetailsBtn;


    public UpcomingBikesVH(@NonNull View itemView) {
        super(itemView);
        newBikeImageBig = itemView.findViewById(R.id.newBikeImageBig);
        bikeImageSmall= itemView.findViewById(R.id.bikeImageSmall);
        newBikeCompanyName= itemView.findViewById(R.id.newBikeCompanyName);
        newBikeName= itemView.findViewById(R.id.newBikeName);
        expectedCost= itemView.findViewById(R.id.expectedCost);
        expectedDate = itemView.findViewById(R.id.expectedDate);
        newBikedetailsBtn = itemView.findViewById(R.id.newBikedetailsBtn);

    }
}