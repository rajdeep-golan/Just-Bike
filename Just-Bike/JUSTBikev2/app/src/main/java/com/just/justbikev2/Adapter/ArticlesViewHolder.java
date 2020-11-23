package com.just.justbikev2.Adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.R;



public class ArticlesViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgArticle;
        public ImageView favoriteArticleIcon;
        public TextView tvTitle,tvDesc;
        public Button btReadMore;

        public ArticlesViewHolder(@NonNull View itemView) {
            super(itemView);

            imgArticle  = itemView.findViewById(R.id.imgArticle);
            tvTitle     = itemView.findViewById(R.id.tvTitle);
            tvDesc      = itemView.findViewById(R.id.tvDesc);
            btReadMore  = itemView.findViewById(R.id.btReadMore);
            favoriteArticleIcon = itemView.findViewById(R.id.favoriteArticleIcon);
        }

}

