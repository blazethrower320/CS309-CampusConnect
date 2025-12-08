package com.example.androidexample;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CreateSessionActivity extends AppCompatActivity {

    private EditText editClassName, editClassCode, editMeetingLocation, editMeetingTime;
    private Button createButton;
    private ImageButton backButton;

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";

    private User currentUser;  // store the current user object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        // Get current user
        currentUser = User.getInstance();

        if (currentUser == null) {
            Toast.makeText(this, "Error: User not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        editClassName = findViewById(R.id.create_class_name);
        editClassCode = findViewById(R.id.create_class_code);
        editMeetingLocation = findViewById(R.id.create_meeting_location);
        editMeetingTime = findViewById(R.id.create_meeting_time);
        createButton = findViewById(R.id.button_create_session);
        backButton = findViewById(R.id.back_button);

        RequestQueue queue = Volley.newRequestQueue(this);

        // Handle Create Session button
        editMeetingTime.setOnClickListener(v -> showDatePicker());
        createButton.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            String classCode = editClassCode.getText().toString().trim();
            String meetingLocation = editMeetingLocation.getText().toString().trim();
            String meetingTime = editMeetingTime.getText().toString().trim();

            if (className.isEmpty() || classCode.isEmpty() || meetingLocation.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int tutorId = currentUser.getTutorId();  // pull directly from User
            int userId = currentUser.getUserId();
            String username = currentUser.getUsername();

            if (tutorId == -1) {
                Toast.makeText(this, "Tutor ID not found for user.", Toast.LENGTH_SHORT).show();
                Log.e("CreateSession", "Tutor ID missing for current user.");
                return;
            }

            createSession(queue, userId, tutorId, className, classCode, meetingLocation, meetingTime, username);
        });

        // Handle Back button
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(CreateSessionActivity.this, SessionActivity.class);
            startActivity(backIntent);
            finish();
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, day) -> showTimePicker(year, month, day),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void showTimePicker(int year, int month, int day) {

        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    // Convert to 12-hour format
                    String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                    int hour = hourOfDay % 12;
                    if (hour == 0) hour = 12;

                    // Format date + time together
                    String formatted = String.format("%02d/%02d/%04d %02d:%02d %s",
                            month + 1,   // months are 0-based
                            day, year, hour, minute, amPm );

                    editMeetingTime.setText(formatted);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // Use 12-hour format
        );

        timePicker.show();
    }


    private void createSession(RequestQueue queue, int userId, int tutorId, String className, String classCode,
                               String meetingLocation, String meetingTime, String username) {

        String url = BASE_URL + "/sessions/createSession";

        JSONObject sessionData = new JSONObject();
        try {
            sessionData.put("tutorId", tutorId);
            sessionData.put("className", className);
            sessionData.put("classCode", classCode);
            sessionData.put("meetingLocation", meetingLocation);
            sessionData.put("meetingTime", meetingTime);
            sessionData.put("dateCreated", java.time.LocalDateTime.now().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, sessionData,
                response -> {
                    Toast.makeText(this, "Session Created Successfully!", Toast.LENGTH_SHORT).show();
                    Intent backIntent = new Intent(CreateSessionActivity.this, SessionActivity.class);
                    backIntent.putExtra("userId", userId);
                    backIntent.putExtra("isTutor", true);
                    backIntent.putExtra("username", username);
                    startActivity(backIntent);
                    finish();
                },
                error -> {
                    String msg = (error.networkResponse != null && error.networkResponse.statusCode == 400)
                            ? "Tutor not found"
                            : "Error creating session";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    Log.e("CreateSession", "Error creating session: " + error.toString());
                });

        queue.add(request);
    }
}