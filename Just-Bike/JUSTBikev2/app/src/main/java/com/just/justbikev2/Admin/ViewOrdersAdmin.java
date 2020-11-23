package com.just.justbikev2.Admin;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Interface.ItemClickListener;
import com.just.justbikev2.Model.MyResponse;
import com.just.justbikev2.Model.Notification;
import com.just.justbikev2.Model.Request;
import com.just.justbikev2.Model.Sender;
import com.just.justbikev2.Model.Token;
import com.just.justbikev2.OrderNavClient;
import com.just.justbikev2.R;
import com.just.justbikev2.Remote.APIService;
import com.just.justbikev2.TrackingOrderList;
import com.just.justbikev2.ViewOrders;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrdersAdmin extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<Request, OrderVHAdmin> adapter;

    Spinner spinner;

    APIService mService;
    ImageButton reachedVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        Paper.init(this);

        recyclerView = findViewById(R.id.orderList);
        recyclerView.setHasFixedSize(true);

        layoutManager= new LinearLayoutManager(ViewOrdersAdmin.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);

        //Init Service
        mService = Common.getFCMService();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Requests");

        if(Common.isConnectedToInternet(this))
        {
            loadOrder();
            checkStartTrip();
        }
        else{
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void checkStartTrip() {
        Paper.init(this);
//        Paper.book().delete(Common.TRIP_START);
//        Paper.book().delete("orderData");
        if(!TextUtils.isEmpty(Paper.book().read(Common.TRIP_START)))
            startActivity(new Intent(this,OrderNav.class));
    }

    private void loadOrder() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Requests")
                .limitToLast(10000);

        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(query, Request.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderVHAdmin>(options) {

            @Override
            protected void onBindViewHolder(@NonNull OrderVHAdmin orderVH, final int i, final @NonNull Request category) {

                orderVH.orderId.setText(adapter.getRef(i).getKey());    //To get the orderId , i.e. key of each entry in the Requests table
                orderVH.address.setText(category.getAddress());
                orderVH.price.setText(category.getTotal());
                orderVH.status.setText(Common.convertCodeToStatus(category.getStatus()));
              orderVH.clientNo.setText(category.getPhone());
              orderVH.clientName.setText(category.getName());
              orderVH.endDate.setText("End Date: "+category.getEndDate());
              orderVH.startDate.setText("Start Date: "+category.getStartDate());

              orderVH.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.currentRequest = category;
                        showUpdateDialog(adapter.getRef(i).getKey(),adapter.getItem(i),category.getOutstandingAmt());
                    }
                });
                orderVH.detailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetails = new Intent(ViewOrdersAdmin.this, OrderDetailAdmin.class);
                        Common.currentRequest = category;
                        orderDetails.putExtra("orderId",adapter.getRef(i).getKey());
//                        intent.putExtra("CategoryId",adapter.getRef(i).getKey());
                        startActivity(orderDetails);
                    }
                });
                orderVH.directionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(ViewOrdersAdmin.this, TrackingOrder.class);
                        Paper.book().write("orderData",new Gson().toJson(category.getOrderList().get(0)));
                       if(category.getEndDate() !=null)
                           Paper.book().write("EndTime",category.getEndDate());
                        if(category.getAddress() !=null)
                            Paper.book().write("address",category.getAddress());
                        if(category.getPhone() !=null)
                            Paper.book().write("phoneNumber",category.getPhone());

                        Intent intent = new Intent(ViewOrdersAdmin.this, OrderNavClient.class);
                        Common.currentRequest = category;
                        intent.putExtra("isFromAdmin","1");
                        intent.putExtra("orderId",adapter.getRef(i).getKey());
//                        intent.putExtra("CategoryId",adapter.getRef(i).getKey());
                        startActivity(intent);
                    }
                });
                orderVH.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      AlertDialog.Builder alert = new AlertDialog.Builder(ViewOrdersAdmin.this);
                      alert.setTitle("Are you sure you want to delete this order permanently?");
                      LayoutInflater inflater = ViewOrdersAdmin.this.getLayoutInflater();
                      final View view = inflater.inflate(R.layout.update_order_admin,null);

                       view.findViewById(R.id.orderStatusSpinner).setVisibility(View.GONE);
                      view.findViewById(R.id.reachedVerify).setVisibility(View.GONE);
                      alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                          deleteOrder(adapter.getRef(i).getKey());
                        }
                      });
                      alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();

                        }
                      });
                      alert.show();
                    }
                });
//                Picasso.get().load(category.getBikeImageSmall()).into(orderVH.image);
//                final Request clickItem = category;
//                orderVH.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//                        if(!isLongClick){
//                            Intent intent = new Intent(ViewOrdersAdmin.this, TrackingOrder.class);
//                            Common.currentRequest = category;
////                        intent.putExtra("CategoryId",adapter.getRef(i).getKey());
//                            startActivity(intent);
//                        }
//                   /*    Commented because we can't see the context menu due to Long Click
//                    else{
//                            Intent orderDetails = new Intent(ViewOrdersAdmin.this, OrderDetailAdmin.class);
//                            Common.currentRequest = category;
//                            orderDetails.putExtra("orderId",adapter.getRef(position).getKey());
////                        intent.putExtra("CategoryId",adapter.getRef(i).getKey());
//                            startActivity(orderDetails);
//                        } */
//
//                    }
//                });
            }

            @NonNull
            @Override
            public OrderVHAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_list_card_admin, parent, false);

                return new OrderVHAdmin(view);
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
       if(adapter!=null) adapter.startListening();
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        if(item.getTitle().equals("Update"))
//            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
//        else if(item.getTitle().equals("Delete "))
//            deleteOrder(adapter.getRef(item.getOrder()).getKey());
//        return super.onContextItemSelected(item);
//    }

    private void deleteOrder(String key) {
        databaseReference.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(String key, final Request item, String outstandingAmt) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Update Order");
        alert.setMessage("Please choose Status");
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.update_order_admin,null);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.order_status, android.R.layout.simple_spinner_item);

        spinner = v.findViewById(R.id.orderStatusSpinner);
        reachedVerify =  v.findViewById(R.id.reachedVerify);
        reachedVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewOrdersAdmin.this,Delivery.class);
                i.putExtra("orderId",key);
                i.putExtra("outstandingAmt",outstandingAmt);
                startActivity(i);
            }
        });
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapterSpinner);

        alert.setView(v);
        final String localKey = key;

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(positionOfSpinner));
                databaseReference.child(localKey).setValue(item);
                adapter.notifyDataSetChanged();

                sendOrderStatusToUser(localKey,item);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alert.show();

    }
    int positionOfSpinner = 0;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        positionOfSpinner = pos;
        String selected = parent.getItemAtPosition(pos).toString();
        if(pos!=0)
            Toast.makeText(this, "New Status :"+selected, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    private void sendOrderStatusToUser(String localKey, Request item) {
        DatabaseReference tokens = firebaseDatabase.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postData : dataSnapshot.getChildren()){
                    Token token = postData.getValue(Token.class);
                    //Making raw payload
                    Notification notification = new Notification("Your order "+localKey+" was updated!","JUST Bike");
                    Sender content = new Sender(token.getToken(),notification);

                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.body().success == 1)
                                Toast.makeText(ViewOrdersAdmin.this, "Order was updated", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(ViewOrdersAdmin.this, "Order was updated but failed to inform Customer!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("Error",t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
