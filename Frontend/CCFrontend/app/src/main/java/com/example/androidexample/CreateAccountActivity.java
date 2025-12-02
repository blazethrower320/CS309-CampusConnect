package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccountActivity extends AppCompatActivity {

    // UI components
    private Button backBtn, createAccountBtn;
    private TextView usernameTxt, passwordTxt, confirmPassTxt, msgResponse;
    private CheckBox  tutorCheckBox;

    // Flags
    private boolean isAdmin = false;
    private boolean isTutor = false;

    // Constants
    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String URL_CREATE_USER = BASE_URL + "/users/createUser";
    private static final String URL_CREATE_ADMIN = BASE_URL + "/admin/createAdmin/";
    private static final String URL_CREATE_TUTOR = BASE_URL + "/tutors/createTutor/";

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI
        backBtn = findViewById(R.id.back_btn);
        createAccountBtn = findViewById(R.id.signup_btn);
        tutorCheckBox = findViewById(R.id.tutor_checkbox);
        usernameTxt = findViewById(R.id.create_username);
        passwordTxt = findViewById(R.id.create_password);
        confirmPassTxt = findViewById(R.id.create_password_confirm);
        msgResponse = findViewById(R.id.msgResponse);

        queue = Volley.newRequestQueue(this);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
            finish();
        });

        createAccountBtn.setOnClickListener(v -> handleCreateAccount());
    }

    /** Validate inputs and create user **/
    private void handleCreateAccount() {
        String username = usernameTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();
        String confirmPass = confirmPassTxt.getText().toString().trim();
        isTutor = tutorCheckBox.isChecked();

        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showToast("Please fill out all fields");
            msgResponse.setText("Incomplete Field(s)");
            return;
        }

        if (!password.equals(confirmPass)) {
            showToast("Passwords do not match");
            msgResponse.setText("Passwords do not match");
            return;
        }

        createUser(username, password);
    }

    /** Create user and update global User instance **/
    private void createUser(String username, String password) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_CREATE_USER,
                response -> {
                    Log.d("CreateUser", "Response: " + response);
                    msgResponse.setText("User Created Successfully");
                    showToast("User Created");

                    // Attempt to parse returned user data
                    try {
                        JSONObject json = new JSONObject(response);
                        int userId = json.optInt("userId", -1);

                        // Set user data globally in User.java singleton
                        User user = User.getInstance();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setAdmin(isAdmin);
                        user.setTutor(isTutor);
                        user.setUserId(userId);

                        Log.d("UserSingleton", "User saved globally: " + user.toString());

                        if (isAdmin) {
                            createRoleAccount(username, URL_CREATE_ADMIN, true, false);
                        } else if (isTutor) {
                            createRoleAccount(username, URL_CREATE_TUTOR, false, true);
                        } else {
                            goToMainMenu();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error parsing user response");
                    }
                },
                error -> {
                    Log.e("CreateUser", "Error: " + error.toString());
                    showToast("User Creation Failed");
                    msgResponse.setText("User Creation Failed");
                }
        ) {
            @Override
            public byte[] getBody() {
                String jsonBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
                return jsonBody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(request);
    }

    /** Admin or Tutor account creation **/
    private void createRoleAccount(String username, String baseUrl, boolean isAdmin, boolean isTutor) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                baseUrl + username,
                response -> {
                    String role = isAdmin ? "Admin" : "Tutor";
                    Log.d(role + " Response", response);
                    msgResponse.setText(role + " Created Successfully!");
                    showToast(role + " Account Created");
                    goToMainMenu();
                },
                error -> {
                    String role = isAdmin ? "Admin" : "Tutor";
                    Log.e(role + " Error", error.toString());
                    showToast(role + " Creation Failed");
                    msgResponse.setText(role + " Creation Failed");
                }
        );

        queue.add(request);
    }

    /** Navigate to Main Menu **/
    private void goToMainMenu() {
        Intent intent = new Intent(CreateAccountActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /** Utility: quick toast **/
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
