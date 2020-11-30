package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private String converName;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    final long ONE_MEGABYTE = 1024 * 1024;

    private Toolbar myToolbarFriendActivity;


    private ArrayList<User> myFriendsList = new ArrayList<>();
    private ArrayList<String> friendListId = new ArrayList<>();
    private ArrayList<Bitmap> imageListOfFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_my_friends);

        //FIREBASE INIT
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        friendsRecyclerView = findViewById(R.id.list_of_my_friends_RecyclerView);
        friendsRecyclerView.setHasFixedSize(true);
        myFriendsAdapter = new MyFriendsAdapter(myFriendsList, imageListOfFriends);
        friendsManager = new LinearLayoutManager(this);
        friendsRecyclerView.setLayoutManager(friendsManager);
        friendsRecyclerView.setAdapter(myFriendsAdapter);

        // toolbar
        myToolbarFriendActivity = findViewById(R.id.my_toolbar_friends);
        setSupportActionBar(myToolbarFriendActivity);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("My Friends");

        myFriendsAdapter.setFriendOnItemClickListener(new MyFriendsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position, View view) {

                User myFriends = myFriendsList.get(position);
                idUFriend = myFriends.getIdFirebase();

                chatIntent();
            }
        });
        //imageListOfFriends.clear();
        //friendListId.clear();
        getFriendList();


    }

    // SHOW LIST OF FRIENDS
    private void showFriendList() {
        Log.d("TAG", "showFriendList: " + friendListId.size());
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
                                        idUFriend = document.getId();

                                        StorageReference imagesRef =
                                                storageReference.child("images/" + idUFriend);
                                        imagesRef.getBytes(ONE_MEGABYTE)
                                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        Bitmap bitmap =
                                                                BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                        imageListOfFriends.add(bitmap);
                                                        Log.d("TAG", "onSuccess: " + imageListOfFriends.size());
                                                        myFriendsAdapter.notifyDataSetChanged();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Bitmap bitmap =
                                                        BitmapFactory.decodeResource(getResources(), R.drawable.defaultavatar);
                                                imageListOfFriends.add(bitmap);
                                                Log.d("TAG", "onFailure: error " + e.getLocalizedMessage());

                                            }

                                        });
                                        myFriendsAdapter.notifyDataSetChanged();
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

        Log.d("alona", "chatIntent: " + currentUserId);
        Log.d("alona", "chatIntent: " + idUFriend);

        DocumentReference docRef =
                db.collection("User conversation").document("id" + idUFriend + currentUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();


                    if (document.exists()) {

                        Log.d("alona", "onComplete: DOCUMENT FINNS");
                        converName = idUFriend + currentUserId;
                        converName.toString();

                        Intent intent = new Intent(ListOfMyFriends.this, Chat_messages.class);
                        intent.putExtra("chat_key", converName);
                        intent.putExtra("friend_photo", idUFriend);
                        startActivity(intent);


                    } else {
                        db.collection("User conversation").document("id" + currentUserId + idUFriend).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                converName = currentUserId + idUFriend;
                                                converName.toString();

                                                Intent intent =
                                                        new Intent(ListOfMyFriends.this, Chat_messages.class);
                                                intent.putExtra("chat_key", converName);
                                                intent.putExtra("friend_photo", idUFriend);
                                                startActivity(intent);




                                            } else {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("id", "2");
                                                db.collection("User conversation").document("id" + currentUserId + idUFriend).set(data);

                                                converName = currentUserId + idUFriend;
                                                converName.toString();

                                                Intent intent =
                                                        new Intent(ListOfMyFriends.this, Chat_messages.class);
                                                intent.putExtra("chat_key", converName);
                                                intent.putExtra("friend_photo", idUFriend);
                                                startActivity(intent);


                                            }
                                        }
                                    }
                                });
                    }

                }
            }
        });
    }

    public void addFriendBtn(View view) {
        startActivity(new Intent(this, AddFriends.class));
    }

    public void profileBtn(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }




}











