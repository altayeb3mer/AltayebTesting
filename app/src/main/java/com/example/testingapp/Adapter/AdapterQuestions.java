package com.example.testingapp.Adapter;


import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testingapp.Activity.QuestionDetailsActivity;
import com.example.testingapp.Model.ModelQuestion;
import com.example.testingapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterQuestions extends RecyclerView.Adapter<AdapterQuestions.ViewHolder> {

    ArrayList<ModelQuestion> modelQuestionArrayList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity activity;
    public AdapterQuestions(Activity activity, ArrayList<ModelQuestion> newsPaperArrayList) {
        this.mInflater = LayoutInflater.from(activity);
        this.modelQuestionArrayList = newsPaperArrayList;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.q_items, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ModelQuestion item = modelQuestionArrayList.get(position);
        holder.textView_name.setText(item.getOwnerName());
        holder.textView_title.setText(item.getTitle());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, QuestionDetailsActivity.class);
                intent.putExtra("id",item.getId());
                intent.putExtra("isAnswered",item.isAnswered());
                intent.putExtra("views",item.getViews());
                intent.putExtra("answerCount",item.getAnswerCount());
                intent.putExtra("creation",item.getCreatedAt());

                intent.putExtra("img",item.getImgUrl());
                intent.putExtra("name",item.getOwnerName());
                intent.putExtra("title",item.getTitle());
                activity.startActivity(intent);
            }
        });

        try {
            Glide.with(activity).load(item.getImgUrl()).into(holder.imageView);
        }catch (Exception e){
            holder.imageView.setBackgroundResource(R.drawable.account_circle);
        }

    }


    @Override
    public int getItemCount() {
        return modelQuestionArrayList.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView_name, textView_title;
        CircleImageView imageView;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.owner);
            textView_title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.img);
            cardView = itemView.findViewById(R.id.cardContainer);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


}

