package com.example.cryptoclub1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeFeedFragment extends Fragment implements View.OnClickListener{

    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,like_ref;
    Boolean isLiked = false;
    PostClass postMember;
    DatabaseReference db1,db2,db3;
    FloatingActionButton post_fab;


    @Override
    public void onStop() {
        super.onStop();
        ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_feed_fragment,container,false);
        recyclerView = view.findViewById(R.id.recylerview_feed);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        button = getActivity().findViewById(R.id.create_post);
        post_fab = getActivity().findViewById(R.id.fab);

        postMember = new PostClass();
        reference = database.getInstance().getReference("All Posts");
        like_ref = database.getReference("Post Likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            post_fab.setVisibility(View.GONE); //user not registered cant post
        }
        if(user!=null) {
            String currentUserUid = user.getUid();


        db1 = database.getReference("All images").child(currentUserUid);
        db2 = database.getReference("All texts").child(currentUserUid);
        db3 = database.getReference("All Posts"); //no child for all posts
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {

                    private static final float SPEED = 300f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }

                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };



        recyclerView.setHasFixedSize(true);
        layoutManager.setReverseLayout(true); //feed from new to old
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);


        post_fab.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<PostClass> options= new FirebaseRecyclerOptions.Builder<PostClass>()
                .setQuery(reference,PostClass.class).build();


        FirebaseRecyclerAdapter<PostClass,PostViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<PostClass, PostViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull final PostClass model) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String curruid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.setPost(getActivity(), model.getName(), model.getUrl(), model.getPostUri(),
                                model.getTime(), model.getType(), model.getUid(), model.getDescription());

                        final String name = getItem(position).getName();
                        final String url = getItem(position).getUrl();
                        final String description = getItem(position).getDescription();
                        final String uid = getItem(position).getUid();
                        final String type = getItem(position).getType();
                        final String time = getItem(position).getTime();
                        final String postUri = getItem(position).getPostUri();





                        holder.likeChecker(postkey);
                        holder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent= new Intent(getActivity(),ProfileInflater.class);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });
                        holder.optionsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ShowDialog(name,time,uid,type,postUri);

                            }
                        });


                        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isLiked = true;
                                like_ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (isLiked) {
                                            if (snapshot.child(postkey).hasChild(curruid)) {
                                                like_ref.child(postkey).child(curruid).removeValue();
                                                isLiked = false;
                                            } else {
                                                like_ref.child(postkey).child(curruid).setValue(true);
                                                isLiked = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        });

                        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(),CommentsActivity.class);
                                intent.putExtra("postkey",postkey);
                                intent.putExtra("name",name);
                                intent.putExtra("url",url);
                                startActivity(intent);
                            }
                        });

                        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(),CommentsActivity.class);
                                intent.putExtra("postkey",postkey);
                                intent.putExtra("name",name);
                                intent.putExtra("url",url);
                                startActivity(intent);
                            }
                        });
                    }



                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);

                        return new PostViewHolder(view);

                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);




    }

    private void ShowDialog( String name, String time, String uid,String type,String postUri) {

        Context context;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        ViewGroup root;
        View view = inflater.inflate(R.layout.post_options, null,false);
        TextView download = view.findViewById(R.id.download_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        alertDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String curruserid = user.getUid();

        if(!uid.equals(curruserid))
            delete.setVisibility(View.GONE); //user cant delete others posts

        if(postUri.contains("text"))
            download.setVisibility(View.GONE); //download only image

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete from 3 database exists, by time values
                //by text or image
                Query query1 = db1.orderByChild("time").equalTo(time); //images

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds1 : snapshot.getChildren())
                                ds1.getRef().removeValue();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                Query query2 = db2.orderByChild("time").equalTo(time); //text

                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds1 : snapshot.getChildren())
                            ds1.getRef().removeValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query3 = db3.orderByChild("time").equalTo(time);
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds1 : snapshot.getChildren())
                            ds1.getRef().removeValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        alertDialog.dismiss();

                    }
                });

                Toast.makeText(getActivity(),"Post Deleted",Toast.LENGTH_SHORT).show();

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    // this will request for permission when user has not granted permission for the app
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                else{
                    if(type.equals("iv")){
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(postUri));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                        request.setTitle("Download");
                        request.setDescription("Downloading Image...");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"CryptoClub"+"_"+name+uid+ ".jpg");
                        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

//                    Toast.makeText(getActivity(),"Downloading",Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharetxt = name +"\n"+"\n"+postUri;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT,sharetxt);
                intent.setType("text/plain");
                startActivity(intent.createChooser(intent,"Share Via"));
            }
        });
    }


}