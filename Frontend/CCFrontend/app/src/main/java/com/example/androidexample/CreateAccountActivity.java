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
    private TextView usernameTxt, passwordTxt, confirmPassTxt, msgResponse, firstNameTxt, lastNameTxt;;
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
        createAccountBtn = findViewById(R.id.create_account_btn);
        tutorCheckBox = findViewById(R.id.tutor_checkbox);
        usernameTxt = findViewById(R.id.create_username);
        passwordTxt = findViewById(R.id.create_password);
        firstNameTxt = findViewById(R.id.create_first_name);
        lastNameTxt = findViewById(R.id.create_last_name);
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
        String firstName = firstNameTxt.getText().toString().trim();
        String lastName = lastNameTxt.getText().toString().trim();
        String username = usernameTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();
        String confirmPass = confirmPassTxt.getText().toString().trim();
        isTutor = tutorCheckBox.isChecked();

        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showToast("Please fill out all fields");
            msgResponse.setText("Incomplete Field(s)");
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty()) {
            showToast("First and Last name required");
            msgResponse.setText("First and Last name required");
            return;
        }

        if (!password.equals(confirmPass)) {
            showToast("Passwords do not match");
            msgResponse.setText("Passwords do not match");
            return;
        }

        createUser(username, password, firstName, lastName);
    }

    /** Create user and update global User instance **/
    private void createUser(String username, String password, String firstName, String lastName) {
        // Step 1: POST to create the user
        StringRequest createRequest = new StringRequest(
                Request.Method.POST,
                URL_CREATE_USER,
                response -> {
                    Log.d("CreateUser", "Response: " + response);

                    // Check if response is "User"
                    if (response.trim().equalsIgnoreCase("User created successfully")) {
                        // Step 2: GET userId from username
                        fetchUserId(username, password, firstName, lastName);
                    } else {
                        showToast("Unexpected response from server: " + response);
                        msgResponse.setText("User Creation Failed");
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
                try {
                    JSONObject body = new JSONObject();
                    body.put("username", username);
                    body.put("password", password);
                    return body.toString().getBytes("utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(createRequest);
    }

    private void fetchUserId(String username, String password, String firstName, String lastName) {
        String url = BASE_URL + "/users/getUserId/" + username;

        StringRequest getRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        int userId = Integer.parseInt(response.trim());

                        // Populate User singleton
                        User user = User.getInstance();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setUserId(userId);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setAdmin(isAdmin);
                        user.setTutor(isTutor);

                        Log.d("UserSingleton", "User saved globally: " + user.toString());

                        // Update database
                        updateUserInDatabase(user);

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        showToast("Error parsing userId from server");
                    }
                },
                error -> {
                    Log.e("GetUserId", "Error: " + error.toString());
                    showToast("Failed to fetch userId");
                }
        );

        queue.add(getRequest);
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

    private static final String URL_UPDATE_USER = BASE_URL + "/users/update";

    private void updateUserInDatabase(User user) {
        try {
            JSONObject body = new JSONObject();
            body.put("firstName", user.getFirstName());
            body.put("lastName", user.getLastName());
            body.put("username", user.getUsername());
            body.put("password", user.getPassword());
            body.put("isTutor", user.isTutor());
            body.put("isAdmin", user.isAdmin());
            body.put("major", "");          // Optional: set if you have values
            body.put("classification", "");
            body.put("bio", "");

            Log.d("UpdateUserDebug", "PUT Body: " + body.toString());

            StringRequest updateRequest = new StringRequest(
                    Request.Method.PUT,
                    URL_UPDATE_USER,
                    response -> {
                        Log.d("UpdateUserDebug", "User updated successfully: " + response);
                        // After updating, continue with Admin/Tutor creation or main menu
                        if (user.isAdmin()) {
                            createRoleAccount(user.getUsername(), URL_CREATE_ADMIN, true, false);
                        } else if (user.isTutor()) {
                            createRoleAccount(user.getUsername(), URL_CREATE_TUTOR, false, true);
                        } else {
                            goToMainMenu();
                        }
                    },
                    error -> {
                        Log.e("UpdateUserDebug", "Error updating user: " + error.toString());
                        showToast("Failed to update user info");
                    }
            ) {
                @Override
                public byte[] getBody() {
                    return body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            queue.add(updateRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UpdateUserDebug", "Exception building JSON body: " + e.getMessage());
        }
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
