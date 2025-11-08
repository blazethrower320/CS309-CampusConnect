package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
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

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.java_websocket.handshake.ServerHandshake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SessionActivity extends AppCompatActivity implements WebSocketListener {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup

    private int userId;                // must be passed from login/signup
    private String username;
    private String password;

    private TextView createSession;

    private Spinner majorSpinner;
    private SearchView classSearchView;
    private RecyclerView sessionsRecycler;
    private SessionAdapter sessionAdapter;
    private ArrayList<Session> allSessions = new ArrayList<>();
    private RequestQueue requestQueue;

    // Change this to your actual backend base URL
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

        // Get values passed from login/signup
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        userId = getIntent().getIntExtra("userId", -1);


        LinearLayout homeButton = findViewById(R.id.nav_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, MainMenuActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        LinearLayout profileButton = findViewById(R.id.nav_profile);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        LinearLayout reviewsButton = findViewById(R.id.nav_reviews);
        reviewsButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, ReviewListActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
                });

        // Open sidebar when menu button clicked
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                drawerLayout.closeDrawer(findViewById(R.id.nav_view));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.nav_view));
            }
        });

        createSession.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, CreateSessionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        });


        // --- New UI wiring ---
        majorSpinner = findViewById(R.id.major_spinner);

        classSearchView = findViewById(R.id.class_search_view);
        int id = classSearchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = classSearchView.findViewById(id);
        searchText.setTextColor(Color.WHITE);
        searchText.setHintTextColor(Color.LTGRAY);

        sessionsRecycler = findViewById(R.id.sessions_recycler);

        // Setup spinner with string-array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.majors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(adapter);

        // RecyclerView + adapter
        sessionsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // inside onCreate(), replace adapter creation with:
        sessionAdapter = new SessionAdapter(allSessions, session -> {
            // Prevent tutors from joining their own session
            if (session.getTutorUsername() != null && session.getTutorUsername().equalsIgnoreCase(username)) {
                Toast.makeText(SessionActivity.this, "You cannot join your own session.", Toast.LENGTH_SHORT).show();
                return;
            }

            joinSession(session.getSessionId(), username);
        });

        sessionsRecycler.setAdapter(sessionAdapter);

        requestQueue = Volley.newRequestQueue(this);  // ✅ only once


        if (isTutor) {
            createSession.setVisibility(View.VISIBLE);
        }

