package com.example.cryptoclub1;

import android.app.Application;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    TextView textViewName,textViewProfession,viewUserProfile, sendmsgBtn;
    ImageView imageView;
    CardView cardView;
    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProfile(Fragment fragmentActivity,String name,String uid,String prof,String url) {

        cardView = itemView.findViewById(R.id.cardview_profile);
        textViewName = itemView.findViewById(R.id.tv_name_profile);
        textViewProfession = itemView.findViewById(R.id.tv_prof_profile);
//        viewUserProfile = itemView.findViewById(R.id.);
        imageView = itemView.findViewById(R.id.iv_profilepic);

        Glide.with(fragmentActivity).load(url).into(imageView);
        textViewProfession.setText(prof);
        textViewName.setText(name);
    }

    public void setProfileInChat(Application fragmentActivity, String name, String uid, String prof, String url) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        ImageView imageView =itemView.findViewById(R.id.user_imageview);
        TextView nameTv = itemView.findViewById(R.id.name_chat_item_tv);
        TextView profTv = itemView.findViewById(R.id.chat_item_prof_tv);
        sendmsgBtn = itemView.findViewById(R.id.sendMessage_tv);


        if(userId.equals(uid)){//Cant send message to myself
            Glide.with(fragmentActivity).load(url).into(imageView);
            nameTv.setText(name);
            profTv.setText(prof);
            sendmsgBtn.setVisibility(View.INVISIBLE);

        }else{
            Glide.with(fragmentActivity).load(url).into(imageView);
            nameTv.setText(name);
            profTv.setText(prof);

        }




    }
}
