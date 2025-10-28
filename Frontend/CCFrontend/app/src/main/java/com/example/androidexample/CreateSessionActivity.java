package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateSessionActivity extends AppCompatActivity {

    private EditText editClassName, editClassCode, editMeetingLocation, editMeetingTime;
    private Button createButton;
    private ImageButton backButton;
    private int userId;
    private boolean isTutor;

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        // Get data from intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        isTutor = intent.getBooleanExtra("isTutor", false);

        // Initialize views
        editClassName = findViewById(R.id.edit_class_name);
        editClassCode = findViewById(R.id.edit_class_code);
        editMeetingLocation = findViewById(R.id.edit_meeting_location);
        editMeetingTime = findViewById(R.id.edit_meeting_time);
        createButton = findViewById(R.id.button_create_session);
        backButton = findViewById(R.id.back_button);

        RequestQueue queue = Volley.newRequestQueue(this);

        // Handle Create Session button
        createButton.setOnClickListener(v -> {
            String className = editClassName.getText().toString();
            String classCode = editClassCode.getText().toString();
            String meetingLocation = editMeetingLocation.getText().toString();
            String meetingTime = editMeetingTime.getText().toString();

            if (className.isEmpty() || classCode.isEmpty() || meetingLocation.isEmpty() || meetingTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject sessionData = new JSONObject();
            try {
                sessionData.put("userId", userId);
                sessionData.put("tutorId", userId);
                sessionData.put("className", className);
                sessionData.put("classCode", classCode);
                sessionData.put("meetingLocation", meetingLocation);
                sessionData.put("meetingTime", meetingTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = BASE_URL + "/sessions/createSession";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, sessionData,
                    response -> {
                        Toast.makeText(this, "Session Created Successfully!", Toast.LENGTH_SHORT).show();
                        // Go back to SessionActivity
                        Intent backIntent = new Intent(CreateSessionActivity.this, SessionActivity.class);
                        backIntent.putExtra("userId", userId);
                        backIntent.putExtra("isTutor", isTutor);
                        startActivity(backIntent);
                        finish();
                    },
                    error -> {
                        String errorMessage = (error.networkResponse != null && error.networkResponse.statusCode == 400)
                                ? "Tutor not found" : "Error creating session";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    });

            queue.add(request);
        });

        // Handle Back button
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(CreateSessionActivity.this, SessionActivity.class);
            backIntent.putExtra("userId", userId);
            backIntent.putExtra("isTutor", isTutor);
            startActivity(backIntent);
            finish();
        });
    }
}
