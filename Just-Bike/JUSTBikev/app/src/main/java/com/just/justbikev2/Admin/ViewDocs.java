package com.just.justbikev2.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.just.justbikev2.FullScreenImageActivity;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

public class ViewDocs extends AppCompatActivity {

  TextView creationTime,address,emailId;
  ImageView aadharBack,aadharFront,dlFront,dlBack,profilePhoto;
  UserModel userModel;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_docs);

    bindViews();

    if(getIntent()!=null) {
      userModel = (UserModel) getIntent().getSerializableExtra("user");
      if(userModel!=null)
        setValues();
    }
  }

  private void setValues() {
    creationTime.setText(userModel.getCreationTime());
    emailId.setText(userModel.getEmail());
    address.setText(userModel.getHomeAddress());
    if(userModel.getAadhar()!=null && !userModel.getAadhar().equals("")) {
      Picasso.get().load(userModel.getAadhar()).placeholder(R.drawable.biker).into(aadharFront);
      aadharFront.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onClickImage(userModel.getAadhar());
        }
      });
    }
    if(userModel.getProfilePic()!=null && !userModel.getProfilePic().equals("")) {
      Picasso.get().load(userModel.getProfilePic()).placeholder(R.drawable.biker).into(profilePhoto);
      profilePhoto.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onClickImage(userModel.getProfilePic());
        }
      });
    }
    if(userModel.getAadharBack()!=null && !userModel.getAadharBack().equals(""))
    {
      Picasso.get().load(userModel.getAadharBack()).placeholder(R.drawable.biker).into(aadharBack);
      aadharBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onClickImage(userModel.getAadharBack());
        }
      });
    }
    if(userModel.getLicense()!=null && !userModel.getLicense().equals("")) {
      Picasso.get().load(userModel.getLicense()).placeholder(R.drawable.biker).into(dlFront);
      dlFront.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onClickImage(userModel.getLicense());
        }
      });
    }
    if(userModel.getLicesneBack()!=null && !userModel.getLicesneBack().equals("")) {
      Picasso.get().load(userModel.getLicesneBack()).placeholder(R.drawable.biker).into(dlBack);
      dlBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onClickImage(userModel.getLicesneBack());
        }
      });
    }
  }

  private void onClickImage(String iamgeUrl) {
    Intent i = new Intent(ViewDocs.this, FullScreenImageActivity.class);
    i.putExtra("imageUrl",iamgeUrl);
    startActivity(i);

  }

  private void bindViews() {
    profilePhoto = findViewById(R.id.profilePhoto);
    creationTime = findViewById(R.id.creationTime);
    emailId = findViewById(R.id.emailId);
    address = findViewById(R.id.address);
    aadharFront = findViewById(R.id.aadharFront);
    aadharBack = findViewById(R.id.aadharBack);
    dlBack = findViewById(R.id.dlBack);
    dlFront = findViewById(R.id.dlFront);
  }
}
