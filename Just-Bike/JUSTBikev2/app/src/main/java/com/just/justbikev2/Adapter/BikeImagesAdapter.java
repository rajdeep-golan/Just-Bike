package com.just.justbikev2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.BikeDetailsActivity;
import com.just.justbikev2.FullScreenImageActivity;
import com.just.justbikev2.Model.DocumentsModel;
import com.just.justbikev2.R;
import com.just.justbikev2.ViewHolder.BikeImageViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BikeImagesAdapter extends RecyclerView.Adapter<BikeImageViewHolder> {
    String[] bikeImages;
    Context context;

    public BikeImagesAdapter( String[] onBordingLists, Context context) {
        this.bikeImages = onBordingLists;
        this.context = context;
    }

    @NonNull
    @Override
    public BikeImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_images_card,parent,false);
        return new BikeImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeImageViewHolder holder, int position) {
        String ob= bikeImages[position];
        Picasso.get()
                .load(ob)
                .into(holder.imgBike);
        holder.imgBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FullScreenImageActivity.class);
                i.putExtra("imageUrl",bikeImages[position]);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeImages.length;
    }




}

