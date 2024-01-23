package com.example.final5;

import static android.app.ProgressDialog.show;

import static com.google.android.material.internal.ContextUtils.getActivity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class settings extends AppCompatActivity {
    private Button saveBtn;
    private EditText userNameET, userBioET;
    private ImageView profileImageView;

    private FirebaseStorage storage;

    private static int GalleyPick = 1;
    private Uri ImageUri;
     //  private StorageReference userProfileImagef;
    private String downloadUrl;
    private DatabaseReference userRef;

   // private Bitmap bitmap;

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;

    String imageurl1;
  //  FirebaseUser user;

    DatabaseReference dbreference;
    StorageReference storageReference;


    private ProgressDialog progressDialog;


    //private Button btnSelect, btnUpload;

    // view for image view
 //   private ImageView imageView;

    // Uri indicates, where the image will be picked from
   // private Uri filePath;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Uri imagePath;
    private String myUri = "";
    String updateurl;
    private Storage TaskuploadTask;
    private StorageReference storageProfilePicsRef;

    Button editprofile;

    // request code
    //   private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    String UserID;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
      //  editprofile = findViewById(R.id.edit_settings_btn);
         storage = FirebaseStorage.getInstance();
         storageReference = FirebaseStorage.getInstance().getReference();

         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         UserID=user.getUid();



        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbreference =FirebaseDatabase.getInstance().getReference().child("userprofile");
      //  storageReference=FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("image");

          storage = FirebaseStorage.getInstance();
         storageReference =storage.getReference();
      //   storageReference = FirebaseStorage.getInstance().getReference();

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();




        saveBtn = findViewById(R.id.save_settings_btn);
        userNameET = findViewById(R.id.username_settings);
        userBioET = findViewById(R.id.bio_settings);
        profileImageView = findViewById(R.id.settings_profile_image);
        progressDialog = new ProgressDialog(this);


        profileImageView.setOnClickListener(v -> {

            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 1);
        });
