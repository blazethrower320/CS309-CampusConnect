package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class CreateAccountActivity extends AppCompatActivity {

    // UI components
    private Button backBtn;
    private Button createAccountBtn;
    private TextView usernameTxt;
    private TextView passwordTxt;
    private TextView confirmPassTxt;
    private TextView msgResponse;
    private CheckBox adminCheckBox;

    private boolean isAdmin = false;

    // Variables
    private String username;
    private String password;
    private String confirmPass;

    // API Endpoints
    private static final String URL_CREATE_USER = "http://coms-3090-037.class.las.iastate.edu:8080/users/createUser";
    private static final String URL_CREATE_ADMIN = "http://coms-3090-037.class.las.iastate.edu:8080/admin/createAdmin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        backBtn = findViewById(R.id.back_btn);
        createAccountBtn = findViewById(R.id.signup_btn);
        adminCheckBox = findViewById(R.id.admin_checkbox);
        usernameTxt = findViewById(R.id.create_username);
        passwordTxt = findViewById(R.id.create_password);
        confirmPassTxt = findViewById(R.id.create_password_confirm);
        msgResponse = findViewById(R.id.msgResponse);

        // Back button
        backBtn.setOnClickListener(v ->
                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class))
        );

        // Create account button
        createAccountBtn.setOnClickListener(v -> {
            username = usernameTxt.getText().toString().trim();
            password = passwordTxt.getText().toString().trim();
            confirmPass = confirmPassTxt.getText().toString().trim();
            isAdmin = adminCheckBox.isChecked();

            if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                msgResponse.setText("Incomplete Field(s)");
                return;
            }

            if (!password.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                msgResponse.setText("Passwords do not match");
                return;
            }

            // Create user first
            CreateUser(username, password);
        });
    }

    private void CreateUser(String username, String password) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_CREATE_USER,
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("User Created Successfully");

                    if (isAdmin) {
                        CreateAdmin(username);
                    } else {
                        // Non-admin user → go to main menu
                        goToMainMenu(username, password, false);
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(this, "User Creation Failed", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("User Creation Failed");
                }
        ) {
            @Override
            public byte[] getBody() {
                String body = "{"
                        + "\"username\":\"" + username + "\","
                        + "\"password\":\"" + password + "\""
                        + "}";
                return body.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void CreateAdmin(String username) {
        String url = URL_CREATE_ADMIN + username;

        StringRequest adminRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("Admin Response", response);
                    Toast.makeText(this, "Admin Account Created", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("Admin Created Successfully!");

                    // ✅ Now go to main menu as admin
                    goToMainMenu(username, password, true);
                },
                error -> {
                    Log.e("Admin Error", error.toString());
                    Toast.makeText(this, "Admin Creation Failed", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("Admin Creation Failed");
                }
        );

        Volley.newRequestQueue(this).add(adminRequest);
    }

    private void goToMainMenu(String username, String password, boolean isAdmin) {
        Intent intent = new Intent(CreateAccountActivity.this, MainMenuActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("isAdmin", isAdmin);
        intent.putExtra("userId", -1);
        startActivity(intent);
        finish();
    }
}
