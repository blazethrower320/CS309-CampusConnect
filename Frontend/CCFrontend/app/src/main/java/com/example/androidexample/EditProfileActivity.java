package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    //Text Fields
    private TextView roleText;
    private TextView nameText;
    private TextView usernameText;
    private EditText bioText;
    private EditText majorText;
    private EditText classificationText;
    private EditText contactInfoText;

    //Buttons
    private ImageButton confirmBtn; //Edit profile btn
    private ImageButton cancelBtn; //Three line btn

    //User variables
    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private int userId;                // must be passed from login/signup
    private String username;
    private String password;           // pass this from login/signup if required

    //profile fields
    private String bio;
    private String major;
    private String classification;
    private String contactInfo;
    private String firstName;
    private String lastName;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //Initialize UI elements

        //Text Fields
        roleText = findViewById(R.id.role_text);
        nameText = findViewById(R.id.name_text);
        usernameText = findViewById(R.id.username_text);
        bioText = findViewById(R.id.edit_bio_text);
        majorText = findViewById(R.id.edit_major_text);
        classificationText = findViewById(R.id.edit_classification_text);
        contactInfoText = findViewById(R.id.edit_contactInfo_text);



        //Buttons
        cancelBtn = findViewById(R.id.cancel_button);
        confirmBtn = findViewById(R.id.confirm_btn);

        //initialize user data
        username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        password = getIntent().getStringExtra("password");
        bio = getIntent().getStringExtra("bio");
        major = getIntent().getStringExtra("major");
        classification = getIntent().getStringExtra("classification");
        contactInfo = getIntent().getStringExtra("contactInfo");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");

        //set button listeners to be active
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        //Set text fields to have user data
        usernameText.setText("@" + username);
        if(isAdmin)
        {
            roleText.setText("Admin");
        }
        else if(isTutor)
        {
            roleText.setText("Tutor");
        }
        else
        {
            roleText.setText("Student");
        }
        nameText.setText(firstName + " " + lastName);
        bioText.setText(bio);
        majorText.setText(major);
        classificationText.setText(classification);
        contactInfoText.setText(contactInfo);
    }

    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.confirm_btn)
        {
            UpdateProfile(username);
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if(id == R.id.cancel_button)
        {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
    }
    public void UpdateProfile(String username)
    {
        //URL for put request
        final String URL_UPDATE_USER = "http://coms-3090-037.class.las.iastate.edu:8080/users/update";

        // Create a JSON object to hold the updated user data.
        JSONObject requestBodyJson = new JSONObject();
        try
        {
            //Get updated fields
            String updatedBio = bioText.getText().toString();
            String updatedMajor = majorText.getText().toString();
            String updatedClassification = classificationText.getText().toString();
            String updatedContactInfo = contactInfoText.getText().toString();

            //Add info to JSON object
            requestBodyJson.put("firstName", firstName);
            requestBodyJson.put("lastName", lastName);
            requestBodyJson.put("username", username);
            requestBodyJson.put("password", password);
            requestBodyJson.put("major", updatedMajor);
            requestBodyJson.put("bio", updatedBio);
            requestBodyJson.put("classification", updatedClassification);
            requestBodyJson.put("contactInfo", updatedContactInfo);



        }
        catch (Exception e)
        {
            e.printStackTrace();
            // If there's an error creating the JSON, we can't proceed.
            Toast.makeText(this, "Error creating update request.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the JSON object to a string for the request body.
        final String requestBody = requestBodyJson.toString();
        Log.d("Volley UpdateProfile", "Request Body: " + requestBody);

        //Create request
        StringRequest putRequest = new StringRequest(
                Request.Method.PUT,
                URL_UPDATE_USER,
                response -> {
                    // This is the success listener.
                    Log.d("Volley Success", "Profile updated: " + response);
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // This is the error listener.
                   Log.e("Volley Error", "Failed to update profile: " + error.toString());

                    // Providing more detailed error feedback is helpful for debugging.
                    String responseBody = "";
                    if (error.networkResponse != null)
                    {
                        try
                        {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("Volley Error", "Response Body: " + responseBody);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(EditProfileActivity.this, "Update failed: " + responseBody, Toast.LENGTH_LONG).show();
                }
        ) {
            //override for custom body
            @Override
            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody()
            {
                // Return the JSON object as a byte array.
                return requestBody.getBytes();
            }
        };

        //Request for volley
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(putRequest);
    }


}
