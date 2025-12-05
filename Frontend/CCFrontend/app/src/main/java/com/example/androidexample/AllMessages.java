package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllMessages extends AppCompatActivity {

    private static final String TAG = "AllMessages";

    private RecyclerView recyclerView;
    private AllMessagesAdapter adapter;
    private List<MessageGroup> messageGroupList;

    private String username;
    private int userId;
    private boolean isTutor;
    private boolean isAdmin;
    private String password;

    private User user;

    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allmessages_page);

        //Get current user
        user = User.getInstance();
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v ->
        {
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        });



        // Initialize RecyclerView
        recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageGroupList = new ArrayList<>();

        // The click listener now receives the full MessageGroup object
        adapter = new AllMessagesAdapter(this, messageGroupList, (messageGroup) -> {
            // This code runs when a user taps on a card
            Intent chatIntent = new Intent(AllMessages.this, ChatActivity.class);

            // ===================================================================
            // THE KEY CHANGE: Pass the 'Id' from the clicked MessageGroup object
            // This ID will now be used by ChatActivity to know which chat to open.
            // ===================================================================
            chatIntent.putExtra("sessionId", messageGroup.getId());
            chatIntent.putExtra("isGroupChat", messageGroup.isGroupChat());

            startActivity(chatIntent);
        });
        recyclerView.setAdapter(adapter);

        fetchMessageGroups();
    }

    private void fetchMessageGroups() {
        // This URL should return an array of JSON objects for the user
        String url = "http://coms-3090-037.class.las.iastate.edu:8080/messages/getAllChats/" + user.getUserId(); // Example URL
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    messageGroupList.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject chatObject = response.getJSONObject(i);

                            // ===================================================================
                            // THE KEY CHANGE: Parse the fields from your backend's JSON
                            // ===================================================================
                            int chatId = chatObject.getInt("Id");
                            String chatName = chatObject.getString("Name");
                            boolean isGroup = chatObject.getBoolean("isGroupChat");
                            // ===================================================================
                            Log.i("AllMessages Data", "Chat ID: " + chatId + ", Name: " + chatName + ", isGroup: " + isGroup);
                            String chatType = "";
                            if(isGroup)
                            {
                                chatType = "Group Chat";
                            }
                            else
                            {
                                chatType = "Direct Message";
                            }
                            messageGroupList.add(new MessageGroup(chatId, chatName, isGroup, chatType));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        Toast.makeText(AllMessages.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged(); // Refresh the list
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.toString());
                    Toast.makeText(AllMessages.this, "Failed to load messages!", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonArrayRequest);
    }
}
