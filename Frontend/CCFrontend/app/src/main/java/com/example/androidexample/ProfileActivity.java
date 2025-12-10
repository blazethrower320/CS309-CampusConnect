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
import com.android.volley.toolbox.StringRequest;
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

    private User loggedInUser;   // represents the current logged-in user
    private User profileUser;    // represents the profile we are displaying
    private TextView tutorRatingText, tutorRatingValue;

    // Buttons
    private Button msgBtn, logoutBtn;
    private ImageButton editProfileBtn, menuBtn;
    private DrawerLayout drawerLayout;
    private LinearLayout homeBtn, sessionsBtn, reviewsBtn;

    // User info
    private User user;
    private User otherUser;
    private String tutorUsername;
    private int profileUserId;

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

        msgBtn.setEnabled(false);

        loggedInUser = User.getInstance();

        // Determine which profile to show
        String tutorUsername = getIntent().getStringExtra("tutorUsername");
        if (tutorUsername != null && !tutorUsername.isEmpty()) {
            // Viewing tutor's profile
            GetUserInfo(tutorUsername);          // populates profileUser fields
            fetchUserIdByUsername(tutorUsername); // fetch numeric userId

        } else {
            // Viewing own profile
            GetUserInfo(loggedInUser.getUsername());
        }


        // Load past sessions
        pastSessionsRecyclerView = findViewById(R.id.past_sessions_recycler_view);
        pastSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPastSessionData();


        // Button listeners
        msgBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        sessionsBtn.setOnClickListener(this);
        reviewsBtn.setOnClickListener(this);


        if(user.isAdmin())
        {
            roleText.setText("Admin");
            ratingLayout.setVisibility(View.GONE);
            tutorRatingText.setVisibility(View.GONE);
        }
        else if(user.isTutor())
        {
            roleText.setText("Tutor");
            ratingLayout.setVisibility(View.VISIBLE);
            tutorRatingText.setVisibility(View.VISIBLE);
        }
        else
        {
            roleText.setText("Student");
            ratingLayout.setVisibility(View.GONE);
            tutorRatingText.setVisibility(View.GONE);
        }

        if (tutorUsername != null && !tutorUsername.isEmpty()) {
            editProfileBtn.setVisibility(View.GONE);
            msgBtn.setVisibility(View.VISIBLE); // maybe allow messaging
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.message_btn) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("sessionId", 0);
                intent.putExtra("tutorUserId", profileUser.getUserId());
                intent.putExtra("tutorUsername", profileUser.getUsername());
                intent.putExtra("userId", loggedInUser.getUserId());
                intent.putExtra("username", loggedInUser.getUsername());
                intent.putExtra("chatName", profileUser.getFirstName());
                intent.putExtra("isGroupChat", false);

            Log.d("ProfileActivity", "Starting ChatActivity with tutorUserId: "
                        + profileUser.getUserId() + ", tutorUsername: " + profileUser.getUsername());
                v.getContext().startActivity(intent);
        }
        if (id == R.id.logout_btn) {
            User.clearInstance(); // clear singleton
            WebSocketManager.getInstance().disconnectWebSocket();
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

    private void updateUIWithProfileUser() {
        if (profileUser == null) return;

        usernameText.setText("@" + profileUser.getUsername());
        nameText.setText(profileUser.getFirstName() + " " + profileUser.getLastName());
        bioText.setText(profileUser.getBio());
        majorText.setText(profileUser.getMajor());
        classificationText.setText(profileUser.getClassification());

        // Role-based UI
        if (profileUser.isAdmin()) {
            roleText.setText("Admin");
            ratingLayout.setVisibility(View.GONE);
        } else if (profileUser.isTutor()) {
            roleText.setText("Tutor");
            ratingLayout.setVisibility(View.VISIBLE);
        } else {
            roleText.setText("Student");
            ratingLayout.setVisibility(View.GONE);
        }

        // Buttons visibility
        if (!profileUser.getUsername().equals(loggedInUser.getUsername())) {
            editProfileBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
            msgBtn.setVisibility(View.VISIBLE);
        } else {
            editProfileBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            msgBtn.setVisibility(View.GONE);
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
                    try {
                        profileUser = User.fromJson(response); // create a new User object
                        //if(profileUser.getTutorId()!=0)
                        //{
                            profileUser.setUserId(profileUserId);
                            //getTutorRating(profileUser.getUserId());
                            Log.i("ProfileActivity", "Tutor ID: " + profileUser.getUserId());
                        //}
                        updateUIWithProfileUser(); // update UI after network call
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("UserInfo", "Error fetching user info", error)
        );

        queue.add(request);
    }

    private void fetchUserIdByUsername(String username) {
        String url = BASE_URL + "/users/getUserId/" + username;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        int userId = Integer.parseInt(response.trim());
                        if (profileUser != null) {
                            profileUser.setUserId(userId);
                        }
                        Log.d("ProfileActivity", "Fetched userId: " + userId);
                        profileUserId = userId;
                        getTutorRating(userId);
                        msgBtn.setEnabled(true);
                    } catch (NumberFormatException e) {
                        Log.e("ProfileActivity", "Failed to parse userId: " + response, e);
                    }
                },
                error -> Log.e("ProfileActivity", "Failed to fetch userId for username: " + username, error)
        );

        queue.add(request);
    }



    private void getTutorRating(int tutorId) {
        String url = BASE_URL + "/ratings/getTutorAverageUsrId/" + tutorId;
        RequestQueue queue = Volley.newRequestQueue(this);

        // Since the endpoint returns a plain string (the double), a StringRequest is appropriate.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try
                    {
                        // The response is the rating, e.g., "4.5"
                        double rating = Double.parseDouble(response.trim());
                        // Format the rating to one decimal place and set it to the TextView
                        tutorRatingValue.setText(String.format("%.1f", rating));
                        tutorRatingText.setVisibility(View.VISIBLE);
                        Log.d("GetTutorRating", "Successfully fetched rating: " + rating);
                    }
                    catch (NumberFormatException e)
                    {
                        Log.e("GetTutorRating", "Failed to parse rating from response: " + response, e);
                        tutorRatingValue.setText("N/A"); // Show 'Not Available' on parsing error
                    }
                },
                error -> {
                    Log.e("GetTutorRating", "Error fetching tutor rating for ID " + tutorId, error);
                    tutorRatingValue.setText("N/A"); // Show 'Not Available' on network error
                });

        queue.add(stringRequest);
    }




        private void loadPastSessionData() {
        pastSessionList = new ArrayList<>();
        sessionAdapter = new PastSessionAdapter(pastSessionList);
        pastSessionsRecyclerView.setAdapter(sessionAdapter);

        String url = BASE_URL + "/sessions/inactive/" + User.getInstance().getUserId(); //TODO NEW CODE TO TROUBLESHOOT
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
