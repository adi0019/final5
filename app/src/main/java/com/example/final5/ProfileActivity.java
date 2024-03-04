package com.example.final5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserID = "", receiverUserName = "", receiverUserImage = "";
    String sendername;
    private String myUsername, myPrfileImageUrl;

    String imageUrl, nameDb, bioDb, uid;
    String userid, name,image,bio;
    String UserID;
    private DatabaseReference userRef1;


    private String visit_user_id = "", profile_name = "";
    private ImageView background_profile_view;
    private TextView name_profile;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String senderUserId, senderImage;
    private String currentState = "new";
    private DatabaseReference friendRequestRef, requestRef, friends, mUserRef;

    Button btn_perform, btnDecline;

    String CurrentState = "nothing_happen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        //   receiverUserID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("visit_user_id")).toString ();
        //  receiverUserID = getIntent().getStringExtra("visit_user_id");
        //  receiverUserID="6BPMWQdYitMmWmSvxnUgyGo2KKn2";

        userRef1 = FirebaseDatabase.getInstance().getReference().child("Users");
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        // receiverUserName = getIntent().getExtras ().get ("profile_name") .toString();
        receiverUserName = getIntent().getStringExtra("profile_name");
       name = getIntent().getStringExtra("name");
       image = getIntent().getStringExtra("image");
       bio = getIntent().getStringExtra("bio");
       userid = getIntent().getStringExtra("Userid");

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
       // senderImage= mAuth.getCurrentUser().getim()
        sendername = mAuth.getCurrentUser().getDisplayName();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserID);
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        // friends = FirebaseDatabase.getInstance().getReference().child("Friends");

        background_profile_view = findViewById(R.id.background_profile_view);
        name_profile = findViewById(R.id.name_profile);
        btn_perform = findViewById(R.id.add_friend);
        btnDecline = findViewById(R.id.decline_friend);
        loadMyProfile();
        retrievUserInfo();

        Picasso.get().load(receiverUserImage).into(background_profile_view);
        name_profile.setText(receiverUserName);


        btn_perform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAction(receiverUserID);
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unfriend(receiverUserID);
            }
        });

        CheckUserExistance(receiverUserID);
    }



  public void  loadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot){
                if (snapshot.exists()) {
                    myPrfileImageUrl = snapshot.child("image").getValue().toString();
                    myUsername= snapshot.child("name").getValue().toString();


                } else {
                    Toast.makeText(ProfileActivity.this,
                    "Data not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Toast.makeText(ProfileActivity.this, "not recived" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void Unfriend(String receiverUserID) {
        if(currentState.equals("friend")){
            friendRequestRef.child(mUser.getUid()).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        friendRequestRef.child(receiverUserID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this, "You are Unfrined", Toast.LENGTH_SHORT).show();
                                    currentState="nothing_happen";
                                    btn_perform.setText("Send Friend Request");
                                    btnDecline.setVisibility(View.GONE);
                                }

                            }
                        });

                    }
                }
            });
        }
        if(CurrentState.equals("he_send_pending")){
            HashMap hashMap=new HashMap();
            hashMap.put("status","decline");
            requestRef.child (receiverUserID).child(mUser.getUid()).updateChildren (hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete (@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "You have Decline Friend", Toast.LENGTH_SHORT).show();
                        CurrentState = "he_sent_decline";
                        btn_perform.setVisibility(View.GONE);
                        btnDecline.setVisibility(View.GONE);

                    }
                }
                    });

        }
    }



    private void CheckUserExistance(String receiverUserID) {
        friendRequestRef.child(mUser.getUid()).child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CurrentState = "friend";
                    btn_perform.setText("Send SMS");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        friendRequestRef.child(receiverUserID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CurrentState = "friend";
                    btn_perform.setText("Send SMS");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        CurrentState = "I_send_pending";
                        btn_perform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    if (snapshot.child("status").getValue().toString().equals("decline")) {
                        CurrentState = "I_sent_decline";
                        btn_perform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


        });
        requestRef.child(receiverUserID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists()){
                  if(snapshot.child("status").getValue().toString().equals("pending")){
                      CurrentState="he_send_pending";
                      btn_perform.setText("Accept Friend Request");
                      btnDecline.setText("Decline Friend");
                      btnDecline.setVisibility (View.VISIBLE);
                  }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(CurrentState.equals("nothing_happen")){
            currentState="nothing_happen";
            btn_perform.setText("Send Friend Request");
            btnDecline.setVisibility(View.GONE);
        }


    }

    private void PerformAction(String receiverUserID) {
        if(CurrentState.equals("nothing_happen")){
            HashMap hashMap = new HashMap();
            hashMap.put("status", "pending");
            requestRef.child(mUser.getUid()).child(receiverUserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, "You have sent Friend Request", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        CurrentState ="I_send_pending";
                        btn_perform.setText("Cancal Friend request");
                    }
                    else {
                        Toast.makeText(ProfileActivity.this,""+task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        if(CurrentState.equals("I_send_pending")|| CurrentState.equals("I_sent_decline"))
        {
            requestRef.child(mUser.getUid()).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, "You have Cancelled Friend Request", Toast.LENGTH_SHORT).show();
                        CurrentState ="nothing_happen";
                        btn_perform.setText("Send Friend Request");
                        btnDecline.setVisibility(View.GONE);


                    }
                    else {
                        Toast.makeText(ProfileActivity.this,""+task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
        if(CurrentState.equals("he_send_pending")){
            requestRef.child(mUser.getUid()).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()){
                      HashMap hashMap=new HashMap();
                      hashMap.put("status","friend");
                      hashMap.put("name", receiverUserName);
                      hashMap.put("uid", receiverUserID);
                      hashMap.put("bio",bio);
                      hashMap.put("image", receiverUserImage);

                      HashMap hashMap1=new HashMap();
                      hashMap1.put("status","friend1");
                      hashMap1.put("name", nameDb);
                      hashMap1.put("uid", senderUserId);
                      hashMap1.put("image",imageUrl);
                      hashMap1.put("bio",bioDb);
                    //  hashMap1.put("profileImageUrl",myPrfileImageUrl);

                      friendRequestRef.child(mUser.getUid ()).child (receiverUserID).updateChildren (hashMap).addOnCompleteListener(new OnCompleteListener() {
                          @Override
                          public void onComplete(@NonNull Task task) {
                              if(task.isSuccessful()){
                                  friendRequestRef.child(receiverUserID).child(mUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                      @Override
                                      public void onComplete(@NonNull Task task) {
                                          Toast.makeText(ProfileActivity.this, "You added Friend",Toast.LENGTH_SHORT).show();
                                          currentState="friend";
                                          btn_perform.setText("send sms");
                                          btnDecline.setText("Unfriend");
                                          btnDecline.setVisibility(View.VISIBLE);
                                      }
                                  });
                              }
                          }
                      });
                  }
                }
            });

        }
        if(currentState.equals("friend")){

        }


    }
    public void retrievUserInfo() {
        //  super.onStart();
        // DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        //  String userId = "user_id";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserID=user.getUid();
        userRef1.child(UserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                             imageUrl = dataSnapshot.child("image").getValue().toString();
                             nameDb = dataSnapshot.child("name").getValue().toString();
                             bioDb = dataSnapshot.child("bio").getValue().toString();
                             uid = dataSnapshot.child("uid").getValue().toString();
                           // userNameET.setText(nameDb);
                           // userBioET.setText(bioDb);
                           // updateurl =imageUrl;


                          //  Picasso.get ().load (imageUrl) .placeholder (R. drawable.profile_image).into(profileImageView);
                            // Glide.with(settings.this).load(imageUrl).into(profileImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}