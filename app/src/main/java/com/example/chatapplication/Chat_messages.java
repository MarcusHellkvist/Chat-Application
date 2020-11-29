package com.example.chatapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;




public class Chat_messages extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    private RecyclerView messagesRecyclerView;
    private MessagesAdapter adapter;

    private Bitmap userProfilePhoto;



    private ArrayList<Message> messages = new ArrayList<>();

    private ArrayList<User> chatUser = new ArrayList<>();

    private ImageButton sendBtn;
    private EditText editTextMessage;

    LinearLayoutManager layoutManager;

    private String friendPhoto;


    private String userNName;
    private String conversetionID;

    private ImageView profPHOTO;
    private TextView textNameUs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();





        messages.clear();


        Bundle idConvName = getIntent().getExtras();
        conversetionID = idConvName.getString("chat_key");
        friendPhoto = idConvName.getString("friend_photo");


        mAuth = FirebaseAuth.getInstance();


        userNName = mAuth.getCurrentUser().getEmail();
        sendBtn = findViewById(R.id.send_message_button);
        editTextMessage = findViewById(R.id.editText_message);
        profPHOTO = findViewById(R.id.user_photo);

        textNameUs = findViewById(R.id.text_user_name);
        textNameUs.setText(userNName);



        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String textMessage = editTextMessage.getText().toString();
                Message message = new Message(userNName, textMessage);

                addMessageToFB(message);


            }

        });


        layoutManager = new LinearLayoutManager(this);
        messagesRecyclerView = Chat_messages.this.findViewById(R.id.recyclerView_userMessage);
        adapter = new MessagesAdapter(messages, userNName);


        getUsPHoto();
       // getFrPHoto();
        messagesRecyclerView.setAdapter(adapter);

        messagesRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);


    }


    public void back_to_friendList(View view) {
        Intent intent = new Intent(this, ListOfMyFriends.class);
        startActivity(intent);
    }

    private void addMessageToFB(Message message) {


        // messages.clear();
        db.collection("User conversation").document("id" + conversetionID)
                .collection("messages").add(message);
        editTextMessage.setText("");


    }

    @Override
    protected void onStart() {
        super.onStart();


        getFBMessages();
    }

    private void getFBMessages() {

        Log.d("alona", "ConversationID" + conversetionID);

        final Query docRef = db.collection("User conversation").document("id" + conversetionID).collection("messages").orderBy("messageTime");
        docRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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

                    for (DocumentSnapshot document : value.getDocuments()) {
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


        private void getUsPHoto() {

        String refToFireStore = mAuth.getCurrentUser().getUid();
        StorageReference profilePHUser = storageReference.child("images/" + refToFireStore);
        final long ONE_MEGABYTE = 1024 * 1024;
        profilePHUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                userProfilePhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                profPHOTO.setImageBitmap(userProfilePhoto);



            }


        });
        Log.d("alona", " message photo" + userProfilePhoto);


    }

  /*  private void getNameForUs (){
        final String searchPhoneNumber = textNameUs.getText().toString().trim();

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid()).getChild("name");
                        //("name", searchPhoneNumber);
    }*/



}






