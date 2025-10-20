package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    //Text Fields
    private TextView roleText;
    private TextView nameText;
    private TextView usernameText;
    private EditText bioText;
    private EditText majorText;
    private EditText classificationText;
    private EditText contactInfoText;

    //Buttons
    private ImageButton confirmBtn; //Edit profile btn
    private ImageButton cancelBtn; //Three line btn

    //User variables
    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup
    private int userId;                // must be passed from login/signup
    private String username;
    private String password;           // pass this from login/signup if required


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //Initialize UI elements

        //Text Fields
        roleText = findViewById(R.id.role_text);
        nameText = findViewById(R.id.name_text);
        usernameText = findViewById(R.id.username_text);

        //Buttons
        cancelBtn = findViewById(R.id.cancel_button);
        confirmBtn = findViewById(R.id.confirm_btn);

        //initialize user data
        username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        password = getIntent().getStringExtra("password");

        //Menu Bar

        //set button listeners to be active
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

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
        if (id == R.id.confirm_btn)
        {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
        if(id == R.id.cancel_button)
        {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
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
