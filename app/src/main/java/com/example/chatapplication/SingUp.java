package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SingUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private EditText email;
    private EditText pass;
    private EditText confrimPassword;
    private EditText name;
    //private EditText userNumber;
    private String idFirebase;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        email = findViewById(R.id.user_email_editText);
        pass = findViewById(R.id.user_pass_editText);
        confrimPassword = findViewById(R.id.user_confirm_pass_editText);
        name = findViewById(R.id.user_name);
        //userNumber = findViewById(R.id.user_telNumber);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

    }

    public void singUpButtonPressed(View view) {

        final String userEmail = email.getText().toString().toLowerCase().trim();
        final String userPass = pass.getText().toString().trim();
        final String confirmedPass = confrimPassword.getText().toString().trim();
        final String userName = name.getText().toString().trim().toLowerCase();
        //final String userTelNumber = userNumber.getText().toString().trim();

        //Added an if statement to make sure the password is entered correctly. /JR
        if (userPass.equals(confirmedPass)) {
            mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        //get user id from Firebase
                        idFirebase = mAuth.getCurrentUser().getUid();
                        User user = new User(userName, userEmail, idFirebase);
                        db.collection("users").document(idFirebase).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(SingUp.this, Chat_Activity.class);
                                startActivity(intent);
                                Log.d("Jenny", "Ny använade skall vara skapad");
                            }
                        });
                        // FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SingUp.this, "user done log in ", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(SingUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }


                }


            });
        } else {
            Toast.makeText(SingUp.this, "Passwords don't match, pls try again", Toast.LENGTH_LONG).show();
        }
    }


    public void informationTermsAndConditionsClicked(View view) {
        Terms_And_ConditionsIntentActivity();
    }

    private void Terms_And_ConditionsIntentActivity() {
        Intent intent = new Intent(this, Terms_And_Conditions.class);
        startActivity(intent);
    }

    public void acceptTermsAndConditionsTicked(View view) {

    }
}

