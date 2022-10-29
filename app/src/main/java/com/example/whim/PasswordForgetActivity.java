package com.example.whim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PasswordForgetActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Objects.requireNonNull(getSupportActionBar()).hide();

        EditText forgetemail = findViewById(R.id.forget_email);
//        EditText verifyCode = findViewById(R.id.forget_verify);
        Button recoverbutton = findViewById(R.id.recover_button);
        TextView backlogin = findViewById(R.id.backlogin);

        firebaseAuth = FirebaseAuth.getInstance();



        backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordForgetActivity.this, ExistLoginActivity.class);
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
                    // send recover email

                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Mail sent, please recover password from your email.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordForgetActivity.this, ExistLoginActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(), "Email is wrong or Account does not exist.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });



    }

}