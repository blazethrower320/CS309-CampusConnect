package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessageList;

    // Define constants for the different view types
    private static final int VIEW_TYPE_TEXT_SENT = 1;
    private static final int VIEW_TYPE_TEXT_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;


    public MessageAdapter(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    /**
     * This method determines which layout to use for a given message.
     * It checks if the message is sent by the user and its type (text or image).
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessageList.get(position);
        if (message.isSentByUser()) {
            if (message.getMessageType() == 0) { // Text message
                return VIEW_TYPE_TEXT_SENT;
            } else { // Image message
                return VIEW_TYPE_IMAGE_SENT;
            }
        } else {
            if (message.getMessageType() == 0) { // Text message
                return VIEW_TYPE_TEXT_RECEIVED;
            } else { // Image message
                return VIEW_TYPE_IMAGE_RECEIVED;
            }
        }
    }

    /**
     * This method creates the appropriate ViewHolder for the given view type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_TEXT_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
                return new SentTextViewHolder(view);
            case VIEW_TYPE_TEXT_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recieved_chat_message, parent, false);
                return new ReceivedTextViewHolder(view);
            case VIEW_TYPE_IMAGE_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_sent, parent, false);
                return new SentImageViewHolder(view);
            case VIEW_TYPE_IMAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_recieved_placeholder, parent, false);
                return new ReceivedImageViewHolder(view);
            default:
                // This should not happen
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    /**
     * This method binds the data from the ChatMessage object to the views in the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT_SENT:
                ((SentTextViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_TEXT_RECEIVED:
                ((ReceivedTextViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_SENT:
                ((SentImageViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_RECEIVED:
                ((ReceivedImageViewHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    // --- VIEW HOLDERS FOR EACH MESSAGE TYPE ---

    // ViewHolder for sent text messages
    private static class SentTextViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        SentTextViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text); // Make sure this ID matches your layout
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
        }
    }

    // ViewHolder for received text messages
    private static class ReceivedTextViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // You can also add TextView for sender name here if you want

        ReceivedTextViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.recieved_message_text); // Make sure this ID matches your layout
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
        }
    }

    // ViewHolder for sent image messages
    private static class SentImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMessage;

        SentImageViewHolder(View itemView) {
            super(itemView);
            imageMessage = itemView.findViewById(R.id.sent_image_view); // Make sure this ID matches your layout
        }

        void bind(ChatMessage message) {
            // Use Glide to load the image from the URL
            Glide.with(itemView.getContext())
                    .load(message.getContent()) // The content is the image URL
                    .placeholder(R.drawable.image_placeholder) // Optional: create a placeholder drawable
                    .into(imageMessage);
        }
    }

    // ViewHolder for received image messages
    private static class ReceivedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMessage;
        // You can also add TextView for sender name here if you want

        ReceivedImageViewHolder(View itemView)
        {
            super(itemView);
            imageMessage = itemView.findViewById(R.id.recieved_image_view); // Make sure this ID matches your layout
        }

        void bind(ChatMessage message)
        {
            // Use Glide to load the image from the URL
            Glide.with(itemView.getContext())
                    .load(message.getContent()) // The content is the image URL
                    .placeholder(R.drawable.image_placeholder) // Optional
                    .into(imageMessage);
        }
    }
}
