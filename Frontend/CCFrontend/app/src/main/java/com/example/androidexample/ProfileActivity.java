package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    //Text Fields
    private TextView roleText;
    private TextView nameText;
    private TextView usernameText;
    private TextView bioText;
    private TextView majorText;
    private TextView classificationText;
    private TextView contactInfoText;

    //Buttons
    private Button logoutBtn; //Logout button
    private ImageButton editProfileBtn; //Edit profile btn
    private ImageButton menuBtn; //Three line btn


    private DrawerLayout drawerLayout; //Menu bar layout

    //Menu Bar buttons
    private LinearLayout homeBtn; //Home btn inside of menu bar
    private LinearLayout sessionsBtn; //Sessions btn inside of menu bar

    //User variables
    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private int userId;                // must be passed from login/signup
    private String username;
    private String password;           // pass this from login/signup if required


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Initialize UI elements

        //Text Fields
        roleText = findViewById(R.id.role_text);
        nameText = findViewById(R.id.name_text);
        usernameText = findViewById(R.id.username_text);

        //Buttons
        menuBtn = findViewById(R.id.menu_button);
        logoutBtn = findViewById(R.id.logout_btn);
        homeBtn = findViewById(R.id.nav_home);
        sessionsBtn = findViewById(R.id.nav_sessions);
        editProfileBtn = findViewById(R.id.edit_profile_btn);

        //initialize user data
        username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        password = getIntent().getStringExtra("password");

        //Menu Bar
        drawerLayout = findViewById(R.id.drawer_layout);

        //set button listeners to be active
        homeBtn.setOnClickListener(this);
        sessionsBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);

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
        nameText.setText("First Last");


    }

    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.logout_btn)
        {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }
        if(id == R.id.menu_button)
        {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
            {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        if(id == R.id.edit_profile_btn)
        {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if(id == R.id.nav_sessions)
        {
            Intent intent = new Intent(ProfileActivity.this, SessionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if(id == R.id.nav_home)
        {
            Intent intent = new Intent(ProfileActivity.this, MainMenuActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }

    }
}
