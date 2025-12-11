package com.example.androidexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllMessagesAdapter extends RecyclerView.Adapter<AllMessagesAdapter.ViewHolder> {

    private final Context context;
    private final List<MessageGroup> messageGroupList;
    private final OnItemClickListener listener;

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(MessageGroup messageGroup);
    }

    public AllMessagesAdapter(Context context, List<MessageGroup> messageGroupList, OnItemClickListener listener) {
        this.context = context;
        this.messageGroupList = messageGroupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messagesgroup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data for the current position
        MessageGroup currentGroup = messageGroupList.get(position);
        // Bind the data to the views in the ViewHolder
        holder.bind(currentGroup, listener);
    }

    @Override
    public int getItemCount() {
        return messageGroupList.size();
    }

    // ViewHolder class to hold the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameText;
        TextView messageTimeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views from your item_messagesgroup.xml layout
            groupNameText = itemView.findViewById(R.id.session_course_name_text);
            messageTimeText = itemView.findViewById(R.id.message_sent_time);
        }

        public void bind(final MessageGroup messageGroup, final OnItemClickListener listener) {
            // Set the data to the views
            groupNameText.setText(messageGroup.getGroupName());
            messageTimeText.setText(messageGroup.getMessageTime());

            // Set the click listener on the whole item view
            itemView.setOnClickListener(v -> listener.onItemClick(messageGroup));
        }
    }
}
