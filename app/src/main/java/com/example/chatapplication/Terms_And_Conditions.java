package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Terms_And_Conditions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms__and__conditions);
    }

    public void goBackButtonPressed(View view) {
        singUPIntentActivity();
    }
    public void singUPIntentActivity(){
        Intent intent = new Intent(this,SingUp.class);
        startActivity(intent);
    }

}

