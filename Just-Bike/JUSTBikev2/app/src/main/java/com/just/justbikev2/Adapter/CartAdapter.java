package com.just.justbikev2.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.R;
import com.just.justbikev2.ViewHolder.CartVH;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartVH> {

        List<Order> orderList=new ArrayList<>();
        Context context;

public CartAdapter(List<Order> orderList,Context context){
        this.orderList=orderList;
        this.context=context;
        }

@NonNull
@Override
public CartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cart_card_view,parent,false);
        return new CartVH(view);
        }

@Override
public void onBindViewHolder(@NonNull CartVH holder,int position){
//        TextDrawable textDrawable=TextDrawable.builder().buildRound(""+orderList.get(position).getPrice(), Color.RED);
//        holder.image.setImageDrawable(textDrawable);

        Picasso.get().load(orderList.get(position).getImage()).into(holder.cart_item_image);

        Locale locale=new Locale("en","in");
        int price=Integer.parseInt(orderList.get(position).getPrice());
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        holder.price.setText(format.format(price));

        holder.name.setText(orderList.get(position).getProductName());



        }

@Override
public int getItemCount(){
        return orderList.size();
        }


}