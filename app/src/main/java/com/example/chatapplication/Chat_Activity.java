package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Chat_Activity extends AppCompatActivity {

    private Button btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        //BUTTON VIEWS
        btnProfile = findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(goToProfileListener);

    }

    public void logOutButtonPressed(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private View.OnClickListener goToProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Chat_Activity.this, ProfileActivity.class));
        }
    };
}