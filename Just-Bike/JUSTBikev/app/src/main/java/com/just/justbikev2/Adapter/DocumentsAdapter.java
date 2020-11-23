package com.just.justbikev2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.justbikev2.Model.DocumentsModel;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.ViewHolder> implements View.OnClickListener {
    List<DocumentsModel> onBordingLists;
    Context context;

    public DocumentsAdapter(List<DocumentsModel> onBordingLists, Context context) {
        this.onBordingLists = onBordingLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_documents_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentsModel ob=onBordingLists.get(position);
        Glide.with(context)
                .load(ob.getImage())
                .into(holder.imageView);
        holder.name.setText(ob.getName());
    }

    @Override
    public int getItemCount() {
        return onBordingLists.size();
    }

    @Override
    public void onClick(View v) {


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.onbording_image);
            name=itemView.findViewById(R.id.txt_title);
        }
    }
}

