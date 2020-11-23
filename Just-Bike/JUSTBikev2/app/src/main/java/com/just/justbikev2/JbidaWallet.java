package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Upi;
import com.just.justbikev2.Model.UserModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JbidaWallet extends AppCompatActivity implements PaymentResultListener {
    TextView walletCost;

    CardView addMoney , tranferMoney;

    VideoView jbidaPromotion;
    DatabaseReference user_table;

    Locale locale;
    NumberFormat format ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.activity_jbida_wallet);

        Checkout.preload(getApplicationContext());

        user_table = FirebaseDatabase.getInstance().getReference("User");

        walletCost = findViewById(R.id.walletCost);
        addMoney = findViewById(R.id.addMoney);
        tranferMoney = findViewById(R.id.transferMoney);
        jbidaPromotion = findViewById(R.id.jbidaPromotion);

        locale = new Locale("en", "in");
        format = NumberFormat.getCurrencyInstance(locale);
        walletCost.setText(format.format(Integer.parseInt(Common.currentUser.getWallet())));
      upiId="rajput.dbs@dbs";
      DatabaseReference upi = FirebaseDatabase.getInstance().getReference().child("Upi");
      upi.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for(DataSnapshot postData : dataSnapshot.getChildren()){
            Upi upiModel = postData.getValue(Upi.class);
            if(Common.cityId.equals(upiModel.getCityId()))
              upiId = upiModel.getUpiId();
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JbidaWallet.this,R.style.RedDialogTheme);
                LayoutInflater inflater = getLayoutInflater();
                    View add_money_dialog = inflater.inflate(R.layout.add_money_dialog, null);
                    alertDialogBuilder.setView(add_money_dialog);
                    AlertDialog alertDialog = alertDialogBuilder.create();
              alertDialog.show();
                EditText addMoneyET =  add_money_dialog.findViewById(R.id.amtTransfer);
                    add_money_dialog.findViewById(R.id.transferBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          if( addMoneyET.getText() !=null && !addMoneyET.getText().toString().equals("")) {
                            walletAmount = Integer.parseInt(addMoneyET.getText().toString());
                            startPayment();
                            addMoneyET.setText("");
                            alertDialog.dismiss();
                          }else
                            Toast.makeText(JbidaWallet.this, "Please provide an amount", Toast.LENGTH_SHORT).show();
                        }
                    });
                add_money_dialog.findViewById(R.id.upiBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     if( addMoneyET.getText() !=null && !addMoneyET.getText().toString().equals("")) {
                       walletAmount = Integer.parseInt(addMoneyET.getText().toString());
                       payUsingUPI();
                       alertDialog.dismiss();
                     }
                     else
                       Toast.makeText(JbidaWallet.this, "Please provide an amount", Toast.LENGTH_SHORT).show();
                    }
                });

           }
        });
        tranferMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final AlertDialog.Builder alertDialog = new AlertDialog.Builder(JbidaWallet.this);
              LayoutInflater inflater = getLayoutInflater();
              View tranfer_money_dialog = inflater.inflate(R.layout.transfer_buddy_dialog, null);
              alertDialog.setView(tranfer_money_dialog);
              EditText buddyNumber = tranfer_money_dialog.findViewById(R.id.amtTransfer);
              ImageButton phoneNumber = tranfer_money_dialog.findViewById(R.id.verifyNumber);
             TextView name =  tranfer_money_dialog.findViewById(R.id.buddyName);
              CountryCodePicker countryCodePicker = tranfer_money_dialog.findViewById(R.id.countryCodePicker);

              helpBuddyVerifyFlag=0;
              name.setVisibility(View.GONE);
              phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  helpBuddyVerifyFlag=0;
                  user_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      for(DataSnapshot postdata: dataSnapshot.getChildren()){
                        UserModel userToTranfer = postdata.getValue(UserModel.class);
                        String checkPhoneNo = "+"+ countryCodePicker.getSelectedCountryCode()+buddyNumber.getText().toString().trim();
                        if(checkPhoneNo.equals(Common.currentUser.getPhone())) {
                          Toast.makeText(JbidaWallet.this, "Please don't be self-interested", Toast.LENGTH_SHORT).show();
                          helpBuddyVerifyFlag=2;
                          break;
                        }
                        if(checkPhoneNo.equals(userToTranfer.getPhone())) {
                          buddy = userToTranfer;
                          name.setText(userToTranfer.getName());
                          name.setTextColor(getResources().getColor(R.color.green));
                          name.setVisibility(View.VISIBLE);
                          helpBuddyVerifyFlag=1;
                        }
                      }
                      if(helpBuddyVerifyFlag==0){
                        name.setText("Not Found");
                        name.setTextColor(getResources().getColor(R.color.btn_red_bg));
                        name.setVisibility(View.VISIBLE);
                      }else if(helpBuddyVerifyFlag==2){
                        name.setText("You");
                        name.setTextColor(getResources().getColor(R.color.btn_red_bg));
                        name.setVisibility(View.VISIBLE);
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                      Toast.makeText(JbidaWallet.this, "Error! Try again.", Toast.LENGTH_SHORT).show();
                    }
                  });
                }
              });
              alertDialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  if(helpBuddyVerifyFlag==1)
                    openTransferDialog();
                  else
                    Toast.makeText(JbidaWallet.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                }
              });
              alertDialog.show();
            }
        });

        playJbidaPromotion();
    }
    UserModel buddy;
  TextView walletAmt;
  private void openTransferDialog() {
    if(buddy!=null){
      final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JbidaWallet.this);
      LayoutInflater inflater = getLayoutInflater();
      View tranfer_money_dialog = inflater.inflate(R.layout.tranfer_buddy2_dialog, null);
      alertDialogBuilder.setView(tranfer_money_dialog);
      TextView name = tranfer_money_dialog.findViewById(R.id.name);
      name.setText(buddy.getName()+" , "+buddy.getPhone());
       walletAmt = tranfer_money_dialog.findViewById(R.id.walletAmt);
      walletAmt.setText(format.format(Integer.parseInt(Common.currentUser.getWallet())));
      EditText amtTransfer =  tranfer_money_dialog.findViewById(R.id.amtTransfer);
      isFromHelpBuddy=0;
      AlertDialog alertDialog = alertDialogBuilder.create();
      tranfer_money_dialog.findViewById(R.id.transferBtn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if( amtTransfer.getText() !=null || !amtTransfer.getText().toString().equals("")) {
            int walletBalance = Integer.parseInt(Common.currentUser.getWallet());
            int transferAmt = Integer.parseInt(amtTransfer.getText().toString());
            if(transferAmt > walletBalance)
              Toast.makeText(JbidaWallet.this, "Tranfer Amount cannot be greater than wallet balance", Toast.LENGTH_LONG).show();
            else {
              user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(buddy.getPhone()).exists()) {
                      Map<String, Object> passMap = new HashMap<>();
                      int currentBalance = Integer.parseInt(buddy.getWallet());
                      passMap.put("wallet", String.valueOf(transferAmt + currentBalance) );
                      user_table.child(buddy.getPhone()).updateChildren(passMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          Map<String, Object> passMap2 = new HashMap<>();
                          passMap2.put("wallet",String.valueOf(walletBalance - transferAmt));
                          user_table.child(Common.currentUser.getPhone()).updateChildren(passMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              Toast.makeText(JbidaWallet.this, "Transfer Successful!", Toast.LENGTH_LONG).show();
                              isFromHelpBuddy=1;
                              updateCurrentUser();
                              amtTransfer.setText("");
                              alertDialog.dismiss();
                            }
                          });
                        }
                      });
                      buddy = dataSnapshot.child(buddy.getPhone()).getValue(UserModel.class);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                  Toast.makeText(JbidaWallet.this, "Error! Please try again.", Toast.LENGTH_SHORT).show();
                }
              });

            }
          }else
            Toast.makeText(JbidaWallet.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
      });
      alertDialog.show();
    }else
       Toast.makeText(JbidaWallet.this, "Error! Please try again.", Toast.LENGTH_SHORT).show();

  }

  int helpBuddyVerifyFlag=0;  // 0 : not found; 1: valid buddy; 2: client putting his own number
  int isFromHelpBuddy=0;
    private void updateCurrentUser() {
        user_table.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                    UserModel userModel = dataSnapshot.child(Common.currentUser.getPhone()).getValue(UserModel.class);
                  walletCost.setText(format.format(Integer.parseInt(Common.currentUser.getWallet())));
                  Common.currentUser = userModel;
                    if(isFromHelpBuddy==1)
                      walletAmt.setText(format.format(Integer.parseInt(Common.currentUser.getWallet())));
                    user_table.removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              Toast.makeText(JbidaWallet.this, "Your details could not be updated. Please restart your app.", Toast.LENGTH_SHORT).show();
            }
        }));
    }


    @Override
    public void onPaymentSuccess(String s) {
        if (Common.currentUser != null) {

            Map<String, Object> passMap = new HashMap<>();
            passMap.put("wallet", String.valueOf(Integer.parseInt(Common.currentUser.getWallet()) + walletAmount));
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
            users.child(Common.currentUser.getPhone()).updateChildren(passMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateCurrentUser();
                  Intent i = new Intent(JbidaWallet.this,Home.class);
                  startActivity(i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(JbidaWallet.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } Toast.makeText(this, "Please Login before Booking!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Error during payment , Try again!", Toast.LENGTH_SHORT).show();
    }

    private final int UPI_PAYMENT = 342;
    String upiId = "";
    private void payUsingUPI() {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", "Just Bike")
                .appendQueryParameter("tn", "Jbida Wallet")
                .appendQueryParameter("am", String.valueOf(walletAmount))
                .appendQueryParameter("cu", "INR")
                .build();
        Intent upiPaymentIntent = new Intent(Intent.ACTION_VIEW);
        upiPaymentIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPaymentIntent, "Pay with");

        if (chooser.resolveActivity(getPackageManager()) != null)
            startActivityForResult(chooser, UPI_PAYMENT);
        else {
            Toast.makeText(this, "No UPI app found! Please download any OR choose other payment options", Toast.LENGTH_LONG).show();
        }

    }

    private void upiPaymentDataOperation(ArrayList<String> dataList) {
        if (Common.isConnectedToInternet(this)) {
            String str = dataList.get(0);
            String paymentCancel = "";
            if (str == null)
                str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase()))
                        status = equalStr[1].toLowerCase();
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) ||
                            equalStr[0].toLowerCase().equals("txnRef".toLowerCase()))
                        approvalRefNo = equalStr[1];
                    else
                        paymentCancel = "Payment Cancelled by You";
                }
            }
            if (status.equals("success")) {
                upiPaymentSuccessful();
                Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show();
            } else if (paymentCancel.equals("Payment Cancelled by You")) {
                Toast.makeText(this, "Payment Cancelled by You", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upiPaymentSuccessful() {
        if (Common.currentUser != null) {
            Map<String, Object> addedMoneyMap = new HashMap<>();
            addedMoneyMap.put("wallet", String.valueOf(Integer.parseInt(Common.currentUser.getWallet())+walletAmount));
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
            users.child(Common.currentUser.getPhone()).updateChildren(addedMoneyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateCurrentUser();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(JbidaWallet.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if (resultCode == RESULT_OK || resultCode == 11) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    String razorpayAmt;
    int walletAmount;
    public void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_vyPSpJi7gF28TF");

        checkout.setImage(R.drawable.just_black_logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Just Bike");

            options.put("description", "Secured Payment Gateway");
            options.put("image", R.drawable.biker);
            // options.put("order_id", Common.getOrderId());
            options.put("currency", "INR");
            razorpayAmt = String.valueOf(walletAmount * 100);  // 18% tax (*100 for paise)
            options.put("amount", razorpayAmt);

            checkout.open(activity, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void playJbidaPromotion() {
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.jbida_promotion);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        jbidaPromotion.setVideoURI(video);
        jbidaPromotion.start();
        jbidaPromotion.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(jbidaPromotion!=null)
            playJbidaPromotion();
    }
}
