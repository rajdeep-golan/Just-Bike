package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;

import org.w3c.dom.Text;

public class ShowCommentsVH extends RecyclerView.ViewHolder {
    public TextView comment , name;
    public RatingBar ratingBar;

    public ShowCommentsVH(@NonNull View itemView) {
        super(itemView);
        comment = itemView.findViewById(R.id.cmntCommentTV);
        name = itemView.findViewById(R.id.cmntName);
        ratingBar = itemView.findViewById(R.id.ratingBarCmnt);
    }
}
