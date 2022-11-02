package com.example.whim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.whim.Models.DrawableUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ExistLoginActivity extends AppCompatActivity {
    //    login text
    private EditText loginemail, loginpwd;

    //    set if password can be seen
    private boolean isHideFirst = true;

    //    firebase
    private FirebaseAuth firebaseAuth;


    //    get verification
    private static final String SHARED_PREFS = "sharedPrefs";

    //    progressBar to show running process
    ProgressBar mprogressbarforlogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_login);

        getSupportActionBar().hide();

//        initialise button and view
        loginemail = findViewById(R.id.acc_input);
        loginpwd = findViewById(R.id.password_input);
//        login button
        Button loginbutton = findViewById(R.id.login_button);
//        create account buttons
        Button signupbutton = findViewById(R.id.create_button);
//        forget password link
        TextView forget = findViewById(R.id.forget);
//        progressBar
        mprogressbarforlogin = findViewById(R.id.progressbarforlogin);
//        guestLogin link
        TextView guestlogin = findViewById(R.id.guestlogin);
//        login title
        TextView login_title = (TextView) findViewById(R.id.login_title);

//        bind open_eye and close_eye drawable
        final Drawable drawableEyeOpen = getResources().getDrawable(R.drawable.open);
        final Drawable drawableEyeCLose = getResources().getDrawable(R.drawable.hidden);

//        initialise firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();  // never used

//        get colorful title
        String h = getColoredSpanned("h", "#67B1F9");
        String i = getColoredSpanned("i", "#6E80FA");
        String dot = getColoredSpanned(".", "#FFCA3A");
        login_title.setText(Html.fromHtml("W" + h + i + "m" + dot));

//        used to check if already login and continue use without login again
        checkBox();

//        click eye to show password and hide password
        new DrawableUtil(loginpwd, new DrawableUtil.OnDrawableListener() {
            @Override
            public void onLeft(View v, Drawable left) {
                Toast.makeText(getApplicationContext(), "input password", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRight(View v, Drawable right) {
                isHideFirst = !isHideFirst;
                if (isHideFirst) {
                    loginpwd.setCompoundDrawablesWithIntrinsicBounds(null,
                            null,
                            drawableEyeCLose, null);
                    loginpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
                    loginpwd.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            drawableEyeOpen,
                            null);
                    loginpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });

//        click signup button and convert to SignUpActivity page
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistLoginActivity.this, SignUpActivity.class));
            }
        });

//        click guest login link and convert to MainActivity page
        guestlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistLoginActivity.this, MainActivity.class));
            }
        });

//        click login button and check account and convert to ExistUserMainPage page
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                get email and password from loginemail and loginpwd
                String mail = loginemail.getText().toString().trim();
                String pwd = loginpwd.getText().toString().trim();

//                check account
                if (mail.isEmpty() || pwd.isEmpty()) {
//                    input failed
                    Toast.makeText(getApplicationContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                } else {
//                    input correct and login the user
                    mprogressbarforlogin.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(mail, pwd).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                            account exist and check if verified
                            checkEmailVerification();
                        } else {
//                            account not exist
                            Toast.makeText(getApplicationContext(), "Account does not exist.", Toast.LENGTH_SHORT).show();
                            mprogressbarforlogin.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

//        click forget password link and convert to PasswordForgetActivity
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistLoginActivity.this, PasswordForgetActivity.class));
            }
        });
    }


    //    used to check if already login and continue use without login again
    private void checkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("name", "");
        if (check.equals("true")) {
            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(ExistLoginActivity.this, ExistUserMainPage.class));
            finish();
        }
    }

    //    check if email is verified and if verified convert to ExistUserMainPage
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.isEmailVerified()) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", "true");
            editor.apply();

            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(ExistLoginActivity.this, ExistUserMainPage.class));
        } else {
            mprogressbarforlogin.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Please verify your email first.", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

    }

    //    change color for text
    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }


}
