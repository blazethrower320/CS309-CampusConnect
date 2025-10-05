package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    public Button logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Simple example to show user logged in
        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);
        TextView welcomeText = findViewById(R.id.welcome_text);
        String username = getIntent().getStringExtra("username");
        welcomeText.setText("Welcome, " + username + "!");

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn) {
            // Send user back to login screen
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);

            // Optional: close MainMenu so user can’t press back to return
            finish();
        }
    }
}
