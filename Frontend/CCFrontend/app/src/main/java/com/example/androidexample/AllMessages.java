package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private ImageButton backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to your main layout for this page
        setContentView(R.layout.activity_allmessages_page);

        // Get user data passed from the previous activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userId = intent.getIntExtra("userId", -1);
        isTutor = intent.getBooleanExtra("isTutor", false);
        isAdmin = intent.getBooleanExtra("isAdmin", false);
        password = intent.getStringExtra("password");

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list that will hold the message groups
        messageGroupList = new ArrayList<>();

        //Initialize Ui elements
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v ->
        {
            finish();
        });


        // Create and set the adapter
        adapter = new AllMessagesAdapter(this, messageGroupList, (messageGroup) -> {
            // This is the click listener for each item
            // It will be executed when a user taps on a message group card

            Intent chatIntent = new Intent(AllMessages.this, ChatActivity.class);

            // Pass all necessary data to ChatActivity
            chatIntent.putExtra("username", username);
            chatIntent.putExtra("userId", userId);
            chatIntent.putExtra("isTutor", isTutor);
            chatIntent.putExtra("isAdmin", isAdmin);
            chatIntent.putExtra("password", password);

            // Pass the specific data for the selected chat
            chatIntent.putExtra("sessionId", messageGroup.getSessionId()); // Assuming it's a group chat/session
            // If you have a 'userId2' for DMs, you'd pass it here instead based on the messageGroup type
            // chatIntent.putExtra("userId2", messageGroup.getOtherUserId());

            startActivity(chatIntent);
        });
        recyclerView.setAdapter(adapter);

        // Fetch the message groups from the server
        fetchMessageGroups();
    }

    private void fetchMessageGroups() {
        // The URL to fetch all sessions/DMs for the current user.
        // You'll need to replace this with your actual backend endpoint.
        String url = "http://coms-3090-037.class.las.iastate.edu:8080/sessions/user/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Successfully fetched the list
                    Log.d(TAG, "Response: " + response.toString());
                    messageGroupList.clear(); // Clear old data

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject sessionObject = response.getJSONObject(i);

                            int sessionId = sessionObject.getInt("id");
                            String groupName = sessionObject.getJSONObject("course").getString("name");
                            // You might need to get last message and time from the object too
                            // For now, using placeholder values
                            String lastMessage = "Tap to open chat...";
                            String messageTime = "9:00 PM";

                            messageGroupList.add(new MessageGroup(sessionId, groupName, lastMessage, messageTime));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        Toast.makeText(AllMessages.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                    }

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    // Failed to fetch the list
                    Log.e(TAG, "Volley error: " + error.toString());
                    Toast.makeText(AllMessages.this, "Failed to load messages!", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonArrayRequest);
    }
}
