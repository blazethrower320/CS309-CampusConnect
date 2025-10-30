package com.example.androidexample;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
public class TutorReviewsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private int tutorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_reviews);

        Intent intent = getIntent();
        tutorId = intent.getIntExtra("tutorId", -1);

        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(TutorReviewsActivity.this, ReviewListActivity.class);
            backIntent.putExtra("tutorId", tutorId);
            startActivity(backIntent);
            finish();
        });
    }
}
