package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.PhotoUploadAdapter;
import com.just.justbikev2.Model.PhotoUpload;

import java.util.ArrayList;
import java.util.List;

public class PhotoUploadList extends AppCompatActivity {
    RecyclerView recyclerView;
    PhotoUploadAdapter photoUploadAdapter;

    DatabaseReference databaseReference;
    List<PhotoUpload> photoUploads;
    ProgressBar pBarRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload_list);
        recyclerView = findViewById(R.id.photoUploadRecyclerView);
        pBarRV = findViewById(R.id.pBarRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        photoUploads = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot postDataSnapShot : dataSnapshot.getChildren()){
                        PhotoUpload photoUpload = postDataSnapShot.getValue(PhotoUpload.class);
                        photoUploads.add(photoUpload);
                    }
                    photoUploadAdapter = new PhotoUploadAdapter(PhotoUploadList.this,photoUploads);
                    recyclerView.setAdapter(photoUploadAdapter);
                    pBarRV.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PhotoUploadList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pBarRV.setVisibility(View.INVISIBLE);
            }
        });
    }
}
