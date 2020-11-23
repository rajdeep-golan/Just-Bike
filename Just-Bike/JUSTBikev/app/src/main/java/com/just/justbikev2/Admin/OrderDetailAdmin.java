package com.just.justbikev2.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.just.justbikev2.Common.Common;
import com.just.justbikev2.R;

import org.w3c.dom.Text;

public class OrderDetailAdmin extends AppCompatActivity {

    TextView orderId,orderPrice,orderStsus,orderAddress,comment;
    TextView clientName,purpose,selfDrop,selfPickup,noOfHelmets,endDate,startDate,paymentMethod,clientNo,outstandingAmt;
    String orderIdValue = "";
    RecyclerView itemList;
    RecyclerView.LayoutManager layoutManager;

  String contactNo = "+918585858586";
  private static int CALL_CODE = 34;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_admin);
        bindViews();


        itemList = findViewById(R.id.ItemList);
        itemList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        itemList.setLayoutManager(layoutManager);

        if(getIntent()!=null){
            orderIdValue = getIntent().getStringExtra("orderId");
        }

        setValues();


        OrderDetailAdpaterAdmin adpater = new OrderDetailAdpaterAdmin(Common.currentRequest.getOrderList());
        adpater.notifyDataSetChanged();
        itemList.setAdapter(adpater);
    }

  private void askForCallPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_CODE);
  }

  public void callUs() {

    Intent callIntent = new Intent(Intent.ACTION_CALL);
    callIntent.setData(Uri.parse("tel:" + contactNo));

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
      askForCallPermission();
    else
      startActivity(callIntent);

  }

  @SuppressLint("MissingPermission")
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == CALL_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contactNo));
        startActivity(callIntent);
      } else
        Toast.makeText(this, "Please accept calling permission before making a Call", Toast.LENGTH_SHORT).show();
    }

  }

  private void setValues() {

    contactNo = Common.currentRequest.getPhone();
    orderId.setText(orderIdValue);
    orderStsus.setText("Order Status: "+Common.currentRequest.getStatus());
    orderPrice.setText("Total: "+Common.currentRequest.getTotal());
    comment.setText("Comment :"+Common.currentRequest.getComment());
    orderAddress.setText(Common.currentRequest.getAddress());
    clientNo.setText(Common.currentRequest.getPhone());
    clientName.setText(Common.currentRequest.getName());
    outstandingAmt.setText("Oustanding Amount: "+Common.currentRequest.getOutstandingAmt());
    purpose.setText(Common.currentRequest.getPurpose());

    if(Common.currentRequest.getSelfDrop()==1)  //He will drop it to us
     selfDrop.setText("Customer will drop back");
    else if(Common.currentRequest.getSelfDrop()==0)
      selfDrop.setText("We have to Pickup");

    if(Common.currentRequest.getSelfPickup()==1)  //He will take it from us
      selfPickup.setText("Customer will come and take");
    else if(Common.currentRequest.getSelfPickup()==0)
      selfPickup.setText("We have to Deliver");

    noOfHelmets.setText("No. of free helmets: "+Common.currentRequest.getNoOfHelmets());
    endDate.setText("End Date: "+Common.currentRequest.getEndDate());
    startDate.setText("Start Date: "+Common.currentRequest.getStartDate());
    paymentMethod.setText(Common.currentRequest.getPaymentMethod());

    clientNo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callUs();
      }
    });
  }

  private void bindViews() {
      orderAddress = findViewById(R.id.orderAddress);
    orderId=findViewById(R.id.orderId);
    orderPrice=findViewById(R.id.orderPrice);
    orderStsus=findViewById(R.id.orderStatus);
    comment=findViewById(R.id.commentTV);
    outstandingAmt = findViewById(R.id.outstandingAmt);
    clientName=findViewById(R.id.clientName);
    purpose=findViewById(R.id.purpose);
    selfDrop=findViewById(R.id.selfDrop);
    selfPickup=findViewById(R.id.selfPickup);
    noOfHelmets=findViewById(R.id.noOfHelmets);
    endDate=findViewById(R.id.endDate);
    startDate=findViewById(R.id.startDate);
    paymentMethod=findViewById(R.id.paymentMethod);
    clientNo=findViewById(R.id.clientNo);
  }
}
