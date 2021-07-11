package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cryptoclub1.LoadingScreen.Splashscreen;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    private Uri selectedUri;
    public static final int PICK_FILE = 1; //image
    UploadTask uploadTask;
    EditText et_desc;
    String url,name;
    StorageReference storageReference;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3;
    MediaController mediaController;
    String type;
    Button btnChooseFile,btnUploadFile;
    PostClass postMember; //initialize the class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postMember = new PostClass();
        mediaController = new MediaController(this);
        imageView= findViewById(R.id.iv_post);
        progressBar= findViewById(R.id.pb_post);
        et_desc = findViewById(R.id.comment_post);
//        videoView= findViewById(R.id.vv_post);
        btnChooseFile= findViewById(R.id.btn_choosefile_post);
        btnUploadFile= findViewById(R.id.btn_uploadfile_post);
        postMember = new PostClass();


        storageReference = FirebaseStorage.getInstance().getReference("User Posts");



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUid = user.getUid();

        db1 = db.getReference("All images").child(currentUserUid);
        db2 = db.getReference("All texts").child(currentUserUid);
        db3 = db.getReference("All Posts"); //no child for all posts

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    post();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//                Intent intent = new Intent(PostActivity.this,HomeFeedFragment.class);
//                startActivity(intent);
//                finish();

            }
        });

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent,PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null)
            selectedUri = Uri.parse(String.valueOf(R.drawable.bottom_lock));//no picture
        //check validations
        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data!=null ){


            selectedUri = data.getData();
            if(selectedUri.toString().contains("image")){
                Glide.with(getApplicationContext()).load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
//                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            }
            else{
//                selectedUri = Uri.parse(String.valueOf(R.drawable.bottom_lock));
                Toast.makeText(this,"No file selected!",Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUid = user.getUid();//get user

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentUserUid); //get db

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    name = task.getResult().getString("name"); //retrieve name,url from firestore
                    url = task.getResult().getString("url");
                }else{
                    Toast.makeText(PostActivity.this,"error",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void post() throws UnsupportedEncodingException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUid = user.getUid();//get user

        String desc = et_desc.getText().toString();//the comment

        //the timepost
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd--MMMM--yy");
        final String resDate = currDate.format(calendar.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        final String resTime = currTime.format(ctime.getTime());

        String time = resDate + " " + resTime;


        if (!TextUtils.isEmpty(desc) && selectedUri != null) {

            progressBar.setVisibility(View.VISIBLE);

            final StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedUri));  //save the posts
            uploadTask = ref.putFile(selectedUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    return null;
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (type.equals("iv")) {
                            postMember.setDescription(desc);
                            postMember.setName(name);
                            postMember.setPostUri(downloadUri.toString());
                            postMember.setTime(time);
                            postMember.setType("iv");
                            postMember.setUrl(url);
                            postMember.setUid(currentUserUid);

                            String id1 = db1.push().getKey(); //images
                            db1.child(id1).setValue(postMember);

                            String id2 = db3.push().getKey(); //all posts
                            db3.child(id2).setValue(postMember);

                            Toast.makeText(PostActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(PostActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (selectedUri == null && !TextUtils.isEmpty(desc)) {
            //only text
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference ref = storageReference.child("text");  //save the posts
            uploadTask = ref.putBytes(desc.getBytes("UTF-8"));

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    return null;
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        postMember.setDescription(desc);
                        postMember.setName(name);
                        postMember.setPostUri(downloadUri.toString());
                        postMember.setTime(time);
                        postMember.setType("txt");
                        postMember.setUrl(url);
                        postMember.setUid(currentUserUid);

                        String id1 = db2.push().getKey(); //texts
                        db2.child(id1).setValue(postMember);

                        String id2 = db3.push().getKey(); //all posts
                        db3.child(id2).setValue(postMember);

                        Toast.makeText(PostActivity.this, "Text Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(PostActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else {
                        Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton(); //using singelton
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}