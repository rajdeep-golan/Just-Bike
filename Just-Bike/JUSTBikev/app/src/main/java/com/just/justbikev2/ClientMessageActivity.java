package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.MessageAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Chat;
import com.just.justbikev2.Model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientMessageActivity extends AppCompatActivity {
    TextView userName;
    ImageView profileImage;
    Intent i;
    DatabaseReference reference;

    EditText typedMessage;
    ImageButton btn_send;

    MessageAdapter adapter;

    List<Chat> chats;
    RecyclerView messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_message);
        userName = findViewById(R.id.userNameMessgae);
        profileImage = findViewById(R.id.profileImageMessage);
        btn_send = findViewById(R.id.btn_send);
        typedMessage = findViewById(R.id.typeMessageET);


        messages = findViewById(R.id.messages);
        messages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messages.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolbarMessage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
                userName.setText("Just Bike Care");
                Picasso.get().load(R.drawable.just_black_logo).into(profileImage);
                readMessages(Common.currentUser.getPhone() , "https://firebasestorage.googleapis.com/v0/b/just-bike-6a869.appspot.com/o/just_bike_logo.png?alt=media&token=8aff1b0d-306d-4a44-aea7-c3bdf4efc880");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = typedMessage.getText().toString();
                if(msg!=null && !msg.equals("")){
                    sendMessage(Common.currentUser.getPhone() , "+918972143147",msg);
                    sendMessage(Common.currentUser.getPhone() , "+918585858586",msg);
                    sendMessage(Common.currentUser.getPhone() , "+918877770977",msg);
                  sendMessage(Common.currentUser.getPhone() , "+918877772277",msg);
                  sendMessage(Common.currentUser.getPhone() , "+916289275606",msg);
                  sendMessage(Common.currentUser.getPhone() , "+9193330512322",msg);
                }
                else
                    Toast.makeText(ClientMessageActivity.this, "Please Add Content to your message!", Toast.LENGTH_SHORT).show();
                typedMessage.setText("");

            }
        });
    }
    private void readMessages(String myId  , String imageUrl){
        chats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    Chat chatmsg = data.getValue(Chat.class);
                    //Message from client is sent to all 3 numbers , so any 1 receiver and sender client OR either of 3 numbers have sent client
                    if((chatmsg.getSender().equals(myId) && chatmsg.getReceiver().equals("+918972143147") )  ||
                      (chatmsg.getSender().equals(myId) && chatmsg.getReceiver().equals("+918585858586") ) ||
                  (chatmsg.getSender().equals(myId) && chatmsg.getReceiver().equals("+918877770977"))  ||
                            (chatmsg.getReceiver().equals(myId) && (chatmsg.getSender().equals("+918972143147") ||
                                                                      chatmsg.getSender().equals("+918585858586") ||
                              chatmsg.getSender().equals("+918877772277") ||chatmsg.getSender().equals("+916289275606") ||
                              chatmsg.getSender().equals("+9193330512322") )))
                        chats.add(chatmsg);
                    adapter = new MessageAdapter(chats,ClientMessageActivity.this  , imageUrl);
                    adapter.notifyDataSetChanged();
                    messages.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(String sender , String receiver , String message){

        DatabaseReference chat = FirebaseDatabase.getInstance().getReference("Chat");
        HashMap<String,String> userMap = new HashMap<>();

        userMap.put("sender",sender);
        userMap.put("receiver",receiver);
        userMap.put("message",message);
        userMap.put("time",String.valueOf(System.currentTimeMillis()));
        chat.push().setValue(userMap);
    }
}
