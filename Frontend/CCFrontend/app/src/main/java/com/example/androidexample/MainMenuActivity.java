package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String URL_DELETE_USER = BASE_URL + "/users/deleteUser";

    private Button logoutBtn;
    //private Button changeStatusBtn;
    //private Button setNumClassBtn;
    private TextView welcomeText;

    private Button deleteAccountBtn;

    //private LinearLayout numClassPanel;

    private EditText numClassEdt;


    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private int userId;                // must be passed from login/signup
    private String username;
    private String password;           // pass this from login/signup if required

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        deleteAccountBtn = findViewById(R.id.delete_account_btn);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);

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



        // Admin-only buttons
        if (isAdmin)
        {

        }
        else if (isTutor) // Tutor-only buttons
        {
            //numClassPanel.setVisibility(View.VISIBLE);
            //changeStatusBtn.setVisibility(View.GONE);
        }
        else
        {
            //changeStatusBtn.setVisibility(View.GONE);
            //numClassPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn)
        {
            startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
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
