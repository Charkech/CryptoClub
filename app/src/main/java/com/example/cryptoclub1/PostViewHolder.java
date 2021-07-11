package com.example.cryptoclub1;



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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static java.lang.Boolean.valueOf;


public class PostViewHolder extends RecyclerView.ViewHolder {

    LinearLayout linear;
    ImageView imageViewProfile,iv_post;
    TextView tv_likes,tv_description,tv_comment,tv_time,tv_profname;
    ImageButton likeBtn,optionsBtn,commentBtn;
    DatabaseReference likes_ref,comments_ref;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    int count_likes , count_cmts = 0;;
    String myUid;
    String pId,pTitle,pImage,pTimeStamp,pLikes,pComments,uDp,uName,uEmail;
    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public void setPost(FragmentActivity fragmentActivity, String name, String url,
                        String postUri, String time, String type, String uid, String description) {

        imageViewProfile = itemView.findViewById(R.id.iv_profilepic);
        iv_post = itemView.findViewById(R.id.iv_post_item);
        tv_description = itemView.findViewById(R.id.tv_desc_post);
        commentBtn = itemView.findViewById(R.id.commentBtn_post);
        likeBtn = itemView.findViewById(R.id.likeBtn_post);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        optionsBtn = itemView.findViewById(R.id.moreOptionBtn_post);
        tv_profname = itemView.findViewById(R.id.tv_name_post);
        tv_likes = itemView.findViewById(R.id.tv_likes_post);
        tv_comment= itemView.findViewById(R.id.tv_comments_post);


        Glide.with(fragmentActivity).load(url).into(imageViewProfile);

        Glide.with(fragmentActivity).load(postUri).into(iv_post);
        if(postUri.contains("text")){
            iv_post.setVisibility(View.INVISIBLE);

        }

        tv_description.setText(description);
        tv_time.setText(time);
        tv_profname.setText(name);
        if(postUri.contains("text")){
            iv_post.setVisibility(View.GONE);

        }

    }
    private void addToHisNotifications(String hisUid,String pId,String message){
        String timestamp=""+System.currentTimeMillis();

        HashMap<Object,String> hashMap=new HashMap<>();
        hashMap.put("pId",pId);
        hashMap.put("timestamp",timestamp);
        hashMap.put("pUid",hisUid);
        hashMap.put("notification",message);
        hashMap.put("sUid",myUid);

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
    public void likeChecker(final String postkey){
        likeBtn = itemView.findViewById(R.id.likeBtn_post);

        likes_ref = db.getReference("Post Likes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        myUid=uid;
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeBtn.isPressed()){
                    addToHisNotifications(""+uid,""+pId,"Liked your post");
                }
            }
        });
        likes_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(uid)){
                    likeBtn.setImageResource(R.drawable.ic_baseline_favorite_24);

                }else{
                    likeBtn.setImageResource(R.drawable.ic_baseline_favorite_dislike_24);
                }
                count_likes = (int)snapshot.child(postkey).getChildrenCount();

                tv_likes.setText(Integer.toString(count_likes)+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}