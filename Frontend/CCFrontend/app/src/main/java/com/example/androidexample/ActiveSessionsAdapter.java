package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActiveSessionsAdapter extends RecyclerView.Adapter<ActiveSessionsAdapter.ViewHolder> {

    private List<Session> sessions;

    public ActiveSessionsAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Session session = sessions.get(position);

        holder.tutorName.setText(session.getTutorUsername());

        String className = session.getClassName();
        String time = session.getMeetingTime();
        String sessionLocation = session.getMeetingLocation();


        // Combine location and time nicely
        String meetingInfo = className + " — " + time;
        holder.meetingInfo.setText(meetingInfo);
        holder.sessionLocation.setText(sessionLocation);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tutorName, meetingInfo, sessionLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorName = itemView.findViewById(R.id.tutor_name);
            meetingInfo = itemView.findViewById(R.id.meeting_info);
            sessionLocation = itemView.findViewById(R.id.session_location);
        }
    }
}
