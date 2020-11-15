package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFriends extends AppCompatActivity {

    private EditText searchEditText;

    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private RecyclerView.LayoutManager searchManager;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String idFriend;
    private String idFirebase;
    private User user;


    private final CollectionReference usersCollectionRef = db.collection("users");
    private DocumentReference documentReference;
    private CollectionReference friendsCollectionRef;
    private Query query;


    private ArrayList<User> searchUser = new ArrayList<>();
    private ArrayList<String> idNewFriend = new ArrayList<>();

    private ArrayList<String> friendListIds = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);


        idFirebase = mAuth.getCurrentUser().getUid();

        friendsCollectionRef = db.collection("users").document(idFirebase)
                .collection("friends");


        searchRecyclerView = findViewById(R.id.search_RecyclerView);
        searchEditText = findViewById(R.id.search_edit_text);

        searchRecyclerView = findViewById(R.id.search_RecyclerView);
        searchRecyclerView.setHasFixedSize(true);
        searchAdapter = new SearchAdapter(searchUser);
        searchManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(searchManager);
        searchRecyclerView.setAdapter(searchAdapter);

        searchAdapter.setSearchOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position, View view) {
                User newFriend = searchUser.get(position);
                profileIntent();
            }

            // ADD FRIENDS ID TO CURRENT USER FRIENDS COLLECTIONS
            @Override
            public void OnAddIconClicked(int position, View view) {

                final String friendUserId = searchUser.get(position).getIdFirebase();
                Map<String, Object> newFriend = new HashMap<>();
                newFriend.put("uID", friendUserId);

                final Map<String, Object> currentUser = new HashMap<>();
                newFriend.put("uID", idFirebase);

                db.collection("users")
                        .document(idFirebase)
                        .collection("friends")
                        .document(friendUserId)
                        .set(newFriend)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddFriends.this, "new friend added.", Toast.LENGTH_SHORT).show();
                                db.collection("users")
                                        .document(friendUserId)
                                        .collection("friends")
                                        .document(idFirebase)
                                        .set(currentUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddFriends.this, "funkar!", Toast.LENGTH_SHORT).show();    
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddFriends.this, "something went wrong: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                /*
                User newFriend = searchUser.get(position);
                //view.setBackgroundResource(R.drawable.check_icon);
                if (idFirebase.equals(idFriend)) {
                    Toast.makeText(AddFriends.this, "cant be friend", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                            if (idFriend.equals(document.getId())) {
                                                Log.d("tag", document.getId() + " = " + document.getData());
                                                //idArray.add(document.getId());
                                                friendsCollectionRef.document(document.getId()).set(document.getData())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(AddFriends.this, "added", Toast.LENGTH_SHORT).show();
                                                                friendIntent();
                                                            }
                                                        });
                                            }

                                        }
                                    } else {
                                        Log.w("tag", "Error getting documents.", task.getException());
                                    }
                                }
                            });


                }*/
            }
        });
    }


    // SEARCH FOR FRIENDS
    public void searchPressed(View view) {
        searchUser.clear();

        Toast.makeText(this, "search button pressed.", Toast.LENGTH_SHORT).show();
        String searchPhoneNumber = searchEditText.getText().toString().trim();

        db.collection("users")
                .whereEqualTo("phoneNumber", searchPhoneNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                user = document.toObject(User.class);
                                searchUser.add(user);
                            }
                            searchAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddFriends.this, "something went wrong: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        
        /*
        searchUser.clear();
        String searchText = searchEditText.getText().toString().trim();

        if (searchText.isEmpty()) {
            searchUser.clear();
        } else {
            db.collection("users")
                    .whereEqualTo("phoneNumber", searchText)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            idFriend = document.getId();
                            user = document.toObject(User.class);
                            idNewFriend.add(idFriend);
                            searchUser.add(user);
                            //documentReference = db.collection("users").document(idFriend);
                            searchAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(AddFriends.this, task.getException().getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        searchAdapter.notifyDataSetChanged(); */
    }


    public void chatIntent() {
        startActivity(new Intent(this, Chat_Activity.class));
    }

    public void friendIntent() {
        startActivity(new Intent(this, ListOfMyFriends.class));
    }

    public void profileIntent() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void friendsConnect() {
        for (User user : searchUser) {
            if (user.getIdFirebase().equals(idNewFriend)) {

            }

        }
    }

}
