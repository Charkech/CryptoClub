package com.example.cryptoclub1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.cryptoclub1.MainActivity.CHANNEL_1;
import static com.example.cryptoclub1.MainActivity.CHANNEL_2;

public class MsgActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView imageView;
    ImageButton sendButton,camBtn,micBtn;
    TextView username;
    EditText messageEt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref1,ref2;
    MsgObject msgObject;
    Uri uri;
    String reciver_name,reciver_uid,sender_uid,url;
    private NotificationManagerCompat notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        notificationManager = NotificationManagerCompat.from(this);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            url = bundle.getString("u");
            reciver_name = bundle.getString("n");
            reciver_uid = bundle.getString("uid");


        }else{
            Toast.makeText(this, "No Such User", Toast.LENGTH_SHORT).show();
        }

        msgObject = new MsgObject();
        recyclerView = findViewById(R.id.rv_msgs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MsgActivity.this));
        imageView = findViewById(R.id.iv_msg);
        messageEt = findViewById(R.id.msg_et);
        sendButton = findViewById(R.id.send_ib);
        camBtn = findViewById(R.id.camBtn_msg);
        micBtn = findViewById(R.id.micBtn);
        username = findViewById(R.id.username_msg_tv);

        Glide.with(getApplicationContext()).load(url).into(imageView);
        username.setText(reciver_name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid = user.getUid();

        ref1 = database.getReference("Message").child(sender_uid).child(reciver_uid); //create reference in data that shows who send who
        ref2 = database.getReference("Message").child(reciver_uid).child(sender_uid);

//        Log.e("t",reciver_name);


        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1); //pick image code
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = sendMsg();
                sendOnChannel1(v,msg,username.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //hide keyboard

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==1|| resultCode == RESULT_OK||
                    data!=null||data.getData()!=null){

                uri = data.getData();

                String url = uri.toString();
                Intent intent = new Intent(MsgActivity.this, SendImage.class);
                intent.putExtra("u",url);
                intent.putExtra("n",reciver_name);
                intent.putExtra("ruid",reciver_uid);
                intent.putExtra("suid",sender_uid);
                startActivity(intent);
            }
        }
        catch (Exception e){
            Toast.makeText(this,"No File Selected!",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MsgObject> options = new FirebaseRecyclerOptions.Builder<MsgObject>()
                .setQuery(ref1, MsgObject.class).build();


        FirebaseRecyclerAdapter<MsgObject, MsgViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MsgObject, MsgViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull MsgViewHolder holder, int position, @NonNull MsgObject model) {
                        holder.setMessage(getApplication(),model.getMessage(),model.getTime(),model.getDate(),model.getType(),model.getSenderUid(),model.getReceiverUid());
                    }


                    @NonNull
                    @Override
                    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_layout, parent, false);
                        return new MsgViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    private String sendMsg() {


        String message = messageEt.getText().toString();

        //the timepost
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd--MMMM--yy");
        final String resDate = currDate.format(calendar.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        final String resTime = currTime.format(ctime.getTime());

        String time = resDate + " " + resTime;

        if(message.isEmpty()){
            Toast.makeText(this, "Empty Message", Toast.LENGTH_SHORT).show();
        }else{

            msgObject.setDate(resTime);
            msgObject.setTime(time);
            msgObject.setMessage(message);
            msgObject.setReceiverUid(reciver_uid);
            msgObject.setSenderUid(sender_uid);
            msgObject.setType("text");

            String id = ref1.push().getKey();
            ref1.child(id).setValue(msgObject); //push the msg in to the firebase

            String id2 = ref2.push().getKey();
            ref2.child(id2).setValue(msgObject); //push the msg in to the firebase

            messageEt.setText("");//clear textview after sending


        }
    return msgObject.getMessage();
    }

public void sendOnChannel1(View v,String msg,String userName){

    String fromUser = ref1.getKey();
    String toUser = ref2.getKey();
    Log.e("u1",fromUser);
    Log.e("u2", toUser);
    Query mQuery = ref1.limitToLast(1);

    mQuery.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot dataSnapshot : snapshot.child("").getChildren()) {
                String lastMsg =  dataSnapshot.child("message").getValue().toString();//this is the msg
                String newMsg = "Message from "+userName+": " + lastMsg;
                Log.e("t",newMsg);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });


    Notification notification = new NotificationCompat.Builder(this,CHANNEL_1).setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("New Message").setContentText(msg).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE).build();  //msgObject.setSenderUid(sender_uid)?

//    notificationManager.notify(1,notification);

}
    public void sendOnChannel2(View v){
        String message = messageEt.getText().toString();
        String title = "Message from " + "dsgsdf";
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_2).setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE).build();  //msgObject.setSenderUid(sender_uid)?

        notificationManager.notify(2,notification);

    }
}