//        profileImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updatetofirebase();
//            }
//        });




        saveBtn.setOnClickListener(v -> {
           // saveUserData();
           updatetofirebase();
          //  uploadImage();
          //  retrievUserInfo();

           // saveInfOnlyWithoutImage();
        });


        retrievUserInfo();


    }
    public void updatetofirebase(){
        final  StorageReference uploader=storageReference.child("profileimage/"+"img"+System.currentTimeMillis());
        uploader.putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String updateuri =uri.toString();
                                final Map<String, Object> map= new HashMap<>();
                                map.put("uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                map.put("image",uri.toString());
                                map.put("name",userNameET.getText().toString());
                                map.put("bio",userBioET.getText().toString());
                                String fcm="fcm";
                                map.put("fcm",fcm.toString());

                                userRef.child (String.valueOf(UserID)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange (@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            userRef.child(String.valueOf(UserID)).updateChildren(map);
                                        } else {

                                          //  Toast.makeText(settings.this, "image is mandatory.", Toast.LENGTH_SHORT).show();
                                             userRef.child(String.valueOf(UserID)).setValue(map);


                                        }



                                    }
                                    @Override
                                    public void onCancelled (@NonNull DatabaseError error) {
                                    }
                                });
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Updated Succesfully",Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                });
    }
    private  void edittext(){
        final String getUserName = userNameET.getText().toString();
        final String getUserStatus = userBioET.getText().toString();

        final Map<String, Object> map= new HashMap<>();
        map.put("name",userNameET.getText().toString());
        map.put("bio",userBioET.getText().toString());

        userRef.child (String.valueOf(UserID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    userRef.child(String.valueOf(UserID)).updateChildren(map);

                } else {

                    Toast.makeText(settings.this, "image is mandatory.", Toast.LENGTH_SHORT).show();
                    // userRef.child(String.valueOf(UserID)).updateChildren(map1);


                }


//                                  else {
//
//
//                                      userRef.child(String.valueOf(UserID)).setValue(map);
//                                  }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error) {
            }
        });


    }



    private void uploadImage() {
        final String getUserName = userNameET.getText().toString();
        final String getUserStatus = userBioET.getText().toString();
      //  final StorageReference uploder =storageReference.child("pr")
        FirebaseStorage.getInstance().getReference("images/"+UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                   // task.getResult().getStorage().getDownloadUrl().addOnCompleteListener
                  task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @SuppressLint("RestrictedApi")
                      @Override
                      public void onSuccess(Uri uri) {
//                           imageurl1 = uri.toString();
//                          saveUserData();
//                          saveInfOnlyWithoutImage();
                          String updateuri =uri.toString();
                          final Map<String, Object> map= new HashMap<>();
                          map.put("image",uri.toString());
                          map.put("name",userNameET.getText().toString());
                          map.put("bio",userBioET.getText().toString());
                          String fcm ="fcm";
                          map.put("fcm", fcm.toString());

                          String link = "https://firebasestorage.googleapis.com/v0/b/final5-437aa.appspot.com/o/images%2F8fd7fcc6-c1d5-4b89-8ad0-d99378270c20?alt=media&token=cbd3740c-b10b-4ed4-b36d-217aa4f36dba";
                          final Map<String, Object> map1= new HashMap<>();
                          map1.put("image",link.toString());
                          map1.put("name",userNameET.getText().toString());
                          map1.put("bio",userBioET.getText().toString());



                          userRef.child (String.valueOf(UserID)).addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange (@NonNull DataSnapshot snapshot) {

                                      if (snapshot.exists()) {
                                          userRef.child(String.valueOf(UserID)).updateChildren(map);
                                      } else {

                                              Toast.makeText(settings.this, "image is mandatory.", Toast.LENGTH_SHORT).show();
                                             // userRef.child(String.valueOf(UserID)).updateChildren(map1);


                                      }


//                                  else {
//
//
//                                      userRef.child(String.valueOf(UserID)).setValue(map);
//                                  }
                              }
                              @Override
                              public void onCancelled (@NonNull DatabaseError error) {
                              }
                          });
                          progressDialog.dismiss();
                          Toast.makeText(getApplicationContext(), "Updated Succesfully",Toast.LENGTH_SHORT).show();

                      }
                  });
                }
            }
        });
    }

//    private void updateProfilePicture(String url) {
//        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/image").setValue(url);
//
//    }


//    public void choosePicture(){
//        Intent intent =new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 1);
//    }


     @Override
        protected void onActivityResult(int requestCode, int resultCode,  @Nullable Intent data) {
            super.onActivityResult (requestCode, resultCode, data);
            if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
                   imagePath = data.getData();
                   profileImageView.setImageURI(ImageUri);
                getImageInImageView();

            }
    }
//     protected void onActivityResult(int requestCode, int resultCode,  @Nullable Intent data) {
//         super.onActivityResult (requestCode, resultCode, data);
//         if (requestCode==1 && resultCode==RESULT_OK ){
//             imagePath=data.getData();
//             try {
//                 InputStream inputStream =getContentResolver().openInputStream(imagePath);
//                 Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                 profileImageView.setImageBitmap(bitmap);
//
//             } catch (FileNotFoundException e) {
//                 throw new RuntimeException(e);
//             }
//
//         }
//     }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profileImageView.setImageBitmap(bitmap);
    }
//    protected void onActivityResult(int requestCode, int resultCode, @androidx. annotation. Nullable Intent data) {
//        super.onActivityResult (requestCode, resultCode, data);
//        if(requestCode == 1000) {
//            if (resultCode == Activity. RESULT_OK) {
//                Uri imageUri =data.getData();
//                profileImageView.setImageURI(imageUri);
//
//                uploadImageToFirbase(imageUri);
//            }
//        }
//    }




