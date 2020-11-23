package com.just.justbikev2.Admin;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.R;

public class OrderVHAdmin extends RecyclerView.ViewHolder //implements View.OnClickListener ,View.OnCreateContextMenuListener //,View.OnLongClickListener
 {


    public TextView orderId , price , address , status,clientNo,clientName,endDate,startDate;
    public ImageView imageView;
    public ImageButton editBtn ,removeBtn , detailsBtn , directionBtn;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderVHAdmin(@NonNull View itemView) {
        super(itemView);
        orderId = itemView.findViewById(R.id.orderId);
        price = itemView.findViewById(R.id.orderPrice);
        address = itemView.findViewById(R.id.orderAddress);
        status = itemView.findViewById(R.id.orderStatus);
        editBtn = itemView.findViewById(R.id.couponBtn);
        removeBtn = itemView.findViewById(R.id.removeBtn);
        detailsBtn = itemView.findViewById(R.id.detailsBtn);
        directionBtn = itemView.findViewById(R.id.directionBtn);
      clientNo= itemView.findViewById(R.id.clientNo);
      clientName= itemView.findViewById(R.id.clientName);
      startDate =itemView.findViewById(R.id.startDate);
        endDate = itemView.findViewById(R.id.endDate);
//        itemView.setOnCreateContextMenuListener(this);
//        itemView.setOnClickListener(this);
     //   itemView.setOnLongClickListener(this);

    }

//    @Override
//    public void onClick(View v) {
//                itemClickListener.onClick(v,getAdapterPosition(),false);
//    }


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select the Action!");
//            menu.add(0,0,getAdapterPosition(),"Update");
//              menu.add(0,1,getAdapterPosition(),"Delete");
//    }
//    @Override
//    public boolean onLongClick(View v) {
//        itemClickListener.onClick(v,getAdapterPosition(),true);
//        return true;
//    }
}
