package com.example.cryptoclub1.LoadingScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cryptoclub1.LoginActivity;
import com.example.cryptoclub1.MainActivity;
import com.example.cryptoclub1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen extends AppCompatActivity {
    ImageView imageView,LogoView;
    TextView nameTv,name2Tv;
    long animationTime = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        imageView=findViewById(R.id.iv_logo_splash);
        nameTv=findViewById(R.id.tv_splash_name);
        name2Tv=findViewById(R.id.tv_splash_name2);
//        LogoView=findViewById(R.id.iv_logo_crypto);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y",400f);
        ObjectAnimator animatorName=ObjectAnimator.ofFloat(nameTv,"x",120f);
        animatorY.setDuration(animationTime);
        animatorName.setDuration(animationTime+200);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animatorY,animatorName);
        animatorSet.start();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Splashscreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);


    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //if the users exists go to main, instead of running the splash screen
                if(user!=null){
                    Intent intent= new Intent(Splashscreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent= new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },4000);
    }
}