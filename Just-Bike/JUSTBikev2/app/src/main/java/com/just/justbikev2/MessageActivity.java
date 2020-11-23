package com.just.justbikev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.just.justbikev2.Adapter.MessageAdapter;
import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Database.Database;
import com.just.justbikev2.Model.Chat;
import com.just.justbikev2.Model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    TextView userName;
    ImageView profileImage;
     Intent i;
    DatabaseReference reference;
    String userPhone;

    EditText typedMessage;
    ImageButton btn_send;
    UserModel userModel;

    MessageAdapter adapter;

    List<Chat> chats;
    RecyclerView messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
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
        i = getIntent();
        if(i!=null)
            userPhone = i.getStringExtra("phone");

        reference = FirebaseDatabase.getInstance().getReference("User").child(userPhone);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 userModel = dataSnapshot.getValue(UserModel.class);
                userName.setText(userModel.getName());
                Picasso.get().load(userModel.getProfilePic()).into(profileImage);
                readMessages(Common.currentUser.getPhone(),userPhone ,userModel.getProfilePic());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = typedMessage.getText().toString();
                if(msg!=null && !msg.equals("")){
                    sendMessage(Common.currentUser.getPhone() , userModel.getPhone(),msg);
                }
                else
                    Toast.makeText(MessageActivity.this, "Please Add Content to your message!", Toast.LENGTH_SHORT).show();
                typedMessage.setText("");

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

    private void readMessages(String myId , String userId , String imageUrl){
        chats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    Chat chatmsg = data.getValue(Chat.class);
                    if((chatmsg.getSender().equals(myId) && chatmsg.getReceiver().equals(userId)) ||
                            (chatmsg.getReceiver().equals(myId) && chatmsg.getSender().equals(userId)))
                        chats.add(chatmsg);
                    adapter = new MessageAdapter(chats,MessageActivity.this  , imageUrl);
                    adapter.notifyDataSetChanged();
                    messages.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
