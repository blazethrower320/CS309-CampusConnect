package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TutorReviewsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private int tutorId, userId;
    private boolean isTutor;

    private RecyclerView recyclerView;
    private RatingListAdapter adapter;
    private List<RatingItem> ratingList = new ArrayList<>();

    private String username;
    private String password;

    private boolean isAdmin;

    private RatingBar ratingBar;
    private EditText commentBox;
    private Button submitButton;
    private ImageButton backButton;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_reviews);

        recyclerView = findViewById(R.id.reviewsRecyclerView);
        ratingBar = findViewById(R.id.ratingBar);
        commentBox = findViewById(R.id.comment_box);
        submitButton = findViewById(R.id.submit_button);
        backButton = findViewById(R.id.back_button);

        queue = Volley.newRequestQueue(this);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        userId = getIntent().getIntExtra("userId", -1);
        tutorId = getIntent().getIntExtra("tutorId", -1);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RatingListAdapter(this, ratingList);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());

        if (isTutor) {
            submitButton.setEnabled(false);
            submitButton.setAlpha(0.5f);
            Toast.makeText(this, "Tutors cannot rate themselves", Toast.LENGTH_SHORT).show();
        }

        loadTutorRatings();

        submitButton.setOnClickListener(v -> submitRating());
    }

    private void loadTutorRatings() {
        String url = BASE_URL + "/ratings/getTutorRatings/" + tutorId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ratingList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String username = obj.getJSONObject("user").getString("username");
                            String comment = obj.optString("comments", "");
                            double rating = obj.optDouble("rating", 0);
                            ratingList.add(new RatingItem(username, comment, rating));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Log.e("TutorReviewsActivity", "Failed to load ratings: " + error.toString())
        );

        queue.add(request);
    }

    private void submitRating() {
        String comment = commentBox.getText().toString().trim();
        int ratingValue = (int) ratingBar.getRating();
        Log.d("TutorReviewsActivity", "Submitting rating with tutorId=" + tutorId + ", userId=" + userId);
        JSONObject body = new JSONObject();
        try {
            body.put("rating", ratingValue);
            body.put("comments", comment);
            body.put("tutorId", tutorId);
            body.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                BASE_URL + "/ratings/createRating",
                response -> {
                    Toast.makeText(this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                    loadTutorRatings();
                    commentBox.setText("");
                    ratingBar.setRating(3);
                },
                error -> {
                    Log.e("TutorReviewsActivity", "Error submitting rating: " + error.toString());
                    Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public byte[] getBody() {
                return body.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(postRequest);
    }
}
