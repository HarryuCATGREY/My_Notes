package com.example.whim;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whim.Models.DrawableUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.Executor;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ImageView iv_eye1;
    private EditText signupwd, pwdConfirm;
    private boolean isHideFirst = true;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        Objects.requireNonNull(getSupportActionBar()).hide();

        TextView signTitle = findViewById(R.id.signup_title);
        EditText signupemail = findViewById(R.id.email_input);
        signupwd = findViewById(R.id.acc_pwd);
        Button signupbutton = findViewById(R.id.continue_button);
        TextView mlogin = findViewById(R.id.gotologin);
        pwdConfirm = findViewById(R.id.acc_pwd_ag);

        final Drawable drawableEyeOpen = getResources().getDrawable(R.drawable.open);
        final Drawable drawableEyeCLose = getResources().getDrawable(R.drawable.hidden);
        final Drawable edit_ic = getResources().getDrawable(R.drawable.edit);


        DrawableUtil pwdCheck = new DrawableUtil(signupwd, new DrawableUtil.OnDrawableListener(){
            @Override
            public void onLeft(View v, Drawable left) {
                Toast.makeText(getApplicationContext(), "input password", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRight(View v, Drawable right) {
                isHideFirst = !isHideFirst;
                if (isHideFirst) {
                    signupwd.setCompoundDrawablesWithIntrinsicBounds(edit_ic,
                            null,
                            drawableEyeCLose, null);

                    signupwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
                    signupwd.setCompoundDrawablesWithIntrinsicBounds(edit_ic, null,
                            drawableEyeOpen,
                            null);
                    signupwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });

        DrawableUtil pwdCheckAg = new DrawableUtil(pwdConfirm, new DrawableUtil.OnDrawableListener(){
            @Override
            public void onLeft(View v, Drawable left) {
                Toast.makeText(getApplicationContext(), "input password", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRight(View v, Drawable right) {
                isHideFirst = !isHideFirst;
                if (isHideFirst) {
                    pwdConfirm.setCompoundDrawablesWithIntrinsicBounds(edit_ic,
                            null,
                            drawableEyeCLose, null);

                    pwdConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
                    pwdConfirm.setCompoundDrawablesWithIntrinsicBounds(edit_ic, null,
                            drawableEyeOpen,
                            null);
                    pwdConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();

        String already = getColoredSpanned("Already have an account?", "#042243");
        mlogin.setText(Html.fromHtml(already + " Login"));

        String i = getColoredSpanned("i", "#67B1F9");
        String g = getColoredSpanned("g","#6E80FA");
        String p = getColoredSpanned("p","#FFCA3A");
        signTitle.setText(Html.fromHtml("S"+ i + g + "n " + "U" + p));


        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, ExistLoginActivity.class);
                startActivity(intent);
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = signupemail.getText().toString().trim();
                String pwd = signupwd.getText().toString().trim();
                String pwd2 = pwdConfirm.getText().toString().trim();



                if (mail.isEmpty() || pwd.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                }else if(!pwd.equals(pwd2)){
                    Toast.makeText(getApplication(),"Confirm fails，Please enter same password.",Toast.LENGTH_SHORT).show();
                }else if(pwd.length() < 7){
                    Toast.makeText(getApplicationContext(), "Password should be longer than 7 digits.", Toast.LENGTH_SHORT).show();
                }else {
                    // register in firebase
                    firebaseAuth.createUserWithEmailAndPassword(mail, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();

                            }else{
                                Toast.makeText(getApplicationContext(), "Registration Failed :( ", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            }
        });




    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification email is sent, please verify then login in again.", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(SignUpActivity.this, ExistLoginActivity.class));
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Verification email sent failed.", Toast.LENGTH_SHORT).show();

        }
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

}