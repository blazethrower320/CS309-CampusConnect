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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ForgotPasswordActivity extends AppCompatActivity  {
    // UI components
    private Button backBtn;
    private Button createAccountBtn;
    private TextView usernameTxt;
    private TextView passwordTxt;
    private TextView confirmPassTxt;
    private TextView msgResponse;



    //Variables
    private String username;
    private String password;
    private String confirmPass;
    //Path to server
    private static final String URL_UPDATE_PASSWORD = "http://coms-3090-037.class.las.iastate.edu:8080/users/updatePassword/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Initializing UI components
        backBtn = findViewById(R.id.back_btn);
        createAccountBtn = findViewById(R.id.signup_btn);
        //Initialize text fields
        usernameTxt = findViewById(R.id.create_username);
        passwordTxt = findViewById(R.id.create_password);
        confirmPassTxt = findViewById(R.id.create_password_confirm);
        msgResponse = findViewById(R.id.msgResponse);



        //Setting click listener on the back button to trigger a new intent
        backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            }
        });

        // Setting click listener on the button to trigger the string request
        createAccountBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO add functionality to this button
                //startActivity(new Intent(CreateAccountActivity.this, StringReqActivity.class));
                username = usernameTxt.getText().toString();
                password = passwordTxt.getText().toString();
                confirmPass = confirmPassTxt.getText().toString();
                //Keep in mind: username must also not be in database already so need checkusername function.
                //If fields are not filled
                if(password.isEmpty() || confirmPass.isEmpty() || username.isEmpty())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("Incomplete Field(s)");
                }
                //If passwords not equal
                else if(!password.equals(confirmPass))
                {
                    Log.e("Password Error", "Passwords do not match");
                    Toast.makeText(ForgotPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("Passwords do not match");
                }
                //If all information is good, create user
                else
                {
                    UpdatePassword(username, password);
                }
            }
        });
    }

    public void UpdatePassword(String username, String password) {
        String url = URL_UPDATE_PASSWORD + username;

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        Toast.makeText(ForgotPasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                        msgResponse.setText("Password updated successfully!");
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
                String body = "{\"password\":\"" + password + "\"}";
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
