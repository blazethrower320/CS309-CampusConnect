package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare button variables
    //public Button strBtn;
    public Button loginBtn;
    public Button signupBtn;
    public Button forgotpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        loginBtn = findViewById(R.id.login_btn);
        signupBtn = findViewById(R.id.signup_btn);
        forgotpBtn = findViewById(R.id.forgotp_btn);

        // Set click listeners
        //strBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        forgotpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.signup_btn) {
            startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
            finish();
        } else if (id == R.id.forgotp_btn) {
            startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            finish();
        } else if (id == R.id.login_btn) {
            Log.i("PageInfo", "Login Button Clicked");

            EditText usernameEdt = findViewById(R.id.login_username_edt);
            EditText passwordEdt = findViewById(R.id.login_password_edt);

            String username = usernameEdt.getText().toString().trim();
            String password = passwordEdt.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty())
            {
                passwordEdt.setError("Please fill in both fields");
                return;
            }
            //TODO remove when done with the project/can be uncommented to test app
            else if (username.equals("Test") && password.equals("Test"))
            {
                // Initialize mock user data for offline/testing mode
                User user = User.getInstance();
                user.setUsername("Test");
                user.setPassword("Test");
                user.setAdmin(false);
                user.setTutor(false);
                user.setUserId(9999); // dummy ID

                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
            String url = "http://coms-3090-037.class.las.iastate.edu:8080/users/login";

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("username", username);
                requestBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    requestBody,
                    response -> {
                        try {
                            int userId = response.optInt("userId", -1);

                            // Make a second call to get full user info (including tutor flag)
                            String userInfoUrl = "http://coms-3090-037.class.las.iastate.edu:8080/users/find/" + username;

                            JsonObjectRequest userInfoRequest = new JsonObjectRequest(
                                    Request.Method.GET,
                                    userInfoUrl,
                                    null,
                                    userResponse -> {
                                        boolean isTutor = userResponse.optBoolean("isTutor", false);
                                        boolean isAdmin = userResponse.optBoolean("isAdmin", false);

                                        Log.i("LoginSuccess", "User logged in. isAdmin=" + isAdmin + ", isTutor=" + isTutor + ", userId=" + userId);

                                        // Inside login success:
                                        User user = User.getInstance();
                                        user.setUsername(username);
                                        user.setPassword(password);
                                        user.setAdmin(isAdmin);
                                        //Log.i("LoginSuccess", "isAdmin=" + isAdmin);
                                        user.setTutor(isTutor);
                                        user.setUserId(userId);
                                        user.setLastName(userResponse.optString("lastName", ""));
                                        user.setFirstName(userResponse.optString("firstName", ""));
                                        user.setBio(userResponse.optString("bio", ""));
                                        user.setMajor(userResponse.optString("major", ""));
                                        user.setClassification(userResponse.optString("classification", ""));

                                        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                                        startActivity(intent);
                                        finish();
                                    },
                                    error -> {
                                        Log.e("UserInfoError", "Failed to fetch user info: " + error.getMessage());
                                        Toast.makeText(MainActivity.this, "Failed to get user details", Toast.LENGTH_SHORT).show();
                                    }
                            );

                            Volley.newRequestQueue(this).add(userInfoRequest);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 404) {
                                usernameEdt.setError("User not found");
                            } else if (statusCode == 403) {
                                passwordEdt.setError("Incorrect password");
                            } else {
                                passwordEdt.setError("Login failed. Try again.");
                            }
                        } else {
                            passwordEdt.setError("Network error. Check connection.");
                        }
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest);
        }
    }
}
