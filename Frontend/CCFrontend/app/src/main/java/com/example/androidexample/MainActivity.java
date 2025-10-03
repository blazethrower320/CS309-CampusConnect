package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare button variables
    public Button strBtn;
    public Button loginBtn;
    public Button signupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the UI layout for the activity

        // Initialize buttons by finding them using their IDs from XML layout
        strBtn = findViewById(R.id.btnStringRequest);
        loginBtn = findViewById(R.id.login_btn);
        signupBtn = findViewById(R.id.signup_btn);

        /* Set click listeners for each button */
        strBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnStringRequest) {
            startActivity(new Intent(MainActivity.this, StringReqActivity.class));
        }
        else if (id == R.id.login_btn) {
            Log.i("PageInfo", "Login Button Clicked");

            // Get username & password inputs
            EditText usernameEdt = findViewById(R.id.login_username_edt);
            EditText passwordEdt = findViewById(R.id.login_password_edt);

            String username = usernameEdt.getText().toString().trim();
            String password = passwordEdt.getText().toString().trim();

            // Placeholder login validation
            if (username.equals("testuser") && password.equals("password123")) {
                // If login is successful → go to main menu
                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                // Show error in password field
                passwordEdt.setError("Invalid username or password");
            }
        }
        else if (id == R.id.signup_btn) {
            Log.i("PageInfo", "Signup Button Clicked");
        }
    }

}
