package com.example.whim;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whim.Models.DrawableUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class ExistLoginActivity extends AppCompatActivity {

    private EditText loginemail, loginpwd; // login text
    private boolean isHideFirst = true;  //
    private Button loginbutton, signupbutton;  // two buttons
    private TextView forget;  // forget password link
    private TextView guestlogin;  // guestLogin link

    private FirebaseAuth firebaseAuth;

    ProgressBar mprogressbarforlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_login);

        // Bind view and functions
        getSupportActionBar().hide();
        loginemail = findViewById(R.id.acc_input);
        loginpwd = findViewById(R.id.password_input);
        loginbutton = findViewById(R.id.login_button);
        signupbutton = findViewById(R.id.create_button);
        forget = findViewById(R.id.forget);
        mprogressbarforlogin=findViewById(R.id.progressbarforlogin);
        guestlogin = findViewById(R.id.guestlogin);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();  // never used

        TextView login_title = (TextView)findViewById(R.id.login_title);

        String h = getColoredSpanned("h", "#67B1F9");
        String i = getColoredSpanned("i","#6E80FA");
        String dot = getColoredSpanned(".","#FFCA3A");
        login_title.setText(Html.fromHtml("W"+h+i+"m"+dot));

        final Drawable drawableEyeOpen = getResources().getDrawable(R.drawable.open);
        final Drawable drawableEyeCLose = getResources().getDrawable(R.drawable.hidden);
//        final Drawable edit_ic = getResources().getDrawable(R.drawable.edit);


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


        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistLoginActivity.this, SignUpActivity.class));
            }
        });

        guestlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistLoginActivity.this, MainActivity.class));
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

                    mprogressbarforlogin.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(mail,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkEmailVerification();
                            }else{
                                Toast.makeText(getApplicationContext(), "Account does not exist.", Toast.LENGTH_SHORT).show();
                                mprogressbarforlogin.setVisibility(View.INVISIBLE);

                            }
                        }
                    });
                }
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistLoginActivity.this, PasswordForgetActivity.class));
            }
        });
    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()==true){
            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(ExistLoginActivity.this, ExistUserMainPage.class));
        }else{
            mprogressbarforlogin.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Please verify your email first.", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

    }
    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}
