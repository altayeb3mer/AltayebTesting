package com.example.testingapp.Adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testingapp.Model.ModelAnswer;
import com.example.testingapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterAnswer extends RecyclerView.Adapter<AdapterAnswer.ViewHolder> {

    ArrayList<ModelAnswer> answerArrayList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity activity;

    public AdapterAnswer(Activity activity, ArrayList<ModelAnswer> newsPaperArrayList) {
        this.mInflater = LayoutInflater.from(activity);
        this.answerArrayList = newsPaperArrayList;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.answered_by, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ModelAnswer item = answerArrayList.get(position);

        holder.textView_name.setText(item.getName());

        try {
            Glide.with(activity).load(item.getImgUrl()).into(holder.imageView);
        } catch (Exception e) {
            holder.imageView.setBackgroundResource(R.drawable.account_circle);
        }

    }


    @Override
    public int getItemCount() {
        return answerArrayList.size();
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
        TextView textView_name;
        CircleImageView imageView;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.owner);
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

