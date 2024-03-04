package com.example.final5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;


import java.net.URL;
import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {
    private TextView nameContact;
    private ImageView profileImage;
    private ImageView cancelCallBtn, makeCallBtn;
    private String receiverUserId="", receiverUserImage="", receiverUserName="";
    private String senderUserId="", senderUserImage="", senderUserName="", checker="";
    private  String callingID="", ringingID="";

    public static  final  String REMOTE_MEETING_ROOM ="meetingRoom";

  
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        receiverUserId =getIntent ().getExtras ().get ("visit_user_id") .toString();
        senderUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef = FirebaseDatabase.getInstance ().getReference ().child ("Users");

        nameContact = findViewById(R.id.name_calling);
        profileImage =findViewById(R.id.profile_image_calling);
        cancelCallBtn =findViewById(R.id.cancel_call);
        makeCallBtn = findViewById(R.id.make_call);
        
        getAndSetReciverProfileInfo();

        cancelCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                cancelCallingUser();

            }
        });
        makeCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final HashMap<String, Object> callingPickUpMap = new HashMap<>();
             callingPickUpMap.put("picked", "picked");
             usersRef.child(senderUserId).child("Ringing")
                     .updateChildren(callingPickUpMap)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) { try {
                          //   URL serverURL = new URL("https://meet.jit.si");
                             URL serverURL = new URL("https://meet.guifi.net");
                             JitsiMeetConferenceOptions conferenceOptions =
                                     new JitsiMeetConferenceOptions.Builder()
                                             .setServerURL(serverURL)
                                             .setRoom(senderUserId)
                                             .build();
                             JitsiMeetActivity.launch(CallingActivity.this,conferenceOptions);
                             finish();

                         }
                         catch (Exception exception){
                             Toast.makeText(CallingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                             finish();
                         }

                         }
                     });

            }
        });
    }



    private void getAndSetReciverProfileInfo() {
        usersRef.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(receiverUserId).exists()) {
                 //   receiverUserImage = dataSnapshot.child(receiverUserId).child("image").getValue().toString();
                 //   receiverUserName = dataSnapshot.child(receiverUserId).child("name").getValue().toString();

                  //  nameContact.setText(receiverUserName);
                 //   Picasso.get().load(receiverUserImage).placeholder(R.drawable.profile_image).into(profileImage);
                }
                if (dataSnapshot.child(senderUserId).exists()) {
                     senderUserImage=dataSnapshot.child(senderUserId).child("image").getValue().toString();
                     senderUserName=dataSnapshot.child(senderUserId).child("name").getValue().toString();
                }
            }
            @Override
            public void onCancelled (DatabaseError databaseError) {
            }
        });
            }
    protected  void  onStart() {

        super.onStart();
        usersRef.child(receiverUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!checker.equals("clicked") && !snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")){

                            final HashMap<String, Object> callingInfo = new HashMap();

                            callingInfo.put ("calling", receiverUserId);
                            usersRef.child (senderUserId)
                                    .child ("Calling")
                                    .updateChildren (callingInfo)
                                    . addOnCompleteListener(task -> {
                                        if (task.isSuccessful () )
                                        {
                                            final HashMap <String, Object> ringingInfo = new HashMap<>();
                                            ringingInfo.put ("ringing", senderUserId);

                                            usersRef.child(receiverUserId)
                                                    .child("Ringing")
                                                    .updateChildren(ringingInfo);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(senderUserId).hasChild("Ringing") && !snapshot.child(senderUserId).hasChild("Calling")){
                    makeCallBtn.setVisibility(View.VISIBLE);
                }
                if(snapshot.child(receiverUserId).child("Ringing").hasChild("picked")){
                    try {
                      //  URL serverURL = new URL("https://meet.jit.si");
                        URL serverURL = new URL("https://meet.guifi.net");
                        JitsiMeetConferenceOptions conferenceOptions =
                                new JitsiMeetConferenceOptions.Builder()
                                        .setServerURL(serverURL)
                                        .setFeatureFlag("welcomepage.enabled", false)
                                        .setRoom(receiverUserId)
                                        .build();
                        JitsiMeetActivity.launch(CallingActivity.this,conferenceOptions);
                        finish();

                    }
                    catch (Exception exception){
                        Toast.makeText(CallingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void cancelCallingUser() {
        usersRef.child(senderUserId)
                .child("Calling")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("calling")){
                            callingID = snapshot.child("calling").getValue().toString();

                            usersRef.child(callingID)
                                    .child("Ringing")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               usersRef.child(senderUserId)
                                                       .child("Calling")
                                                       .removeValue()
                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(new Intent(CallingActivity.this,settings.class));
                                                                finish();
                                                           }
                                                       });
                                           }
                                        }
                                    });
                        }
                        else {
                            startActivity(new Intent(CallingActivity.this, homepage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // from reciver side
        usersRef.child(senderUserId)
                .child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("ringing")){
                            ringingID = snapshot.child("ringing").getValue().toString();

                            usersRef.child(ringingID)
                                    .child("Calling")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                usersRef.child(senderUserId)
                                                        .child("Ringing")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(new Intent(CallingActivity.this,settings.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else {
                            startActivity(new Intent(CallingActivity.this,homepage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    }
