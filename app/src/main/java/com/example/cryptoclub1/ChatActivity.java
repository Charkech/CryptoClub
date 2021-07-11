package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.cryptoclub1.MainActivity.CHANNEL_1;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference profileReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    RecyclerView recyclerView;
    EditText searchEt;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UsersMembers> options = new FirebaseRecyclerOptions.Builder<UsersMembers>()
                .setQuery(profileReference, UsersMembers.class).build();


        FirebaseRecyclerAdapter<UsersMembers, ProfileViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UsersMembers, ProfileViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull UsersMembers model) {

                        final String postkey = getRef(position).getKey();

                        final String name = getItem(position).getName();
                        final String url = getItem(position).getUrl();
                        final String uid = getItem(position).getUid();

                        holder.setProfileInChat(getApplication(), model.getName(), model.getUid(), model.getProf(),model.getUrl());



                        holder.sendmsgBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChatActivity.this,MsgActivity.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",uid);

                                startActivity(intent);

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_profile_item, parent, false);
                        return new ProfileViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        searchEt = findViewById(R.id.search_user_et);
        recyclerView = findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        profileReference = database.getReference("All Users");
        Context context;




        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String query = searchEt.getText().toString(); //there is no support for lower case in firebase
                Query search = profileReference.orderByChild("name").startAt(query).endAt(query+"\uf0ff");

                FirebaseRecyclerOptions<UsersMembers> options = new FirebaseRecyclerOptions.Builder<UsersMembers>()
                        .setQuery(search, UsersMembers.class).build();


                FirebaseRecyclerAdapter<UsersMembers, ProfileViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<UsersMembers, ProfileViewHolder>(options) {

                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull UsersMembers model) {


                                final String name = getItem(position).getName();
                                final String url = getItem(position).getUrl();
                                final String uid = getItem(position).getUid();


                                holder.setProfileInChat(getApplication(), model.getName(), model.getUid(), model.getProf(),model.getUrl());

                                holder.sendmsgBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ChatActivity.this,MsgActivity.class);
                                        intent.putExtra("n",name);
                                        intent.putExtra("u",url);
                                        intent.putExtra("uid",uid);

                                        startActivity(intent);

                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_profile_item, parent, false);
                                return new ProfileViewHolder(view);
                            }
                        };
                firebaseRecyclerAdapter.startListening();
//                firebaseRecyclerAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(firebaseRecyclerAdapter);


            }
        });

    }

}