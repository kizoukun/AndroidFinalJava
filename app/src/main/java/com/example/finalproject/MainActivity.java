package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://192.168.1.9/final";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        if(!sharedPreferencesManager.getIsLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;

        }

        Button logout = findViewById(R.id.logoutBtn);
        logout.setOnClickListener(v -> {
            sharedPreferencesManager.setIsLoggedIn(false);
            sharedPreferencesManager.clearUserData();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        System.out.println(sharedPreferencesManager.getIsLoggedIn());
        System.out.println(sharedPreferencesManager.getEmail());

    }



}