package com.just.justbikev2.ViewHolder;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.just.justbikev2.R;

public class AccessoryListHolder extends RecyclerView.ViewHolder{
    public ImageView accessoryImage;
    public TextView cost,name;
    public CheckBox checkBoxAccessory;
    public CardView accessoryCard;

    public AccessoryListHolder(@NonNull View itemView) {
        super(itemView);
        this.accessoryImage = itemView.findViewById(R.id.bikeImageSmall);
        this.name = itemView.findViewById(R.id.name);
        this.cost = itemView.findViewById(R.id.costValueAccessory);
        this.checkBoxAccessory = itemView.findViewById(R.id.checkBoxAccessory);
        this.accessoryCard = itemView.findViewById(R.id.accessoryCard);
    }
}
