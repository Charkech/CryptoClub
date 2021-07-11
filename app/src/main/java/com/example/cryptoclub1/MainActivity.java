package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.cryptoclub1.CoinsValues.ValuesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_1 = "ch1";
    public static final String CHANNEL_2 = "ch2";
    Button signout_btn;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getActivity().getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);
        Bundle extras = getIntent().getExtras();
        if(extras==null) {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFeedFragment()).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProfileFragment()).commit();
        }

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Chat Messages");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2, "Channel 2", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Post Notifications");

            Context context;
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            switch (item.getItemId()){
                case R.id.profile_bottom:
                    if(user==null){
                        Intent intent= new Intent(MainActivity.this,RegisterActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    selected=new ProfileFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).addToBackStack(null).commit();
                    break;
                case R.id.home_bottom:
                    selected=new HomeFeedFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).addToBackStack(null).commit();
                    break;
                case R.id.values_bottom:
                    selected=new ValuesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).addToBackStack("values").commit();
                    break;
                case R.id.chat_bottom_fragment:
                    Intent intent= new Intent(MainActivity.this,ChatActivity.class);
                    startActivity(intent);
                    break;
            }

            return true;
        }
    };
    public void logout(View view) {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }


}