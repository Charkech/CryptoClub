package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cryptoclub1.LoadingScreen.Splashscreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailEt,passEt;
    Button registerBtn,loginBtn;
    CheckBox showPass_cb;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        emailEt = findViewById(R.id.login_email_et);
        passEt = findViewById(R.id.login_pass_et);
        registerBtn = findViewById(R.id.login_to_signup);
        loginBtn = findViewById(R.id.login_btn);
        showPass_cb = findViewById(R.id.showpass_cb);
        progressBar = findViewById(R.id.progressbar_login);
        auth = FirebaseAuth.getInstance();


        showPass_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        //if Enter pressed will login
        passEt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    loginBtn.performClick();
                    return true;
                }
                return false;
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= emailEt.getText().toString();
                String pass = passEt.getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)){
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);
                                sendToMain();
                            }else{
                                String err = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error: "+ err,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this,"Please Enter all Fields",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this, Splashscreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}