package com.just.justbikev2.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Model.Order;
import com.just.justbikev2.R;

import java.util.List;

class DetailsVH extends RecyclerView.ViewHolder{
    TextView name , price , discount , quantity;
    public DetailsVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.productName);
        price = itemView.findViewById(R.id.ProductPrice);
        discount = itemView.findViewById(R.id.productDiscount);
        quantity = itemView.findViewById(R.id.productQuant);

    }
}

public class OrderDetailAdpaterAdmin extends RecyclerView.Adapter<DetailsVH> {

    List<Order> orderList;

    public OrderDetailAdpaterAdmin(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public DetailsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_info_card_admin,parent,false);
        return new DetailsVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsVH holder, int position) {

        Order order = orderList.get(position);
        holder.name.setText(order.getProductName());
        holder.quantity.setText(order.getQuantity()+"");
        holder.discount.setText(order.getDiscount());
        holder.price.setText(order.getPrice());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
