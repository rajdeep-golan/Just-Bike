package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;

public class UserVH  extends RecyclerView.ViewHolder {
  public TextView phoneNo , verified,wallet;
  public ImageView profilePic,contactsIcon,locationIcon;
  public TextView viewDocs,name;

  public UserVH(@NonNull View itemView) {
    super(itemView);
    phoneNo = itemView.findViewById(R.id.phoneNo);
    verified = itemView.findViewById(R.id.verified);
    wallet = itemView.findViewById(R.id.wallet);
    profilePic = itemView.findViewById(R.id.bikeImage);
    contactsIcon = itemView.findViewById(R.id.contactsIcon);
    locationIcon = itemView.findViewById(R.id.locationIcon);
    viewDocs = itemView.findViewById(R.id.viewDocs);
    name = itemView.findViewById(R.id.name);
  }
}
