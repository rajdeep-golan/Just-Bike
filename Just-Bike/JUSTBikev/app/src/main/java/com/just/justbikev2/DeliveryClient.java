package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.just.justbikev2.Adapter.AccessoryDisplayAdapter;
import com.just.justbikev2.Adapter.CitySpinnerAdapter;
import com.just.justbikev2.Admin.HomeAdmin;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.Model.UserModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeliveryClient extends AppCompatActivity {
    Intent intent;
    String outstandingAmt , orderId , orderStatus,totalAmt;
    CardView QRCodeImageCard;
    ImageView QRCodeImage;
    ImageButton generateQR,doneBtn;
    TextView readyBikeName,startTimeReady,endTimeReady;
    RecyclerView accessoryList;
    TextView generateQRTV ;
    String bikeName;
  ArrayList<String> accessoryNames;


    int flag; // 1:Payment Done 2:QR Code Generated
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
        setContentView(R.layout.activity_delivery_client);

        Checkout.preload(getApplicationContext());

        bindViews();

        if(Common.currentRequest!=null) {
          startTimeReady.setText(Common.currentRequest.getStartDate());
          endTimeReady.setText(Common.currentRequest.getEndDate());
        }
        setOnClicks();

        //Setting Recycler View for the accessories, if chosen
        if(Common.currentRequest!=null && Common.currentRequest.getOrderList()!=null && Common.currentRequest.getOrderList().size()>1){
          accessoryNames = new ArrayList<>();
          accessoryList.setHasFixedSize(true);
          accessoryList.setLayoutManager(new LinearLayoutManager(this));

          for(int i=1 ;i<Common.currentRequest.getOrderList().size();i++)
            accessoryNames.add(Common.currentRequest.getOrderList().get(i).getProductName());
          AccessoryDisplayAdapter adapter = new AccessoryDisplayAdapter(accessoryNames,this);
          adapter.notifyDataSetChanged();
          accessoryList.setAdapter(adapter);
        }
        intent = getIntent();
        if(intent!=null){
            outstandingAmt = intent.getStringExtra("outstandingAmt");
            orderId = intent.getStringExtra("orderId");
            orderStatus= intent.getStringExtra("orderStatus");
            totalAmt = intent.getStringExtra("totalAmt");
          bikeName = intent.getStringExtra("bikeName");
          if(bikeName!=null)
            readyBikeName.setText(bikeName);
//            if(Integer.parseInt(outstandingAmt)==0)
//            {
//                payTV.setText("Paid");
//                payOutstanding.setClickable(false);
//                outstandingAmtValue.setText("0");
//            }
//            else
//                outstandingAmtValue.setText(outstandingAmt);
        }

    }

    private void setOnClicks() {
        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String data_in_code;
                    if (Common.currentUser != null && Common.currentUser.getName() != null) {
                      data_in_code = Common.currentUser.getName() + " is authorised to take the vehicle ";
                      flag=2;
                    }
                    else
                        data_in_code = "Invalid User. Do not give the vehicle";
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(data_in_code, BarcodeFormat.QR_CODE, 300, 300);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        QRCodeImage.setImageBitmap(bitmap);
                        QRCodeImageCard.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              FirebaseDatabase.getInstance().getReference("Requests").child(orderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if(flag==2)
                  {
                    Intent i = new Intent(DeliveryClient.this , ActiveJourney.class);
                    i.putExtra("totalAmt",totalAmt);
                    i.putExtra("orderId",orderId);
                    i.putExtra("orderStatus",orderStatus);
                    i.putExtra("bikeName",bikeName);
                    startActivity(i);
                    finish();
                  }
                  else
                    Toast.makeText(DeliveryClient.this, "Let the executive verify you.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                  Toast.makeText(DeliveryClient.this, "Not verified. Try again.", Toast.LENGTH_SHORT).show();

                }
              });

            }
        });
    }
    private void bindViews() {
        QRCodeImageCard=findViewById(R.id.QRCodeImageCard);
         QRCodeImage=findViewById(R.id.QRCodeImage);
         generateQR=findViewById(R.id.generateQR);
         doneBtn=findViewById(R.id.doneBtn);
         readyBikeName=findViewById(R.id.readyBikeName);
         startTimeReady=findViewById(R.id.startTimeReady);
         endTimeReady=findViewById(R.id.endTimeReady);
         accessoryList=findViewById(R.id.accessoryList);
        generateQRTV = findViewById(R.id.generateQRTV);

    }
}
