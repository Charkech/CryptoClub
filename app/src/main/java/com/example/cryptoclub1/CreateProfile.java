package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {

    EditText etName,etBio,etProfession,etWeb,etEmail;
    Button cp_btn;
    ImageView imageView;
    ProgressBar progressBar;
    UsersMembers usersMembers;
    String currUserid;
    Uri image;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        usersMembers = new UsersMembers();

        imageView = findViewById(R.id.pic_prof);
        etName = findViewById(R.id.et_name);
        etBio = findViewById(R.id.et_bio);
        etProfession = findViewById(R.id.et_prof);
        etWeb = findViewById(R.id.et_website);
        etEmail = findViewById(R.id.et_email);

        progressBar = findViewById(R.id.prog_cp);
        cp_btn = findViewById(R.id.createProf_btn);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        currUserid =  user.getUid();

        documentReference = db.collection("user").document(currUserid);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images");
        databaseReference = firebaseDatabase.getReference("All Users");

        cp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==PICK_IMAGE|| resultCode == RESULT_OK||
                    data!=null||data.getData()!=null){

                image = data.getData();
                Glide.with(getApplicationContext()).load(image).into(imageView);

            }
        }
        catch (Exception e){
            Toast.makeText(this,"No File Selected!",Toast.LENGTH_SHORT).show();

        }

    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton(); //using singelton
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadData() {
        String name = etName.getText().toString();
        String bio = etBio.getText().toString();
        String web = etWeb.getText().toString();
        String prof = etProfession.getText().toString();
        String email = etEmail.getText().toString();
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(bio) && !TextUtils.isEmpty(web)
                 &&!TextUtils.isEmpty(prof) && !TextUtils.isEmpty(email) && image!=null) {
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(image));
            uploadTask = ref.putFile(image);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    return null;
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        //creating hash map in order to save the profile

                        Map<String,String> profile = new HashMap<>();
                        profile.put("name",name);
                        profile.put("prof",prof);
                        profile.put("url",downloadUri.toString());
                        profile.put("email",email);
                        profile.put("web",web);
                        profile.put("bio",bio);
                        profile.put("uid",currUserid);
                        profile.put("privacy","Public"); //by default the privacy will be public

                        usersMembers.setName(name);
                        usersMembers.setProf(prof);
                        usersMembers.setUrl(downloadUri.toString());
                        usersMembers.setUid(currUserid);

                        //create ref to database
                        databaseReference.child(currUserid).setValue(usersMembers); //this is the key
                        documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(CreateProfile.this,"Profile Created Successfully",Toast.LENGTH_SHORT).show();
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Fragment fragment=new ProfileFragment();
                                        Intent intent = new Intent(CreateProfile.this,MainActivity.class);
                                        intent.putExtra("profile","yes");
                                        startActivity(intent);
                                    }
                                    },2000);
                            }
                        });
                    }
                }
            });
        }
        else{
            Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show();
        }

    }



}