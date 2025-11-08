package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

/**
 * Adapter for the RecyclerView to display a list of past sessions.
 * This adapter works directly with a List of JSONObjects.
 */
public class PastSessionAdapter extends RecyclerView.Adapter<PastSessionAdapter.SessionViewHolder> {

    // UPDATED: The adapter now holds a list of JSONObjects.
    private List<JSONObject> sessionList;

    public PastSessionAdapter(List<JSONObject> sessionList) {
        this.sessionList = sessionList;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_session, parent, false);
        return new SessionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        // Get the JSONObject for the current row.
        JSONObject currentSessionObject = sessionList.get(position);

        // UPDATED: Extract data directly from the JSONObject here.
        String title = currentSessionObject.optString("className", "No Title");
        String course = currentSessionObject.optString("classCode", "N/A");
        String date = currentSessionObject.optString("dateCreated", "N/A");

        // Set the text for each TextView.
        holder.titleTextView.setText(title);
        holder.courseTextView.setText(course);
        holder.dateTextView.setText(date);
    }

    @Override
    public int getItemCount() {
        return sessionList != null ? sessionList.size() : 0;
    }

    /**
     * The ViewHolder class. It holds and caches the UI components for a single row.
     */
    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView courseTextView;
        public TextView dateTextView;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            // These IDs MUST match the IDs in your item_past_session.xml
            titleTextView = itemView.findViewById(R.id.session_course_name_text);
            courseTextView = itemView.findViewById(R.id.session_course_id);
            dateTextView = itemView.findViewById(R.id.session_date_text);
        }
    }
}
