package com.example.cryptoclub1;

import android.app.Application;
import android.app.Notification;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.cryptoclub1.MainActivity.CHANNEL_1;
import static com.example.cryptoclub1.MainActivity.CHANNEL_2;

public class MsgViewHolder extends RecyclerView.ViewHolder {

    TextView sender_tv,reciver_tv;
    ImageView iv_sender,iv_reciver;



    public MsgViewHolder(@NonNull View itemView) {
        super(itemView);




    }
    public void setMessage(Application application, String message,String time,String date,String type,String senderUid,String receiverUid){
        sender_tv = itemView.findViewById(R.id.sender_tv);
        reciver_tv = itemView.findViewById(R.id.reciver_tv);
        iv_reciver = itemView.findViewById(R.id.iv_receiver);
        iv_sender = itemView.findViewById(R.id.iv_sender);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currUser = user.getUid();


        if(currUser.equals(senderUid)){
            if(type.equals("iv")){
                reciver_tv.setVisibility(View.GONE);
                sender_tv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.VISIBLE);
                Glide.with(application).load(message).into(iv_sender);

            }else if(type.equals("text")){
                sender_tv.setVisibility(View.VISIBLE);
                sender_tv.setText(message + "    ");//for padding
                reciver_tv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_reciver.setVisibility(View.GONE);
            }

            }else if(currUser.equals(receiverUid)){
            if(type.equals("iv")){
                reciver_tv.setVisibility(View.GONE);
                iv_reciver.setVisibility(View.VISIBLE);
                sender_tv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                Glide.with(application).load(message).into(iv_reciver);

            }else if(type.equals("text")){
                reciver_tv.setVisibility(View.VISIBLE);
                sender_tv.setVisibility(View.GONE);
                reciver_tv.setText(message);
                iv_sender.setVisibility(View.GONE);
                iv_reciver.setVisibility(View.GONE);
            }
        }
    }



}
