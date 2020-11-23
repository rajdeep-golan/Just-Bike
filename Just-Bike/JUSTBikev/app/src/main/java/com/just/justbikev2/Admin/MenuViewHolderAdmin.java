package com.just.justbikev2.Admin;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.R;


public class MenuViewHolderAdmin extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener {

    public TextView name;
    public ImageView image;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuViewHolderAdmin(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.category_name);
        image = itemView.findViewById(R.id.category_image);

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
    }
}
