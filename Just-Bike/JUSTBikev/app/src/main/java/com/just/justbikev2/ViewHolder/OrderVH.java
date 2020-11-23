package com.just.justbikev2.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.R;

public class OrderVH extends RecyclerView.ViewHolder {

    public TextView orderId , price , address , status;
    public ImageView imageView;
    public ImageButton editBtn ,removeBtn , detailsBtn , directionBtn;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderVH(@NonNull View itemView) {
        super(itemView);
        orderId = itemView.findViewById(R.id.orderId);
        price = itemView.findViewById(R.id.orderPrice);
        address = itemView.findViewById(R.id.orderAddress);
        status = itemView.findViewById(R.id.orderStatus);
        editBtn = itemView.findViewById(R.id.couponBtn);
        removeBtn = itemView.findViewById(R.id.removeBtn);
        detailsBtn = itemView.findViewById(R.id.detailsBtn);
        directionBtn = itemView.findViewById(R.id.directionBtn);
        imageView = itemView.findViewById(R.id.statusIV);

    }
}
