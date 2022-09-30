package com.example.whim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class existlogin extends AppCompatActivity {

    private EditText loginemail, loginpwd;

    private Button loginbutton, signupbutton;
    private TextView forget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        getSupportActionBar().hide();
        loginemail = findViewById(R.id.acc_input);
        loginpwd = findViewById(R.id.password_input);
        loginbutton = findViewById(R.id.login_button);
        signupbutton = findViewById(R.id.create_button);
        forget = findViewById(R.id.forget);

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(existlogin.this, forgetpassword.class));
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = loginemail.getText().toString().trim();
                String pwd = loginpwd.getText().toString().trim();

                if (mail.isEmpty() || pwd.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                }else{
                    // login the user
                }
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(existlogin.this, signup.class));
            }
        });
    }
}
