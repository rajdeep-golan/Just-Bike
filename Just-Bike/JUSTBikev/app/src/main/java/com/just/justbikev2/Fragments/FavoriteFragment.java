package com.just.justbikev2.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.just.justbikev2.Adapter.FavoriteArticlesAdapater;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Model.Article;
import com.just.justbikev2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    RecyclerView rvFavorite;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference vehicleList;
    FavoriteArticlesAdapater  adapter;

    Database localdb;
    List<Article> orderList = new ArrayList<>();

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

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite, container, false);
        rvFavorite = v.findViewById(R.id.rvFavorite);
        rvFavorite.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvFavorite.setLayoutManager(layoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        vehicleList = firebaseDatabase.getReference("Article");

        localdb = new Database(getContext());

        rvFavorite.scheduleLayoutAnimation();
        loadArticles();
        return v;
    }
    private void loadArticles() {

        orderList = new Database(getContext()).getFavoriteArticles();
        if(orderList.size() > 0)
        {
            adapter = new FavoriteArticlesAdapater(orderList,getContext());
            adapter.notifyDataSetChanged();
            rvFavorite.setAdapter(adapter);
        }
        else
            Toast.makeText(getContext(), "Favorite List is empty!", Toast.LENGTH_SHORT).show();

//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Article")
//                //        .orderByChild("userId").equalTo(Common.currentUser.getPhone())        For myList
//                .limitToLast(50);
//
//        FirebaseRecyclerOptions<Article> options =
//                new FirebaseRecyclerOptions.Builder<Article>()
//                        .setQuery(query, Article.class)
//                        .build();
//
//        adapter = new FirebaseRecyclerAdapter<Article, FavoriteViewHolder>(options) {
//
//            int positionTemp=0;
//            @NonNull
//            @Override
//            public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//                    View view = LayoutInflater.from(parent.getContext())
//                            .inflate(R.layout.view_fav_article, parent, false);
////                if(!localdb.isArticleFavorite(adapter.getRef(positionTemp).getKey()))
////                    view=null;
//                return new FavoriteViewHolder(view);
//
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position, @NonNull Article model) {
//                positionTemp=position+1;
//                if(localdb.isArticleFavorite(adapter.getRef(position).getKey())) {
//                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layoutanim);
//                    holder.tvTitle.setText(model.getTitle());
//                    holder.rl1.setLayoutAnimation(animation);
//                    holder.tvDesc.setText(model.getDescription());
//                    Picasso.get().load(model.getBikeImageSmall()).fit().into(holder.imgArticle);
//                }
//                else{
//                }
//            }
//        };
//
//        madapter = adapter;
//        adapter.notifyDataSetChanged();
//        rvFavorite.setAdapter(madapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
