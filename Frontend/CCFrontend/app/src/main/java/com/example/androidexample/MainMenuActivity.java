package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.MediaRouteButton;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener, WebSocketListener {
    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String URL_DELETE_USER = BASE_URL + "/users/deleteUser";

    private RequestQueue requestQueue;
    private WebSocketManager wsManager;
    private Button logoutBtn;
    //private Button changeStatusBtn;
    //private Button setNumClassBtn;
    private TextView welcomeText;

    private Button deleteAccountBtn;

    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    private ProgressBar loadingSpinner;

    //private LinearLayout numClassPanel;

    private EditText numClassEdt;


    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private int userId;                // must be passed from login/signup
    private String username;
    private String password;           // pass this from login/signup if required

    private RecyclerView activeSessionsRecycler;
    private ActiveSessionsAdapter activeSessionsAdapter;
    private ArrayList<Session> activeSessions = new ArrayList<>();
    private TextView emptySessionsMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        deleteAccountBtn = findViewById(R.id.delete_account_btn);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);

        loadingSpinner = findViewById(R.id.loading_spinner);

        requestQueue = Volley.newRequestQueue(this);

        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        LinearLayout sessionsBtn = findViewById(R.id.nav_sessions);
        sessionsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, SessionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        LinearLayout profileBtn = findViewById(R.id.nav_profile);
        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        LinearLayout reviewsBtn = findViewById(R.id.nav_reviews);
        reviewsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ReviewListActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });


        // Get values passed from login/signup
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        userId = getIntent().getIntExtra("userId", -1);

        // UI
        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Welcome, " + username + "!");

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);
        //setNumClassBtn.setOnClickListener(this);

        deleteAccountBtn.setOnClickListener(v -> DeleteUser());

        activeSessionsRecycler = findViewById(R.id.active_sessions_recycler);
        emptySessionsMessage = findViewById(R.id.empty_sessions_message);

        activeSessionsAdapter = new ActiveSessionsAdapter(activeSessions);
        activeSessionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        activeSessionsRecycler.setAdapter(activeSessionsAdapter);


        fetchUserSessions();


        // Admin-only buttons
        if (isAdmin)
        {

        }
        else if (isTutor) {
            connectWebSocketForTutor();
        }
        else
        {
            //changeStatusBtn.setVisibility(View.GONE);
            //numClassPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWebSocketOpen(org.java_websocket.handshake.ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected in MainMenuActivity");
    }

    @Override
    public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Received: " + message);
        runOnUiThread(() -> showPushNotification("Session Update", message));
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
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
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn)
        {
            startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
            if (wsManager != null) {
                wsManager.disconnectWebSocket();
            }
            finish();
        }
        if (v.getId() == R.id.delete_account_btn)
        {
            DeleteUser();
            startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
            finish();
        }
    }

    private void DeleteUser()
    {
        if (username == null || password == null)
        {
            Toast.makeText(this, "Missing credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBodyJson = new JSONObject();
        try
        {
            requestBodyJson.put("username", username);
            requestBodyJson.put("password", password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        final String requestBody = requestBodyJson.toString();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, // changed from DELETE to POST
                URL_DELETE_USER,
                response -> {
                    Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
                    finish();
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(this, "Unable to delete account", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }


    private void fetchUserSessions() {
        String url = BASE_URL + "/sessions/user/" + userId;
        loadingSpinner.setVisibility(View.VISIBLE);
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    activeSessions.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int sessionId = obj.optInt("sessionId", -1);
                            String className = obj.optString("className", "");
                            String meetingLocation = obj.optString("meetingLocation", "");
                            String meetingTime = obj.optString("meetingTime", "");
                            JSONObject tutorObj = obj.optJSONObject("tutor");

                            String tutorName = tutorObj != null ? tutorObj.optString("username", "Unknown Tutor") : "Unknown Tutor";

                            Session s = new Session(sessionId, className, "", meetingLocation, meetingTime, tutorName);
                            activeSessions.add(s);
                        }
                    } catch (JSONException e) {
                        Log.e("MainMenu", "JSON parse error", e);
                        loadingSpinner.setVisibility(View.GONE);
                    }

                    loadingSpinner.setVisibility(View.GONE);

                    // Toggle empty message visibility
                    if (activeSessions.isEmpty()) {
                        emptySessionsMessage.setVisibility(View.VISIBLE);
                    } else {
                        emptySessionsMessage.setVisibility(View.GONE);
                    }

                    activeSessionsAdapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e("MainMenu", "Session fetch failed", error);
                    loadingSpinner.setVisibility(View.GONE);
                    emptySessionsMessage.setVisibility(View.VISIBLE);
                }
        );

        Volley.newRequestQueue(this).add(request);
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

    private void connectWebSocketForTutor() {
        wsManager = WebSocketManager.getInstance();

        // Only connect if not already connected
        if (wsManager.isConnected()) {
            wsManager.setWebSocketListener(this);
            Log.d("WebSocket", "Already connected, listener set");
            return;
        }

        String url = BASE_URL + "/tutors/getTutorFromUserId/" + userId; // API to get tutorId
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    int tutorId = response.optInt("tutorId", -1);
                    if (tutorId > 0) {
                        String wsUrl = "ws://coms-3090-037.class.las.iastate.edu:8080/push/" + tutorId;
                        wsManager.setWebSocketListener(this);
                        wsManager.connectWebSocket(wsUrl);
                        Log.d("WebSocket", "Connecting to WS: " + wsUrl);
                    } else {
                        Log.e("WebSocket", "Tutor ID not found");
                    }
                },
                error -> Log.e("WebSocket", "Failed to fetch tutor ID", error)
        );

        requestQueue.add(request);
    }




    private void SetNumClasses() {
        //Get Text from field
        String classCountStr = numClassEdt.getText().toString();
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "User information not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (classCountStr.isEmpty()) {
            Toast.makeText(this, "Please enter the number of classes.", Toast.LENGTH_SHORT).show();
            return;
        }

        //URL for tutor put
        final String URL_SET_CLASSES = "http://coms-3090-037.class.las.iastate.edu:8080/tutors/editTotalClasses";
        Log.d("Volley URL", "PUT Request URL: " + URL_SET_CLASSES);

        // Request Body
        JSONObject requestBodyJson = new JSONObject();
        try {
            int classCount = Integer.parseInt(classCountStr);
            requestBodyJson.put("username", username);
            requestBodyJson.put("totalClasses", classCount);
            requestBodyJson.put("rating", null);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = requestBodyJson.toString();
        Log.d("Volley Body", "PUT Request Body: " + requestBody);


        //Create Put request
        StringRequest putRequest = new StringRequest(
                Request.Method.PUT, // Use the PUT method directly
                URL_SET_CLASSES,
                response -> {
                    // Success listener
                    Log.d("Volley Success", "SetNumClasses Response: " + response);
                    Toast.makeText(MainMenuActivity.this, "Class count updated successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Error listener with detailed logging
                    Log.e("Volley Error", "SetNumClasses Error: " + error.toString());
                    String responseBody = "";
                    if (error.networkResponse != null) {
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("Volley Error", "Response Body: " + responseBody);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(MainMenuActivity.this, "Failed to update: " + responseBody, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }

        };

        //Add req to queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(putRequest);
    }



}
