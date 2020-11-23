package com.just.justbikev2.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Adapter.CartAdapter;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.R;



public class CartVH extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener {

    public TextView name, price,image;
    public ImageView cart_item_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CartVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.ItemName);
        price = itemView.findViewById(R.id.price);
        image = itemView.findViewById(R.id.item_image);
        cart_item_image = itemView.findViewById(R.id.cart_item_image);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAdapterPosition(),"Delete");
    }
}
