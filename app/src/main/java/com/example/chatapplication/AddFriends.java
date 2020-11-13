package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddFriends extends AppCompatActivity {

    private EditText searchEditText;

    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private RecyclerView.LayoutManager searchManager;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userReference;

    private String idUFriend;

    private ArrayList<User> searchUser = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

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
            }
        });

    }

    public void searchPressed(View view) {
        searchUser.clear();
        String searchText = searchEditText.getText().toString().toLowerCase().trim();
        if (searchText.isEmpty()) {
            searchUser.clear();
        } else {
            db.collection("users")
                    .whereEqualTo("name", searchText)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String friendName = document.getString("name");
                            String friendEmail = document.getString("email");
                            idUFriend = document.getId();
                            //if (idUFriend == null) return;
                            checkIfAlreadyFriend();
                            searchUser.add(new User(friendName, friendEmail, idUFriend));
                            searchAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(AddFriends.this, task.getException().getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        searchAdapter.notifyDataSetChanged();
    }

    public void checkIfAlreadyFriend() {
        String idFirebase = mAuth.getCurrentUser().getUid();
        db.collection("users").document(idFirebase).collection("friends")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String idFriend = document.getString("idFirebase");
                    if (idFriend.equals(idFriend)) return;
                }
            }
        });

    }
}
