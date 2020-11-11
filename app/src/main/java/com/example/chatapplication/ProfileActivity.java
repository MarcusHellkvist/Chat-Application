package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity implements ProfileDialog.ProfileDialogListener {

    private static final int REQUEST_CODE_CAMERA = 1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference usersRef;

    private ImageView ivProfilePicture;
    private TextView tvIdNumber, tvUsername, tvEmail, tvPhone, tvAddress, tvAmountFriends;
    private Button btnEditProfile;

    private User user;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        usersRef = db.collection("users");
        currentUserId = mAuth.getCurrentUser().getUid();

        // IMAGE VIEWS
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        
        // TEXT VIEWS
        tvIdNumber = findViewById(R.id.tv_id_number);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvAmountFriends = findViewById(R.id.tv_amount_friends);
        
        //BUTTON VIEWS
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(editListener);

        getUserData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        usersRef.document(currentUserId)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()){
                            String name = value.getString("name");
                            String phone = value.getString("phoneNumber");
                            tvUsername.setText(name);
                            tvPhone.setText(phone);
                        }
                    }
                });
    }


    private void setUserData() {
        String idNumber = "#3391";
        String username = user.getName();
        String email = user.getEmail();
        String phone = user.getPhoneNumber();
        String address = "SKÖVDE, SWEDEN";
        String amountOfFriends = "246";

        tvIdNumber.setText(idNumber);
        tvUsername.setText(username);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvAddress.setText(address);
        tvAmountFriends.setText(amountOfFriends);
    }

    private void openDialog(){
        ProfileDialog profileDialog = new ProfileDialog(user);
        profileDialog.show(getSupportFragmentManager(), "profile dialog");
    }


    private void getUserData() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        usersRef.document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                Log.d("TAG", "onComplete: " + document.getData());
                                user = document.toObject(User.class);
                                setUserData();
                            } else {
                                Log.d("TAG", "onComplete: No such data!");
                            }
                        } else {
                            Log.d("TAG", "onComplete: get failed with ", task.getException());
                        }
                    }
                });
    }

    private View.OnClickListener editListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDialog();
        }
    };

    @Override
    public void updateUserInfo(String name, String phone) {

        // Update User class.
        user.setName(name);
        user.setPhoneNumber(phone);

        FirebaseUser fUser = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        // update auth
        fUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // update firestore
                            usersRef.document(currentUserId)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileActivity.this, "profile updated.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

    }
}