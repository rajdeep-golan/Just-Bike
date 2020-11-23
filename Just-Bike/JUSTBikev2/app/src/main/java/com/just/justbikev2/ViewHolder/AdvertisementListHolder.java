package com.just.justbikev2.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.just.justbikev2.R;
public class AdvertisementListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
   public ImageView gifImage;
   public TextView gifText;


//    @NonNull
//    @Override
//    public AdvertisementListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.advertisement_gif_card,null);
//        return new AdvertisementListHolder(view);
//    }


        public AdvertisementListHolder(@NonNull View itemView) {
            super(itemView);
            this.gifImage = itemView.findViewById(R.id.gifImage);
            this.gifText = itemView.findViewById(R.id.gifText);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
