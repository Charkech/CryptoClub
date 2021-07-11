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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UpdatePhoto extends AppCompatActivity {

    FirebaseFirestore db=FirebaseFirestore.getInstance();
    StorageReference storageReference;
    ImageView imageView;
    UploadTask uploadTask;
    Uri imageuri,url;
    ProgressBar progressBar;
    FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    String currentuid;
    FirebaseDatabase firebaseDatabase;
    Button button;
    private final static int PICK_IMAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo);

        imageView=findViewById(R.id.iv_update_profile);
        button=findViewById(R.id.btn_updatephoto);
        progressBar=findViewById(R.id.pv_updatephoto);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        currentuid=user.getUid();

        storageReference = firebaseStorage.getReference("Profile images");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
            }


        });
    }
    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton(); //using singelton
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
    private void updateImage() {
        final StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
        uploadTask = ref.putFile(imageuri);

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



                    final DocumentReference sDoc = db.collection("user").document(currentuid);
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            transaction.update(sDoc, "url", downloadUri);


                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UpdatePhoto.this,"Profile Updated",Toast.LENGTH_SHORT).show();

                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                            DatabaseReference db1,db2;
                            Map<String,Object> profile = new HashMap<>();
                            profile.put("url",downloadUri.toString());
                            db1 = database.getReference("All Posts");
                            Query query= db1.orderByChild("uid").equalTo(currentuid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot ds :snapshot.getChildren()){
                                        ds.getRef().updateChildren(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdatePhoto.this, "done", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            db2 = database.getReference("All Profiles");
                            Query query1= db2.orderByChild("uid").equalTo(currentuid);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot ds :snapshot.getChildren()){
                                        ds.getRef().updateChildren(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdatePhoto.this, "done", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdatePhoto.this,"Failed",Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                    data != null || data.getData() != null) {
                imageuri = data.getData();
                Glide.with(getApplicationContext()).load(imageuri).into(imageView);

            }
        } catch (Exception e) {
            Toast.makeText(this, "No File Selected!", Toast.LENGTH_SHORT).show();

        }
    }
    public void chooseimage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }
}