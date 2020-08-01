package com.project.tictrac;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

import com.project.tictrac.session.SessionActivity;

public class MainActivity extends AppCompatActivity {
    private Button sessionActivityButton;
    private Button statsActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to buttons
        sessionActivityButton = findViewById(R.id.sessionActivityButton);
        statsActivityButton = findViewById(R.id.statsActivityButton);

        // Set button listeners
        setButtonListeners();
    }

    private void setButtonListeners() {
        // Launch SessionActivity on button click
        sessionActivityButton.setOnClickListener(v -> {
            Intent i = new Intent(this, SessionActivity.class);
            startActivity(i);
        });

        // Launch StatsActivity on button click
        statsActivityButton.setOnClickListener(v -> {
//            Intent i = new Intent(this, StatsActivity.class);
//            startActivity(i);
        });
    }

}