package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private Button changeStatusBtn;
    private Button deleteBtn;
    private TextView welcomeText;

    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private int userId;                // must be passed from login/signup
    private String username;
    private String password;           // pass this from login/signup if required

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Get values passed from login/signup
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password"); // make sure to pass from login
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        userId = getIntent().getIntExtra("userId", -1);

        // UI
        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Welcome, " + username + "!");

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        changeStatusBtn = findViewById(R.id.change_status_btn);
        changeStatusBtn.setOnClickListener(this);

        deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(this);

        // Only admins see tutor toggle
        if (isAdmin) {
            changeStatusBtn.setVisibility(View.VISIBLE);
            fetchTutorStatus();
        } else {
            changeStatusBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn) {
            startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
            finish();
        } else if (v.getId() == R.id.change_status_btn) {
            toggleTutorStatus();
        } else if (v.getId() == R.id.delete_btn) {
            DeleteUser();
        }
    }

    private void fetchTutorStatus() {
        String url = BASE_URL + "/users/IsTutor/" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    isTutor = Boolean.parseBoolean(response.trim());
                    updateStatusButtonText();
                },
                error -> Log.e("Volley Error", "Failed to fetch tutor status: " + error.toString()));

        Volley.newRequestQueue(this).add(request);
    }

    private void toggleTutorStatus() {
        String url = BASE_URL + "/users/setTutor";
        final boolean newStatus = !isTutor;

        StringRequest request = new StringRequest(Request.Method.PATCH, url,
                response -> {
                    isTutor = newStatus;
                    updateStatusButtonText();
                    Toast.makeText(this, "Tutor status updated", Toast.LENGTH_SHORT).show();
                },
                error -> Log.e("Volley Error", "Failed to toggle tutor status: " + error.toString())) {

            @Override
            public byte[] getBody() {
                String body = "{"
                        + "\"userID\":" + userId + ","
                        + "\"isTutor\":" + newStatus
                        + "}";
                return body.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateStatusButtonText() {
        if (isTutor) {
            changeStatusBtn.setText("Set as Non-Tutor");
        } else {
            changeStatusBtn.setText("Set as Tutor");
        }
    }

    private void DeleteUser() {
        if (username == null || password == null) {
            Toast.makeText(this, "Missing credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("username", username);
            requestBodyJson.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        final String requestBody = requestBodyJson.toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
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
}
