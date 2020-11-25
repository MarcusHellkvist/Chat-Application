package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.user_editText);
        password = findViewById(R.id.user_pass_editText);

    }

    public void singUpButtonPressed(View view) {
        singUPIntentActivity();
    }

    public void loginButtonPressed(View view) {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "User loged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (MainActivity.this, Chat_Activity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void singUPIntentActivity(){
        Intent intent = new Intent(this,SingUp.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       if(currentUser != null) {
           Intent intent = new Intent (this, Chat_Activity.class);
           startActivity(intent);
       }
    }



}