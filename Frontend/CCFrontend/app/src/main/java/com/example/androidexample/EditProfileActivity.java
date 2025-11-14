package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // UI
    private TextView roleText, nameText, usernameText;
    private EditText bioText, majorText, classificationText;
    private ImageButton confirmBtn, cancelBtn;

    // Singleton user
    private User user;

    private static final String URL_UPDATE_USER = "http://coms-3090-037.class.las.iastate.edu:8080/users/update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize user singleton
        user = User.getInstance();
        if (user == null || user.getUsername() == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Initialize UI
        roleText = findViewById(R.id.role_text);
        nameText = findViewById(R.id.name_text);
        usernameText = findViewById(R.id.username_text);
        bioText = findViewById(R.id.edit_bio_text);
        majorText = findViewById(R.id.edit_major_text);
        classificationText = findViewById(R.id.edit_classification_text);
        confirmBtn = findViewById(R.id.confirm_btn);
        cancelBtn = findViewById(R.id.cancel_button);

        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        // Load user info into fields
        populateFields();
    }

    private void populateFields() {
        usernameText.setText("@" + user.getUsername());
        nameText.setText(user.getFirstName() + " " + user.getLastName());
        bioText.setText(user.getBio());
        majorText.setText(user.getMajor());
        classificationText.setText(user.getClassification());

        if (user.isAdmin()) {
            roleText.setText("Admin");
        } else if (user.isTutor()) {
            roleText.setText("Tutor");
        } else {
            roleText.setText("Student");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.confirm_btn) {
            updateProfile();
        } else if (id == R.id.cancel_button) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }

    private void updateProfile() {
        try {
            // Get updated values from text fields
            String updatedBio = bioText.getText().toString();
            String updatedMajor = majorText.getText().toString();
            String updatedClassification = classificationText.getText().toString();

            // Update singleton
            user.setBio(updatedBio);
            user.setMajor(updatedMajor);
            user.setClassification(updatedClassification);

            // Create JSON object
            JSONObject body = new JSONObject();
            body.put("userId", user.getUserId());
            body.put("username", user.getUsername());
            body.put("password", user.getPassword());
            body.put("firstName", user.getFirstName());
            body.put("lastName", user.getLastName());
            body.put("bio", user.getBio());
            body.put("major", user.getMajor());
            body.put("classification", user.getClassification());
            body.put("isTutor", user.isTutor());
            body.put("isAdmin", user.isAdmin());

            String requestBody = body.toString();
            Log.d("EditProfile", "Request Body: " + requestBody);

            // Create Volley PUT request
            StringRequest putRequest = new StringRequest(
                    Request.Method.PUT,
                    URL_UPDATE_USER,
                    response -> {
                        Log.d("Volley Success", "Profile updated: " + response);
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                        // Go back to profile
                        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                        finish();
                    },
                    error -> {
                        Log.e("Volley Error", "Failed to update profile: " + error);
                        String responseBody = "";
                        if (error.networkResponse != null) {
                            try {
                                responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e("Volley Error", "Response Body: " + responseBody);
                            } catch (Exception ignored) {}
                        }
                        Toast.makeText(EditProfileActivity.this, "Update failed: " + responseBody, Toast.LENGTH_LONG).show();
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

            // Send request
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(putRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error updating profile.", Toast.LENGTH_SHORT).show();
        }
    }
}
