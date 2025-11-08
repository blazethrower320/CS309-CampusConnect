package com.example.androidexample;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

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
        holder.meetingInfo.setText(session.getClassName() + " — " + session.getMeetingTime());
        holder.sessionLocation.setText(session.getMeetingLocation());

        // CAMDEN HERE
        holder.messagesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("sessionId", session.getSessionId());
            v.getContext().startActivity(intent);
        });

        // CAMDEN HERE
        holder.profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProfileActivity.class); // replace with your target Activity
            intent.putExtra("tutorUsername", session.getTutorUsername()); // pass data if needed
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tutorName, meetingInfo, sessionLocation;
        Button messagesBtn;
        ImageView profilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorName = itemView.findViewById(R.id.tutor_name);
            meetingInfo = itemView.findViewById(R.id.meeting_info);
            sessionLocation = itemView.findViewById(R.id.session_location);
            messagesBtn = itemView.findViewById(R.id.btn_messages);
            profilePic = itemView.findViewById(R.id.profile_pic);  // <-- new
        }
    }
}
