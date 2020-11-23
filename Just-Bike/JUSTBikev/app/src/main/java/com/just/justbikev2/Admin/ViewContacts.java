package com.just.justbikev2.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.ContactsAdapter;
import com.just.justbikev2.Model.ContactModel;
import com.just.justbikev2.R;
import com.just.justbikev2.ShowComments;
import com.just.justbikev2.ViewHolder.ShowCommentsVH;

import java.util.ArrayList;
import java.util.Map;

public class ViewContacts extends AppCompatActivity {

  RecyclerView contacts_list;
  ContactsAdapter adapter;
  TextView countContacts;
  String phoneNo;
  ArrayList<String> contactName;
  ArrayList<String> contactNo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_contacts);
    contacts_list = findViewById(R.id.contacts_list);
    countContacts = findViewById(R.id.countContacts);
    contacts_list.setLayoutManager(new LinearLayoutManager(this));

    if(getIntent()!=null){
      phoneNo = getIntent().getStringExtra("phoneNo");
    }
    contactName = new ArrayList<>();
    contactNo = new ArrayList<>();

FirebaseDatabase.getInstance().getReference("Contacts").child(phoneNo).addValueEventListener(new ValueEventListener() {
  @Override
  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    countContacts.setText(dataSnapshot.getChildrenCount()+"");
    for (DataSnapshot postData : dataSnapshot.getChildren()) {

        contactNo.add(postData.getKey());
        contactName.add((String)postData.getValue());

    }
    adapter = new ContactsAdapter(contactName,contactNo,ViewContacts.this);
    contacts_list.setAdapter(adapter);
  }

  @Override
  public void onCancelled(@NonNull DatabaseError databaseError) {
    Toast.makeText(ViewContacts.this, "No Contacts Found / Removed by Admin", Toast.LENGTH_SHORT).show();
  }
});


  }


}
