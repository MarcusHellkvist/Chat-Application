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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListOfMyFriends extends AppCompatActivity {

    private RecyclerView friendsRecyclerView;
    private MyFriendsAdapter myFriendsAdapter;
    private RecyclerView.LayoutManager friendsManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userReference;

    private String idUFriend;
    private String currentUserId;
    private User user;

    private ArrayList<User> myFriendsList = new ArrayList<>();

    private ArrayList<String> friendListId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_my_friends);

        //FIREBASE INIT
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

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

        getFriendList();

    }

    // SHOW LIST OF FRIENDS
    private void showFriendList() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                for (int i = 0; i < friendListId.size(); i++) {
                                    if (friendListId.get(i).contentEquals(documentId)) {
                                        // LÄGG TILL I FRIENDS ADAPTER
                                        user = document.toObject(User.class);
                                        myFriendsList.add(user);
                                    }
                                }
                            }
                            myFriendsAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListOfMyFriends.this, "something went wrong: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // GET LIST OF FRIENDS
    private void getFriendList() {
        db.collection("users")
                .document(currentUserId)
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String friendId = document.getId();
                                friendListId.add(friendId);
                            }
                            showFriendList();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListOfMyFriends.this, "something went wrong: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void chatIntent() {
        startActivity(new Intent(this, Chat_Activity.class));
    }

}