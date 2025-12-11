package com.example.androidexample;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * SessionActivity displays all tutoring sessions, handles filtering,
 * joining sessions, navigation drawer actions, and real-time WebSocket updates.
 *
 * @author William Rossow
 */
public class SessionActivity extends AppCompatActivity implements WebSocketListener {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private TextView createSession;
    private Spinner majorSpinner;
    private SearchView classSearchView;
    private RecyclerView sessionsRecycler;
    private SessionAdapter sessionAdapter;
    private final ArrayList<Session> allSessions = new ArrayList<>();
    private RequestQueue requestQueue;

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String SESSIONS_ENDPOINT = "/sessions";
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;


    /**
     * Initializes the UI, loads user info, configures navigation,
     * sets up search filters and populates the session list.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        drawerLayout = findViewById(R.id.drawer_layout);
        createSession = findViewById(R.id.btn_create_session);
        menuButton = findViewById(R.id.menu_button);
        majorSpinner = findViewById(R.id.major_spinner);
        classSearchView = findViewById(R.id.class_search_view);
        sessionsRecycler = findViewById(R.id.sessions_recycler);
        requestQueue = Volley.newRequestQueue(this);

        // Retrieve logged-in user info
        User user = User.getInstance();
        String username = user.getUsername();
        boolean isTutor = user.isTutor();
        boolean isAdmin = user.isAdmin();

        setupNavigation();
        setupSearchAndSpinner();
        setupRecycler(username);

        // Tutors can create sessions
        if (isTutor) createSession.setVisibility(View.VISIBLE);
        createSession.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateSessionActivity.class));
            finish();
        });
        if (isAdmin) createSession.setVisibility(View.GONE);

        fetchSessionsFromBackend();
    }

    /**
     * Configures navigation drawer buttons and hamburger icon behavior.
     */
    private void setupNavigation() {
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);
        LinearLayout reviewsButton = findViewById(R.id.nav_reviews);

