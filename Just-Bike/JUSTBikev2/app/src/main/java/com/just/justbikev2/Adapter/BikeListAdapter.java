package com.just.justbikev2.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.just.justbikev2.BikeListScreen;
import com.just.justbikev2.Model.Vehicle;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BikeListAdapter extends RecyclerView.Adapter<BikeListAdapter.VehicleViewHolder> {
    Context context;
    ArrayList<Vehicle> bikeDetailsModels;
    OnClickListener onClickListener;

    public interface OnClickListener{
        void onSelect(int position);
    }


    public BikeListAdapter(Context context, ArrayList<Vehicle> bikeDetailsModels, OnClickListener onClickListener) {
        this.context = context;
        this.bikeDetailsModels = bikeDetailsModels;
        this.onClickListener = onClickListener;
    }




    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bike_card_view,viewGroup,false);
        return new VehicleViewHolder(view,onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int i) {

      Vehicle model = bikeDetailsModels.get(i);
      holder.bikeName.setText(model.getName());
      holder.costTV.setText("Cost : ₹");
      Picasso.get().load(model.getImage1()).into(holder.bikeImage);//.transform(new RoundedCornersTransformation(50,0))
      Picasso.get().load(model.getCompany()).into(holder.company);

      holder.shareIcon.setVisibility(View.INVISIBLE);
      holder.favoriteIcon.setVisibility(View.INVISIBLE);
      BikeListScreen bikeListScreen = BikeListScreen.getInstance();
      holder.costValue.setText(String.valueOf(bikeListScreen.getAmountAsPerTime(model.getCost() + "")));
    }

    @Override
    public int getItemCount() {
        if(bikeDetailsModels==null) return 0;
        return bikeDetailsModels.size();
    }

  public class VehicleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView bikeImage,bikeImageBlur,favoriteIcon,shareIcon , company;
    public TextView mileageValue , costValue , bikeName , colour , ccValue , yearValue , weightValue ;
    public TextView costTV;

    public TextView availableFrom;
    private BikeListAdapter.OnClickListener itemClickListener;

    public void setItemClickListener(BikeListAdapter.OnClickListener itemClickListener) {
      this.itemClickListener = itemClickListener;
    }

    public VehicleViewHolder(@NonNull View itemView , BikeListAdapter.OnClickListener onClickListener) {
      super(itemView);
      this.itemClickListener = onClickListener;
      bikeName = itemView.findViewById(R.id.phoneNo);
      company = itemView.findViewById(R.id.company);
      bikeImage = itemView.findViewById(R.id.bikeImage);
      bikeImageBlur = itemView.findViewById(R.id.bikeImageBlur);
      mileageValue = itemView.findViewById(R.id.mileageValue);
      costValue = itemView.findViewById(R.id.viewDocs);
      colour = itemView.findViewById(R.id.colourValue);
      ccValue = itemView.findViewById(R.id.ccValue);
      yearValue = itemView.findViewById(R.id.yearValue);
      weightValue = itemView.findViewById(R.id.weightValue);
      favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
      shareIcon = itemView.findViewById(R.id.shareIcon);
      costTV =  itemView.findViewById(R.id.name);
      availableFrom = itemView.findViewById(R.id.availableFrom);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      itemClickListener.onSelect(getAdapterPosition());
    }
  }

}
