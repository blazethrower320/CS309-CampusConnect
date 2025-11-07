package com.example.androidexample;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private ImageButton sendBtn;
    private ImageButton imageBtn;
    private EditText msgTxt;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> chatMessageList;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagespage);

        // Initialize UI elements
        sendBtn = findViewById(R.id.send_btn);
        imageBtn = findViewById(R.id.attatchment_btn);
        msgTxt = findViewById(R.id.message_edt);
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);

        // Setup RecyclerView
        chatMessageList = new ArrayList<>();
        // Assuming your MessageAdapter is updated for multiple view types
        messageAdapter = new MessageAdapter(chatMessageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        // Connect to the WebSocket instance
        WebSocketManager.getInstance().setWebSocketListener(this);

        // Connect to the WebSocket when the activity starts
        try
        {
            String serverUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/DM/{userId1}/{userId2}";
            userId = getIntent().getIntExtra("userId", -1);
            serverUrl = serverUrl.replace("{userId1}", String.valueOf(userId));
            Log.d("ChatActivity", "Current User ID: " + userId);
            //TODO get other user sending message to
            serverUrl = serverUrl.replace("{userId2}", "");

            WebSocketManager.getInstance().connectWebSocket(serverUrl);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // --- IMAGE PICKER SETUP ---
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null)
                    {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null)
                        {
                            // Start the upload process
                            uploadImageToServer(selectedImageUri);
                        }
                    }
                });

        imageBtn.setOnClickListener(v ->
        {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // --- UPDATED SEND BUTTON LISTENER ---
        sendBtn.setOnClickListener(v -> {
            try {
                String messageText = msgTxt.getText().toString();
                if (!messageText.isEmpty()) {
                    // 1. Construct JSON to send to server
                    JSONObject messageJson = new JSONObject();
                    messageJson.put("type", 0);
                    messageJson.put("message", messageText);
                    WebSocketManager.getInstance().sendMessage(messageJson.toString());

                    // 2. Add to UI immediately (Local Echo)
                    chatMessageList.add(new ChatMessage(messageText, true, 0));
                    messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
                    messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);

                    // 3. Clear input
                    msgTxt.setText("");
                }
            } catch (Exception e) {
                Log.e("ExceptionSendMessage:", e.getMessage(), e);
            }
        });
    }

    // --- UPDATED WEBSOCKET MESSAGE HANDLER ---
    @Override
    public void onWebSocketMessage(String message)
    {
        runOnUiThread(() -> {
            try {
                JSONObject messageJson = new JSONObject(message);
                String messageType = messageJson.optString("type", "0");
                int senderId = messageJson.getInt("senderId");

                // Ignore messages sent by the current user (server echo)
                if (senderId == userId) return;

                boolean isSentByUser = false; // It's always from others here

                if ("IMAGE".equals(messageType))
                {
                    // It's an image message from the server, containing a URL
                    String imageUrl = messageJson.getString("url");
                    chatMessageList.add(new ChatMessage(imageUrl, isSentByUser, 1));
                }
                else
                {
                    // It's a standard text message
                    String content = messageJson.getString("content");
                    chatMessageList.add(new ChatMessage(content, isSentByUser, 0));
                }

                messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
                messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);

            }
            catch (JSONException e)
            {
                Log.e("WebSocket", "Could not parse JSON from received message: " + message, e);
            }
        });
    }

    // --- IMAGE UPLOAD METHODS ---
    private void uploadImageToServer(Uri imageUri)
    {
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            String base64Image = bitmapToBase64(bitmap);

            JSONObject messageJson = new JSONObject();
            messageJson.put("type", 1);
            messageJson.put("message", base64Image);

            WebSocketManager.getInstance().sendMessage(messageJson.toString());
            Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        }
        catch (IOException e)
        {
            Log.e("ImageUpload", "Error reading image file.", e);
        }
        catch (JSONException e)
        {
            Log.e("ImageUpload", "Error creating JSON for image upload.", e);
        }
    }

    private String bitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress the image to reduce its size. Adjust quality as needed.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // --- OTHER WEBSOCKET METHODS ---
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata)
    {
        Log.d("WebSocket", "Connection opened successfully!");
        runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Connection Successful!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote)
    {
        Log.d("WebSocket", "Connection closed. Reason: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex)
    {
        Log.e("WebSocket", "An error occurred: " + ex.getMessage());
    }

    // --- CLEAN UP THE CONNECTION ---
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Close the WebSocket connection to prevent memory and resource leaks
        WebSocketManager.getInstance().disconnectWebSocket();
    }
}
