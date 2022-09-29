package com.example.whim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class forgetpassword extends AppCompatActivity {

    private EditText forgetemail;
    private Button recoverbutton;
    private TextView backlogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpwd);

        getSupportActionBar().hide();
        forgetemail = findViewById(R.id.forget_email);
        recoverbutton = findViewById(R.id.recover_button);
        backlogin = findViewById(R.id.backlogin);

        backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgetpassword.this, existlogin.class);
                startActivity(intent);
            }
        });

        recoverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = forgetemail.getText().toString().trim();
                if(mail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your email.", Toast.LENGTH_SHORT).show();

                }else{
                    //send verification
                }
            }
        });



    }

}
