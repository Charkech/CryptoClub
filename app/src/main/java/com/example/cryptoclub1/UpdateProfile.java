package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class UpdateProfile extends AppCompatActivity {
    EditText name_et,prof_et,web_et,bio_et,email_et;
    Button updateBtn;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currUserId = currentUser.getUid();
        documentReference= db.collection("user").document(currUserId);


        name_et = findViewById(R.id.et_name_up);
        prof_et = findViewById(R.id.et_prof_up);
        web_et = findViewById(R.id.et_website_up);
        bio_et = findViewById(R.id.et_bio_up);
        email_et = findViewById(R.id.et_email_up);
        updateBtn = findViewById(R.id.update_btn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //load to screen the previous data (profile)


        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String nameResult = task.getResult().getString("name");
                    String bioResult = task.getResult().getString("bio");
                    String profResult = task.getResult().getString("prof");
                    String emailResult = task.getResult().getString("email");
                    String webResult = task.getResult().getString("web");
                    String url = task.getResult().getString("url");


                    name_et.setText(nameResult);
                    web_et.setText(webResult);
                    bio_et.setText(bioResult);
                    prof_et.setText(profResult);
                    email_et.setText(emailResult);
                } else {
                    Toast.makeText(getApplicationContext(), "No Profile!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void updateProfile() {
            String name = name_et.getText().toString();
            String bio = bio_et.getText().toString();
            String prof = prof_et.getText().toString();
            String email = email_et.getText().toString();
            String website = web_et.getText().toString();

             final DocumentReference sDoc = db.collection("user").document(currentuid);
                    db.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {
                            transaction.update(sDoc, "name", name);
                            transaction.update(sDoc, "prof", prof);
                            transaction.update(sDoc, "email", email);
                    transaction.update(sDoc, "bio", bio);
                    transaction.update(sDoc, "web", website);


                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }