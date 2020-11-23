package com.just.justbikev2.Admin;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.R;

public class BannerAdminVH extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

public TextView name;
public ImageView image;
private ItemClickListener itemClickListener;

public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        }

public BannerAdminVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.banner_name);
        image = itemView.findViewById(R.id.banner_image);


    itemView.setOnCreateContextMenuListener(this);
        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action!");
        menu.add(0,0,getAdapterPosition(),"Add");
        menu.add(0,1,getAdapterPosition(),"Update");
        menu.add(0,2,getAdapterPosition(),"Delete");
    }
}
