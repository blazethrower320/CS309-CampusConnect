package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    public interface OnJoinClickListener {
        void onJoinClick(Session session);
    }

    private List<Session> sessions;
    private OnJoinClickListener joinListener;

    public SessionAdapter(List<Session> sessions, OnJoinClickListener joinListener) {
        this.sessions = sessions;
        this.joinListener = joinListener;
    }

    public void updateList(List<Session> newList) {
        this.sessions = newList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Session s = sessions.get(position);

        holder.className.setText(s.getClassName());
        holder.classCode.setText(s.getClassCode());
        holder.meetingLocation.setText(s.getMeetingLocation());
        holder.meetingTime.setText(s.getMeetingTime());

        String tutor = s.getTutorUsername() != null ? s.getTutorUsername() : "Loading tutor...";
        holder.tutorUsername.setText("Tutor: " + tutor);


        if (s.isJoined()) {
            holder.joinButton.setText("Joined");
            holder.joinButton.setEnabled(false);
            holder.joinButton.setAlpha(0.6f);
        } else {
            holder.joinButton.setText("Join");
            holder.joinButton.setEnabled(true);
            holder.joinButton.setAlpha(1f);
        }

        // 🖱 Click handler
        if (User.getInstance().isTutor()) {
            holder.joinButton.setVisibility(View.GONE);
        } else {
            holder.joinButton.setVisibility(View.VISIBLE);
        }
        holder.joinButton.setOnClickListener(v -> {
            if (joinListener != null && !s.isJoined()) {
                joinListener.onJoinClick(s);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className, classCode, meetingLocation, meetingTime, tutorUsername, joinButton;

        ViewHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
            classCode = itemView.findViewById(R.id.class_code);
            meetingLocation = itemView.findViewById(R.id.meeting_location);
            meetingTime = itemView.findViewById(R.id.meeting_time);
            tutorUsername = itemView.findViewById(R.id.tutor_username);
            joinButton = itemView.findViewById(R.id.btn_join_session);
        }
    }

    @Override
    public int getItemCount() {
        return sessions == null ? 0 : sessions.size();
    }
}

