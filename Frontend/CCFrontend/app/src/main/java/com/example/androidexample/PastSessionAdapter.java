package com.example.androidexample; // Use your actual package name

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PastSessionAdapter extends RecyclerView.Adapter<PastSessionAdapter.SessionViewHolder> {

    private final List<Session> sessionList;

    public PastSessionAdapter(List<Session> sessionList)
    {
        this.sessionList = sessionList;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessionList.get(position);
        holder.courseName.setText(session.getClassName());
        holder.sessionDate.setText(null); //TODO add session date
        holder.sessionSummary.setText(session.getSessionId());
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, sessionDate, sessionSummary;

        public SessionViewHolder(@NonNull View itemView)
        {
            super(itemView);
            courseName = itemView.findViewById(R.id.session_course_name_text);
            sessionDate = itemView.findViewById(R.id.session_date_text);
            sessionSummary = itemView.findViewById(R.id.session_summary_text);
        }
    }
}
    