//    private void saveUserData() {
//        final String getUserName = userNameET.getText().toString();
//        final String getUserStatus = userNameET.getText().toString();
//        if (ImageUri == null) {
//            userRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                    if (datasnapshot.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).hasChild("image")) {
//                        saveInfOnlyWithoutImage();
//                    } else {
//                      //  saveUserData();
//                    //    Toast.makeText(settings.this, "Please select Image first", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        } else if (getUserName.equals("")) {
//            Toast.makeText(this, "userName is mandatory.", Toast.LENGTH_SHORT).show();
//        } else if (getUserStatus.equals("")) {
//            Toast.makeText(this, "bio is mandatory.", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            progressDialog.setTitle("Account Settings");
//            progressDialog.setMessage("please wait...");
//            progressDialog.show();
//            final StorageReference filePath = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//            final UploadTask uploadTask = filePath.putFile(ImageUri);
//
//            uploadTask.continueWithTask(task -> {
//                if (!task.isSuccessful()) {
//                    throw Objects.requireNonNull(task.getException());
//                }
//                downloadUrl = filePath.getDownloadUrl().toString();
//                return filePath.getDownloadUrl();
//            }).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    downloadUrl = task.getResult().toString();
//                    HashMap<String, Object> profileMap = new HashMap<>();
//                    profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    profileMap.put("name", getUserName);
//                    profileMap.put("bio", getUserStatus);
//                    profileMap.put("image", imageurl1);
//
//                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            .updateChildren(profileMap).addOnCompleteListener(task1 -> {
//                                if(task1.isSuccessful()){
//                                    Intent intent = new Intent(settings.this, homepage.class);
//                                    finish();
//                                    progressDialog.dismiss();
//
//                                    Toast.makeText(settings.this,"Profile settings has been up", Toast.LENGTH_SHORT).show();
//                                }
//
//                            });
//
//                }
//            });
//        }
//
//    }

//    private void saveInfOnlyWithoutImage() {
//        final String getUserName = userNameET.getText().toString();
//        final String getUserStatus = userBioET.getText().toString();
//
//
//
//        if (getUserName.equals("")) {
//            Toast.makeText(this, "userName is mandatory.", Toast.LENGTH_SHORT).show();
//        } else if (getUserStatus.equals("")) {
//            Toast.makeText(this, "bio is mandatory.", Toast.LENGTH_SHORT).show();
//        } else {
//
//            progressDialog.setTitle("Account Settings");
//            progressDialog.setMessage("please wait...");
//            progressDialog.show();
//
//            HashMap<String, Object> profileMap = new HashMap<>();
//            profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//            profileMap.put("name", getUserName);
//            profileMap.put("bio", getUserStatus);
//         //   profileMap.put("image", downloadUrl);
//
//
//            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .updateChildren(profileMap).addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful()) {
//                           // Intent intent = new Intent(settings.this, homepage.class);
//                          //  finish();
//                            progressDialog.dismiss();
//
//                            Toast.makeText(settings.this, "Profile settings has been up", Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    });
//        }
//
//
//    }

//    protected void onStart() {
//        super.onStart();
//        // DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
//        //  String userId = "user_id";
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        UserID=user.getUid();
//        userRef.child(UserID)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                               String imageUrl = dataSnapshot.child("image").getValue().toString();
//                            String nameDb = dataSnapshot.child("name").getValue().toString();
//                            String bioDb = dataSnapshot.child("bio").getValue().toString();
//                            userNameET.setText(nameDb);
//                            userBioET.setText(bioDb);
//
//
//                              Picasso.get ().load (imageUrl) .placeholder (R. drawable.profile_image).into(profileImageView);
//                            // Glide.with(settings.this).load(imageUrl).into(profileImageView);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
    public void retrievUserInfo() {
      //  super.onStart();
        // DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        //  String userId = "user_id";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserID=user.getUid();
        userRef.child(UserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String imageUrl = dataSnapshot.child("image").getValue().toString();
                            String nameDb = dataSnapshot.child("name").getValue().toString();
                            String bioDb = dataSnapshot.child("bio").getValue().toString();
                            userNameET.setText(nameDb);
                            userBioET.setText(bioDb);
                            updateurl =imageUrl;


                            Picasso.get ().load (imageUrl) .placeholder (R. drawable.profile_image).into(profileImageView);
                            // Glide.with(settings.this).load(imageUrl).into(profileImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }









}
