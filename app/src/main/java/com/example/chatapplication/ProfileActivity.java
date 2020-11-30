package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileDialog.ProfileDialogListener {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference usersRef;
    private StorageReference storageReference;

    private ImageView ivProfilePicture, ivPenIcon;
    private TextView tvUsername, tvEmail, tvPhone, tvAmountFriends;
    private Button btnEditProfile;

    private String currentUserId;
    private Uri imageUri;
    private int friendAmount;
    private String currentPhotoPath;

    private Toolbar myToolbarProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        currentUserId = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // IMAGE VIEWS
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        ivPenIcon = findViewById(R.id.iv_pen_icon);
        ivProfilePicture.setOnClickListener(changePictureListener);
        
        // TEXT VIEWS
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAmountFriends = findViewById(R.id.tv_amount_friends);
        
        //BUTTON VIEWS
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(editListener);

        //TOOLBAR
        myToolbarProfile = findViewById(R.id.my_toolbar_friends);
        setSupportActionBar(myToolbarProfile);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        if (uid != null){
            currentUserId = uid;
            btnEditProfile.setVisibility(View.INVISIBLE);
            ivProfilePicture.setClickable(false);
            ivPenIcon.setVisibility(View.INVISIBLE);
        }


        getProfilePicture();

        // TODO Animations!

    }

    @Override
    protected void onStart() {
        super.onStart();
        // REAL-TIME FOR USER INFO
        usersRef.document(currentUserId)
                .addSnapshotListener(ProfileActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null){
                            if (value.exists()){
                                String name = value.getString("name");
                                String phone = value.getString("phoneNumber");
                                String email = value.getString("email");
                                tvUsername.setText(name);
                                tvPhone.setText(phone);
                                tvEmail.setText(email);
                            }
                        }
                    }
                });
        //REAL-TIME FOR AMOUNT OF FRIENDS
        usersRef.document(currentUserId)
                .collection("friends")
                .addSnapshotListener(ProfileActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<String> friends = new ArrayList<>();
                        if (value != null) {
                            for (QueryDocumentSnapshot document : value) {
                                friends.add(document.getId());
                            }
                            friendAmount = friends.size();
                            tvAmountFriends.setText(friendAmount + "");
                        }
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            ivProfilePicture.setImageURI(imageUri);
            uploadImage(imageUri);
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            Uri file = Uri.fromFile(new File(currentPhotoPath));
            ivProfilePicture.setImageURI(file);
            uploadImage(file);
        }
    }

    private void uploadImage(Uri file) {
        StorageReference imagesRef = storageReference.child("images/" + currentUserId);
        imagesRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfileActivity.this, "picture uploaded to database successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Something went wrong: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getProfilePicture(){

        StorageReference imagesRef = storageReference.child("images/" + currentUserId);
        final long ONE_MEGABYTE = 1024 * 1024;

        imagesRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivProfilePicture.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ivProfilePicture.setImageResource(R.drawable.defaultavatar);
                    }
                });

    }

    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void startCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){

            }

            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.chatapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }

    }

    private void openDialog(){
        ProfileDialog profileDialog = new ProfileDialog();
        profileDialog.show(getSupportFragmentManager(), "profile dialog");
    }

    private void selectImage (Context context){
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Photo")){
                    startCamera();
                } else if (options[which].equals("Choose from Gallery")){
                    choosePicture();
                } else if (options[which].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp +  "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener editListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDialog();
        }
    };

    private View.OnClickListener changePictureListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectImage(ProfileActivity.this);
        }
    };

    @Override
    public void updateUserInfo(final String name, final String phone) {

        FirebaseUser fUser = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        // update auth
        fUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // update firestore
                            usersRef.document(currentUserId)
                                    .update("name", name, "phoneNumber", phone)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileActivity.this, "profile updated.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
}