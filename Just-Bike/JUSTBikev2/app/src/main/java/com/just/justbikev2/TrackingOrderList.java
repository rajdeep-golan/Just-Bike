package com.just.justbikev2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackingOrderList extends AppCompatActivity {
    View view_order_placed,view_order_confirmed,view_order_processed,view_order_pickup,con_divider,ready_divider,placed_divider;
    ImageView img_orderconfirmed,orderprocessed,orderpickup;
    TextView textorderpickup,text_confirmed,textorderprocessed;
    String orderNo,orderStatus;
    TextView orderNoTV,homeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order_list);

        bindViews();
        Intent i = getIntent();
        if(i!=null){
            orderNo=i.getStringExtra("orderNo");
             orderStatus=i.getStringExtra("orderStatus");

        }
        getOrderStatus(orderStatus);
        orderNoTV.setText(orderNo);

        homeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackingOrderList.this,Home.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void bindViews() {
        view_order_placed=findViewById(R.id.view_order_placed);
        view_order_confirmed=findViewById(R.id.view_order_confirmed);
        view_order_processed=findViewById(R.id.view_order_processed);
        view_order_pickup=findViewById(R.id.view_order_pickup);
        placed_divider=findViewById(R.id.placed_divider);
        con_divider=findViewById(R.id.con_divider);
        ready_divider=findViewById(R.id.ready_divider);

        textorderpickup=findViewById(R.id.textorderpickup);
        text_confirmed=findViewById(R.id.text_confirmed);
        textorderprocessed=findViewById(R.id.textorderprocessed);

        img_orderconfirmed=findViewById(R.id.img_orderconfirmed);
        orderprocessed=findViewById(R.id.orderprocessed);
        orderpickup=findViewById(R.id.orderpickup);
        orderNoTV = findViewById(R.id.orderNo);
        homeTV = findViewById(R.id.homeTV);
    }

    private void getOrderStatus(String orderStatus) {
        if (orderStatus.equals("0")){
            float alfa= (float) 0.3;
            setStatus(alfa);

        }else if (orderStatus.equals("1")){
            float alfa= (float) 1;
            setStatus1(alfa);



        }else if (orderStatus.equals("2")){
            float alfa= (float) 1;
            setStatus2(alfa);


        }else if (orderStatus.equals("3")){
            float alfa= (float) 1;
            setStatus3(alfa);
        }
    }


    private void setStatus(float alfa) {
        float myf= (float) 0.3;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        placed_divider.setAlpha(alfa);
        img_orderconfirmed.setAlpha(alfa);
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderpickup.setAlpha(alfa);

        textorderpickup.setAlpha(myf);




    }

    private void setStatus1(float alfa) {
        float myf= (float) 0.3;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(myf);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        img_orderconfirmed.setAlpha(alfa);

        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(myf);
        view_order_pickup.setAlpha(myf);
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderpickup.setAlpha(myf);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        textorderpickup.setAlpha(myf);
    }

    private void setStatus2(float alfa) {
        float myf= (float) 0.3;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        img_orderconfirmed.setAlpha(alfa);

        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        textorderpickup.setAlpha(myf);
        orderpickup.setAlpha(myf);

    }

    private void setStatus3(float alfa) {
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));

        img_orderconfirmed.setAlpha(alfa);
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        textorderpickup.setAlpha(alfa);
        orderpickup.setAlpha(alfa);
    }

  @Override
  public void onBackPressed() {
    Intent i = new Intent(TrackingOrderList.this,Home.class);
    startActivity(i);
  }
}
