package com.just.justbikev2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.just.justbikev2.FullScreenImageActivity;
import com.just.justbikev2.Model.Article;
import com.just.justbikev2.R;
import com.just.justbikev2.ViewHolder.FavoriteViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoriteArticlesAdapater extends RecyclerView.Adapter<FavoriteViewHolder> {

        List<Article> orderList=new ArrayList<>();
        Context context;

public FavoriteArticlesAdapater(List<Article> orderList, Context context){
        this.orderList=orderList;
        this.context=context;
        }

@NonNull
@Override
public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.view_fav_article,parent,false);
        return new FavoriteViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull FavoriteViewHolder holder,int position){
      //  TextDrawable textDrawable=TextDrawable.builder().buildRound(""+orderList.get(position).getPrice(), Color.RED);
     //   holder.imgArticle.setImageDrawable(textDrawable);

        Picasso.get().load(orderList.get(position).getImage()).fit().into(holder.imgArticle);
        holder.imgArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent i = new Intent(context, FullScreenImageActivity.class);
                        i.putExtra("imageUrl",orderList.get(position).getImage());
                        context.startActivity(i);
                }
        });
        holder.tvTitle.setText(orderList.get(position).getTitle());
        holder.articleTVCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        final BottomSheetDialog bt = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                        View view = LayoutInflater.from(context).inflate(R.layout.new_bike_details_bottom, null);
                        view.findViewById(R.id.btn_book).setVisibility(View.GONE);
                        TextView detailsTV = view.findViewById(R.id.detailsTV);
                        detailsTV.setText(orderList.get(position).getDescription());
                        TextView bikeName = view.findViewById(R.id.new_bike_name);
                        bikeName.setText(orderList.get(position).getTitle());

                        bt.setContentView(view);
                        bt.show();
                }
        });
        holder.tvDesc.setText(orderList.get(position).getDescription());

        }

@Override
public int getItemCount(){
        return orderList.size();
        }


}
