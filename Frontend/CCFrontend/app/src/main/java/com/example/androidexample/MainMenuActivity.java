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
    private Button setNumClassBtn;
    private Button deleteBtn;
    private TextView welcomeText;

    private LinearLayout numClassPanel;

    private EditText numClassEdt;


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
        password = getIntent().getStringExtra("password");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        userId = getIntent().getIntExtra("userId", -1);

        // UI
        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Welcome, " + username + "!");

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        changeStatusBtn = findViewById(R.id.change_status_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(this);

        //Tutor UI elements
        numClassPanel = findViewById(R.id.numclass_panel);
        numClassEdt = findViewById(R.id.numclass_edttxt);

        setNumClassBtn = findViewById(R.id.setnumclass_btn);
        setNumClassBtn.setOnClickListener(this);


        // Admin-only button for editing tutor ratings
        if (isAdmin)
        {
            changeStatusBtn.setVisibility(View.VISIBLE);
            numClassPanel.setVisibility(View.GONE);
            changeStatusBtn.setText("Edit Ratings");
            changeStatusBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MainMenuActivity.this, EditTutorRatingActivity.class);
                startActivity(intent);
            });
        }
        else if (isTutor)
        {
            numClassPanel.setVisibility(View.VISIBLE);
            changeStatusBtn.setVisibility(View.GONE);
        }
        else
        {
            changeStatusBtn.setVisibility(View.GONE);
            numClassPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn)
        {
            startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
            finish();
        }
        else if (v.getId() == R.id.change_status_btn)
        {
            startActivity(new Intent(MainMenuActivity.this, EditTutorRatingActivity.class));
            finish();
        }
        else if (v.getId() == R.id.delete_btn)
        {
            DeleteUser();
        }
        else if (v.getId() == R.id.setnumclass_btn)
        {
            SetNumClasses();
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
        //Get the text from the EditText and the username
        String classCountStr = numClassEdt.getText().toString();
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "User information not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (classCountStr.isEmpty()) {
            Toast.makeText(this, "Please enter the number of classes.", Toast.LENGTH_SHORT).show();
            return;
        }

        //URL for patch
        final String URL_SET_CLASSES = "http://coms-3090-037.class.las.iastate.edu:8080/tutors/editTotalClassCount/" + username;

        //Request Body
        JSONObject requestBodyJson = new JSONObject();
        try {
            int classCount = Integer.parseInt(classCountStr);
            requestBodyJson.put("totalClassCount", classCount);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = requestBodyJson.toString();

        // Create StringReq
        StringRequest patchRequest = new StringRequest(
                Request.Method.POST, //override this to be PATCH
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

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // This is the workaround to send a PATCH request
                Map<String, String> headers = new HashMap<>();
                headers.put("X-HTTP-Method-Override", "PATCH");
                return headers;
            }
        };

        // 5. Add the request to the queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(patchRequest);
    }

}
