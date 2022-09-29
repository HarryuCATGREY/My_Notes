package com.example.whim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity {
    private EditText signupemail, signupwd;
    private Button signupbutton;
    private Button mlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        getSupportActionBar().hide();

        signupemail = findViewById(R.id.email_input);
        signupwd = findViewById(R.id.acc_input);
        signupbutton = findViewById(R.id.continue_button);
        mlogin = findViewById(R.id.gotologin);

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this, existlogin.class);
                startActivity(intent);
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = signupemail.getText().toString().trim();
                String pwd = signupwd.getText().toString().trim();

                if (mail.isEmpty() || pwd.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                }else if(pwd.length() < 7){
                    Toast.makeText(getApplicationContext(), "Password should be longer than 7 digits.", Toast.LENGTH_SHORT).show();

                }else{
                    // register user to firebase
                }
            }
        });




    }
}
