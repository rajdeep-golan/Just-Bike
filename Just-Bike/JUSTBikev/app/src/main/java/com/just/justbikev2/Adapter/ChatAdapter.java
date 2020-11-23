package com.just.justbikev2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.MessageActivity;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    public List<UserModel> users;
    public Context context;

    public ChatAdapter(List<UserModel> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_card,parent,false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        UserModel user  = users.get(position);
        holder.userName.setText(user.getName());
        Picasso.get().load(user.getProfilePic()).into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context, MessageActivity.class);
                i.putExtra("phone",user.getPhone());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public ImageView profileImage;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
