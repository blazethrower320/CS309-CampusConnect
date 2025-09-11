package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CounterActivity extends AppCompatActivity {

    private TextView numberTxt; // define number textview variable
    private TextView numberTxt2; // define number textview variable

    private Button increaseBtn; // define increase button variable
    private Button increaseBtn2; // define increase button variable


    private Button decreaseBtn; // define decrease button variable
    private Button decreaseBtn2; // define decrease button variable


    private Button backBtn;     // define back button variable

    private Button resetBtn; // define reset button variable
    private Button resetBtn2; // define reset button variable

    private int counter = 0;    // counter variable
    private int counter2 = 0;    // counter variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        /*setContentView(R.layout.activity_counter2); */

        /* initialize UI elements */
        numberTxt = findViewById(R.id.number);
        numberTxt2 = findViewById(R.id.number2);
        increaseBtn = findViewById(R.id.counter_increase_btn);
        increaseBtn2 = findViewById(R.id.counter_increase_btn2);
        decreaseBtn = findViewById(R.id.counter_decrease_btn);
        decreaseBtn2 = findViewById(R.id.counter_decrease_btn2);
        backBtn = findViewById(R.id.counter_back_btn);
        resetBtn = findViewById(R.id.counter_reset_btn);
        resetBtn2 = findViewById(R.id.counter_reset_btn2);

        /* when increase btn is pressed, counter++, reset number textview */
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberTxt.setText(String.valueOf(++counter));
            }
        });

        /* when decrease btn is pressed, counter--, reset number textview */
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberTxt.setText(String.valueOf(--counter));
            }
        });

        /* when reset btn is pressed, counter = 0, reset number textview */
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
                numberTxt.setText(String.valueOf(counter));
            }
        });



        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.putExtra("NUM", String.valueOf(counter));  // key-value to pass to the MainActivity
                intent.putExtra("NUM2", String.valueOf(counter2));  // key-value to pass to the MainActivity for num 2
                startActivity(intent);
            }
        });
        /*Second Score*/


        /* when increase btn is pressed, counter++, reset number textview */
        increaseBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberTxt2.setText(String.valueOf(++counter2));
            }
        });

        /* when decrease btn is pressed, counter--, reset number textview */
        decreaseBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberTxt2.setText(String.valueOf(--counter2));
            }
        });

        /* when reset btn is pressed, counter = 0, reset number textview */
        resetBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter2 = 0;
                numberTxt2.setText(String.valueOf(counter2));
            }


        });
    }
}