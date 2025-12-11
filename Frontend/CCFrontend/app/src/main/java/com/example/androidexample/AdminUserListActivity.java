package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private UserAdapter adapter;
    private List<SimpleUser> usersList;

    private RequestQueue queue;

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String URL_GET_USERS = BASE_URL + "/users";
    private static final String URL_DELETE_USER = BASE_URL + "/users/deleteUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_deleteusers);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.past_sessions_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        queue = Volley.newRequestQueue(this);
        usersList = new ArrayList<>();
        adapter = new UserAdapter(usersList, user -> deleteUser(user), user -> makeAdmin(user));
        recyclerView.setAdapter(adapter);

        // Spinner and SearchView
        Spinner roleSpinner = findViewById(R.id.role_spinner);
        SearchView userSearchView = findViewById(R.id.user_search_view);

        // Populate spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"All", "Admin", "Tutor"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);

        // SearchView text listener
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query, roleSpinner.getSelectedItem().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, roleSpinner.getSelectedItem().toString());
                return true;
            }
        });

        // Spinner selection listener
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.filter(userSearchView.getQuery().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adapter.filter(userSearchView.getQuery().toString(), "All");
            }
        });

        fetchAllUsers();
    }

    private void fetchAllUsers() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                URL_GET_USERS,
                response -> {
                    try {
                        usersList.clear();
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            SimpleUser user = new SimpleUser(
                                    obj.optInt("userId", 0),
                                    obj.optString("username", ""),
                                    obj.optString("firstName", ""),
                                    obj.optString("lastName", ""),
                                    obj.optString("password", ""),
                                    obj.optBoolean("isAdmin", false),
                                    false
                            );

                            usersList.add(user);

                            // Fetch tutor status for each user
                            fetchTutorStatus(user, () -> {
                                adapter.reapplyFilter(); // instead of notifyDataSetChanged
                            });
                        }

                        // initially populate filteredList
                        adapter.setFilteredList(new ArrayList<>(usersList));
                        adapter.reapplyFilter();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("AdminUserList", "Response: " + response);
                        Toast.makeText(this, "Failed to parse users", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("AdminUserList", "Error fetching users: " + error.toString());
                    Toast.makeText(this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }


    private void deleteUser(SimpleUser user) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", user.getUsername());
            body.put("password", user.getPassword());

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    URL_DELETE_USER,
                    response -> {
                        if (response.equals("Deleted User")) {
                            Toast.makeText(this, "User deleted: " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            usersList.remove(user);
                            adapter.reapplyFilter();
                        } else {
                            Toast.makeText(this, "Error: " + response, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("AdminUserList", "Delete error: " + error.toString());
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
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

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeAdmin(SimpleUser user) {
        String url = BASE_URL + "/admin/createAdmin/" + user.getUserId();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    boolean success = Boolean.parseBoolean(response.trim());

                    if (success) {
                        Toast.makeText(this, "Promoted to Admin: " + user.getUsername(), Toast.LENGTH_SHORT).show();
                        user.setAdmin(true);
                        adapter.reapplyFilter();
                    } else {
                        Toast.makeText(this, "Server returned false — user not promoted", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to promote user", Toast.LENGTH_SHORT).show();
                    Log.e("AdminUserList", "MakeAdmin error: " + error.toString());
                }
        );

        queue.add(request);
    }


    private void fetchTutorStatus(SimpleUser user, Runnable callback) {
        String url = BASE_URL + "/users/getTutor/" + user.getUserId();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    Log.d("TutorStatus", "User " + user.getUsername() + " response: " + response);

                    boolean isTutor;

                    // CASE 1: backend returns empty string → NOT A TUTOR
                    if (response == null || response.trim().isEmpty()) {
                        isTutor = false;
                    }
                    // CASE 2: backend returns a JSON object → IS A TUTOR
                    else if (response.trim().startsWith("{")) {
                        isTutor = true;
                    }
                    // failsafe
                    else {
                        isTutor = false;
                    }

                    user.setTutor(isTutor);

                    String name = user.getFirstName() + " " + user.getLastName();
                    user.setDisplayName(isTutor ? name + " (tutor)" : name);

                    callback.run();
                },
                error -> {
                    // treat errors as non-tutor
                    user.setTutor(false);
                    user.setDisplayName(user.getFirstName() + " " + user.getLastName());
                    callback.run();
                }
        );

        queue.add(request);
    }


}
