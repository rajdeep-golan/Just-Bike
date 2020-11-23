package com.just.justbikev2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Model.PhotoUpload;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoUploadAdapter extends RecyclerView.Adapter<PhotoUploadAdapter.PhotoUploadVH> {

    private Context context;
    private List<PhotoUpload> photoUploadList;

    public PhotoUploadAdapter(Context context, List<PhotoUpload> photoUploadList) {
        this.context = context;
        this.photoUploadList = photoUploadList;
    }

    @NonNull
    @Override
    public PhotoUploadVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.upload_photo_card_view,parent , false);
        return new PhotoUploadVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoUploadVH  holder, int position) {
        PhotoUpload currentPhoto = photoUploadList.get(position);
        holder.aboutPhoto.setText(currentPhoto.getName());
        Picasso.get().load(currentPhoto.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return photoUploadList.size();
    }
    public class PhotoUploadVH extends RecyclerView.ViewHolder{
        TextView aboutPhoto;
        ImageView photo;

        public PhotoUploadVH(@NonNull View itemView) {
            super(itemView);
            aboutPhoto = itemView.findViewById(R.id.upload_name_RV);
            photo = itemView.findViewById(R.id.upload_image_RV);
        }
    }
}
