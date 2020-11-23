package com.just.justbikev2.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;

public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgArticle;
        public TextView tvTitle,tvDesc;
        public RelativeLayout rl1;
        public CardView articleTVCardView;

        public FavoriteViewHolder( View itemView) {

               super(itemView);
               imgArticle = itemView.findViewById(R.id.imgArticle);
               tvTitle = itemView.findViewById(R.id.tvTitle);
               tvDesc = itemView.findViewById(R.id.tvDesc);
               rl1 = itemView.findViewById(R.id.rl1);
            articleTVCardView = itemView.findViewById(R.id.articleTVCardView);
           }

    }

