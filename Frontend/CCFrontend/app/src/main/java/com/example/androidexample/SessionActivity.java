package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class SessionActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        String username = getIntent().getStringExtra("username");
        int userId = getIntent().getIntExtra("userId", -1);
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        boolean isTutor = getIntent().getBooleanExtra("isTutor", false);
        String password = getIntent().getStringExtra("password");

        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);

        // Find the "Home" button layout
        LinearLayout homeButton = findViewById(R.id.nav_sessions);

        // Open sidebar when menu button clicked
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                drawerLayout.closeDrawer(findViewById(R.id.nav_view));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.nav_view));
            }
        });

        // When user clicks "Home", go to MainMenuActivity
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, MainMenuActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish(); // optional: close SessionActivity so it doesn’t pile up in the backstack
        });

    }
}
