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

import com.just.justbikev2.Common.Common;
import com.just.justbikev2.FullScreenImageActivity;
import com.just.justbikev2.MessageActivity;
import com.just.justbikev2.Model.Chat;
import com.just.justbikev2.Model.UserModel;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ChatViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public List<Chat> chats;
    public Context context;
    public String imageUrl;

    public MessageAdapter(List<Chat> chats, Context context, String imageUrl) {
        this.chats = chats;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType == MSG_TYPE_LEFT)
         v = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
        else
            v = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);

        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat  = chats.get(position);
        if(chat.getMedia()==null) {     //Only text
            holder.messgeTV.setText(chat.getMessage());
            holder.messageImage.setVisibility(View.GONE);
        }
        else {          //Media is sent
            holder.messgeTV.setVisibility(View.GONE);
            Picasso.get().load(chat.getMedia()).into(holder.messageImage);
            holder.messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fullScreenIntent = new Intent(context, FullScreenImageActivity.class);
                    fullScreenIntent.putExtra("imageUrl",chat.getMedia());
                    context.startActivity(fullScreenIntent);
                }
            });
        }
        Picasso.get().load(imageUrl).into(holder.profileImageChat);

        if(chat.getTime()!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String time = formatter.format(Long.parseLong(chat.getTime()));

            holder.messageTime.setText(time);
        }



    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public TextView messgeTV;
        public ImageView profileImageChat;
        public ImageView messageImage;
        public TextView messageTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messgeTV = itemView.findViewById(R.id.messgeTV);
            profileImageChat = itemView.findViewById(R.id.profileImageChat);
            messageImage = itemView.findViewById(R.id.messageImage);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(chats.get(position).getSender().equals(Common.currentUser.getPhone()))
            return MSG_TYPE_RIGHT;
        return MSG_TYPE_LEFT;
    }
}
