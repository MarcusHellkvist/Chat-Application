package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    private ImageView ivProfilePicture;
    private TextView tvIdNumber, tvUsername, tvEmail, tvPhone, tvAddress, tvAmountFriends;
    private Button btnEditProfile;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

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

    private void setUserData() {
        String idNumber = "#3391";
        String username = user.getName().toString();
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
            Toast.makeText(ProfileActivity.this, "edit.", Toast.LENGTH_SHORT).show();
        }
    };
}