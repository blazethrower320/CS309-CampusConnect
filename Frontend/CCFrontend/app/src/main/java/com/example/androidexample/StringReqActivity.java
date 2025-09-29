package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class StringReqActivity extends AppCompatActivity {

    // UI components
    private Button btnStringReq;
    private Button backStrReqBtn;
    private Button majorReqBtn;
    private Button passReqBtn;
    private TextView msgResponse;

    // API URL for fetching string response
    private static final String URL_STRING_REQ = "https://5263cc61-9068-4005-8b00-e644fdf97858.mock.pstmn.io/users/1";
    private static final String URL_MAJOR_REQ = "http://coms-3090-037.class.las.iastate.edu:8080/usernames";
    private static final String URL_PASS_REQ = "http://coms-3090-037.class.las.iastate.edu:8080/users/password/Zach";

    // Alternative URLs for testing purposes
    // public static final String URL_STRING_REQ = "https://2aa87adf-ff7c-45c8-89bc-f3fbfaa16d15.mock.pstmn.io/users/1";
    // public static final String URL_STRING_REQ = "http://10.0.2.2:8080/users/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_req);

        // Initializing UI components
        btnStringReq = findViewById(R.id.btnStringReq);
        backStrReqBtn = findViewById(R.id.backStrReqBtn);
        majorReqBtn = findViewById(R.id.mjrReqBtn);
        passReqBtn = findViewById(R.id.passReqBtn);

        msgResponse = findViewById(R.id.msgResponse);

        //Setting click listener on the back button to trigger a new intent
        backStrReqBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(StringReqActivity.this, MainActivity.class));
            }
        });

        // Setting click listener on the button to trigger the string request
        btnStringReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeStringReq();
            }
        });

        // Setting click listener on the button to trigger the string request
        majorReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMajReq();
            }
        });
        passReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePassReq();
            }
        });
    }



    /**
     * Makes a string request using Volley library
     **/
    private void makeStringReq() {
        // Creating a new String request
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, // HTTP method (GET request)
                URL_STRING_REQ, // API URL
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log the response for debugging purposes
                        Log.d("Volley Response", response);

                        // Display response in the TextView
                        msgResponse.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error details
                        Log.e("Volley Error", error.toString());

                        // Show an error message in the UI
                        msgResponse.setText("Failed to load data. Please try again.");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Headers for the request (if needed)
                Map<String, String> headers = new HashMap<>();
                // Example headers (uncomment if needed)
                // headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                // headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Parameters for the request (if needed)
                Map<String, String> params = new HashMap<>();
                // Example parameters (uncomment if needed)
                // params.put("param1", "value1");
                // params.put("param2", "value2");
                return params;
            }
        };
        // Add request to queue so it actually runs
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void makeMajReq() {
        // Create request
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URL_MAJOR_REQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        msgResponse.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        msgResponse.setText("Failed to load data. Please try again.");
                    }
                }
        );


        // Add request to queue so it actually runs
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void makePassReq()
    {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URL_PASS_REQ,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        msgResponse.setText(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("Volley Error", error.toString());
                        msgResponse.setText("Failed to load data. Please try again.");
                    }
                }
        );


        // Add request to queue so it actually runs
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
