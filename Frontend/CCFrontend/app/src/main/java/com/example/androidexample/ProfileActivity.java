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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    //Text Fields
    private TextView roleText;
    private TextView nameText;
    private TextView usernameText;
    private TextView bioText;
    private TextView majorText;
    private TextView classificationText;
    private TextView contactInfoText;

    //Buttons
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
    private String contactInfo;
    private String firstName;
    private String lastName;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Initialize variables

        //Initialize UI elements

        //Text Fields
        roleText = findViewById(R.id.role_text);
        nameText = findViewById(R.id.name_text);
        usernameText = findViewById(R.id.username_text);
        bioText = findViewById(R.id.bio_text);
        majorText = findViewById(R.id.major_text);
        classificationText = findViewById(R.id.classification_text);
        contactInfoText = findViewById(R.id.contactInfo_text);

        //Buttons
        menuBtn = findViewById(R.id.menu_button);
        logoutBtn = findViewById(R.id.logout_btn);
        homeBtn = findViewById(R.id.nav_home);
        sessionsBtn = findViewById(R.id.nav_sessions);
        editProfileBtn = findViewById(R.id.edit_profile_btn);


        //Call GetUserInfo TODO IMPORTANT BUT COMPLETE!!!
        //Get username from intent
        username = getIntent().getStringExtra("username");
        //Get all info from user
        GetUserInfo(username);

        //initialize user data
        //username = getIntent().getStringExtra("username");
        //userId = getIntent().getIntExtra("userId", -1);
        //isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        //isTutor = getIntent().getBooleanExtra("isTutor", false);
        //password = getIntent().getStringExtra("password");

        //Menu Bar
        drawerLayout = findViewById(R.id.drawer_layout);

        //set button listeners to be active
        homeBtn.setOnClickListener(this);
        sessionsBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);

        //Set text fields to have user data
       // usernameText.setText("@" + username);
        //if(isAdmin)
        //{
        //    roleText.setText("Admin");
        //}
        //else if(isTutor)
        //{
        //    roleText.setText("Tutor");
        //}
        //else
        //{
        //    roleText.setText("Student");
        //}
        //nameText.setText("First Last");


    }

    public void onClick(View v)
    {
        int id = v.getId();
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
            intent.putExtra("contactInfo", contactInfo);
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
                            contactInfo = response.optString("contactInfo", "Not provided");
                            isTutor = response.optBoolean("isTutor", false);
                            isAdmin = response.optBoolean("isAdmin", false);


                            //Update the TextViews with the fetched data
                            usernameText.setText("@" + username);
                            nameText.setText(firstName + " " + lastName);
                            bioText.setText(bio);
                            majorText.setText(major);
                            classificationText.setText(classification);
                            contactInfoText.setText(contactInfo);

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
}
