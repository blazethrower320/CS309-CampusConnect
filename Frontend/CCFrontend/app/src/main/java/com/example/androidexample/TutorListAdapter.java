package com.example.androidexample;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.TutorViewHolder> implements Filterable {

    private List<TutorItem> tutorList;
    private List<TutorItem> tutorListFull; // copy for filtering
    private OnTutorClickListenerWithReviews listener;
    private Context context;

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String TAG = "TutorListAdapter";

    public TutorListAdapter(Context context, List<TutorItem> tutorList, OnTutorClickListenerWithReviews listener) {
        this.context = context;
        this.tutorList = tutorList;
        this.tutorListFull = new ArrayList<>(tutorList);
        this.listener = listener;
    }
    //Commentssssss

    public interface OnTutorClickListenerWithReviews extends OnTutorClickListener {
        void onReviewsClicked(TutorItem tutor);
    }


    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // context is already set, no need to reassign
        View view = LayoutInflater.from(context).inflate(R.layout.item_tutor, parent, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        TutorItem tutor = tutorList.get(position);
        holder.usernameText.setText("@" + tutor.username);
        holder.ratingValue.setText(String.format("%.1f", tutor.rating));

        // If first/last name are already set, display them
        if (tutor.displayName != null && !tutor.displayName.isEmpty()) {
            holder.nameText.setText(tutor.displayName);
        } else {
            // Otherwise fetch from /users/getTutor/{id}
            fetchTutorName(tutor, holder);
        }

        holder.itemView.setOnClickListener(v -> listener.onTutorClicked(tutor));
        holder.reviewsButton.setOnClickListener(v -> {
            if (listener instanceof OnTutorClickListenerWithReviews) {
                ((OnTutorClickListenerWithReviews) listener).onReviewsClicked(tutor);
            }
        });

    }

    private void fetchTutorName(TutorItem tutor, TutorViewHolder holder) {
        String url = BASE_URL + "/tutor/info/" + tutor.tutorId;
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Handle either a flat or nested user object
                        String firstName = "";
                        String lastName = "";

                        if (response.has("firstName")) {
                            firstName = response.optString("firstName", "");
                            lastName = response.optString("lastName", "");
                        } else if (response.has("user")) {
                            // Handle case where tutor object contains a nested user
                            firstName = response.getJSONObject("user").optString("firstName", "");
                            lastName = response.getJSONObject("user").optString("lastName", "");
                        }

                        String fullName = (firstName + " " + lastName).trim();
                        tutor.displayName = fullName.isEmpty() ? tutor.username : fullName;

                        holder.nameText.setText(tutor.displayName);

                        // Update full list for search filtering
                        updateFullList();

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing tutor name: " + e.getMessage());
                        holder.nameText.setText(tutor.username);
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to fetch tutor info for ID " + tutor.tutorId + ": " + error.toString());
                    holder.nameText.setText(tutor.username);
                }
        );

        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    @Override
    public Filter getFilter() {
        return tutorFilter;
    }

    private final Filter tutorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<TutorItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tutorListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (TutorItem item : tutorListFull) {
                    if ((item.displayName != null && item.displayName.toLowerCase().contains(filterPattern))
                            || (item.username != null && item.username.toLowerCase().contains(filterPattern))) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tutorList.clear();
            tutorList.addAll((List<TutorItem>) results.values);
            notifyDataSetChanged();
        }
    };

    public void updateFullList() {
        tutorListFull = new ArrayList<>(tutorList);
    }

    // Add this method to update both lists when data changes
    public void updateTutorList(List<TutorItem> newList) {
        tutorList.clear();
        tutorList.addAll(newList);
        tutorListFull.clear();
        tutorListFull.addAll(newList);
        notifyDataSetChanged();
    }

    static class TutorViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, usernameText, reviewsButton, ratingValue;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.tutor_name);
            usernameText = itemView.findViewById(R.id.tutor_username);
            reviewsButton = itemView.findViewById(R.id.btn_reviews);
            ratingValue = itemView.findViewById(R.id.tv_rating_value);
        }

    }

    public interface OnTutorClickListener {
        void onTutorClicked(TutorItem tutor);
    }
}
