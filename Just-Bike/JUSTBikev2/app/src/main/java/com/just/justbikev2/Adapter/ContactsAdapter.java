package com.just.justbikev2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.FullScreenImageActivity;
import com.just.justbikev2.R;
import com.just.justbikev2.ViewHolder.BikeImageViewHolder;
import com.just.justbikev2.ViewHolder.ShowCommentsVH;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ContactsAdapter extends RecyclerView.Adapter<ShowCommentsVH> {
  ArrayList<String> contactName;
  ArrayList<String> contactNo;
  Context context;

  public ContactsAdapter( ArrayList<String> contactName,ArrayList<String> contactNo, Context context) {
    this.contactName = contactName;
    this.contactNo = contactNo;
    this.context = context;
  }

  @NonNull
  @Override
  public ShowCommentsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_card,parent,false);
    return new ShowCommentsVH(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ShowCommentsVH holder, int position) {
   holder.name.setText(contactName.get(position));
    holder.comment.setText(contactNo.get(position));
    if(contactNo.get(position).length()<9)
      holder.ratingBar.setVisibility(View.GONE);
  }

  @Override
  public int getItemCount() {
    return contactName.size();
  }



}
