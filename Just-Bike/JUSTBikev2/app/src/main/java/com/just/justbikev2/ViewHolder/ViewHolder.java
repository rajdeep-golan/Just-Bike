package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  public ImageView bikeImage,bikeImageBlur,favoriteIcon,shareIcon , company;
  public TextView mileageValue , costValue , bikeName , colour , ccValue , yearValue , weightValue ;
  public TextView costTV;

  public TextView availableFrom;
  private ItemClickListener itemClickListener;

  public void setItemClickListener(ItemClickListener itemClickListener) {
    this.itemClickListener = itemClickListener;
  }

  public ViewHolder(@NonNull View itemView ) {
    super(itemView);
    bikeName = itemView.findViewById(R.id.phoneNo);
    company = itemView.findViewById(R.id.company);
    bikeImage = itemView.findViewById(R.id.bikeImage);
    bikeImageBlur = itemView.findViewById(R.id.bikeImageBlur);
    mileageValue = itemView.findViewById(R.id.mileageValue);
    costValue = itemView.findViewById(R.id.viewDocs);
    colour = itemView.findViewById(R.id.colourValue);
    ccValue = itemView.findViewById(R.id.ccValue);
    yearValue = itemView.findViewById(R.id.yearValue);
    weightValue = itemView.findViewById(R.id.weightValue);
    favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
    shareIcon = itemView.findViewById(R.id.shareIcon);
    costTV =  itemView.findViewById(R.id.name);
    availableFrom = itemView.findViewById(R.id.availableFrom);

    itemView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    itemClickListener.onClick(v,getAdapterPosition(),false);
  }
}
