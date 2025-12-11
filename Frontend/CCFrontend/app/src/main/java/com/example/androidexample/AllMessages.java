package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allmessages_page);

        // Get user data passed from the previous activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userId = intent.getIntExtra("userId", -1);
        isTutor = intent.getBooleanExtra("isTutor", false);
        isAdmin = intent.getBooleanExtra("isAdmin", false);
        password = intent.getStringExtra("password");

        recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageGroupList = new ArrayList<>();

        adapter = new AllMessagesAdapter(this, messageGroupList, (messageGroup) -> {
            // Click listener for each item
            Intent chatIntent = new Intent(AllMessages.this, ChatActivity.class);

            // Pass all necessary data to ChatActivity
            chatIntent.putExtra("username", username);
            chatIntent.putExtra("userId", userId);
            chatIntent.putExtra("isTutor", isTutor);
            chatIntent.putExtra("isAdmin", isAdmin);
            chatIntent.putExtra("password", password);

            // Pass the specific data for the selected chat
            chatIntent.putExtra("sessionId", messageGroup.getSessionId());

            startActivity(chatIntent);
        });
        recyclerView.setAdapter(adapter);

        fetchMessageGroups();
    }

    private void fetchMessageGroups() {
        // The URL to fetch all sessions/DMs for the current user.
        String url = "http://coms-3090-037.class.las.iastate.edu:8080/sessions/user/" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    messageGroupList.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject sessionObject = response.getJSONObject(i);

                            int sessionId = sessionObject.getInt("id");
                            String groupName = sessionObject.getJSONObject("course").getString("name");
                            // Using a placeholder for time. You might get this from your JSON.
                            String messageTime = ""; // e.g., "9:00 PM"

                            messageGroupList.add(new MessageGroup(sessionId, groupName, messageTime));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        Toast.makeText(AllMessages.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.toString());
                    Toast.makeText(AllMessages.this, "Failed to load messages!", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonArrayRequest);
    }
}
