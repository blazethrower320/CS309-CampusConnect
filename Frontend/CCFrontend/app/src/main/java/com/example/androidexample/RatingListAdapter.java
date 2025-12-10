package com.example.androidexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.RatingViewHolder> {
    private final List<RatingItem> ratings;
    private final Context context;

    public RatingListAdapter(Context context, List<RatingItem> ratings) {
        this.context = context;
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false);
        return new RatingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        RatingItem item = ratings.get(position);
        holder.username.setText(item.username);
        holder.comment.setText(item.comment);
        holder.ratingBar.setRating((float) item.rating);
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView username, comment;
        RatingBar ratingBar;
        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.rating_user);
            comment = itemView.findViewById(R.id.rating_comment);
            ratingBar = itemView.findViewById(R.id.user_rating_bar);
        }
    }
}
