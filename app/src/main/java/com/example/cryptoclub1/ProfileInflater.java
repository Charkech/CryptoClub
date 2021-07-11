package com.example.cryptoclub1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileInflater extends AppCompatActivity implements View.OnClickListener{
    Context context;
    ImageView imageView;
    TextView nameEt,profEt,emailEt,bioEt,websiteEt,profileTopET;
    ImageButton editProfile,imageButtonMenu;
    Button chatBtn;
    String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_fragment);
        imageView =findViewById(R.id.imageview_profile);
        nameEt = findViewById(R.id.tv_name_profile);
        bioEt = findViewById(R.id.tv_bio_profile);
        profEt = findViewById(R.id.tv_prof_profile);
        websiteEt = findViewById(R.id.tv__website_profile);
        emailEt = findViewById(R.id.tv_email_profile);
        editProfile = findViewById(R.id.imageBtn_edit_profile);
        imageButtonMenu = findViewById(R.id.imageBtn_menu_profile);
        chatBtn = findViewById(R.id.sendMessage_btn);
        profileTopET=findViewById(R.id.profile_top);

        profileTopET.setText("USER PROFILE");
        imageView.setClickable(false);
        imageView.setEnabled(false);
        imageButtonMenu.setVisibility(View.INVISIBLE);
        editProfile.setVisibility(View.INVISIBLE);
        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");
        LoadData();
        chatBtn.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        imageButtonMenu.setOnClickListener(this);
        imageView.setOnClickListener(this);
        websiteEt.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void LoadData(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("user").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) { //created a user
                    String nameResult = task.getResult().getString("name");
                    String bioResult = task.getResult().getString("bio");
                    String profResult = task.getResult().getString("prof");
                    String emailResult = task.getResult().getString("email");
                    String webResult = task.getResult().getString("web");
//                    String uidResult = task.getResult().getString("uid");
//                    String privacyResult = task.getResult().getString("privacy");
                    String url = task.getResult().getString("url");
                    imageButtonMenu.setVisibility(View.INVISIBLE);
                    editProfile.setVisibility(View.INVISIBLE);
                    imageView.setEnabled(false);
                    imageView.setClickable(false);
                    Glide.with(ProfileInflater.this).load(url).into(imageView); //todo check this line
                    profileTopET.setText("USER PROFILE");
                    nameEt.setText(nameResult);
                    websiteEt.setText(webResult);
                    bioEt.setText(bioResult);
                    profEt.setText(profResult);
                    emailEt.setText(emailResult);
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageBtn_edit_profile:
                Intent intent = new Intent(this,UpdateProfile.class);
                startActivity(intent);
                break;

            case R.id.imageBtn_menu_profile:
                bottomMenu bottomMenu = new bottomMenu();
                bottomMenu.show(getSupportFragmentManager(),"bottomMenu");
                break;
            case R.id.imageview_profile:
                Intent intent1 = new Intent(this,ImageActivity.class);
                startActivity(intent1);
                break;

            case R.id.sendMessage_btn:
                Intent intent2 = new Intent(this,ChatActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv__website_profile:
                try {
                    String url = websiteEt.getText().toString();
                    Intent intent3=new Intent(Intent.ACTION_VIEW);
                    intent3.setData(Uri.parse(url));
                    startActivity(intent3);
                }catch (Exception e){
                    Toast.makeText(this, "Invalid Url!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
