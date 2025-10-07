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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String URL_Delete_USER = "http://coms-3090-037.class.las.iastate.edu:8080/users/deleteUser";

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
        if(v.getId() == R.id.delete_btn)
        {
            DeleteUser();
        }
    }

    public void DeleteUser()
    {
        JSONObject requestBodyJson = new JSONObject();
        try
        {
            requestBodyJson.put("username", username);
            requestBodyJson.put("password", password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return; // Exit if the JSON object can't be created
        }
        final String requestBody = requestBodyJson.toString();

        // Use a StringRequest instead of a JsonObjectRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                URL_Delete_USER,
                response -> // Success listener
                {
                    // This will now be triggered correctly on a 2xx response
                    Log.d("Volley Success", "Response: " + response);
                    Toast.makeText(MainMenuActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                    topText.setText("Account Deleted");
                    deleteBtn.setVisibility(View.INVISIBLE);

                    // Optional: Redirect the user to the login screen after successful creation
                    // startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                },
                error -> // Error listener
                {
                    // Error: show feedback
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(MainMenuActivity.this, "Unable to delete account", Toast.LENGTH_SHORT).show();
                    topText.setText(error.toString());
                }
        ) {
            // Override getBodyContentType to specify you're sending a JSON
            @Override
            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }

            // Override getBody to send the JSON data
            @Override
            public byte[] getBody()
            {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (java.io.UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
            @Override
            public int getMethod()
            {
                return Method.POST; // Hack: Treat as POST
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-HTTP-Method-Override", "DELETE");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
