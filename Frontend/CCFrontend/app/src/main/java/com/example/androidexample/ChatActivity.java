package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private ImageButton sendBtn;
    private EditText msgTxt;
    // New UI components
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> chatMessageList;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagespage);

        // Initialize UI elements
        sendBtn = findViewById(R.id.send_btn);
        msgTxt = findViewById(R.id.message_edt);
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);

        // Setup RecyclerView
        chatMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(chatMessageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        // Connect to the WebSocket instance
        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        // Connect to the WebSocket when the activity starts
        try
        {
            // Example URL. This should be dynamic based on the user and chat room.
            String serverUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/DM/{userId1}/{userId2}";
            //Get current User
            userId = getIntent().getIntExtra("userId", -1);
            serverUrl = serverUrl.replace("{userId1}", userId + "");
            Log.d("Activity", "Current User ID: " + userId);
            //TODO get other user sending message to
            serverUrl = serverUrl.replace("{userId2}", "789");

            WebSocketManager.getInstance().connectWebSocket(serverUrl);
            //WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // Send button listener
        sendBtn.setOnClickListener(v ->
        {
            try
            {
                String message = msgTxt.getText().toString();
                if (!message.isEmpty())
                {
                    WebSocketManager.getInstance().sendMessage(message);
                    msgTxt.setText("");
                }
            }
            catch (Exception e)
            {
                Log.e("ExceptionSendMessage:", e.getMessage(), e);
            }
        });
    }

    @Override
    public void onWebSocketMessage(String message)
    {
        runOnUiThread(() ->
        {
            // Add the new message to our list
            chatMessageList.add(new ChatMessage(message));
            // Notify the adapter that a new item has been inserted
            messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
            // Scroll the RecyclerView to the bottom to show the latest message
            messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);
        });
    }

    // ... other WebSocket methods (onWebSocketClose, onWebSocketOpen, etc.)
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote)
    {
        // You can also add closure messages to the list
    }
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata)
    {

    }
    @Override
    public void onWebSocketError(Exception ex)
    {

    }

    // --- FIX: ADD onDestroy TO CLEAN UP THE CONNECTION ---
    //@Override
    //protected void onDestroy() {
    //    super.onDestroy();
        // Close the WebSocket connection to prevent memory and resource leaks
    //    WebSocketManager.getInstance().closeWebSocket();
    //}
}
