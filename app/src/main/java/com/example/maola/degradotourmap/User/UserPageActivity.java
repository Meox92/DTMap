package com.example.maola.degradotourmap.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.maola.degradotourmap.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserPageActivity extends AppCompatActivity {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.main_user_page_txt_username)
    TextView mainUserPageTxtUsername;
    @BindView(R.id.main_user_page_txt_email)
    TextView mainUserPageTxtEmail;
    @BindView(R.id.main_user_page_txt_change)
    TextView mainUserPageTxtChange;
    @BindView(R.id.imageView2)
    ImageView imageView2;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String TAG = "UserPageActivity";
    private StorageReference mStorageRef;
    private StorageReference riversRef;
    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*-----------------USER INSTANCE----------------------*/
        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(UserPageActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        /*-------------------------------------------------*/

        mStorageRef = FirebaseStorage.getInstance().getReference();
        //Child references to storage
        riversRef = mStorageRef.child("images/" + user.getUid() + "/profile");


        Log.i(TAG, "Token " + user.getToken(true) +
                "ID: " + user.getUid() +
                " Email: " + user.getEmail() +
                " Name : " + user.getDisplayName() +
                " Photo: " + user.getPhotoUrl());

        //Show photo picture if it exists
        mStorageRef.child("images/" + user.getUid() + "/profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(UserPageActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(riversRef)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(profileImage);

            }
        });

//        Glide.with(UserPageActivity.this)
//                .using(new FirebaseImageLoader())
//                .load(riversRef)
//                .into(imageView2);

        mainUserPageTxtUsername.setText(user.getDisplayName());
        mainUserPageTxtEmail.setText(user.getEmail());

        imageView2.setImageURI(user.getPhotoUrl());

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                }

        }
    }


    @OnClick(R.id.main_user_page_txt_change)
    public void onViewClicked() {
        Intent i = new Intent(UserPageActivity.this, UserSettingsPageActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }
}
