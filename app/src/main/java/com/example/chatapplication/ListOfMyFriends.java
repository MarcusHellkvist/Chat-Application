package com.example.chatapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListOfMyFriends extends AppCompatActivity {

    private RecyclerView friendsRecyclerView;
    private MyFriendsAdapter myFriendsAdapter;
    private RecyclerView.LayoutManager friendsManager;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userReference;

    private String idUFriend;

    private ArrayList<User> myFriendsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_my_friends);

        friendsRecyclerView = findViewById(R.id.list_of_my_friends_RecyclerView);
        friendsRecyclerView.setHasFixedSize(true);
        myFriendsAdapter = new MyFriendsAdapter(myFriendsList);
        friendsManager = new LinearLayoutManager(this);
        friendsRecyclerView.setLayoutManager(friendsManager);
        friendsRecyclerView.setAdapter(myFriendsAdapter);

        myFriendsAdapter.setFriendOnItemClickListener(new MyFriendsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position, View view) {
                User myFriends = myFriendsList.get(position);
                chatIntent();
            }
        });
    }

    @Override
    protected void onStart() {
        String id = mAuth.getCurrentUser().getUid();
        super.onStart();
        db.collection("users").document(id).collection("friends")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            DocumentSnapshot documentSnapshot = dc.getDocument();
                            String name = documentSnapshot.getString("name");

                            myFriendsList.add(0, new User(name));
                            myFriendsAdapter.notifyDataSetChanged();


                        }
                    }
                });
    }
    public void chatIntent() {
        startActivity(new Intent(this, Chat_Activity.class));
    }

}