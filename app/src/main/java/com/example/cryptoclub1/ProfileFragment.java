package com.example.cryptoclub1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;


public class  ProfileFragment extends Fragment implements View.OnClickListener{
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    Context context;
    ImageView imageView;
    TextView nameEt,profEt,emailEt,bioEt,websiteEt;
    ImageButton editProfile,imageButtonMenu;
    Button chatBtn;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) { //deprecated, todo change to lifeCycle
        super.onActivityCreated(savedInstanceState);
        imageView = getActivity().findViewById(R.id.imageview_profile);
        nameEt = getActivity().findViewById(R.id.tv_name_profile);
        bioEt = getActivity().findViewById(R.id.tv_bio_profile);
        profEt = getActivity().findViewById(R.id.tv_prof_profile);
        websiteEt = getActivity().findViewById(R.id.tv__website_profile);
        emailEt = getActivity().findViewById(R.id.tv_email_profile);
        editProfile = getActivity().findViewById(R.id.imageBtn_edit_profile);
        imageButtonMenu = getActivity().findViewById(R.id.imageBtn_menu_profile);
        chatBtn = getActivity().findViewById(R.id.sendMessage_btn);



        chatBtn.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        imageButtonMenu.setOnClickListener(this);
        imageView.setOnClickListener(this);
        websiteEt.setOnClickListener(this);
    }

    //hiding the ACTIONBAR
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

        View view = inflater.inflate(R.layout.profile_fragment,container,false);

        return view;
    }

    // this is a onclick for all views
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageBtn_edit_profile:
                Intent intent = new Intent(getActivity(),UpdateProfile.class);
                startActivity(intent);
                break;

            case R.id.imageBtn_menu_profile:
                bottomMenu bottomMenu = new bottomMenu();
                bottomMenu.show(getChildFragmentManager(),"bottomMenu");
                break;
            case R.id.imageview_profile:
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                startActivity(intent1);
                break;

            case R.id.sendMessage_btn:
                Intent intent2 = new Intent(getActivity(),ChatActivity.class);
                startActivity(intent2);
                break;


            case R.id.tv__website_profile:
                try {

//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(browserIntent);

                    String url = websiteEt.getText().toString();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "https://" + url;
                    Intent intent3=new Intent(Intent.ACTION_VIEW);
                    intent3.setData(Uri.parse(url));
                    startActivity(intent3);
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url!", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }






    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();
        DocumentReference documentReference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        documentReference = firestore.collection("user").document(currentId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){ //created a user
                    String nameResult = task.getResult().getString("name");
                    String bioResult = task.getResult().getString("bio");
                    String profResult = task.getResult().getString("prof");
                    String emailResult = task.getResult().getString("email");
                    String webResult = task.getResult().getString("web");
//                    String uidResult = task.getResult().getString("uid");
//                    String privacyResult = task.getResult().getString("privacy");
                    String url = task.getResult().getString("url");


                    Glide.with(getContext()).load(url).into(imageView); //todo check this line


                    nameEt.setText(nameResult);
                    websiteEt.setText(webResult);
                    bioEt.setText(bioResult);
                    profEt.setText(profResult);
                    emailEt.setText(emailResult);

                }else{
                    Intent intent = new Intent(getActivity(),CreateProfile.class);
                    startActivity(intent); //if data(user) not exists, will go back to create profile

                }
            }
        });

    }
}
