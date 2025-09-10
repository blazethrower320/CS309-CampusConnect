package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CounterActivity extends AppCompatActivity {

    private int counter1 = 0;   // counter for number1
    private int counter2 = 0;   // counter for number2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        /* initialize UI elements */
        TextView number1Txt = findViewById(R.id.number1);
        TextView number2Txt = findViewById(R.id.number2);
        Button increaseBtn1 = findViewById(R.id.increase_btn1);
        Button decreaseBtn1 = findViewById(R.id.decrease_btn1);
        Button increaseBtn2 = findViewById(R.id.increase_btn2);
        Button decreaseBtn2 = findViewById(R.id.decrease_btn2);
        TextView sumResult = findViewById(R.id.sum_result);
        Button sumBtn = findViewById(R.id.sum_btn);
        Button backBtn = findViewById(R.id.back_btn);

        /* counter 1 increase/decrease */
        increaseBtn1.setOnClickListener(v -> {
            counter1++;
            number1Txt.setText(String.valueOf(counter1));
        });

        decreaseBtn1.setOnClickListener(v -> {
            counter1--;
            number1Txt.setText(String.valueOf(counter1));
        });

        /* counter 2 increase/decrease */
        increaseBtn2.setOnClickListener(v -> {
            counter2++;
            number2Txt.setText(String.valueOf(counter2));
        });

        decreaseBtn2.setOnClickListener(v -> {
            counter2--;
            number2Txt.setText(String.valueOf(counter2));
        });

        /* calculate sum */
        sumBtn.setOnClickListener(v -> {
            int sum = counter1 + counter2;
            sumResult.setText("The sum is " + sum);
        });

        /* send result back to MainActivity */
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CounterActivity.this, MainActivity.class);
            intent.putExtra("NUM", String.valueOf(counter1 + counter2));
            startActivity(intent);
        });
    }
}
