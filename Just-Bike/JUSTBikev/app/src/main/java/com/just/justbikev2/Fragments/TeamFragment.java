package com.just.justbikev2.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.just.justbikev2.Model.Order;
import com.just.justbikev2.Model.Team;
import com.just.justbikev2.R;
import com.just.justbikev2.ViewHolder.TeamViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class TeamFragment extends Fragment {
    RecyclerView teamRV;
    DatabaseReference team;
    FirebaseRecyclerAdapter adapter;

    public TeamFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_team, container, false);
        teamRV = view.findViewById(R.id.teamRV);

        team = FirebaseDatabase.getInstance().getReference("Team");

        teamRV.setLayoutManager(new LinearLayoutManager(getContext()));
        teamRV.setHasFixedSize(true);

        loadTeamMates();

        return view;
    }

    private void loadTeamMates() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Team")
                .limitToLast(50);

        FirebaseRecyclerOptions<Team> options =
                new FirebaseRecyclerOptions.Builder<Team>()
                        .setQuery(query, Team.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Team, TeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TeamViewHolder holder, int position, @NonNull Team model) {
                holder.team_name.setText(model.getName());
                holder.team_post.setText(model.getPost());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.biker).into(holder.team_image);//.transform(new RoundedCornersTransformation(50,0))
                holder.gmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final BottomSheetDialog bt = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.team_details, null);
                        TextView mediaId = view.findViewById(R.id.mediaId);
                        ImageView mediaIcon = view.findViewById(R.id.mediaIcon);
                        Picasso.get().load(R.drawable.mail).into(mediaIcon);
                        mediaId.setText(model.getGmail());
                        bt.setContentView(view);
                        bt.show();
                    }
                });
                holder.facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final BottomSheetDialog bt = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.team_details, null);
                        TextView mediaId = view.findViewById(R.id.mediaId);
                        ImageView mediaIcon = view.findViewById(R.id.mediaIcon);
                        Picasso.get().load(R.drawable.facebook).into(mediaIcon);
                        mediaId.setText(model.getFacebook());
                        bt.setContentView(view);
                        bt.show();
                    }
                });
                holder.linkedin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final BottomSheetDialog bt = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.team_details, null);
                        TextView mediaId = view.findViewById(R.id.mediaId);
                        ImageView mediaIcon = view.findViewById(R.id.mediaIcon);
                        Picasso.get().load(R.drawable.linkedin).into(mediaIcon);
                        mediaId.setText(model.getLinkedin());
                        bt.setContentView(view);
                        bt.show();
                    }
                });
            }

            @NonNull
            @Override
            public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.our_team_card, parent, false);
                return new TeamViewHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        teamRV.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
}