// fetch sessions from API then show them
        fetchSessionsFromBackend(); // also uses the same requestQueue



        // Spinner selection listener - filter list whenever selection changes
        majorSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // do nothing
            }
        });

        // SearchView listener - filter as user types / presses search
        classSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            NotificationUtils.showPushNotification(this, "New Session Update", message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
    }

    private void joinSession(int sessionId, String joiningUsername) {
        String url = BASE_URL + "/sessions/joinSession/" + joiningUsername + "/" + sessionId;

        JsonObjectRequest joinRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Toast.makeText(SessionActivity.this, "Joined", Toast.LENGTH_SHORT).show();

                    for (Session s : allSessions) {
                        if (s.getSessionId() == sessionId) {
                            s.setJoined(true);
                            sessionAdapter.notifyDataSetChanged();

                            // --- Send push notification ---
                            sendPushForSessionJoin(s, joiningUsername);

                            break;
                        }
                    }
                },
                error -> {
                    Toast.makeText(SessionActivity.this, "Join failed", Toast.LENGTH_SHORT).show();
                    Log.e("SessionActivity", "Join failed: " + error.toString());
                }
        );

        requestQueue.add(joinRequest);
    }


    private void sendPushForSessionJoin(Session session, String joiningUsername) {
        String url = BASE_URL + "/sessions/getSessionTutor/" + session.getSessionId();

        JsonObjectRequest tutorRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    int tutorId = response.optInt("tutorId", -1);
                    if (tutorId > 0) {
                        sendTutorPush(session, joiningUsername, tutorId);
                    } else {
                        Log.e("SessionActivity", "Tutor ID not found for push for session " + session.getSessionId());
                    }
                },
                error -> Log.e("SessionActivity", "Failed to fetch tutor ID for push: " + error.toString())
        );

        requestQueue.add(tutorRequest);
    }


    private void sendTutorPush(Session session, String joiningUsername, long tutorId) {
        if (tutorId <= 0) return;

        String message = "Class: " + session.getClassName() + ", Joined by: " + joiningUsername;
        String url = BASE_URL + "/push/" + tutorId + "?msg=" + message;

        StringRequest pushRequest = new StringRequest(Request.Method.GET, url,
                response -> Log.d("SessionActivity", "Push sent successfully: " + response),
                error -> Log.e("SessionActivity", "Failed to send push: " + error.toString())
        );

        requestQueue.add(pushRequest);
    }




    private void fetchSessionsFromBackend() {
        String url = BASE_URL + SESSIONS_ENDPOINT;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        allSessions.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                // Expect fields: className, classCode, meetingLocation, meetingTime, sessionId
                                int sessionId = obj.optInt("sessionId", -1);
                                String className = obj.optString("className", "");
                                String classCode = obj.optString("classCode", "");
                                String meetingLocation = obj.optString("meetingLocation", "");
                                String meetingTime = obj.optString("meetingTime", "");

                                // Create Session with temporary tutorUsername = "Loading..."
                                Session s = new Session(sessionId, className, classCode, meetingLocation, meetingTime, "Loading...");
                                allSessions.add(s);

                                // If the backend returns tutorId in the session JSON, use it directly:
                                int tutorId = obj.optInt("tutorId", -1);
                                if (tutorId > 0) {
                                    fetchTutorUsernameById(s, tutorId);
                                } else {
                                    // Fallback if tutorId not present — use username lookup instead
                                    fetchTutorForSession(s);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("SessionActivity", "JSON parse error", e);
                            Toast.makeText(SessionActivity.this, "Failed to parse sessions", Toast.LENGTH_SHORT).show();
                        }

                        applyFilters();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SessionActivity", "Volley error: " + error.getMessage());
                        Toast.makeText(SessionActivity.this, "Failed to load sessions", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }




    /**
     * Applies spinner + search filters to the full session list and updates the RecyclerView adapter.
     */
    private void applyFilters() {
        String selectedMajor = majorSpinner.getSelectedItem() == null ? "All" : majorSpinner.getSelectedItem().toString();
        String query = classSearchView.getQuery() == null ? "" : classSearchView.getQuery().toString().trim().toLowerCase();

        ArrayList<Session> filtered = new ArrayList<>();
        for (Session s : allSessions) {
            boolean matchesMajor = selectedMajor.equals("All") || s.getClassCode().toLowerCase().contains(selectedMajor.toLowerCase()) || s.getClassName().toLowerCase().contains(selectedMajor.toLowerCase());
            boolean matchesQuery = query.isEmpty() || s.getClassName().toLowerCase().contains(query) || s.getClassCode().toLowerCase().contains(query);

            if (matchesMajor && matchesQuery) {
                filtered.add(s);
            }
        }

        sessionAdapter.updateList(filtered);
    }

    private void fetchTutorUsernameById(Session session, int tutorId) {
        String url = BASE_URL + "/tutors/info/" + tutorId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("SessionActivity", "Tutor JSON: " + response.toString());
                    String username = response.optString("username", "Unknown");
                    session.setTutorUsername(username);
                    sessionAdapter.notifyDataSetChanged();
                },
                error -> Log.e("SessionActivity", "Failed to fetch tutor username by ID", error)
        );

        requestQueue.add(request);
    }

    private void showPushNotification(String title, String message) {
        String channelId = "session_notifications";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Session Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for session updates");
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE); // hide sensitive info on lock screen
            notificationManager.createNotificationChannel(channel);
        }

        // Clean up the message: remove URLs or unnecessary backend paths
        String cleanMessage = message.replaceAll("http[s]?://\\S+", ""); // remove URLs
        cleanMessage = cleanMessage.replaceAll("/sessions.*", ""); // remove backend endpoint paths if present

        // Use BigTextStyle for longer content
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(cleanMessage)
                .setBigContentTitle(title);

        // Intent when notification is clicked (optional: open app)
        Intent intent = new Intent(this, SessionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(cleanMessage)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTimeoutAfter(20000) // 20 seconds in milliseconds
                .setContentIntent(pendingIntent);

        checkNotificationPermission();
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                // Permission already granted, safe to post notification
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, safe to post notifications
                Log.d("Notifications", "Permission granted");
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchTutorForSession(Session session) {
        String url = BASE_URL + "/sessions/getSessionTutor/" + session.getSessionId();

        JsonObjectRequest tutorRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    int tutorId = response.optInt("tutorId", -1);
                    Log.d("SessionActivity", "Tutor JSON: " + response.toString());
                    if (tutorId > 0) {
                        // Now that we have the tutorId, fetch their username
                        fetchTutorUsernameById(session, tutorId);
                    } else {
                        Log.e("SessionActivity", "Tutor ID not found for session " + session.getSessionId());
                        session.setTutorUsername("Unknown Tutor");
                        sessionAdapter.notifyDataSetChanged();
                    }
                },
                error -> {
                    Log.e("SessionActivity", "Failed to fetch tutor for session " + session.getSessionId(), error);
                    session.setTutorUsername("Unknown Tutor");
                    sessionAdapter.notifyDataSetChanged();
                }
        );

        requestQueue.add(tutorRequest);
    }





}
