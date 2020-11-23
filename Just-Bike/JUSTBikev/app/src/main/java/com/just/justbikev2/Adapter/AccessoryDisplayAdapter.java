package com.just.justbikev2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;

import java.util.ArrayList;

public class AccessoryDisplayAdapter extends RecyclerView.Adapter<AccessoryDisplayAdapter.AccessoryViewHolder> {
  public ArrayList<String> accessoryName;
  public Context context;

  public AccessoryDisplayAdapter(ArrayList<String> accessoryName, Context context) {
    this.accessoryName = accessoryName;
    this.context = context;
  }

  @NonNull
  @Override
  public AccessoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v= LayoutInflater.from(context).inflate(R.layout.accessory_list_delivery_card_name,null);
    return new AccessoryViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull AccessoryViewHolder holder, int position) {
    holder.accessoryName.setText(accessoryName.get(position));
  }

  @Override
  public int getItemCount() {
    if(accessoryName!=null)
      return accessoryName.size();
    return 0;
  }

  public class AccessoryViewHolder extends RecyclerView.ViewHolder{

    public TextView accessoryName;
    public AccessoryViewHolder(@NonNull View itemView) {
      super(itemView);

      accessoryName = itemView.findViewById(R.id.accessoryName);

    }
  }
}
