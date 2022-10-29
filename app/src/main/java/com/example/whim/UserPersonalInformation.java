package com.example.whim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

public class UserPersonalInformation extends AppCompatActivity {


    // Toolbar
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_personal_information);


        Intent intent = getIntent();
    }
}