package com.example.final5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class imgupdate extends AppCompatActivity {
    private StorageReference storageReference;
    private DatabaseReference userRef;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    Bitmap bitmap;

    ImageView profileimageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgupdate);
        profileimageview = findViewById(R.id.settings_profile_image);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        storageReference = FirebaseStorage.getInstance().getReference();
        profileimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, @androidx. annotation. Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if(requestCode == 1000) {
            if (resultCode == Activity. RESULT_OK) {
                Uri imageUri =data.getData();
                profileimageview.setImageURI(imageUri);

                uploadImageToFirbase(imageUri);
            }
        }
    }
    private void uploadImageToFirbase(Uri imageUri) {
        StorageReference fileRef =storageReference.child("profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(imgupdate.this,"Image uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(imgupdate.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


}