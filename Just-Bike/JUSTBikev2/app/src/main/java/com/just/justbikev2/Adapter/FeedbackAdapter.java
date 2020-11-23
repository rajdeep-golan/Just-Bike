package com.just.justbikev2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.justbikev2.Common.Common;
import com.just.justbikev2.Model.Feedback;
import com.just.justbikev2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackVH> {
  public static final int MSG_TYPE_LEFT = 0;
  public static final int MSG_TYPE_RIGHT = 1;

  public List<Feedback> feedbacks;
  public Context context;

  public FeedbackAdapter(List<Feedback> feedbacks, Context context) {
    this.feedbacks = feedbacks;
    this.context = context;
  }

  @NonNull
  @Override
  public FeedbackVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v;
    if(viewType == MSG_TYPE_LEFT)
      v = LayoutInflater.from(context).inflate(R.layout.feedback_card,parent,false);
    else
      v = LayoutInflater.from(context).inflate(R.layout.feedback_right_card,parent,false);

    return new FeedbackVH(v);
  }

  @Override
  public void onBindViewHolder(@NonNull FeedbackVH holder, int position) {
    Feedback feedback = feedbacks.get(position);
    if(feedback!=null){
      Picasso.get().load(feedback.getImage()).placeholder(R.drawable.biker2).into(holder.image);
      holder.feedbackTV.setText(feedback.getFeedback());
    }
  }

  @Override
  public int getItemCount() {
    return feedbacks.size();
  }
  public class FeedbackVH extends RecyclerView.ViewHolder {
    CircleImageView image;
    TextView feedbackTV;
    public FeedbackVH(@NonNull View itemView) {
      super(itemView);
      image = itemView.findViewById(R.id.custImage);
      feedbackTV = itemView.findViewById(R.id.feedback);
    }

    }
  @Override
  public int getItemViewType(int position) {
    if(position%2==0) //For position 0,2,4..(even)
      return MSG_TYPE_RIGHT;
    return MSG_TYPE_LEFT; //For odd 1,3,5...(odd)
  }
  }

