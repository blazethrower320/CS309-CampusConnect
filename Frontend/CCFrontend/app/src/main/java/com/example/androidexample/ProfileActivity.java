package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, WebSocketListener {

    private RecyclerView pastSessionsRecyclerView;
    private PastSessionAdapter sessionAdapter;
    private List<JSONObject> pastSessionList;

    // Text fields
    private TextView roleText, nameText, usernameText, bioText, majorText, classificationText;
    private LinearLayout ratingLayout;
    private TextView tutorRatingText, tutorRatingValue;

    // Buttons
    private Button msgBtn, logoutBtn;
    private ImageButton editProfileBtn, menuBtn;
    private DrawerLayout drawerLayout;
    private LinearLayout homeBtn, sessionsBtn, reviewsBtn;

    // User info
    private User user;

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Load the current logged-in user
        user = User.getInstance();
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Initialize UI
        ratingLayout = findViewById(R.id.rating_layout);
        tutorRatingText = findViewById(R.id.tutor_rating_text);
        tutorRatingValue = findViewById(R.id.rating_value);
        roleText = findViewById(R.id.role_text);
        nameText = findViewById(R.id.name_text);
        usernameText = findViewById(R.id.username_text);
        bioText = findViewById(R.id.bio_text);
        majorText = findViewById(R.id.major_text);
        classificationText = findViewById(R.id.classification_text);
        msgBtn = findViewById(R.id.message_btn);
        menuBtn = findViewById(R.id.menu_button);
        logoutBtn = findViewById(R.id.logout_btn);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        reviewsBtn = findViewById(R.id.nav_reviews);
        homeBtn = findViewById(R.id.nav_home);
        sessionsBtn = findViewById(R.id.nav_sessions);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Load past sessions
        pastSessionsRecyclerView = findViewById(R.id.past_sessions_recycler_view);
        pastSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPastSessionData();

        // Load user info
        GetUserInfo(user.getUsername());

        // Button listeners
        msgBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        sessionsBtn.setOnClickListener(this);
        reviewsBtn.setOnClickListener(this);

        // Role-based UI
        if (user.isAdmin()) {
            roleText.setText("Admin");
            ratingLayout.setVisibility(View.GONE);
        } else if (user.isTutor()) {
            roleText.setText("Tutor");
            ratingLayout.setVisibility(View.VISIBLE);
        } else {
            roleText.setText("Student");
            ratingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.message_btn) {
            startActivity(new Intent(this, ChatActivity.class));
        }
        if (id == R.id.logout_btn) {
            User.clearInstance(); // clear singleton
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (id == R.id.menu_button) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        }
        if (id == R.id.edit_profile_btn) {
            startActivity(new Intent(this, EditProfileActivity.class));
            finish();
        }
        if (id == R.id.nav_sessions) {
            startActivity(new Intent(this, SessionActivity.class));
            finish();
        }
        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        }
        if (id == R.id.nav_reviews) {
            startActivity(new Intent(this, ReviewListActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WebSocketManager.getInstance().setWebSocketListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WebSocketManager.getInstance().removeWebSocketListener();
    }

    @Override
    public void onWebSocketOpen(org.java_websocket.handshake.ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected in ProfileActivity");
    }

    @Override
    public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Message: " + message);
        runOnUiThread(() ->
                NotificationUtils.showPushNotification(this, "New Session Update", message)
        );
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
    }

    private void GetUserInfo(String username) {
        String url = BASE_URL + "/users/find/" + username;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("UserInfo", "Response: " + response);
                    try {
                        user.setFirstName(response.optString("firstName", ""));
                        user.setLastName(response.optString("lastName", ""));
                        user.setBio(response.optString("bio", "No bio available."));
                        user.setMajor(response.optString("major", "Undeclared"));
                        user.setClassification(response.optString("classification", "N/A"));
                        user.setTutor(response.optBoolean("isTutor", false));
                        user.setAdmin(response.optBoolean("isAdmin", false));

                        usernameText.setText("@" + user.getUsername());
                        nameText.setText(user.getFirstName() + " " + user.getLastName());
                        bioText.setText(user.getBio());
                        majorText.setText(user.getMajor());
                        classificationText.setText(user.getClassification());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("UserInfo", "Error fetching user info", error)
        );

        queue.add(request);
    }

    private void loadPastSessionData() {
        pastSessionList = new ArrayList<>();
        sessionAdapter = new PastSessionAdapter(pastSessionList);
        pastSessionsRecyclerView.setAdapter(sessionAdapter);

        String url = BASE_URL + "/sessions/inactive";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    pastSessionList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            pastSessionList.add(response.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    sessionAdapter.notifyDataSetChanged();
                },
                error -> Log.e("PastSessions", "Error fetching sessions", error)
        );

        queue.add(request);
    }
}
