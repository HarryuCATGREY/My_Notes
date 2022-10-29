package com.example.whim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

public class GuestPersonalInformation extends AppCompatActivity {

    // Toolbar
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_personal_information);


        // Call by main activity
        Intent intent = getIntent();
        //String value = intent.getStringExtra("key"); //if it's a string you stored.
//
//        // Initiate Toolbar
//        mainToolbar = findViewById(R.id.mainToolbar);
//        setSupportActionBar(mainToolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 取代ActionBar


    }
}