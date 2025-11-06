package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewListActivity extends AppCompatActivity implements TutorListAdapter.OnTutorClickListenerWithReviews {

    private ImageButton menuButton;

    private DrawerLayout drawerLayout;

    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup

    private int userId;                // must be passed from login/signup
    private String username;
    private String password;

    private RecyclerView recyclerView;
    private TutorListAdapter adapter;
    private List<TutorItem> tutorList = new ArrayList<>();

    private static final String TAG = "ReviewListActivity";
    private static final String URL_TUTORS = "http://coms-3090-037.class.las.iastate.edu:8080/tutors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        recyclerView = findViewById(R.id.reviewRecyclerView);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        userId = getIntent().getIntExtra("userId", -1);

        LinearLayout homeButton = findViewById(R.id.nav_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewListActivity.this, MainMenuActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        LinearLayout profileButton = findViewById(R.id.nav_profile);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewListActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        LinearLayout sessionsButton = findViewById(R.id.nav_sessions);
        sessionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewListActivity.this, SessionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Open sidebar when menu button clicked
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                drawerLayout.closeDrawer(findViewById(R.id.nav_view));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.nav_view));
            }
        });

        // RecyclerView setup
        adapter = new TutorListAdapter(this, tutorList, (TutorListAdapter.OnTutorClickListenerWithReviews) this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.tutor_search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        loadTutors();
    }

    @Override
    public void onTutorClicked(TutorItem tutor) {
        // What happens when the whole card is clicked
        //Intent intent = new Intent(this, ProfileActivity.class);
        //intent.putExtra("tutorId", tutor.tutorId);
        //startActivity(intent);
    }

    @Override
    public void onReviewsClicked(TutorItem tutor) {
        // What happens when the "Reviews" button is clicked
        Intent intent = new Intent(this, TutorReviewsActivity.class);
        intent.putExtra("tutorId", tutor.tutorId);
        intent.putExtra("username", username);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }



    private void loadTutors() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(
                Request.Method.GET,
                URL_TUTORS,
                response -> {
                    try {
                        JSONArray arr;
                        if (response.trim().startsWith("[")) {
                            arr = new JSONArray(response);
                        } else {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("tutors")) arr = obj.getJSONArray("tutors");
                            else if (obj.has("data")) arr = obj.getJSONArray("data");
                            else {
                                arr = new JSONArray();
                                arr.put(obj);
                            }
                        }

                        List<TutorItem> newTutorList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject t = arr.getJSONObject(i);
                            int tutorId = t.optInt("tutorId", -1);
                            String uname = t.optString("username", t.optString("userName", ""));
                            Log.d(TAG, "Parsed tutorId=" + tutorId + " for " + uname);
                            String first = t.optString("firstName", "");
                            String last = t.optString("lastName", "");

                            String displayName;
                            if ((first == null || first.isEmpty()) && (last == null || last.isEmpty())) {
                                displayName = null; // Set to null so adapter will fetch names
                            } else {
                                displayName = (first + " " + last).trim();
                            }

                            double rating = t.optDouble("rating", 3.5);

                            TutorItem item = new TutorItem(tutorId, uname, displayName, rating);
                            newTutorList.add(item);
                        }

                        // Use the update method to refresh both lists
                        adapter.updateTutorList(newTutorList);

                    } catch (Exception e) {
                        Log.e(TAG, "Failed parse tutors: " + e.toString());
                    }
                },
                error -> Log.e(TAG, "Failed load tutors: " + error.toString())
        );

        queue.add(req);
    }

    private void onTutorReviewsClicked(TutorItem tutor) {
        // Start an activity to show reviews for this tutor (implement later)
        Intent i = new Intent(ReviewListActivity.this, TutorReviewsActivity.class);
        i.putExtra("tutorId", tutor.tutorId);
        i.putExtra("username", username);
        i.putExtra("userId", userId);
        startActivity(i);
    }
}
