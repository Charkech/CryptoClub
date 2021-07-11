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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cryptoclub1.LoadingScreen.Splashscreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEt,passEt,confirmEt;
    Button registerBtn,loginBtn;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt = findViewById(R.id.register_email_et);
        passEt = findViewById(R.id.register_pass_et);
        confirmEt = findViewById(R.id.confirm_pass_et);
        registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.signup_to_login);
        checkBox = findViewById(R.id.register_cb);
        progressBar = findViewById(R.id.progressbar_register);
        auth = FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });
        //if Enter pressed will login (in confirm password only)
        confirmEt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    registerBtn.performClick();
                    return true;
                }
                return false;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= emailEt.getText().toString();
                String pass = passEt.getText().toString();
                String confirmPass = confirmEt.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(confirmPass)){
                    if(pass.equals(confirmPass)){
                        progressBar.setVisibility(View.VISIBLE);
                        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToMain();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }else{
                                    String err = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error: "+ err,Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Passwords Don't Match!",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this,"Please Enter all Fields",Toast.LENGTH_SHORT).show();

                }


            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, Splashscreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            sendToMain();
        }
    }
}