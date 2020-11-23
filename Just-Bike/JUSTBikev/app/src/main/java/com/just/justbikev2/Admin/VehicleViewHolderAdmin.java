package com.just.justbikev2.Admin;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.R;

public class VehicleViewHolderAdmin extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public ImageView bikeImage,bikeImageBlur;
    public TextView mileageValue , costValue , bikeName , colour , ccValue , yearValue , weightValue ;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public VehicleViewHolderAdmin(@NonNull View itemView) {
        super(itemView);
        bikeName = itemView.findViewById(R.id.phoneNo);
        bikeImage = itemView.findViewById(R.id.bikeImage);
        bikeImageBlur = itemView.findViewById(R.id.bikeImageBlur);
        mileageValue = itemView.findViewById(R.id.mileageValue);
        costValue = itemView.findViewById(R.id.viewDocs);
        colour = itemView.findViewById(R.id.colourValue);
        ccValue = itemView.findViewById(R.id.ccValue);
        yearValue = itemView.findViewById(R.id.yearValue);
        weightValue = itemView.findViewById(R.id.weightValue);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action!");
        menu.add(0,0,getAdapterPosition(),"Update");
        menu.add(0,1,getAdapterPosition(),"Delete");
        menu.add(0,2,getAdapterPosition(),"Add");

    }
}
