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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import org.java_websocket.handshake.ServerHandshake;
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
    //Id values
    int sessionId;
    int tutorUserId;
    int userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        // Get user info
        userId = User.getInstance().getUserId();
        username = User.getInstance().getUsername(); // This should be dynamically loaded after login
        tutorUserId = getIntent().getIntExtra("tutorUserId", 0);
        sessionId = getIntent().getIntExtra("sessionId", 0);

        //Group chat detection from AllMessages class
        if(getIntent().getIntExtra("isGroupChat", 0)==1) //FROM ALLMESSAGES
        {
            sessionId = getIntent().getIntExtra("sessionId", 0);
        }
        else
        {
            sessionId = 0;
            tutorUserId = getIntent().getIntExtra("sessionId", 0);
        }

        // Setup WebSocket
        WebSocketManager.getInstance().setWebSocketListener(this);
        String serverUrl;
        if(sessionId==0) //If DM
        {
            try
            {
                serverUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/DM/{userId1}/{userId2}";
                serverUrl = serverUrl.replace("{userId1}", String.valueOf(userId));
                serverUrl = serverUrl.replace("{userId2}", String.valueOf(tutorUserId)); // Placeholder for the other user
                WebSocketManager.getInstance().connectWebSocket(serverUrl);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else //If group chat
        {
            try
            {
                serverUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/groupChat/{sessionId}/{userId}";
                serverUrl = serverUrl.replace("{userId}", String.valueOf(userId));
                serverUrl = serverUrl.replace("{sessionId}", String.valueOf(sessionId)); // Placeholder for the other user
                WebSocketManager.getInstance().connectWebSocket(serverUrl);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        //TODO Remove after done testing
        //try
        //{
        //    serverUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/groupChat/{sessionId}/{userId}";
        //    serverUrl = serverUrl.replace("{userId}", String.valueOf(userId));
        //    serverUrl = serverUrl.replace("{sessionId}", String.valueOf(1)); // Placeholder for the other user
        //    WebSocketManager.getInstance().connectWebSocket(serverUrl);
        //}
        //catch (Exception e)
        //{
        //    e.printStackTrace();
        //}
        

        // --- IMAGE PICKER LAUNCHER ---
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Use the new HTTP upload method
                            uploadImageToServer(selectedImageUri);
                        }
                    }
                });

        imageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        backBtn.setOnClickListener(v ->
        {
            //TODO Ensure previous intent will still be active
            finish();
        });

        // --- TEXT SEND BUTTON LISTENER ---
        sendBtn.setOnClickListener(v -> {
            String messageText = msgTxt.getText().toString();
            if (!messageText.isEmpty())
            {
                try {
                    JSONObject messageJson = new JSONObject();
                    messageJson.put("type", 0);
                    messageJson.put("message", messageText);
                    WebSocketManager.getInstance().sendMessage(messageJson.toString());

                    // Add local echo for instant UI update
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

    /**
     * Handles uploading the image via HTTP multipart request.
     * This is the industry-standard approach.
     */
    /**
     * Handles uploading the image via HTTP multipart request.
     * This is the industry-standard approach.
     */
    private void uploadImageToServer(Uri imageUri) {
        String uploadUrl = "http://coms-3090-037.class.las.iastate.edu:8080/images";

        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl,
                response -> {
                    // SUCCESS: Server received the file and responded.
                    try {
                        Toast.makeText(getApplicationContext(), "Upload Success:", Toast.LENGTH_LONG).show();

                        // The raw response from the server is a STRING (the URL)
                        String imageUrl = new String(response.data);
                        Log.d("HTTPUploadSuccess", "Server Response URL: " + imageUrl);
                        // Now that we have the URL, send it over the WebSocket.
                        sendImageUrlOverWebSocket(imageUrl);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Upload failed: Invalid server response", Toast.LENGTH_LONG).show();
                        Log.e("HTTPUpload", "Response parsing error", e);
                    }
                },
                error ->
                {
                    // ERROR: The HTTP upload failed.
                    Toast.makeText(getApplicationContext(), "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("HTTPUpload", "Volley error", error);
                })
        {
            /**
             * ADDED: This method adds the extra text parameters to the request.
             * The backend is expecting a 'type' field.
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add the "type" parameter with the value "image"
                // This key "type" and value "image" must match exactly what the backend expects.
                params.put("type", "image");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData()
            {
                Map<String, DataPart> params = new HashMap<>();
                try
                {
                    // Get bytes from the image URI
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress to a reasonable quality. 80 is a good balance.
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] imageData = stream.toByteArray();
                    String fileName = "upload_" + System.currentTimeMillis() + ".jpg";

                    // The key "file" must match what the server expects.
                    params.put("image", new DataPart(fileName, imageData));
                }
                catch (IOException e)
                {
                    Log.e("HTTPUpload", "File processing error", e);
                }
                return params;
            }
        };

        // Add the request to the Volley queue to execute it.
        Volley.newRequestQueue(this).add(multipartRequest);
    }


    /**
     * After a successful HTTP upload, this sends the image URL over the WebSocket.
     */
    private void sendImageUrlOverWebSocket(String imageUrl) {
        try
        {
            JSONObject messageJson = new JSONObject();
            // This type tells the receiver it's an image.
            messageJson.put("type", 1);
            // The content is now a URL, not Base64 data.
            messageJson.put("message", imageUrl);

            WebSocketManager.getInstance().sendMessage(messageJson.toString());

            // Add local echo for the image to show it instantly.
            chatMessageList.add(new ChatMessage(imageUrl, true, 1));
            messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
            messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);

        } catch (JSONException e) {
            Log.e("WebSocketSend", "Error sending image URL", e);
        }
    }

    @Override
    public void onWebSocketMessage(String message)
    {
        runOnUiThread(() -> {
            if (message == null || message.trim().isEmpty()) {
                return; // Ignore empty messages
            }

            try
            {
                JSONObject messageJson = new JSONObject(message);
                String senderName = messageJson.optString("sender");

                // If it's your own message (and you use local echo), ignore the server's broadcast.
                if (Objects.equals(senderName, this.username))
                {
                    return;
                }

                int messageType = messageJson.getInt("type");
                String content;
                boolean isSentByUser = false;

                if (messageType == 1)
                { // It's an image message from another user
                    content = messageJson.getString("imageUrl");
                }
                else
                { // It's a text message from another user
                    content = messageJson.getString("message");
                }

                Log.d("WebSocketMessage", "Processing content: '" + content + "' for messageType: " + messageType);

                chatMessageList.add(new ChatMessage(content, isSentByUser, messageType, senderName));
                messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
                messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);

            } catch (JSONException e) {
                // This will happen if the server sends a non-JSON string.
                Log.w("WebSocket", "Received non-JSON message: " + message);
                // Optionally, display it as a plain text message
                chatMessageList.add(new ChatMessage(message, false, 0));
                messageAdapter.notifyItemInserted(chatMessageList.size() - 1);
                messagesRecyclerView.scrollToPosition(chatMessageList.size() - 1);
            }
        });
    }

    // --- Standard WebSocket Listener Methods ---
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata)
    {
        Log.d("WebSocket", "Connection opened");
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote)
    {
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
