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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    private RecyclerView pastSessionsRecyclerView;
    private PastSessionAdapter sessionAdapter;
    // UPDATED: The list now holds JSONObjects directly, not Session objects.
    private List<JSONObject> pastSessionList;

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

        //Initialize UI elements
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
        homeBtn = findViewById(R.id.nav_home);
        sessionsBtn = findViewById(R.id.nav_sessions);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        drawerLayout = findViewById(R.id.drawer_layout);

        //initialize user data from Intent
        username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        password = getIntent().getStringExtra("password");

        // Set up Past Sessions RecyclerView
        pastSessionsRecyclerView = findViewById(R.id.past_sessions_recycler_view);
        pastSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Fetch data from the backend and populate the RecyclerView
        loadPastSessionData();

        // Fetch user info and populate profile fields
        GetUserInfo(username);

        //set button listeners to be active
        msgBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        sessionsBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);

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
    }

    public void onClick(View v)
    {
        int id = v.getId();
        if(id == R.id.message_btn)
        {
            Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            startActivity(intent);
        }
        if (id == R.id.logout_btn)
        {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
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
    }

    public void GetUserInfo(String username)
    {
        final String URL_GET_USER = "http://coms-3090-037.class.las.iastate.edu:8080/users/find/" + username;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_GET_USER,
                null,
                response -> {
                    Log.d("Volley Success", "User Info Response: " + response.toString());
                    try
                    {
                        firstName = response.optString("firstName", "N/A");
                        lastName = response.optString("lastName", "N/A");
                        bio = response.optString("bio", "No bio available.");
                        major = response.optString("major", "Undeclared");
                        classification = response.optString("classification", "N/A");
                        isTutor = response.optBoolean("isTutor", false);
                        isAdmin = response.optBoolean("isAdmin", false);

                        usernameText.setText("@" + username);
                        nameText.setText(firstName + " " + lastName);
                        bioText.setText(bio);
                        majorText.setText(major);
                        classificationText.setText(classification);

                        if (isAdmin) roleText.setText("Admin");
                        else if (isTutor) roleText.setText("Tutor");
                        else roleText.setText("Student");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing user data!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    VolleyLog.e("Volley Error", error.getMessage());
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                    if (error.networkResponse != null)
                    {
                        Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }

    private void loadPastSessionData() {
        // Initialize the list and adapter first.
        pastSessionList = new ArrayList<>();
        sessionAdapter = new PastSessionAdapter(pastSessionList);
        pastSessionsRecyclerView.setAdapter(sessionAdapter);

        final String URL_GET_SESSIONS = "http://coms-3090-037.class.las.iastate.edu:8080/sessions/inactive";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_GET_SESSIONS,
                null,
                response -> {
                    Log.d("Volley Success", "Past Sessions Response: " + response.toString());
                    try {
                        // Clear the list to prevent duplicates if this method is called again.
                        pastSessionList.clear();

                        // Loop through each JSON object from the server.
                        for (int i = 0; i < response.length(); i++)
                        {
                            // UPDATED: Get the JSONObject and add it directly to the list.
                            JSONObject sessionObject = response.getJSONObject(i);
                            pastSessionList.add(sessionObject);
                        }
                        // Notify the adapter that the underlying data has changed.
                        sessionAdapter.notifyDataSetChanged();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing session data!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    VolleyLog.e("Volley Error", "Failed to fetch past sessions: " + error.getMessage());
                    Toast.makeText(ProfileActivity.this, "Failed to fetch past sessions.", Toast.LENGTH_SHORT).show();
                    if (error.networkResponse != null)
                    {
                        Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                    }
                }
        );
        queue.add(jsonArrayRequest);
    }
}
