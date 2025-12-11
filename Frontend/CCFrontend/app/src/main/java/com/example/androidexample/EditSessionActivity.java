package com.example.androidexample;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.User;

import org.json.JSONObject;

public class EditSessionActivity extends AppCompatActivity {

    private EditText className, classCode, meetingLocation, meetingTime;
    private Button saveButton, deleteButton;

    private ImageButton Backbtn;

    private int tutorId;
    private int sessionId;

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);

        className = findViewById(R.id.edit_class_name);
        classCode = findViewById(R.id.edit_class_code);
        meetingLocation = findViewById(R.id.edit_location);
        meetingTime = findViewById(R.id.edit_time);
        saveButton = findViewById(R.id.btn_save_session);
        deleteButton = findViewById(R.id.btn_delete_session);
        Backbtn = findViewById(R.id.btn_back);


        Backbtn.setOnClickListener(v -> {
            Intent intent = new Intent(EditSessionActivity.this, SessionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // close EditSessionActivity
        });


        // Get passed session data
        Intent i = getIntent();
        sessionId = i.getIntExtra("sessionId", -1);

        className.setText(i.getStringExtra("className"));
        classCode.setText(i.getStringExtra("classCode"));
        meetingLocation.setText(i.getStringExtra("meetingLocation"));
        String rawTime = i.getStringExtra("meetingTime");
        meetingTime.setText(formatMeetingTime(rawTime));
        tutorId = i.getIntExtra("tutorId", -1);

        saveButton.setOnClickListener(v -> sendEditRequest());

        meetingTime.setOnClickListener(v -> showDatePicker());

        deleteButton.setOnClickListener(v -> deleteSession());

    }

    private String formatMeetingTime(String backendTime) {
        try {
            // Backend format: "2025-10-29T19:00:00"
            SimpleDateFormat backendFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = backendFormat.parse(backendTime);

            // Desired format: "MM/dd/yyyy hh:mm AM/PM"
            SimpleDateFormat displayFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            return displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return backendTime; // fallback if parsing fails
        }
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

    private void deleteSession() {
        String url = BASE_URL + "/sessions/deleteSession/" + sessionId;

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "Session deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditSessionActivity.this, SessionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Failed to delete session", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(this).add(req);
    }

    private void showTimePicker(int year, int month, int day) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    // Convert 24h → 12h
                    String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                    int hour = hourOfDay % 12;
                    if (hour == 0) hour = 12;

                    // Format string
                    String formatted = String.format(
                            "%02d/%02d/%04d %02d:%02d %s",
                            month + 1, day, year, hour, minute, amPm
                    );

                    meetingTime.setText(formatted);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );

        timePicker.show();
    }


    private void sendEditRequest() {
        try {
            JSONObject body = new JSONObject();
            body.put("tutorId", tutorId);
            body.put("className", className.getText().toString());
            body.put("classCode", classCode.getText().toString());
            body.put("meetingLocation", meetingLocation.getText().toString());
            body.put("meetingTime", meetingTime.getText().toString());
            body.put("dateCreated", "2025-10-29T19:00:00"); // unused but required

            String url = BASE_URL + "/sessions/editSession/" + sessionId;

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    body,
                    response -> {
                        Toast.makeText(this, "Session updated!", Toast.LENGTH_SHORT).show();

                        // Return to SessionActivity and clear EditSessionActivity from back stack
                        Intent intent = new Intent(EditSessionActivity.this, SessionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        finish(); // <- closes EditSessionActivity cleanly
                    },
                    error -> {
                        Toast.makeText(this, "Error updating session", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
            );

            Volley.newRequestQueue(this).add(req);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
