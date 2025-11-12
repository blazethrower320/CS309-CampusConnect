package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewListActivity extends AppCompatActivity implements TutorListAdapter.OnTutorClickListenerWithReviews, WebSocketListener {

    private static final String TAG = "ReviewListActivity";
    private static final String URL_TUTORS = "http://coms-3090-037.class.las.iastate.edu:8080/tutors";

    private ImageButton menuButton;
    private DrawerLayout drawerLayout;

    private RecyclerView recyclerView;
    private TutorListAdapter adapter;
    private List<TutorItem> tutorList = new ArrayList<>();

    // User session info (via singleton)
    private String username;
    private String password;
    private int userId;
    private boolean isTutor;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);

        // RecyclerView setup
        recyclerView = findViewById(R.id.reviewRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TutorListAdapter(this, tutorList, this);
        recyclerView.setAdapter(adapter);

        // Load user data from singleton
        User user = User.getInstance();
        username = user.getUsername();
        password = user.getPassword();
        userId = user.getUserId();
        isTutor = user.isTutor();
        isAdmin = user.isAdmin();

        // Nav menu buttons
        setupNavButtons();

        // Menu open/close
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                drawerLayout.closeDrawer(findViewById(R.id.nav_view));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.nav_view));
            }
        });

        // Search bar functionality
        SearchView searchView = findViewById(R.id.tutor_search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Load tutor data
        loadTutors();
    }

    private void setupNavButtons() {
        LinearLayout homeButton = findViewById(R.id.nav_home);
        homeButton.setOnClickListener(v -> navigateTo(MainMenuActivity.class));

        LinearLayout profileButton = findViewById(R.id.nav_profile);
        profileButton.setOnClickListener(v -> navigateTo(ProfileActivity.class));

        LinearLayout sessionsButton = findViewById(R.id.nav_sessions);
        sessionsButton.setOnClickListener(v -> navigateTo(SessionActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(ReviewListActivity.this, targetActivity);
        startActivity(intent);
        finish();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WebSocketManager.getInstance().setWebSocketListener(this);
        loadTutors(); // refresh when coming back
    }

    @Override
    protected void onPause() {
        super.onPause();
        WebSocketManager.getInstance().removeWebSocketListener();
    }

    private void loadTutors() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(
                Request.Method.GET,
                URL_TUTORS,
                response -> {
                    try {
                        JSONArray arr;
                        if (response.trim().startsWith("[")) {
                            arr = new JSONArray(response);
                        } else {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("tutors")) arr = obj.getJSONArray("tutors");
                            else if (obj.has("data")) arr = obj.getJSONArray("data");
                            else {
                                arr = new JSONArray();
                                arr.put(obj);
                            }
                        }
                        List<TutorItem> newTutorList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject t = arr.getJSONObject(i);
                            int tutorId = t.optInt("tutorId", -1);
                            String uname = t.optString("username", t.optString("userName", ""));
                            Log.d(TAG, "Parsed tutorId=" + tutorId + " for " + uname);

                            String first = t.optString("firstName", "");
                            String last = t.optString("lastName", "");
                            String displayName = (!first.isEmpty() || !last.isEmpty()) ? (first + " " + last).trim() : null;
                            double rating = t.optDouble("totalRating", 3.5);

                            newTutorList.add(new TutorItem(tutorId, uname, displayName, rating));
                        }

                        adapter.updateTutorList(newTutorList);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed parse tutors: " + e.toString());
                    }
                },
                error -> Log.e(TAG, "Failed load tutors: " + error.toString())
        );

        queue.add(req);
    }

    // Clicks from TutorListAdapter
    @Override
    public void onTutorClicked(TutorItem tutor) {
        // Could navigate to tutor profile if needed
    }

    @Override
    public void onReviewsClicked(TutorItem tutor) {
        Intent intent = new Intent(this, TutorReviewsActivity.class);
        intent.putExtra("tutorId", tutor.tutorId);
        startActivity(intent);
    }

    // WebSocketListener methods
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "WebSocket connected in ReviewListActivity");
    }

    @Override
    public void onWebSocketMessage(String message) {
        Log.d(TAG, "WebSocket message: " + message);
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            NotificationUtils.showPushNotification(this, "New Session Update", message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d(TAG, "WebSocket closed: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e(TAG, "WebSocket error", ex);
    }
}
