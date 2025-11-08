package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, WebSocketListener
{
    private RecyclerView pastSessionsRecyclerView;
    private PastSessionAdapter sessionAdapter;
    private List<Session> pastSessionList;
    //Text Fields
    private TextView roleText;
    private TextView nameText;
    private TextView usernameText;
    private TextView bioText;
    private TextView majorText;
    private TextView classificationText;

    //Tutor Profile fields
    private LinearLayout ratingLayout;
    private TextView tutorRatingText;
    private TextView tutorRatingValue;

    //Buttons
    private Button msgBtn; //Message button on profile
    private Button logoutBtn; //Logout button
    private ImageButton editProfileBtn; //Edit profile btn
    private ImageButton menuBtn; //Three line btn


    private DrawerLayout drawerLayout; //Menu bar layout

    //Menu Bar buttons
    private LinearLayout homeBtn; //Home btn inside of menu bar
    private LinearLayout sessionsBtn; //Sessions btn inside of menu bar

    private LinearLayout reviewsBtn; //Reviews btn inside of menu bar

    //User variables
    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private int userId;                // must be passed from login/signup
    private String username;
    private String password;           // pass this from login/signup if required
    private String bio;
    private String major;
    private String classification;
    private String firstName;
    private String lastName;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Initialize variables

        //Initialize UI elements

        //Tutor UI
        ratingLayout = findViewById(R.id.rating_layout);
        tutorRatingText = findViewById(R.id.tutor_rating_text);
        tutorRatingValue = findViewById(R.id.rating_value);

        //Text Fields
        roleText = findViewById(R.id.role_text);
        nameText = findViewById(R.id.name_text);
        usernameText = findViewById(R.id.username_text);
        bioText = findViewById(R.id.bio_text);
        majorText = findViewById(R.id.major_text);
        classificationText = findViewById(R.id.classification_text);

        //Buttons
        msgBtn = findViewById(R.id.message_btn);
        menuBtn = findViewById(R.id.menu_button);
        logoutBtn = findViewById(R.id.logout_btn);
        homeBtn = findViewById(R.id.nav_home);
        sessionsBtn = findViewById(R.id.nav_sessions);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        reviewsBtn = findViewById(R.id.nav_reviews);

        //Past Sessions
        pastSessionsRecyclerView = findViewById(R.id.past_sessions_recycler_view);
        pastSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // In a real app, you would fetch this data from your backend
        loadDummySessionData();

        sessionAdapter = new PastSessionAdapter(pastSessionList);
        pastSessionsRecyclerView.setAdapter(sessionAdapter);


        //Call GetUserInfo TODO IMPORTANT BUT COMPLETE!!!
        //Get username from intent
        username = getIntent().getStringExtra("username");
        //Get all info from user
        GetUserInfo(username);

        //initialize user data
        username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        password = getIntent().getStringExtra("password");

        //Menu Bar
        drawerLayout = findViewById(R.id.drawer_layout);

        //set button listeners to be active
        msgBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        sessionsBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);
        reviewsBtn.setOnClickListener(this);


        //Set text fields to have user data
        if(isAdmin)
        {
            roleText.setText("Admin");
            ratingLayout.setVisibility(View.GONE);
            tutorRatingText.setVisibility(View.GONE);
        }
        else if(isTutor)
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
        nameText.setText("First Last");


    }

    public void onClick(View v)
    {
        int id = v.getId();
        if(id == R.id.message_btn)
        {
            //TODO not all fields needed
            Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("bio", bio);
            intent.putExtra("major", major);
            intent.putExtra("classification", classification);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if (id == R.id.logout_btn)
        {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        }
        if(id == R.id.menu_button)
        {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
            {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }

        if(id == R.id.edit_profile_btn)
        {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("bio", bio);
            intent.putExtra("major", major);
            intent.putExtra("classification", classification);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if(id == R.id.nav_sessions)
        {
            Intent intent = new Intent(ProfileActivity.this, SessionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if(id == R.id.nav_home)
        {
            Intent intent = new Intent(ProfileActivity.this, MainMenuActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if(id == R.id.nav_reviews)
        {
            Intent intent = new Intent(ProfileActivity.this, ReviewListActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
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

    public void GetUserInfo(String username)
    {
            // The URL for the GET request, with the username passed in the path.
            final String URL_GET_USER = "http://coms-3090-037.class.las.iastate.edu:8080/users/find/" + username;

            // Create a request queue
            RequestQueue queue = Volley.newRequestQueue(this);

            //Get Request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL_GET_USER,
                    null, // No request body for a GET request
                    response -> {
                        Log.d("Volley Success", "User Info Response: " + response.toString());
                        try
                        {
                            // Extract data from the JSON object.
                            // The keys ("name", "bio", etc.) MUST match the keys in the JSON response from your server.
                            firstName = response.optString("firstName", "N/A"); // Using optString provides a default value if the key is missing
                            lastName = response.optString("lastName", "N/A"); // Using optString provides a default value if the key is missing
                            bio = response.optString("bio", "No bio available.");
                            major = response.optString("major", "Undeclared");
                            classification = response.optString("classification", "N/A");
                            isTutor = response.optBoolean("isTutor", false);
                            isAdmin = response.optBoolean("isAdmin", false);


                            //Update the TextViews with the fetched data
                            usernameText.setText("@" + username);
                            nameText.setText(firstName + " " + lastName);
                            bioText.setText(bio);
                            majorText.setText(major);
                            classificationText.setText(classification);

                            // Optionally, update the role based on the fetched data for accuracy
                            if (isAdmin)
                            {
                                roleText.setText("Admin");
                            }
                            else if (isTutor)
                            {
                                roleText.setText("Tutor");
                            }
                            else
                            {
                                roleText.setText("Student");
                            }

                        }
                        catch (Exception e)
                        {
                            // This catches errors during JSON parsing
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error parsing user data!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        // This is the error listener.
                        VolleyLog.e("Volley Error", error.getMessage());
                        Toast.makeText(ProfileActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                        // You can add more detailed error logging here if needed, like checking the network response
                        if (error.networkResponse != null)
                        {
                            Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                        }
                    }
            );

            // Add the request to the queue to be executed
            queue.add(jsonObjectRequest);
    }

    //TODO Talk with Preet to load past session data
    private void loadDummySessionData()
    {
        pastSessionList = new ArrayList<>();
        //pastSessionList.add(new Session(1, "Software Dev Practices", "COM S 309", "Nov 4, 2025", "3:00", "TutorExample"));
        //pastSessionList.add(new Session(2, "Math Class", "MATH 265", "Nov 1, 2025", "3:00", "TutorExample"));// Add more sessions as needed
    }
}
