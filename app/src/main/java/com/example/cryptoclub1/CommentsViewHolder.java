package com.example.cryptoclub1;


import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.cryptoclub1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Boolean.valueOf;


public class CommentsViewHolder extends RecyclerView.ViewHolder {


    ImageView imageView;
    TextView nameTv,timeTv,ansTv,likesTv,delete;
    ImageButton likeBtn;
    int likes_count;
    DatabaseReference likesReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public CommentsViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public void setComment(Application application, String comment, String time,
                           String url, String username, String uid) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currUser = user.getUid();

        imageView = itemView.findViewById(R.id.user_imageview_comments);
        nameTv = itemView.findViewById(R.id.name_comments);
        timeTv = itemView.findViewById(R.id.time_comments);
        ansTv = itemView.findViewById(R.id.comment_item);
        likeBtn = itemView.findViewById(R.id.likes_comments);
        delete = itemView.findViewById(R.id.delete_comments);
        likesTv = itemView.findViewById(R.id.likes_comments2);

        nameTv.setText(username);
        timeTv.setText(time);
        ansTv.setText(comment);

        Glide.with(application).load(url).into(imageView);

    }

    public void likeChecker(String postkey){
        likeBtn = itemView.findViewById(R.id.likes_comments);

        likesReference = database.getReference("comment likes");



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        likesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(uid)){
                    likeBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
                }else{
                    likeBtn.setImageResource(R.drawable.ic_baseline_favorite_dislike_24);

                }
                likes_count = (int)snapshot.child(postkey).getChildrenCount();
                likesTv.setText(Integer.toString(likes_count) +" likes");

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}