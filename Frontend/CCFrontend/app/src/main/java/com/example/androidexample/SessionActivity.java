package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SessionActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private boolean isTutor = false;   // cached tutor status
    private boolean isAdmin = false;   // passed from login/signup

    private int userId;                // must be passed from login/signup
    private String username;
    private String password;

    private TextView createSession;

    private Spinner majorSpinner;
    private SearchView classSearchView;
    private RecyclerView sessionsRecycler;
    private SessionAdapter sessionAdapter;
    private ArrayList<Session> allSessions = new ArrayList<>();
    private RequestQueue requestQueue;

    // Change this to your actual backend base URL
    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String SESSIONS_ENDPOINT = "/sessions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        drawerLayout = findViewById(R.id.drawer_layout);
        createSession = findViewById(R.id.btn_create_session);
        menuButton = findViewById(R.id.menu_button);

        // Get values passed from login/signup
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isTutor = getIntent().getBooleanExtra("isTutor", false);
        userId = getIntent().getIntExtra("userId", -1);

        if (isTutor) {
            createSession.setVisibility(View.VISIBLE);
        }

        LinearLayout homeButton = findViewById(R.id.nav_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, MainMenuActivity.class);
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
            Intent intent = new Intent(SessionActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password); // only if needed for certain calls
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        LinearLayout reviewsButton = findViewById(R.id.nav_reviews);
        reviewsButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, ReviewListActivity.class);
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

        createSession.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, CreateSessionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("isTutor", isTutor);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        });


        // --- New UI wiring ---
        majorSpinner = findViewById(R.id.major_spinner);

        classSearchView = findViewById(R.id.class_search_view);
        int id = classSearchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = classSearchView.findViewById(id);
        searchText.setTextColor(Color.WHITE);
        searchText.setHintTextColor(Color.LTGRAY);

        sessionsRecycler = findViewById(R.id.sessions_recycler);

        // Setup spinner with string-array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.majors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(adapter);

        // RecyclerView + adapter
        sessionsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // inside onCreate(), replace adapter creation with:
        sessionAdapter = new SessionAdapter(allSessions, session -> {
            // Prevent tutors from joining their own session
            if (session.getTutorUsername() != null && session.getTutorUsername().equalsIgnoreCase(username)) {
                Toast.makeText(SessionActivity.this, "You cannot join your own session.", Toast.LENGTH_SHORT).show();
                return;
            }

            joinSession(session.getSessionId(), username);
        });

        sessionsRecycler.setAdapter(sessionAdapter);

        sessionsRecycler.setAdapter(sessionAdapter);

        // request queue
        requestQueue = Volley.newRequestQueue(this);

        // fetch sessions from API then show them
        fetchSessionsFromBackend();

        // Spinner selection listener - filter list whenever selection changes
        majorSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // do nothing
            }
        });

        // SearchView listener - filter as user types / presses search
        classSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });
    }

    private void joinSession(int sessionId, String username) {

        String url = BASE_URL + "/sessions/joinSession/" + username + "/" + sessionId;

        JsonObjectRequest joinRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Toast.makeText(SessionActivity.this, "Joined", Toast.LENGTH_SHORT).show();

                    for (Session s : allSessions) {
                        if (s.getSessionId() == sessionId) {
                            s.setJoined(true);
                            break;
                        }
                    }
                    sessionAdapter.notifyDataSetChanged();
                },
                error -> {
                    Toast.makeText(SessionActivity.this, "Join failed", Toast.LENGTH_SHORT).show();
                    Log.e("SessionActivity", "Join failed: " + error.toString());
                });

        requestQueue.add(joinRequest);
    }




    private void fetchSessionsFromBackend() {
        String url = BASE_URL + SESSIONS_ENDPOINT;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        allSessions.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                // Expect fields: className, classCode, meetingLocation, meetingTime, sessionId
                                int sessionId = obj.optInt("sessionId", -1);
                                String className = obj.optString("className", "");
                                String classCode = obj.optString("classCode", "");
                                String meetingLocation = obj.optString("meetingLocation", "");
                                String meetingTime = obj.optString("meetingTime", "");

                                // Create Session with temporary tutorUsername = "Loading..."
                                Session s = new Session(sessionId, className, classCode, meetingLocation, meetingTime, "Loading...");
                                allSessions.add(s);

                                // Fetch tutor username asynchronously
                                fetchTutorForSession(s);
                            }
                        } catch (JSONException e) {
                            Log.e("SessionActivity", "JSON parse error", e);
                            Toast.makeText(SessionActivity.this, "Failed to parse sessions", Toast.LENGTH_SHORT).show();
                        }

                        applyFilters();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SessionActivity", "Volley error: " + error.getMessage());
                        Toast.makeText(SessionActivity.this, "Failed to load sessions", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }


    /**
     * Applies spinner + search filters to the full session list and updates the RecyclerView adapter.
     */
    private void applyFilters() {
        String selectedMajor = majorSpinner.getSelectedItem() == null ? "All" : majorSpinner.getSelectedItem().toString();
        String query = classSearchView.getQuery() == null ? "" : classSearchView.getQuery().toString().trim().toLowerCase();

        ArrayList<Session> filtered = new ArrayList<>();
        for (Session s : allSessions) {
            boolean matchesMajor = selectedMajor.equals("All") || s.getClassCode().toLowerCase().contains(selectedMajor.toLowerCase()) || s.getClassName().toLowerCase().contains(selectedMajor.toLowerCase());
            boolean matchesQuery = query.isEmpty() || s.getClassName().toLowerCase().contains(query) || s.getClassCode().toLowerCase().contains(query);

            if (matchesMajor && matchesQuery) {
                filtered.add(s);
            }
        }

        sessionAdapter.updateList(filtered);
    }
    private void fetchTutorForSession(Session session) {
        String url = BASE_URL + "/sessions/getSessionTutor/" + session.getSessionId();

        JsonObjectRequest tutorRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // The endpoint likely returns a Tutor object, extract username
                    String username = response.optString("username", "Unknown Tutor");
                    session.setTutorUsername(username);
                    sessionAdapter.notifyDataSetChanged(); // update UI when fetched
                },
                error -> Log.e("SessionActivity", "Failed to fetch tutor for session " + session.getSessionId(), error)
        );

        requestQueue.add(tutorRequest);
    }

}
