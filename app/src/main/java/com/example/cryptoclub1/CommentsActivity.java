package com.example.cryptoclub1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView imageView;
    TextView username_tv;
    EditText comment;
    Boolean isCommentLiked = false;
    String userid;
    String name_result, bio_result, email_result, web_result, url, uid, name, post_key;

    Button commentBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference CommentsRef, likesRef, userCommentsRef;
    CommentsObject commentsObject;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        commentsObject = new CommentsObject();
        recyclerView = findViewById(R.id.rv_comments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        userid =  user.getUid();



        commentBtn = findViewById(R.id.post_btn_comment);
        imageView = findViewById(R.id.iv_comments);
        username_tv = findViewById(R.id.username_comments);
        comment = findViewById(R.id.add_comment_et);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
            name = extras.getString("name");
            post_key = extras.getString("postkey");
        }

        CommentsRef = database.getReference("All Posts").child(post_key).child("Comments"); //create a dir in every user, of comments, and save it there

//        likesRef = database.getReference("comment likes");

        userCommentsRef = database.getReference("User Posts").child(userid);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //hide keyboard
            }
        });
    }

    private void comment() {

        String newComment = comment.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currUser = user.getUid();
//        addToHisNotifications(""+userid);
        //the timepost
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd--MMMM--yy");
        final String resDate = currDate.format(calendar.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        final String resTime = currTime.format(ctime.getTime());

        String time = resDate + " " + resTime;

        if (newComment.isEmpty()) {
            Toast.makeText(this, "Empty Comment", Toast.LENGTH_SHORT).show();
        } else {
            commentsObject.setTime(time);
            commentsObject.setComment(newComment);
            commentsObject.setUrl(url);
            commentsObject.setUid(uid);
            commentsObject.setUsername(name_result);

            String id = CommentsRef.push().getKey();
            CommentsRef.child(id).setValue(commentsObject); //push the comment in to the firebase

            Toast.makeText(this, "Comment Added", Toast.LENGTH_SHORT).show();
            comment.setText("");//clear textview after sending

            String id2 = likesRef.push().getKey();
            likesRef.child(id2).setValue(commentsObject); //push the like in to the firebase


        }
    }
    private void addToHisNotifications(String hisUid,String pId,String message){
        String timestamp=""+System.currentTimeMillis();

        HashMap<Object,String> hashMap=new HashMap<>();
        hashMap.put("pId",pId);
        hashMap.put("timestamp",timestamp);
        hashMap.put("pUid",hisUid);
        hashMap.put("notification",message);
//        hashMap.put("sUid",myUid);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //added succesfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed to add
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();


        Glide.with(getApplication()).load(url).into(imageView);
        username_tv.setText("Commenting to " +name+ " Post");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("user").document(userid);

        likesRef = database.getReference("comment likes");


        //to get all my (user connected) info, im searching in the database for same uid
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (Objects.requireNonNull(task.getResult()).exists()) {
                    name_result = task.getResult().getString("name");
                    bio_result = task.getResult().getString("bio");
                    email_result = task.getResult().getString("email");
                    web_result = task.getResult().getString("web");
                    url = task.getResult().getString("url");
                    uid = task.getResult().getString("uid");

                }

            }
        });


        FirebaseRecyclerOptions<CommentsObject> options = new FirebaseRecyclerOptions.Builder<CommentsObject>()
                .setQuery(CommentsRef, CommentsObject.class).build();


        FirebaseRecyclerAdapter<CommentsObject, CommentsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CommentsObject, CommentsViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull CommentsObject model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currUser = user.getUid();
                        final String postkey = getRef(position).getKey();
                        String time = getItem(position).getTime();

                        holder.setComment(getApplication(), model.getComment(), model.getTime(), model.getUrl(), model.getUsername(), model.getUid());
                        holder.likeChecker(postkey);

                        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isCommentLiked = true;
                                likesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (isCommentLiked) {
                                            if (snapshot.child(postkey).hasChild(currUser)) {
                                                likesRef.child(postkey).child(currUser).removeValue();
                                                isCommentLiked = false;
                                            } else {
                                                likesRef.child(postkey).child(currUser).setValue(true);
                                                isCommentLiked = false;
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        holder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Query query = CommentsRef.orderByChild("time").equalTo(time); // order by time, and find by excat time
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            dataSnapshot1.getRef().removeValue();
                                            Toast.makeText(CommentsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });


                    }


                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item, parent, false);
                        return new CommentsViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

}
