package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.ViewHolder.OrderVH;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class ViewOrders extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<Request, OrderVH> adapter;

    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        } setContentView(R.layout.activity_view_orders);

        recyclerView = findViewById(R.id.orderList);
        recyclerView.setHasFixedSize(true);

        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Requests");

        if(Common.isConnectedToInternet(this) && Common.currentUser!=null)
        {
            if(getIntent().getExtras()==null)
              loadOrder(Common.currentUser.getPhone());
            else
                loadOrder(getIntent().getStringExtra("userPhone"));
        }
        else if(!Common.isConnectedToInternet(this)){
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }else{
          Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
          Intent i = new Intent(ViewOrders.this,SignInAcitivity.class);
          startActivity(i);
          finish();
        }
    }
    Request category;
    private void loadOrder(String phone) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Requests")
                .orderByChild("phone")
                .equalTo(phone)
                .limitToLast(50);

        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(query, Request.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderVH>(options) {

            @Override
            protected void onBindViewHolder(@NonNull OrderVH orderVH, final int i, @NonNull final Request category) {
                orderVH.orderId.setText(adapter.getRef(i).getKey());    //To get the orderId , i.e. key of each entry in the Requests table
                orderVH.address.setText(category.getAddress());
                orderVH.price.setText("₹ "+category.getTotal());
                orderVH.status.setText(Common.convertCodeToStatus(category.getStatus()));
                if(category.getOrderList()!=null && category.getOrderList().get(0) !=null )
                Picasso.get().load(category.getOrderList().get(0).getImage()).placeholder(R.drawable.biker).into(orderVH.imageView);
               // Picasso.get().load(category.getBikeImageSmall()).into(orderVH.image);
                  orderVH.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      android.app.AlertDialog.Builder alertdialog = new AlertDialog.Builder(ViewOrders.this);
                      alertdialog.setTitle("Cancel Booking ?");
                      alertdialog.setMessage("If you wish to drop the order, you would be refunded with 20 percent of the booking amount in your Jbida Wallet. Once cancelled , action cannot be revoked. However , you can place another order as per the availability of the vehicle");

                      LayoutInflater inflater = LayoutInflater.from(ViewOrders.this);
                      View layout_home = inflater.inflate(R.layout.add_address_card, null);
                      layout_home.findViewById(R.id.addressLayout).setVisibility(View.GONE);
                      alertdialog.setView(layout_home);
                      alertdialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          Map<String,Object> putMap = new HashMap<>();
                          putMap.put("status","5");
                          if(category.getStatus().equals("0") || category.getStatus().equals("1") ) { //Only when it is just placed or still time to reach you.
                            DatabaseReference requestChild = FirebaseDatabase.getInstance().getReference("Requests").child(adapter.getRef(i).getKey());
                            requestChild.updateChildren(putMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ViewOrders.this, "Order Cancelled!", Toast.LENGTH_SHORT).show();
                              }
                            });
                          }
                          else
                            Toast.makeText(ViewOrders.this, "Cancellation not allowed Now!", Toast.LENGTH_SHORT).show();
                          dialog.dismiss();

                        }
                      });
                      alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                        }
                      });
                      alertdialog.show();
                    }
                  });
                orderVH.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(category.getStatus().equals("2") || category.getStatus().equals("3") )   //Only when the delivery boy has left or has reached the location
                        {
                          Common.currentRequest =category;
                          Intent intent = new Intent(ViewOrders.this, DeliveryClient.class);
                          intent.putExtra("orderId", adapter.getRef(i).getKey());
                          intent.putExtra("outstandingAmt", category.getOutstandingAmt());
                          intent.putExtra("orderStatus", category.getStatus());
                          intent.putExtra("totalAmt", category.getTotal());
                          if(category.getOrderList()!=null && category.getOrderList().size()>0)
                            intent.putExtra("bikeName", category.getOrderList().get(0).getProductName());
                          startActivity(intent);
                        }else
                          Toast.makeText(ViewOrders.this, "Safely pickup and drop!", Toast.LENGTH_SHORT).show();
                    }
                });
                orderVH.detailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.currentRequest = category;
                        Intent intent = new Intent(ViewOrders.this, ActiveJourney.class);
                        intent.putExtra("orderId",adapter.getRef(i).getKey());
                        intent.putExtra("totalAmt",category.getTotal());
                        intent.putExtra("orderStatus",category.getStatus());
                       if(category.getOrderList()!=null && category.getOrderList().size()>0)
                         intent.putExtra("bikeName",category.getOrderList().get(0).getProductName());
                        startActivity(intent);
                    }
                });
                orderVH.directionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      if(category.getStatus().equals("2") )   //Only when the delivery boy has left
                      {
                        FirebaseDatabase.getInstance().getReference("Requests").child(adapter.getRef(i).getKey())
                          .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if (dataSnapshot.exists() && !category.getStatus().equals("0")) {
                                Common.currentRequest = dataSnapshot.getValue(Request.class);
                                if (Common.currentRequest.getCurrentLat() != -1 &&
                                  Common.currentRequest.getCurrentLng() != -1) {
                                  Intent intent = new Intent(ViewOrders.this, OrderNavClient.class);
                                  Paper.book().write("orderData", new Gson().toJson(category.getOrderList().get(0)));
                                  if (category.getEndDate() != null)
                                    Paper.book().write("EndTime", category.getEndDate());
                                  if (category.getAddress() != null)
                                    Paper.book().write("address", category.getAddress());
                                  if (category.getPhone() != null)
                                    Paper.book().write("phoneNumber", category.getPhone());
                                  intent.putExtra("orderId", adapter.getRef(i).getKey());
                                  intent.putExtra("isFromAdmin","1");
                                  Common.currentRequest = category;
//                        intent.putExtra("CategoryId",adapter.getRef(i).getKey());
                                  startActivity(intent);
                                } else
                                  Toast.makeText(ViewOrders.this, "Executive has not started yet! Please Wait.", Toast.LENGTH_SHORT).show();
                              } else {
                                Toast.makeText(ViewOrders.this, "Order just placed! You can see before your vehicle is ready to be delivered!", Toast.LENGTH_LONG).show();
                              }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                              Toast.makeText(ViewOrders.this, "", Toast.LENGTH_SHORT).show();
                            }
                          });
                      }
                      else
                        Toast.makeText(ViewOrders.this, "Only when Executive is reaching out", Toast.LENGTH_SHORT).show();
                    }
               });
            }

            @NonNull
            @Override
            public OrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_list_card, parent, false);

                return new OrderVH(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
          adapter.startListening();
    }
}


