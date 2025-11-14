package com.example.androidexample;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

        // --- Get user info from singleton ---
        User user = User.getInstance();
        String username = user.getUsername();
        boolean isTutor = user.isTutor();
        boolean isAdmin = user.isAdmin();

        setupNavigation();
        setupSearchAndSpinner();
        setupRecycler(username);

        if (isTutor) createSession.setVisibility(View.VISIBLE);
        createSession.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateSessionActivity.class));
            finish();
        });

        fetchSessionsFromBackend();
    }

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

    private void navigateTo(Class<?> target) {
        startActivity(new Intent(SessionActivity.this, target));
        finish();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setupSearchAndSpinner() {
        int id = classSearchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = classSearchView.findViewById(id);
        searchText.setTextColor(Color.WHITE);
        searchText.setHintTextColor(Color.LTGRAY);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.majors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(adapter);

        majorSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { applyFilters(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        classSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { applyFilters(); return true; }
            @Override public boolean onQueryTextChange(String newText) { applyFilters(); return true; }
        });
    }

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

    // ---------- WebSocket Methods ----------
    @Override public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected in SessionActivity");
    }

    @Override public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Message: " + message);
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            showPushNotification("New Session Update", message);
        });
    }

    @Override public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed: " + reason);
    }

    @Override public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
    }

    // ---------- Session Joining + Push ----------
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

    // ---------- Session Fetching ----------
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
                            Session s = new Session(sessionId, className, classCode, location, time, "Loading...");
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

    private void fetchTutorUsernameById(Session session, int tutorId) {
        String url = BASE_URL + "/tutors/info/" + tutorId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String username = response.optString("username", "Unknown");
                    session.setTutorUsername(username);
                    sessionAdapter.notifyDataSetChanged();
                },
                error -> Log.e("SessionActivity", "Tutor fetch failed", error)
        );
        requestQueue.add(request);
    }

    private void fetchTutorForSession(Session session) {
        String url = BASE_URL + "/sessions/getSessionTutor/" + session.getSessionId();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    int tutorId = response.optInt("tutorId", -1);
                    if (tutorId > 0) fetchTutorUsernameById(session, tutorId);
                    else session.setTutorUsername("Unknown Tutor");
                },
                error -> session.setTutorUsername("Unknown Tutor")
        );
        requestQueue.add(request);
    }

    // ---------- Filtering ----------
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

    // ---------- Notifications ----------
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
