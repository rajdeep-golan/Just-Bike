package com.just.justbikev2;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.squareup.picasso.Picasso;

public class JustBike extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    FirebaseApp.initializeApp(this);
    Picasso.setSingletonInstance(
      new Picasso.Builder(this)
        // additional settings
        .build());
  }
}
