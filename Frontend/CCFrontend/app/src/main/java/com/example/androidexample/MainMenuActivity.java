package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    public Button logoutBtn;
    public Button changeStatusBtn;

    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private int userId;                // must be passed from login/signup

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Get values passed from login/signup
        String username = getIntent().getStringExtra("username");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        userId = getIntent().getIntExtra("userId", -1);

        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Welcome, " + username + "!");

        // Buttons
        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        changeStatusBtn = findViewById(R.id.change_status_btn);
        changeStatusBtn.setOnClickListener(this);

        // Show Change Status button only for admins
        if (isAdmin) {
            changeStatusBtn.setVisibility(View.VISIBLE);
            fetchTutorStatus(); // load current tutor status
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn) {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.change_status_btn) {
            toggleTutorStatus();
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
        // You’ll need backend to add this PATCH endpoint:
        String url = BASE_URL + "/users/setTutor";

        final boolean newStatus = !isTutor;

        StringRequest request = new StringRequest(Request.Method.PATCH, url,
                response -> {
                    isTutor = newStatus;
                    updateStatusButtonText();
                    Log.d("Tutor Toggle", "Tutor status updated: " + isTutor);
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
}
