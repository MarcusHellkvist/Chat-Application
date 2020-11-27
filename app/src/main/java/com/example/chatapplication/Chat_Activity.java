package com.example.chatapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Chat_Activity extends AppCompatActivity {

    private Button btnProfile;
    private Button btnMyFriends;
    private Toolbar myToolbarFriendActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        //BUTTON VIEWS
        btnProfile = findViewById(R.id.btn_profile);
        btnMyFriends = findViewById(R.id.btn_myfriends);
        btnProfile.setOnClickListener(goToProfileListener);
        btnMyFriends.setOnClickListener(goToMyFriendsListener);

        //TOOLBAR
        myToolbarFriendActivity = findViewById(R.id.my_toolbar_friendActivity);
        setSupportActionBar(myToolbarFriendActivity);
        ActionBar ab = getSupportActionBar();
    }

    private View.OnClickListener goToProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Chat_Activity.this, ProfileActivity.class));
        }
    };

    private View.OnClickListener goToMyFriendsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Chat_Activity.this, ListOfMyFriends.class));
        }
    };

    public void addFriendPressed(View view) {
        startActivity(new Intent(this,AddFriends.class));

    }

    public void goToChstButtonPressed(View view) {
        Intent intent = new Intent (this, Chat_messages.class);
        startActivity(intent);
    }
}