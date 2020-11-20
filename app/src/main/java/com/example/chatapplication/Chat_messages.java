package com.example.chatapplication;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Chat_messages extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // private FirebaseDatabase database;

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter adapter;
    // private MessageAdapterFriend friendAdapter;

    private ArrayList<Message> messages = new ArrayList<>();
    // private ArrayList<Message> friendMessages = new ArrayList<>();
    private ArrayList<User> chatUser = new ArrayList<>();

    private ImageButton sendBtn;
    private EditText editTextMessage;
    private ImageView userPhoto;
    private int fireBaseIdMap = 0;
    private int fireBaseIdMap2 = 0;

    private String userNName;

    private String friendName;
    private String friendMessage;
    private CollectionReference collection;


    // public  DateFormat messageTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);
        db = FirebaseFirestore.getInstance();


        Intent args = getIntent();
        if (args != null)
            userNName = args.getStringExtra("name");
        else userNName = "UserName";


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
                renderMessage(message);


            }

        });
        messagesRecyclerView = findViewById(R.id.recyclerView_userMessage);

        adapter = new MessagesAdapter(messages, userNName);
        messagesRecyclerView.setAdapter(adapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        //layoutManager.setReverseLayout(true);

        messagesRecyclerView.setLayoutManager(layoutManager);

        getFBMessages();
    }

    private void renderMessage(Message message) {
        // messagesRecyclerView.smoothScrollToPosition(0);

        messages.add(message);
        editTextMessage.setText("");
        adapter.notifyItemInserted(0 - 1);

        // messagesRecyclerView.smoothScrollToPosition(messages.size()-1);
        //  messagesRecyclerView.getAdapter().getItemCount()-1;
    }


    public void logOutButton(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addMessageToFB(Message message) {
        db.collection("User conversation").document("id1")
                .collection("messages").add(message);
        adapter.notifyDataSetChanged();

        // document("id1" ).set(message);
        // fireBaseIdMap2 += 1;

        //add(message);
        //db.collection("conversation").document("WT9sUC6tOGR84WbxqPsX").collection("User").add(message.getUserName());
    }

    private void getFBMessages() {
        Log.d("alona", "Get FBMessages");
        db.collection("User conversation").document(
                "id1").collection("messages").orderBy("messageTime")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("alona", "Listener triggered");
                            Log.d("alona", task.getResult().toString());
                            Log.d("alona", "" + task.getResult().isEmpty());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("alona", document.getId() + " => " + document.getData());
                                Message messagge = document.toObject(Message.class);
                                messages.add(messagge);


                                // Message messageFr = document.toObject(Message.class);
                                //friendMessages.add(messageFr);

                            }


                        } else {
                            Log.d("alona", "Error getting documents: ", task.getException());
                        }

                    }
                });

    }
}





