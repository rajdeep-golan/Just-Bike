package com.just.justbikev2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.just.justbikev2.Common.Common;

import java.util.concurrent.TimeUnit;

public class PhoneAuthInit extends AppCompatActivity implements View.OnClickListener{

        CountryCodePicker countrycode;
        ImageButton verify,sendOTP;

        EditText phoneNumber;
        DatabaseReference user_table;
        ProgressDialog progressDialog;
        PinView pinView;

        TextView sendOTPTV;
        String phone , codeBySystem;
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
        setContentView(R.layout.sign_up_phone);

        phoneNumber = findViewById(R.id.newPhone);
        verify = findViewById(R.id.verify);
        countrycode=findViewById(R.id.countryCodePicker);
        pinView = findViewById(R.id.pinView);
        sendOTP = findViewById(R.id.sendOTP);
        sendOTPTV = findViewById(R.id.sendOTPTV);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        user_table = database.getReference("User");
        verify.setOnClickListener(this);
        sendOTP.setOnClickListener(this);
        }

@Override
public void onClick(View v) {
        switch (v.getId()){
        case R.id.verify:
                callVerifyOTPScreen();

                break;
         case R.id.sendOTP:

                        if(Common.isConnectedToInternet(getBaseContext())) {
                                progressDialog = new ProgressDialog(PhoneAuthInit.this);
                                progressDialog.setMessage("Please Wait . . \n Sending OTP");
                                progressDialog.show();
                                user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                progressDialog.dismiss();
                                                String checkPhoneNo = "+"+countrycode.getSelectedCountryCode()+phoneNumber.getText().toString().trim();
                                                if (dataSnapshot.child(checkPhoneNo).exists())
                                                        Toast.makeText(PhoneAuthInit.this, "Phone number already exists", Toast.LENGTH_LONG).show();
                                                else {
                                                  if(phoneNumber.getText().toString().trim().length()>8 && phoneNumber.getText().toString().trim().length()<11) {
                                                    sendOTPTV.setText("OTP Sent");
                                                    Toast.makeText(PhoneAuthInit.this, "Please wait. If sim in this device it will verify automatically.", Toast.LENGTH_LONG).show();
                                                    callVerifyOTPScreen();
                                                  }
                                                }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                });
                        }
                        else
                        {
                                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                                return;
                        }
                        break;
        }
        }

private void callVerifyOTPScreen() {

        String code = pinView.getText().toString();
        if(!code.isEmpty()){
        verifyCode(code);
        }

//        if(!validatePhoneNumber())
//            return;
        String userEnteredNo = phoneNumber.getText().toString().trim();
        phone = "+"+countrycode.getSelectedCountryCode()+userEnteredNo;
        sendVerificationCode(phone);

        }

private void sendVerificationCode(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
        phone,        // Phone number to verify
        60,                 // Timeout duration
        TimeUnit.SECONDS,   // Unit of timeout
        TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
        mCallbacks);        // OnVerificationStateChangedCallbacks
        }

private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
        signInWithPhoneAuthCredential(credential);
        }

private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
        FirebaseUser user = task.getResult().getUser();
        Toast.makeText(PhoneAuthInit.this, user.getPhoneNumber()+ " verified", Toast.LENGTH_SHORT).show();
            Intent i =new Intent(PhoneAuthInit.this,SignUpAcitivity.class);
            i.putExtra("phone",phone);
            startActivity(i);
            finish();
        // ...
        } else {
        // Sign in failed, display a message and update the UI

        Toast.makeText(PhoneAuthInit.this, "Failed! Try again", Toast.LENGTH_SHORT).show();
        }
        }
        });
        }

private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
@Override
public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        //     super.onCodeSent(s, forceResendingToken);
        codeBySystem = s;
        }

@Override
public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        String code = phoneAuthCredential.getSmsCode();
        if(code!=null) {
        pinView.setText(code);
        verifyCode(code);
        }
        }

@Override
public void onVerificationFailed(@NonNull FirebaseException e) {
        Toast.makeText(PhoneAuthInit.this, "Verification Failed", Toast.LENGTH_SHORT).show();
        }
        };
int c=0;
  @Override
  public void onBackPressed() {
    c++;
    if(c>1)
      super.onBackPressed();
    else
      Toast.makeText(this, "Hit back again to quit!", Toast.LENGTH_SHORT).show();
  }
}
