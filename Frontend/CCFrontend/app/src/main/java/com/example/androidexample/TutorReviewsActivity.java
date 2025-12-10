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

/**
 * TutorReviewsActivity displays all ratings for a selected tutor,
 * allows users to submit new reviews, and handles enabling/disabling
 * the rating UI based on role permissions (user, tutor, or admin).
 *
 * <p>This activity loads tutor ratings from the backend, initializes
 * a RecyclerView for displaying reviews, and validates review actions
 * to prevent tutors from reviewing themselves.
 *
 * @author William Rossow
 */
public class TutorReviewsActivity extends AppCompatActivity {

    /** Base URL for API requests */
    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";

    /** ID of the tutor/user being reviewed */
    private int tutorId, userId;

    /** Whether the user is a tutor */
    private boolean isTutor;

    /** RecyclerView for displaying tutor ratings */
    private RecyclerView recyclerView;

    /** Adapter for the RecyclerView */
    private RatingListAdapter adapter;

    /** List of ratings displayed */
    private List<RatingItem> ratingList = new ArrayList<>();

    /** Current user's username */
    private String username;
    /** Current user's password */
    private String password;
    /** Whether the current user is an admin */
    private boolean isAdmin;

    /** Rating bar input */
    private RatingBar ratingBar;

    /** Comment input box */
    private EditText commentBox;

    /** Button to submit a rating */
    private Button submitButton;

    /** Button to go back */
    private ImageButton backButton;

    /** Volley request queue */
    private RequestQueue queue;

    /**
     * Called when the activity is first created.
     * Initializes UI components, sets up the RecyclerView,
     * and loads tutor ratings.
     *
     * @param savedInstanceState Bundle containing activity state
     */
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

        User user = User.getInstance();
        username = user.getUsername();
        password = user.getPassword();
        isTutor = user.isTutor();
        isAdmin = user.isAdmin();
        userId = user.getUserId();
        tutorId = getIntent().getIntExtra("tutorId", -1);

        // Set up RecyclerView

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RatingListAdapter(this, ratingList);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());

        // Disable UI by default

        // Then control UI access
        if (isTutor) {
            int CtutorId = User.getInstance().getTutorId();
            if (CtutorId == tutorId) {
                // Cannot review yourself
                disableRatingUI();
                Toast.makeText(this, "You cannot review yourself", Toast.LENGTH_SHORT).show();
            } else {
                enableRatingUI(); // Tutors reviewing others can rate
            }
        } else {
            enableRatingUI(); // Normal users can rate
        }

        loadTutorRatings();

    }

    /**
     * Enables the rating UI so the user can submit ratings.
     */
    private void enableRatingUI() {
        submitButton.setOnClickListener(v -> submitRating());
        submitButton.setEnabled(true);
        submitButton.setAlpha(1f);
        ratingBar.setEnabled(true);
        commentBox.setEnabled(true);
    }

    /**
     * Disables the rating UI, preventing the user from submitting ratings.
     */
    private void disableRatingUI() {
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.5f);
        ratingBar.setEnabled(false);
        commentBox.setEnabled(false);
    }



    /**
     * Loads all ratings for the tutor from the server and updates the RecyclerView.
     */
    private void loadTutorRatings() {
        String url = BASE_URL + "/ratings/getTutorRatings/" + tutorId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ratingList.clear();
                    List<RatingItem> adminRatings = new ArrayList<>();
                    List<RatingItem> normalRatings = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            JSONObject userObj = obj.getJSONObject("user");

                            String username = userObj.getString("username");
                            String comment = obj.optString("comments", "");
                            double rating = obj.optDouble("rating", 0);

                            boolean isAdminReviewer = false;

                            // Detect admin based on backend
                            if (userObj.has("isAdmin"))
                                isAdminReviewer = userObj.getBoolean("isAdmin");
                            else if (userObj.has("role"))
                                isAdminReviewer = userObj.getString("role").equalsIgnoreCase("ADMIN");

                            RatingItem newItem = new RatingItem(username, comment, rating, isAdminReviewer);

                            if (isAdminReviewer)
                                adminRatings.add(newItem);
                            else
                                normalRatings.add(newItem);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Put admins FIRST
                    ratingList.addAll(adminRatings);
                    ratingList.addAll(normalRatings);

                    adapter.notifyDataSetChanged();
                },
                error -> Log.e("TutorReviewsActivity", "Failed to load ratings: " + error)
        );

        queue.add(request);
    }

    /**
     * Submits a rating for the tutor to the server.
     */
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
