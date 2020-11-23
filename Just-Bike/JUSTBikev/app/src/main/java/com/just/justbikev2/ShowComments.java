package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.just.justbikev2.Model.RatingVehicle;
import com.just.justbikev2.ViewHolder.ShowCommentsVH;

public class ShowComments extends AppCompatActivity {
    RecyclerView commentsRV;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference reference;

    SwipeRefreshLayout swipeRefreshLayout;

    TextView bikeName;
    String bikeId="",bikeNameString="";

    FirebaseRecyclerAdapter<RatingVehicle, ShowCommentsVH> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.activity_show_comments);

        commentsRV = findViewById(R.id.commentsRV);
        bikeName = findViewById(R.id.phoneNo);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Rating");

        layoutManager = new LinearLayoutManager(this);
        commentsRV.setLayoutManager(layoutManager);

        if(getIntent()!=null) {
            bikeId = getIntent().getStringExtra("vehicleId");
            bikeNameString = getIntent().getStringExtra("vehicleName");
        }
        bikeName.setText(bikeNameString);
        swipeRefreshLayout = findViewById(R.id.swipeLayoutComments);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(bikeId!=null && !bikeId.isEmpty()){
                    Query query = reference.orderByChild("vehicleId").equalTo(bikeId);
                    FirebaseRecyclerOptions<RatingVehicle> options = new FirebaseRecyclerOptions.Builder<RatingVehicle>()
                            .setQuery(query,RatingVehicle.class)
                            .build();
                    adapter = new FirebaseRecyclerAdapter<RatingVehicle, ShowCommentsVH>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentsVH holder, int position, @NonNull RatingVehicle ratingV) {

                            holder.name.setText(ratingV.getUserName());
                            holder.comment.setText(ratingV.getComment());
                            holder.ratingBar.setRating(Float.parseFloat(ratingV.getRating()));
                        }

                        @NonNull
                        @Override
                        public ShowCommentsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.comments_card,parent,false);
                            return new ShowCommentsVH(v);
                        }
                    };
                    loadComments(bikeId);
                }
            }
        });

        //Thread to load comment on first launch
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if(bikeId!=null && !bikeId.isEmpty()){
                    Query query = reference.orderByChild("vehicleId").equalTo(bikeId);
                    FirebaseRecyclerOptions<RatingVehicle> options = new FirebaseRecyclerOptions.Builder<RatingVehicle>()
                            .setQuery(query,RatingVehicle.class)
                            .build();
                    adapter = new FirebaseRecyclerAdapter<RatingVehicle, ShowCommentsVH>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentsVH holder, int position, @NonNull RatingVehicle ratingV) {

                            holder.name.setText(ratingV.getUserName());
                            holder.comment.setText(ratingV.getComment());
                            holder.ratingBar.setRating(Float.parseFloat(ratingV.getRating()));
                        }

                        @NonNull
                        @Override
                        public ShowCommentsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.comments_card,parent,false);
                            return new ShowCommentsVH(v);
                        }
                    };
                    loadComments(bikeId);
                }
            }
        });
    }

    private void loadComments(String bikeId) {
        adapter.startListening();

        commentsRV.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }
}
