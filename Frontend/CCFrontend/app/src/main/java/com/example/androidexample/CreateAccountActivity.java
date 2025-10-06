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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity
{

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
    private static final String URL_CREATE_USER = "http://coms-3090-037.class.las.iastate.edu:8080/users/createUser";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
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
                    Toast.makeText(CreateAccountActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("Incomplete Field(s)");
                }
                //If passwords not equal
                else if(!password.equals(confirmPass))
                {
                    Log.e("Password Error", "Passwords do not match");
                    Toast.makeText(CreateAccountActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("Passwords do not match");
                }
                //If all information is good, create user
                else
                {

                    CreateUser(username, password);
                }
            }
        });
    }

    public void CreateUser(String username, String password)
    {
        // The requestBody part remains the same
        JSONObject requestBodyJson = new JSONObject();
        try
        {
            requestBodyJson.put("firstName", "Test");
            requestBodyJson.put("lastName", "Help");
            requestBodyJson.put("username", username);
            requestBodyJson.put("password", password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return; // Exit if the JSON object can't be created
        }
        final String requestBody = requestBodyJson.toString();

        // Use a StringRequest instead of a JsonObjectRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_CREATE_USER,
                response -> // Success listener
                {
                    // This will now be triggered correctly on a 2xx response
                    Log.d("Volley Success", "Response: " + response);
                    Toast.makeText(CreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                    msgResponse.setText("success!!");

                    // Optional: Redirect the user to the login screen after successful creation
                    // startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                },
                error -> // Error listener
                {
                    // Error: show feedback
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(CreateAccountActivity.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                    msgResponse.setText(error.toString());
                }
        ) {
            // Override getBodyContentType to specify you're sending a JSON
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            // Override getBody to send the JSON data
            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (java.io.UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

}