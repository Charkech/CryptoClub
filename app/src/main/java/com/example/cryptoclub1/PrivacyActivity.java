package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PrivacyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String [] status = {"Choose Option","Public","Private"};
    TextView status_tv;
    Spinner spinner;
    Button saveBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        saveBtn = findViewById(R.id.privacyBtn);
        status_tv = findViewById(R.id.tv_status);
        spinner =findViewById(R.id.spinner);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        documentReference = db.collection("user").document(currentUserId);

        Context context;
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,status);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrivacy();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String privacy_res = task.getResult().getString("privacy");
                    status_tv.setText(privacy_res);

                }else{
                    Toast.makeText(PrivacyActivity.this,"error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePrivacy() {
        final String value = spinner.getSelectedItem().toString();
        if(value == "Choose Option"){
            Toast.makeText(PrivacyActivity.this,"Please select a value",Toast.LENGTH_SHORT).show();
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserId = user.getUid();

            final DocumentReference sDoc = db.collection("user").document(currentUserId);
            db.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {
                    transaction.update(sDoc, "privacy", value);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(PrivacyActivity.this,"Privacy Updated",Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PrivacyActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this,"Select Privacy Level",Toast.LENGTH_SHORT).show();


    }
}