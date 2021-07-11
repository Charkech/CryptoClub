package com.example.cryptoclub1;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterNotification extends  RecyclerView.Adapter<AdapterNotification.HolderNotification>{
    @NonNull
    private Context context;
    private ArrayList<ModelNotification> notificationlist;

    public AdapterNotification(@NonNull Context context, ArrayList<ModelNotification> notificationlist) {
        this.context = context;
        this.notificationlist = notificationlist;
    }

    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view row_notification
        View view= LayoutInflater.from(context).inflate(R.layout.row_notification,parent,false);

        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        //get and set data to views

        //get data
        ModelNotification model=notificationlist.get(position);
        String name=model.getsName();
        String notification=model.getNotification();
        String image=model.getsImage();
        String time=model.getTimestamp();

        //convert timestamp to dd-mm-yyyy hh-mm am-pm
        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
        //set to views
        holder.nameTv.setText(name);
        holder.timeTV.setText(pTime);
        holder.notificationTV.setText(notification);
        try {
            Glide.with(context).load(image).placeholder(R.drawable.ic_baseline_insert_emoticon_24).into(holder.avaterIV);
        }catch(Exception e){
            holder.avaterIV.setImageResource(R.drawable.ic_baseline_insert_emoticon_24);
        }
    }

    @Override
    public int getItemCount() {
        return notificationlist.size();
    }

    class HolderNotification extends RecyclerView.ViewHolder{
        ImageView avaterIV;
        TextView nameTv,timeTV,notificationTV;
        public HolderNotification(@NonNull View itemView) {
            super(itemView);
            //init views
            avaterIV=itemView.findViewById(R.id.avatarIV);
            nameTv=itemView.findViewById(R.id.nameTVnotif);
            timeTV=itemView.findViewById(R.id.timeTVnotif);
            notificationTV=itemView.findViewById(R.id.notificationTV);

        }
    }
}
