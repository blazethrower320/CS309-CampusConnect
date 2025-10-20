package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ForgotPasswordActivity extends AppCompatActivity {
    // UI components
    private Button backBtn;
    private Button updatePassBtn;
    private TextView usernameTxt;
    private TextView passwordTxt;
    private TextView confirmPassTxt;
    private TextView msgResponse;

    // Variables
    private String username;
    private String password;
    private String confirmPass;

    // Path to server
    private static final String URL_EDIT_PASSWORD = "http://coms-3090-037.class.las.iastate.edu:8080/users/editPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Initializing UI components
        backBtn = findViewById(R.id.back_btn);
        updatePassBtn = findViewById(R.id.signup_btn); // reuse existing button
        usernameTxt = findViewById(R.id.create_username);
        passwordTxt = findViewById(R.id.create_password);
        confirmPassTxt = findViewById(R.id.create_password_confirm);
        msgResponse = findViewById(R.id.msgResponse);

        // Back button
        backBtn.setOnClickListener(v ->{
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            finish();
        });
        // Update password button
        updatePassBtn.setOnClickListener(v -> {
            username = usernameTxt.getText().toString().trim();
            password = passwordTxt.getText().toString().trim();
            confirmPass = confirmPassTxt.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                msgResponse.setText("Incomplete Field(s)");
            } else if (!password.equals(confirmPass)) {
                Toast.makeText(ForgotPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                msgResponse.setText("Passwords do not match");
            } else {
                UpdatePassword(username, password);
            }
        });
    }

    public void UpdatePassword(String username, String password) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.PATCH,
                URL_EDIT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        if (response.contains("true")) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                            msgResponse.setText("Password updated!");
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Update failed (user not found)", Toast.LENGTH_SHORT).show();
                            msgResponse.setText("User does not exist");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(ForgotPasswordActivity.this, "Password Update Failed", Toast.LENGTH_SHORT).show();
                        msgResponse.setText("Failed to update password. Try again.");
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                String body = "{\"newUsername\":\"" + username + "\", \"newPassword\":\"" + password + "\"}";
                return body.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
