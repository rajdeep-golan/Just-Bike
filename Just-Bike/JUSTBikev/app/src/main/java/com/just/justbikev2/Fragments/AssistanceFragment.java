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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.ChatAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.R;

import java.util.ArrayList;
import java.util.List;

public class AssistanceFragment extends Fragment {
    private RecyclerView chatList;
    private ChatAdapter adapter;
    private List<UserModel> users;

    public AssistanceFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =   inflater.inflate(R.layout.fragment_assistance, container, false);

        chatList = view.findViewById(R.id.chatList);
        chatList.setHasFixedSize(true);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();
        
        readUsers();
        return  view;
    }

    private void readUsers() {
     //   FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    UserModel userModel = data.getValue(UserModel.class);
                    if(userModel!=null && !userModel.getPhone().equals(Common.currentUser.getPhone()))
                        users.add(userModel);
                }
                adapter = new ChatAdapter(users,getContext());
                adapter.notifyDataSetChanged();
                chatList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
