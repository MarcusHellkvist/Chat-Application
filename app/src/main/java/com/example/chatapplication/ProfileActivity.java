package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileDialog.ProfileDialogListener {

    private static final int REQUEST_CHOOSE_PICTURE = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference usersRef;
    private StorageReference storageReference;

    private ImageView ivProfilePicture;
    private TextView tvUsername, tvEmail, tvPhone, tvAmountFriends;
    private Button btnEditProfile;

    private User user;
    private String currentUserId;
    private Uri imageUri;
    private int friendAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        currentUserId = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        // IMAGE VIEWS
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        ivProfilePicture.setOnClickListener(changePictureListener);
        
        // TEXT VIEWS
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAmountFriends = findViewById(R.id.tv_amount_friends);
        
        //BUTTON VIEWS
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(editListener);

        // TODO MOVE ALL CODE TO onStart();
        //getUserData();
        getProfilePicture();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // REAL-TIME FOR USER INFO
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
        //REAL-TIME FOR AMOUNT OF FRIENDS
        usersRef.document(currentUserId)
                .collection("friends")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<String> friends = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value){
                            friends.add(document.getId());
                        }
                        friendAmount = friends.size();
                        tvAmountFriends.setText(friendAmount + "");
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            ivProfilePicture.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage() {

        StorageReference imagesRef = storageReference.child("images/" + currentUserId);
        imagesRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfileActivity.this, "picture uploaded to database successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Something went wrong: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getProfilePicture(){

        StorageReference imagesRef = storageReference.child("images/" + currentUserId);
        final long ONE_MEGABYTE = 1024 * 1024;

        imagesRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivProfilePicture.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ivProfilePicture.setImageResource(R.drawable.defaultavatar);
                    }
                });

    }

    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_PICTURE);
    }

    private void openDialog(){
        ProfileDialog profileDialog = new ProfileDialog();
        profileDialog.show(getSupportFragmentManager(), "profile dialog");
    }

    private View.OnClickListener editListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDialog();
        }
    };

    private View.OnClickListener changePictureListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            choosePicture();
        }
    };

    @Override
    public void updateUserInfo(final String name, final String phone) {

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
                                    .update("name", name, "phoneNumber", phone)
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