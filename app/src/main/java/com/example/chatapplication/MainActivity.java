package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void singUpButtonPressed(View view) {
        singUPIntentActivity();
    }

    public void loginButtonPressed(View view) {
    }

    public void singUPIntentActivity(){
        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SingUp.class);
        startActivity(intent);
    }
}