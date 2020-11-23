package com.just.justbikev2.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.just.justbikev2.Adapter.ArticlesViewHolder;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.FullScreenImageActivity;
import com.just.justbikev2.Model.Article;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView rvArticles;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference vehicleList;
    FirebaseRecyclerAdapter<Article, ArticlesViewHolder> adapter;
    Database localdb;
    private boolean isViewShown = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            // call your function
        } else {
            isViewShown = false;
        }
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        vehicleList = firebaseDatabase.getReference("Article");

        rvArticles = v.findViewById(R.id.rvArticles);
        rvArticles.setHasFixedSize(true);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvArticles.setLayoutManager(layoutManager);


//        rvArticles.setPadding(130,100,130,100);

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvArticles);
        localdb = new Database(getContext());
        loadArticles();
        if(isViewShown){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.ViewHolder viewHolder = rvArticles.findViewHolderForAdapterPosition(0);
                    RelativeLayout rl1 = viewHolder.itemView.findViewById(R.id.rl1);
                }
            },100);
        }

        return v;
    }

    private void loadArticles() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Article")
        //        .orderByChild("userId").equalTo(Common.currentUser.getPhone())        For myList
                .limitToLast(50);

        FirebaseRecyclerOptions<Article> options =
                new FirebaseRecyclerOptions.Builder<Article>()
                        .setQuery(query, Article.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Article, ArticlesViewHolder>(options) {

            @NonNull
            @Override
            public ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_articles, parent, false);
                return new ArticlesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ArticlesViewHolder holder, int position, @NonNull Article model) {
                holder.tvTitle.setText(model.getTitle());
                Picasso.get().load(model.getImage()).fit().into(holder.imgArticle);
                holder.imgArticle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), FullScreenImageActivity.class);
                        i.putExtra("imageUrl",model.getImage());
                        startActivity(i);
                    }
                });
                holder.tvDesc.setText(model.getDescription());
                if(localdb.isArticleFavorite(adapter.getRef(position).getKey()))
                    holder.favoriteArticleIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                //Toggling on click of favorite
                holder.btReadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final BottomSheetDialog bt = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.new_bike_details_bottom, null);
                        TextView btn_TV = view.findViewById(R.id.btn_TV);
                        btn_TV.setText("Toggle Favorite");
                        view.findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!localdb.isArticleFavorite(adapter.getRef(position).getKey())) {
                                    localdb.addToArticleFavorites(adapter.getRef(position).getKey() , model.getTitle() , model.getDescription() , model.getImage());
                                    holder.favoriteArticleIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                                    Toast.makeText(getContext(), model.getTitle()+ " Added to Favorites!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    localdb.removeFromoArticleFavorites(adapter.getRef(position).getKey());
                                    holder.favoriteArticleIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                    Toast.makeText(getContext(), model.getTitle()+ " Removed from Favorites!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        TextView detailsTV = view.findViewById(R.id.detailsTV);
                        detailsTV.setText(model.getDescription());
                        TextView bikeName = view.findViewById(R.id.new_bike_name);
                        bikeName.setText(model.getTitle());

                        bt.setContentView(view);
                        bt.show();
                    }
                });
                holder.favoriteArticleIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localdb.isArticleFavorite(adapter.getRef(position).getKey())) {
                            localdb.addToArticleFavorites(adapter.getRef(position).getKey() , model.getTitle() , model.getDescription() , model.getImage());
                            holder.favoriteArticleIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(getContext(), model.getTitle()+ " Added to Favorites!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            localdb.removeFromoArticleFavorites(adapter.getRef(position).getKey());
                            holder.favoriteArticleIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(getContext(), model.getTitle()+ " Removed from Favorites!", Toast.LENGTH_SHORT).show();

                        }
                    }
                  });
            }

        };
        adapter.notifyDataSetChanged();
        rvArticles.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
}
