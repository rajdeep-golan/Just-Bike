package com.just.justbikev2;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.just.justbikev2.Admin.HomeAdmin;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.CallLogModel;
import com.just.justbikev2.Model.PhoneMessages;
import com.just.justbikev2.Model.UserModel;
import com.rey.material.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SignInAcitivity extends AppCompatActivity implements View.OnClickListener {

  ImageView signIn;
  EditText phone, password;
  TextView forgetPass, signUp;

  FirebaseDatabase database;
  DatabaseReference user_table;

  CheckBox checkBoxRememberMe;

  private static final int AUTH_REQUEST_CODE = 32;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }
    setContentView(R.layout.signin);
    FirebaseApp.initializeApp(this);


    phone = findViewById(R.id.phone);
    signUp = findViewById(R.id.SignUp);
    signIn = findViewById(R.id.uploadCV);
    forgetPass = findViewById(R.id.forgetPass);
    password = findViewById(R.id.password);
    checkBoxRememberMe = findViewById(R.id.rememberMeCheckBox);
    countrycode = findViewById(R.id.countryCodePicker);

    checkBoxRememberMe.setChecked(true);
    Paper.init(this);

    database = FirebaseDatabase.getInstance();
    user_table = database.getReference("User");

    signIn.setOnClickListener(this);
    signUp.setOnClickListener(this);
    forgetPass.setOnClickListener(this);
  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTH_REQUEST_CODE){
            IdpResponse response =  IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                //Get User
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(Common.isConnectedToInternet(getBaseContext())){
                    android.app.AlertDialog waitingDialog = new SpotsDialog.Builder()
                            .setContext(SignInAcitivity.this)
                            .setTheme(R.style.Custom)
                            .build();
                    waitingDialog.show();
                    user_table.addListenerForSingleValueEvent((new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                          //  progressDialog.dismiss();
                            waitingDialog.dismiss();
                            if(dataSnapshot.child(user.getPhoneNumber()).exists()) {
                                UserModel userModel = dataSnapshot.child(user.getPhoneNumber()).getValue(UserModel.class);
                                if(userModel!=null)  userModel.setPhone(user.getPhoneNumber());
                                if (userModel.getPassword().equals(password.getText().toString())) {
                                    Intent i ;
                                    if(userModel.getIsStaff().equals("true"))
                                        i = new Intent(SignInAcitivity.this, HomeAdmin.class);    //Staff
                                    else
                                        i= new Intent(SignInAcitivity.this, Home.class);             //Client


                                    Common.currentUser = userModel;
                                    startActivity(i);
                                    finish();
                                    user_table.removeEventListener(this);
                                } else {
                                    Toast.makeText(SignInAcitivity.this, "Incorrect Credentials", Toast.LENGTH_LONG).show();
                                    phone.setText("");
                                    password.setText("");
                                }
                            }
                            else {
                                phone.setText("");
                                password.setText("");
                                Toast.makeText(SignInAcitivity.this, "Account does not exist", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }));
                }
                else {
                    Toast.makeText(this, "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
               // FirebaseUserMetadata metadata = auth.getCurrentUser().getMetadata();
            }
            else {
                Toast.makeText(this, ""+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    CountryCodePicker countrycode;
    String phoneString;
    public static boolean isStringOnlyNumbers(String str)
    {
        for(int i=0;i<str.length();i++){
            char c=str.charAt(i);
            if(Character.isLetter(c) || c=='.'||c== '#'||c== '$'||c== '['||c==  ']')
                return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.uploadCV:
                if((phone.getText()==null || phone.getText().equals("")) ||
                        (password.getText()==null || password.getText().equals("")) || !isStringOnlyNumbers(phone.getText().toString()))
                    Toast.makeText(this, "Please enter correct details!", Toast.LENGTH_SHORT).show();
                else {
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        String userEnteredNo = phone.getText().toString().trim();
                        phoneString = "+"+countrycode.getSelectedCountryCode()+userEnteredNo;

                        if (checkBoxRememberMe.isChecked()) {
                            Paper.book().write(Common.UserKey, phoneString);
                            Paper.book().write(Common.PasswordKey, password.getText().toString());
                        }

//                progressDialog = new ProgressDialog(SignInAcitivity.this);
//                progressDialog.setMessage("Please Wait . . .");
//                progressDialog.show();
                        android.app.AlertDialog waitingDialog = new SpotsDialog.Builder()
                                .setContext(SignInAcitivity.this)
                                .setTheme(R.style.Custom)
                                .build();
                        waitingDialog.show();
                        user_table.addListenerForSingleValueEvent((new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                waitingDialog.dismiss();
                                if (dataSnapshot.child(phoneString).exists()) {
                                    UserModel userModel = dataSnapshot.child(phoneString).getValue(UserModel.class);
                                    if (userModel != null)
                                        userModel.setPhone(phoneString);
                                    if (userModel != null && userModel.getPassword() != null && userModel.getPassword().equals(password.getText().toString())) {
                                        Intent i;
                                        if (userModel.getIsStaff()!=null && userModel.getIsStaff().equals("true"))
                                            i = new Intent(SignInAcitivity.this, HomeAdmin.class);    //Staff
                                        else
                                            i = new Intent(SignInAcitivity.this, Home.class);             //Client


                                        Common.currentUser = userModel;
                                        startActivity(i);
                                        finish();
                                        user_table.removeEventListener(this);
                                    } else {
                                        Toast.makeText(SignInAcitivity.this, "Incorrect Credentials", Toast.LENGTH_LONG).show();
                                        phone.setText("");
                                        password.setText("");
                                    }
                                } else {
                                    phone.setText("");
                                    password.setText("");
                                    Toast.makeText(SignInAcitivity.this, "Account does not exist", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }));
                    } else {
                        Toast.makeText(this, "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
            case R.id.SignUp:
                Intent i= new Intent(SignInAcitivity.this,PhoneAuthInit.class);
                startActivity(i);
                break;
            case R.id.forgetPass:
                final AlertDialog.Builder alert = new AlertDialog.Builder(SignInAcitivity.this);
                alert.setTitle("Retrieve Password");

                LayoutInflater inflater = this.getLayoutInflater();
                View forget_pass_card = inflater.inflate(R.layout.forget_pass_card , null);

                alert.setView(forget_pass_card);
                alert.setIcon(R.drawable.ic_refresh_black_24dp);

                EditText phoneET = forget_pass_card.findViewById(R.id.phoneET);
                EditText secureCodeET = forget_pass_card.findViewById(R.id.secureCodeET);
                alert.setPositiveButton("GET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              String phoneForgetNo = "+"+countrycode.getSelectedCountryCode()+phoneET.getText().toString();

                              UserModel user = dataSnapshot.child(phoneForgetNo).getValue(UserModel.class);
                                if(user!=null) {
                                    if (user.getSecureCode().equals(secureCodeET.getText().toString()))
                                        Toast.makeText(SignInAcitivity.this, "Your password is " + user.getPassword(), Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(SignInAcitivity.this, "Wrong Secure Code", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(SignInAcitivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
        }
    }
    int c=0;
  @Override
  public void onBackPressed() {
    Toast.makeText(this, "Hit back again to quit!", Toast.LENGTH_SHORT).show();
    c++;
    if(c>1)
      super.onBackPressed();
  }
}
