package com.just.justbikev2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Admin.HomeAdmin;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.UserModel;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity  {

    ImageView justBikeLogo;
    //TextView noSecurity , doorstepDelivery;
    Animation from_bottom , from_top;


    VideoView videoView;

    LinearLayout splash;


    ImageView biker3a, biker3b , biker3c ,biker3d , biker3e ,biker3f,biker3g , bikea , bikeb , bikec,biked,bikee,bikef,bikeg;
    LazyLoader splashLayout;

    String user,password;
  private boolean checkPlayServices () {
    GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
    int resultCode = gApi.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (gApi.isUserResolvableError(resultCode)) {
       //   gApi.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Toast.makeText(this,"Google play services not supported!", Toast.LENGTH_LONG).show();
        finish();
      }
      return false;
    }
    return true;
  }
  @Override public void onStop() {
    super.onStop();
    if (waitingDialog != null) {
      waitingDialog.dismiss();
      waitingDialog = null;
    }
  }

  @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Paper.init(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.splash_activity);
      checkPlayServices();
        videoView = findViewById(R.id.videoView);
        splash = findViewById(R.id.splash);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(splash.getWidth(),splash.getHeight());
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);
        videoView.setVideoURI(video);
        waitingDialog = new SpotsDialog.Builder()
                .setContext(SplashActivity.this)
                .setTheme(R.style.Custom)
                .build();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if(waitingDialog!=null)
                  waitingDialog.show();
                if (isFinishing()) {
                    return;
                }
            }
        });

        videoView.start();
        FacebookSdk.sdkInitialize(getApplicationContext());


        Paper.init(this);
         if(Paper.book()!=null) {
           user = Paper.book().read(Common.UserKey);
           password = Paper.book().read(Common.PasswordKey);
         }



        new Handler().postDelayed(this::openActivity, 3000);


    }
    public static boolean isStringOnlyNumbers(String str)
    {
      if(str==null || str.length()==0)
        return false;

      for(int i=0;i<str.length();i++){
            char c=str.charAt(i);
            if(Character.isLetter(c) || c=='.'||c== '#'||c== '$'||c== '['||c==  ']')
                return false;
        }
        return true;
    }
    private void openActivity() {
        if(user!=null && password!=null && !user.isEmpty() && !password.isEmpty() && isStringOnlyNumbers(user)){
            login(user,password);
        }
        else {
          if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
          }
            Intent i = new Intent(this, SignInAcitivity.class);
          startActivity(i);
          finish();
        }
    }
    android.app.AlertDialog waitingDialog;
    private void login(String phone, String password) {
        if(Common.isConnectedToInternet(getBaseContext())){

            DatabaseReference user_table;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            user_table = database.getReference("User");


            user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //progressDialog.dismiss();
                    if(dataSnapshot.child(phone).exists()) {
                        UserModel userModel = dataSnapshot.child(phone).getValue(UserModel.class);
                        if(userModel!=null)  userModel.setPhone(phone);
                        if (userModel.getPassword().equals(password)) {
                            Intent i ;
                            if(userModel.getIsStaff()!=null && userModel.getIsStaff().equals("true"))
                                i = new Intent(SplashActivity.this, HomeAdmin.class);    //Staff
                            else
                                i= new Intent(SplashActivity.this, Home.class);             //Client


                            Common.currentUser = userModel;
                          if (waitingDialog != null && waitingDialog.isShowing()) {
                            waitingDialog.dismiss();
                          }
                          startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(SplashActivity.this, "Sign up Failed ,Please try again!", Toast.LENGTH_LONG).show();
                          if (waitingDialog != null && waitingDialog.isShowing()) {
                            waitingDialog.dismiss();
                          }
                          Intent i = new Intent(SplashActivity.this,SignInAcitivity.class);
                          startActivity(i);
                        }
                    }
                    else {
                        Toast.makeText(SplashActivity.this, "Incorrect Credentials", Toast.LENGTH_LONG).show();
                      if (waitingDialog != null && waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                      }
                      Intent i = new Intent(SplashActivity.this,SignInAcitivity.class);
                      startActivity(i);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    waitingDialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(this, "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
            waitingDialog.dismiss();
          AlertDialog.Builder alertdialog = new AlertDialog.Builder(SplashActivity.this);
          alertdialog.setTitle("No Internet Connectivity");
          alertdialog.setPositiveButton("CLOSE APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
          }});
          alertdialog.setCancelable(false);
          setFinishOnTouchOutside(false);
          alertdialog.show();
            return;
        }
    }
}
