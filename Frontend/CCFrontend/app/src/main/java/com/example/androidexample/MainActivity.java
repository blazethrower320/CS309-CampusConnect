package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    public void onClick(View v)
    {
        int id = v.getId(); // Get the ID of the clicked button

        // Check which button was clicked and start the corresponding activity
        if (id == R.id.btnStringRequest)
        {
            startActivity(new Intent(MainActivity.this, StringReqActivity.class));
        }
        else if (id == R.id.login_btn)
        {
            Log.i("PageInfo", "Login Button Clicked");
        }
        else if (id == R.id.signup_btn)
        {
            Log.i("PageInfo", "Signup Button Clicked");
        }

    }
}
