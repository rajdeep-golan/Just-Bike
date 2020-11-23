package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamViewHolder extends RecyclerView.ViewHolder {
  public CircleImageView team_image;
    public ImageView linkedin , gmail , facebook;
    public TextView team_name ,team_post;
    public TeamViewHolder(@NonNull View itemView) {
        super(itemView);
        team_image = itemView.findViewById(R.id.team_image);
        linkedin = itemView.findViewById(R.id.linkedin);
    gmail = itemView.findViewById(R.id.gmail);
        facebook= itemView.findViewById(R.id.facebook);
         team_name = itemView.findViewById(R.id.team_name);
        team_post= itemView.findViewById(R.id.team_post);
    }
}
