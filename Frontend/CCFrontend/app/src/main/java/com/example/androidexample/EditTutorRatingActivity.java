package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class EditTutorRatingActivity extends AppCompatActivity {

    private EditText usernameEdt, ratingEdt;
    private Button confirmBtn;

    private Button back2Btn;
    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080/admin/updateRatingsTutor/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_tutor_rating_activity);

        usernameEdt = findViewById(R.id.tutor_username_edt);
        ratingEdt = findViewById(R.id.tutor_rating_edt);
        confirmBtn = findViewById(R.id.confirm_btn);
        back2Btn = findViewById(R.id.back2_btn);

        confirmBtn.setOnClickListener(v -> updateTutorRating());
        back2Btn.setOnClickListener(v -> finish());
    }

    private void updateTutorRating() {
        String username = usernameEdt.getText().toString().trim();
        String ratingStr = ratingEdt.getText().toString().trim();

        if (username.isEmpty() || ratingStr.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rating format", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + username + "/" + rating;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equalsIgnoreCase("true")) {
                        Toast.makeText(this, "Tutor rating updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // go back to main menu
                    } else {
                        Toast.makeText(this, "Failed to update rating", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