        homeButton.setOnClickListener(v -> navigateTo(MainMenuActivity.class));
        profileButton.setOnClickListener(v -> navigateTo(ProfileActivity.class));
        reviewsButton.setOnClickListener(v -> navigateTo(ReviewListActivity.class));

        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                drawerLayout.closeDrawer(findViewById(R.id.nav_view));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.nav_view));
            }
        });
    }

    /**
     * Starts the selected Activity and closes the navigation drawer.
     */
    private void navigateTo(Class<?> target) {
        startActivity(new Intent(SessionActivity.this, target));
        finish();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Initializes the SearchView, filter spinner, and attaches listeners.
     */
    private void setupSearchAndSpinner() {

        SearchView searchView = findViewById(R.id.class_search_view);
        searchView.post(() -> styleSearchView(searchView));

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.majors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(adapter);

        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });
    }

    /**
     * Applies visual styling to the SearchView components.
     */
    private void styleSearchView(SearchView searchView) {

        // Clear search_plate background
        int plateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View plate = searchView.findViewById(plateId);
        if (plate != null) {
            plate.setBackgroundColor(Color.TRANSPARENT);
        }

        // Clear search_edit_frame background (NEW REQUIRED FIX)
        int frameId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_edit_frame", null, null);
        View frame = searchView.findViewById(frameId);
        if (frame != null) {
            frame.setBackgroundColor(Color.TRANSPARENT);
        }

        // Text
        int textId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(textId);
        if (searchText != null) {
            searchText.setHintTextColor(Color.parseColor("#FFFFFF"));
            searchText.setTextColor(Color.WHITE);
        }

        // Magnifying glass
        int magId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magIcon = searchView.findViewById(magId);
        if (magIcon != null) {
            magIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }

        // Close (X)
        int closeId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeIcon = searchView.findViewById(closeId);
        if (closeIcon != null) {
            closeIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
    }



    /**
     * Configures the RecyclerView and handling for joining sessions.
     */
    private void setupRecycler(String username) {
        sessionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        sessionAdapter = new SessionAdapter(allSessions, session -> {
            if (session.getTutorUsername() != null && session.getTutorUsername().equalsIgnoreCase(username)) {
                Toast.makeText(this, "You cannot join your own session.", Toast.LENGTH_SHORT).show();
                return;
            }
            joinSession(session.getSessionId(), username);
        });
        sessionsRecycler.setAdapter(sessionAdapter);
    }

    /**
     * Registers as the WebSocket listener when resuming the Activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        WebSocketManager.getInstance().setWebSocketListener(this);
    }

    /**
     * Unregisters WebSocket listener when Activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        WebSocketManager.getInstance().removeWebSocketListener();
    }

    /** WebSocket callback: connection opened. */
    @Override public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected in SessionActivity");
    }

    /** WebSocket callback: message received. */
    @Override public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Message: " + message);
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            showPushNotification("New Session Update", message);
        });
    }

    /** WebSocket callback: connection closed. */
    @Override public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed: " + reason);
    }

    /** WebSocket callback: error occurred. */
    @Override public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
    }

    /**
     * Sends join request to backend and updates UI state when successful.
     */
    private void joinSession(int sessionId, String joiningUsername) {
        String url = BASE_URL + "/sessions/joinSession/" + joiningUsername + "/" + sessionId;

        JsonObjectRequest joinRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Toast.makeText(this, "Joined", Toast.LENGTH_SHORT).show();
                    for (Session s : allSessions) {
                        if (s.getSessionId() == sessionId) {
                            s.setJoined(true);
                            sessionAdapter.notifyDataSetChanged();
                            sendPushForSessionJoin(s, joiningUsername);
                            break;
                        }
                    }
                },
                error -> Log.e("SessionActivity", "Join failed: " + error)
        );
        requestQueue.add(joinRequest);
    }

    /**
     * Retrieves tutor ID for the session and triggers push notification.
     */
    private void sendPushForSessionJoin(Session session, String joiningUsername) {
        String url = BASE_URL + "/sessions/getSessionTutor/" + session.getSessionId();

        JsonObjectRequest tutorRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    int tutorId = response.optInt("tutorId", -1);
                    if (tutorId > 0) sendTutorPush(session, joiningUsername, tutorId);
                },
                error -> Log.e("SessionActivity", "Failed to fetch tutor ID: " + error)
        );
        requestQueue.add(tutorRequest);
    }

    /**
     * Sends backend request to notify the tutor about a student joining.
     */
    private void sendTutorPush(Session session, String joiningUsername, long tutorId) {
        if (tutorId <= 0) return;
        String message = "Class: " + session.getClassName() + ", Joined by: " + joiningUsername;
        String url = BASE_URL + "/push/" + tutorId + "?msg=" + message;

        StringRequest pushRequest = new StringRequest(Request.Method.GET, url,
                response -> Log.d("SessionActivity", "Push sent: " + response),
                error -> Log.e("SessionActivity", "Push failed: " + error)
        );
        requestQueue.add(pushRequest);
    }

    /**
     * Fetches all tutoring sessions from the backend and updates the UI.
     * Populates the allSessions list and triggers tutor lookup per session.
     */
    private void fetchSessionsFromBackend() {
        String url = BASE_URL + SESSIONS_ENDPOINT;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    allSessions.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int sessionId = obj.optInt("sessionId", -1);
                            String className = obj.optString("className", "");
                            String classCode = obj.optString("classCode", "");
                            String location = obj.optString("meetingLocation", "");
                            String time = obj.optString("meetingTime", "");
                            Session s = new Session(sessionId, className, classCode, location, -1, time, "Loading...");
                            allSessions.add(s);

                            int tutorId = obj.optInt("tutorId", -1);
                            if (tutorId > 0) fetchTutorUsernameById(s, tutorId);
                            else fetchTutorForSession(s);
                        }
                    } catch (JSONException e) {
                        Log.e("SessionActivity", "Parse error", e);
                    }
                    applyFilters();
                },
                error -> Toast.makeText(this, "Failed to load sessions", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Fetches tutor username associated with a given tutor ID.
     */
    private void fetchTutorUsernameById(Session session, int tutorId) {
        String url = BASE_URL + "/tutors/info/" + tutorId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String username = response.optString("username", "Unknown");

                    // Nested user object
                    JSONObject userObj = response.optJSONObject("user");
                    int tutorUserId = -1;
                    if (userObj != null) {
                        tutorUserId = userObj.optInt("userId", -1);
                    }

                    session.setTutorUsername(username);
                    session.setTutorUserId(tutorUserId);
                    session.setTutorId(tutorId);

                    Log.e("tutorUserId", String.valueOf(tutorUserId));
                    sessionAdapter.notifyDataSetChanged();
                },
                error -> Log.e("SessionActivity", "Tutor fetch failed", error)
        );

        requestQueue.add(request);
    }


    /**
     * Fetches tutor information for a session when tutor ID was not present initially.
     */
    private void fetchTutorForSession(Session session) {
        String url = BASE_URL + "/sessions/getSessionTutor/" + session.getSessionId();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    int tutorId = response.optInt("tutorId", -1);
                    if (tutorId > 0) fetchTutorUsernameById(session, tutorId);
                    else {
                        session.setTutorUsername("Unknown Tutor");
                        session.setTutorUserId(-1); // <-- ADD THIS
                    }
                },
                error -> {
                    session.setTutorUsername("Unknown Tutor");
                    session.setTutorUserId(-1); // <-- ADD THIS
                }
        );
        requestQueue.add(request);
    }


    /**
     * Applies search and major-based filters to the visible session list.
     */
    private void applyFilters() {
        String selectedMajor = majorSpinner.getSelectedItem() == null ? "All" :
                majorSpinner.getSelectedItem().toString();
        String query = classSearchView.getQuery() == null ? "" :
                classSearchView.getQuery().toString().trim().toLowerCase();

        ArrayList<Session> filtered = new ArrayList<>();
        for (Session s : allSessions) {
            boolean matchesMajor = selectedMajor.equals("All") ||
                    s.getClassCode().toLowerCase().contains(selectedMajor.toLowerCase()) ||
                    s.getClassName().toLowerCase().contains(selectedMajor.toLowerCase());
            boolean matchesQuery = query.isEmpty() ||
                    s.getClassName().toLowerCase().contains(query) ||
                    s.getClassCode().toLowerCase().contains(query);

            if (matchesMajor && matchesQuery) filtered.add(s);
        }
        sessionAdapter.updateList(filtered);
    }

    /**
     * Displays a push notification related to session updates.
     */
    private void showPushNotification(String title, String message) {
        String channelId = "session_notifications";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Session Notifications", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        String cleanMsg = message.replaceAll("http[s]?://\\S+", "").replaceAll("/sessions.*", "");

        Intent intent = new Intent(this, SessionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(cleanMsg)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(cleanMsg))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        checkNotificationPermission();
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Requests the POST_NOTIFICATIONS permission on Android 13+ if not yet granted.
     */
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE);
        }
    }
}