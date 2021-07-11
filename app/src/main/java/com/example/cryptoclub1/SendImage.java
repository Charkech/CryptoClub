package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SendImage extends AppCompatActivity {

    String url,reciver_name,sender_uid,reciver_uid;
    ImageView imageView;

    Uri imageUri;
    ProgressBar progressBar;
    Button sendButton;
    UploadTask uploadTask;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference ref11,ref22;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Uri uri;
    MsgObject msgObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);

        msgObject = new MsgObject();
        storageReference = firebaseStorage.getInstance().getReference("Message Images");

        imageView  = findViewById(R.id.iv_sendImage);
        sendButton = findViewById(R.id.btn_sendimage);
        progressBar = findViewById(R.id.progbar_sendimage);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            url = bundle.getString("u");
            reciver_name = bundle.getString("n");
            reciver_uid = bundle.getString("ruid");
            sender_uid = bundle.getString("suid");
        }else{
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
        }
        Glide.with(getApplicationContext()).load(url).into(imageView);
        imageUri = Uri.parse(url);

        ref11 = database.getReference("Message").child(sender_uid).child(reciver_uid); //create reference in data that shows who send who
        ref22 = database.getReference("Message").child(reciver_uid).child(sender_uid);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
                finish();
            }
        });


    }


    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton(); //using singelton
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void sendImage() {

        if(url != null){
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = ref.putFile(imageUri);

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
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat currDate = new SimpleDateFormat("dd--MMMM--yy");
                        final String resDate = currDate.format(calendar.getTime());

                        Calendar ctime = Calendar.getInstance();
                        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
                        final String resTime = currTime.format(ctime.getTime());

                        String time = resDate + " " + resTime;


                        msgObject.setDate(resTime);
                        msgObject.setTime(time);
                        msgObject.setMessage(downloadUri.toString());
                        msgObject.setReceiverUid(reciver_uid);
                        msgObject.setSenderUid(sender_uid);
                        msgObject.setType("iv");

                        String id = ref11.push().getKey();
                        ref11.child(id).setValue(msgObject); //push the msg in to the firebase

                        String id2 = ref22.push().getKey();
                        ref22.child(id2).setValue(msgObject); //push the msg in to the firebase

                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }
            });
        }

        else{
            Toast.makeText(this,"select image",Toast.LENGTH_SHORT).show();
        }



    }
}