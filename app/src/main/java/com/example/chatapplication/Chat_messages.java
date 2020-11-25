package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat_messages extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private RecyclerView messagesRecyclerView;
    private MessagesAdapter adapter;


    private ArrayList<Message> messages = new ArrayList<>();

    private ArrayList<User> chatUser = new ArrayList<>();

    private ImageButton sendBtn;
    private EditText editTextMessage;
    private ImageView userPhoto;
    LinearLayoutManager layoutManager;


    private String userNName;
    private String conversetionID;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);
        db = FirebaseFirestore.getInstance();
        messages.clear();



        Bundle idConvName = getIntent().getExtras();

        conversetionID= idConvName.getString("chat_key");




        mAuth = FirebaseAuth.getInstance();


        userNName = mAuth.getCurrentUser().getEmail();
        sendBtn = findViewById(R.id.send_message_button);
        editTextMessage = findViewById(R.id.editText_message);
        // userPhoto = findViewById(R.id.profil_photo);




        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String textMessage = editTextMessage.getText().toString();
                Message message = new Message(userNName, textMessage);

                addMessageToFB(message);



            }

        });
        messagesRecyclerView = findViewById(R.id.recyclerView_userMessage);

        adapter = new MessagesAdapter(messages, userNName);
        messagesRecyclerView.setAdapter(adapter);


        layoutManager = new LinearLayoutManager(this);



        messagesRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);


    }




    public void logOutButton(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addMessageToFB(Message message) {


        // messages.clear();
        db.collection("User conversation").document("id"+ conversetionID)
                .collection("messages").add(message);
        editTextMessage.setText("");


    }

    @Override
    protected void onStart() {
        super.onStart();


        getFBMessages();
    }

    private void getFBMessages() {

        Log.d("alona", "ConversationID" +  conversetionID);

        final Query docRef = db.collection("User conversation").document("id"+ conversetionID).collection("messages").orderBy("messageTime");
        docRef.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("alona", "Listen failed." + error.getMessage());
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    Log.d("alona", "Hallå Current data: " + value.toString());
                    Log.d("alona", "iajsdijawidjai");

                    messages.clear();

                    for(DocumentSnapshot document: value.getDocuments()){
                        Message messagge = document.toObject(Message.class);
                        messages.add(messagge);


                    }

                   adapter.notifyDataSetChanged();



                } else {
                    Log.d("alona", "Current data: null");
                }
            }
        });




    }
}





