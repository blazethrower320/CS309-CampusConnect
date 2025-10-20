package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<Session> sessions;

    public SessionAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void updateList(List<Session> newList) {
        this.sessions = newList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate your custom session_item.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Session s = sessions.get(position);
        holder.className.setText(s.getClassName());
        holder.classCode.setText(s.getClassCode());
        holder.meetingLocation.setText("Location: " + s.getMeetingLocation());
        holder.meetingTime.setText("Time: " + s.getMeetingTime());

        String tutor = s.getTutorUsername() != null ? s.getTutorUsername() : "Loading tutor...";
        holder.tutorUsername.setText("Tutor: " + tutor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className, classCode, meetingLocation, meetingTime, tutorUsername;

        ViewHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
            classCode = itemView.findViewById(R.id.class_code);
            meetingLocation = itemView.findViewById(R.id.meeting_location);
            meetingTime = itemView.findViewById(R.id.meeting_time);
            tutorUsername = itemView.findViewById(R.id.tutor_username);
        }
    }


    @Override
    public int getItemCount() {
        return sessions == null ? 0 : sessions.size();
    }
}
