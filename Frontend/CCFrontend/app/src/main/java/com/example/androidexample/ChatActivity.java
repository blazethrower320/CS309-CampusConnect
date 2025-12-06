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
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private ImageButton sendBtn;
    private ImageButton imageBtn;
    private ImageButton backBtn;
    private EditText msgTxt;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> chatMessageList;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private TextView chatNameTxt;


    private boolean hasHistoryLoaded = false;

    //Id values
    int sessionId;
    int userId;
    private String chatName;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagespage);

        // Initialize UI
        sendBtn = findViewById(R.id.send_btn);
        imageBtn = findViewById(R.id.attatchment_btn);
        backBtn = findViewById(R.id.back_btn);
        msgTxt = findViewById(R.id.message_edt);
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);

        // Setup RecyclerView
        chatMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(chatMessageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        // Get user info from Singleton
        userId = User.getInstance().getUserId();
        username = User.getInstance().getUsername();

        boolean isGroupChat = getIntent().getBooleanExtra("isGroupChat", false);
        sessionId = getIntent().getIntExtra("sessionId", -1);
        chatName = getIntent().getStringExtra("chatName");

        chatNameTxt = findViewById(R.id.chat_name_title);
        chatNameTxt.setText(chatName);


        if (sessionId == -1) {
            Toast.makeText(this, "Error: Chat ID missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupWebSocket(isGroupChat);
        setupUIListeners();
    }

    private void setupWebSocket(boolean isGroupChat) {
        WebSocketManager.getInstance().setWebSocketListener(this);
        String serverUrl;

        if (isGroupChat) {
            serverUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/groupChat/{sessionId}/{userId}";
            serverUrl = serverUrl.replace("{sessionId}", String.valueOf(sessionId));
            serverUrl = serverUrl.replace("{userId}", String.valueOf(userId));
        } else {
            serverUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/DM/{userId1}/{userId2}";
            serverUrl = serverUrl.replace("{userId1}", String.valueOf(userId));
            serverUrl = serverUrl.replace("{userId2}", String.valueOf(sessionId));
        }

        try {
            WebSocketManager.getInstance().connectWebSocket(serverUrl);
        } catch (Exception e) {
            Log.e("WebSocket", "Connection failed to start", e);
        }
    }

    private void setupUIListeners() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            uploadImageToServer(selectedImageUri);
                        }
                    }
                });

        imageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        backBtn.setOnClickListener(v -> finish());

        sendBtn.setOnClickListener(v -> {
            String messageText = msgTxt.getText().toString().trim();
            if (!messageText.isEmpty()) {
                try {
                    JSONObject messageJson = new JSONObject();
                    messageJson.put("type", 0);
                    messageJson.put("message", messageText);
                    WebSocketManager.getInstance().sendMessage(messageJson.toString());

                    chatMessageList.add(new ChatMessage(messageText, true, 0));
                    messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
                    messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);
                    msgTxt.setText("");

                } catch (JSONException e) {
                    Log.e("ChatActivity", "Error creating text JSON", e);
                }
            }
        });
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            if (message == null || message.trim().isEmpty()) {
                Log.w("WebSocket", "Received null or empty message.");
                return;
            }

            Log.d("WebSocketRaw", "Raw message received: " + message);

            if (!hasHistoryLoaded) {
                // We only attempt to load history ONCE.
                hasHistoryLoaded = true;

                // Check if the message is a valid JSON Array.
                // A simple string like "[]" is a valid array but will have length 0.
                if (message.trim().startsWith("[")) {
                    try {
                        JSONArray historyArray = new JSONArray(message);
                        Log.d("WebSocket", "Parsing as history array...");
                        for (int i = 0; i < historyArray.length(); i++) {
                            JSONObject msgJson = historyArray.getJSONObject(i);
                            String content = msgJson.getString("message");
                            int senderId = msgJson.getInt("senderId");
                            int messageType = msgJson.optInt("type", 0);
                            String senderName = msgJson.optString("sender", "Unknown");

                            boolean isSentByUser = (senderId == this.userId);
                            chatMessageList.add(new ChatMessage(content, isSentByUser, messageType, senderName));
                        }

                        // IMPORTANT: Only update UI if we actually added messages.
                        if (!chatMessageList.isEmpty()) {
                            messageAdapter.notifyDataSetChanged();
                            messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);
                            Log.d("WebSocket", "Successfully loaded " + chatMessageList.size() + " messages from history.");
                        } else {
                            Log.d("WebSocket", "History was an empty array '[]'. No messages loaded.");
                        }

                    } catch (JSONException e) {
                        Log.e("WebSocket", "Error parsing what looked like a history array. Message: " + message, e);
                    }
                } else {
                    // If it doesn't start with '[', it must be a single message.
                    Log.w("WebSocket", "Initial message was not a history array. Treating as a single message.");
                    parseSingleMessage(message);
                }
            } else {
                // If history is already loaded, all subsequent messages are single messages.
                parseSingleMessage(message);
            }
        });
    }

    private void parseSingleMessage(String message) {
        try {
            JSONObject messageJson = new JSONObject(message);
            String senderName = messageJson.optString("sender");

            if (Objects.equals(senderName, this.username)) {
                Log.d("WebSocket", "Ignoring own message broadcast.");
                return;
            }

            int messageType = messageJson.getInt("type");
            String content = (messageType == 1) ? messageJson.getString("imageUrl") : messageJson.getString("message");

            Log.d("WebSocket", "Processing single message from " + senderName);
            chatMessageList.add(new ChatMessage(content, false, messageType, senderName));
            messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
            messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);

        } catch (JSONException jsonException) {
            Log.e("WebSocket", "Error parsing single JSON message: " + message, jsonException);
        }
    }

    // --- All other methods (uploadImageToServer, sendImageUrlOverWebSocket, etc.) remain unchanged ---

    private void uploadImageToServer(Uri imageUri) {
        String uploadUrl = "http://coms-3090-037.class.las.iastate.edu:8080/images";
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl,
                response -> {
                    try {
                        String imageUrl = new String(response.data);
                        Log.d("HTTPUploadSuccess", "Server Response URL: " + imageUrl);
                        sendImageUrlOverWebSocket(imageUrl);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Upload failed: Invalid server response", Toast.LENGTH_LONG).show();
                        Log.e("HTTPUpload", "Response parsing error", e);
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("HTTPUpload", "Volley error", error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("type", "image");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] imageData = stream.toByteArray();
                    String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
                    params.put("image", new DataPart(fileName, imageData));
                } catch (IOException e) {
                    Log.e("HTTPUpload", "File processing error", e);
                }
                return params;
            }
        };
        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private void sendImageUrlOverWebSocket(String imageUrl) {
        try {
            JSONObject messageJson = new JSONObject();
            messageJson.put("type", 1);
            messageJson.put("message", imageUrl);
            WebSocketManager.getInstance().sendMessage(messageJson.toString());

            chatMessageList.add(new ChatMessage(imageUrl, true, 1));
            messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
            messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);
        } catch (JSONException e) {
            Log.e("WebSocketSend", "Error sending image URL", e);
        }
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connection opened. Waiting for history...");
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.e("WebSocket", "Connection closed. Code: " + code + ", Reason: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error: " + ex.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().disconnectWebSocket();
    }
}
