package com.example.cryptoclub1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NotificationsFragment extends Fragment {
    //recyclerview
    RecyclerView notificationRv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);

        //init recyclerview
        notificationRv=view.findViewById(R.id.notifications_recycler);
        return view;
    }
}