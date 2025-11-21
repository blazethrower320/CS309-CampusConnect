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

/**
 * Activity that displays a list of tutors and allows the user to view reviews.
 * <p>
 * Users can search for tutors, open the navigation drawer to navigate to other
 * activities, and view tutor reviews by clicking on the review button. This activity
 * also listens for WebSocket messages for real-time updates.
 * </p>
 *
 * Implements {@link TutorListAdapter.OnTutorClickListenerWithReviews} to handle tutor
 * clicks and review clicks, and {@link WebSocketListener} to handle WebSocket events.
 *
 * @author William Rossow
 */
public class ReviewListActivity extends AppCompatActivity implements TutorListAdapter.OnTutorClickListenerWithReviews, WebSocketListener {

    /** Log tag for debugging */
    private static final String TAG = "ReviewListActivity";

    /** URL for fetching tutors */
    private static final String URL_TUTORS = "http://coms-3090-037.class.las.iastate.edu:8080/tutors";

    /** Button to open the navigation drawer */
    private ImageButton menuButton;

    /** Drawer layout for navigation */
    private DrawerLayout drawerLayout;

    /** RecyclerView for displaying tutors */
    private RecyclerView recyclerView;

    /** Adapter for the RecyclerView */
    private TutorListAdapter adapter;

    /** List of tutors displayed */
    private List<TutorItem> tutorList = new ArrayList<>();

    /** Current user's username */
    private String username;

    /** Current user's password */
    private String password;

    /** Current user's ID */
    private int userId;

    /** Whether the current user is a tutor */
    private boolean isTutor;

    /** Whether the current user is an admin */
    private boolean isAdmin;

    /**
     * Called when the activity is first created.
     * Initializes the navigation drawer, RecyclerView, search functionality,
     * loads user session info, sets up nav buttons, and loads tutor data.
     *
     * @param savedInstanceState Bundle containing activity state
     */
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

    /**
     * Sets up the navigation drawer buttons to navigate to other activities.
     */
    private void setupNavButtons() {
        LinearLayout homeButton = findViewById(R.id.nav_home);
        homeButton.setOnClickListener(v -> navigateTo(MainMenuActivity.class));

        LinearLayout profileButton = findViewById(R.id.nav_profile);
        profileButton.setOnClickListener(v -> navigateTo(ProfileActivity.class));

        LinearLayout sessionsButton = findViewById(R.id.nav_sessions);
        sessionsButton.setOnClickListener(v -> navigateTo(SessionActivity.class));
    }

    /**
     * Navigates to the specified activity and closes the navigation drawer.
     *
     * @param targetActivity Activity class to navigate to
     */
    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(ReviewListActivity.this, targetActivity);
        startActivity(intent);
        finish();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Called when activity resumes. Sets the WebSocket listener and refreshes tutor data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        WebSocketManager.getInstance().setWebSocketListener(this);
        loadTutors(); // refresh when coming back
    }

    /**
     * Called when activity pauses. Removes the WebSocket listener.
     */
    @Override
    protected void onPause() {
        super.onPause();
        WebSocketManager.getInstance().removeWebSocketListener();
    }

    /**
     * Loads the list of tutors from the server and updates the RecyclerView.
     */
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

    /**
     * Called when a tutor item is clicked.
     *
     * @param tutor TutorItem that was clicked
     */
    @Override
    public void onTutorClicked(TutorItem tutor) {
        // Could navigate to tutor profile if needed
    }

    /**
     * Called when the review button of a tutor item is clicked.
     * Navigates to the TutorReviewsActivity.
     *
     * @param tutor TutorItem whose reviews are requested
     */
    @Override
    public void onReviewsClicked(TutorItem tutor) {
        Intent intent = new Intent(this, TutorReviewsActivity.class);
        intent.putExtra("tutorId", tutor.tutorId);
        startActivity(intent);
    }

    /**
     * Called when WebSocket connection opens.
     *
     * @param handshakedata WebSocket handshake data
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "WebSocket connected in ReviewListActivity");
    }

    /**
     * Called when a WebSocket message is received.
     *
     * @param message Message content
     */
    @Override
    public void onWebSocketMessage(String message) {
        Log.d(TAG, "WebSocket message: " + message);
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            NotificationUtils.showPushNotification(this, "New Session Update", message);
        });
    }

    /**
     * Called when the WebSocket connection closes.
     *
     * @param code WebSocket close code
     * @param reason Close reason
     * @param remote Whether the closure was initiated by the remote host
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d(TAG, "WebSocket closed: " + reason);
    }

    /**
     * Called when there is an error in the WebSocket connection.
     *
     * @param ex Exception encountered
     */
    @Override
    public void onWebSocketError(Exception ex) {
        Log.e(TAG, "WebSocket error", ex);
    }
}
