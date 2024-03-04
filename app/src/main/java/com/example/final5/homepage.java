package com.example.final5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class homepage  extends AppCompatActivity  {

    FirebaseRecyclerOptions<Friends > options;
   FirebaseRecyclerAdapter<Friends, FriendMyViewHlder>adapter;

    BottomNavigationView navView;
    private DatabaseReference usersRef;
    String listUserId, calledBy="";

    private  String str="";
    RecyclerView myContactsList;
    ImageView findPeopleBtn;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    DatabaseReference mRef, mUserRef, UsersRef;
    FirebaseUser mUser;
    public String visituserid;

    Button videocall;

    String UserID;
    String receiverUserID;
  private   String currentUserID;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        videocall=findViewById(R.id.accept_btn);
      //  mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserID);
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
       // senderUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
     //   UserID=user.getUid();
        currentUserID =mAuth.getCurrentUser().getUid();


        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        findPeopleBtn = findViewById (R.id. find_people_btn);

      //  getSupportActionBar ().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.contact_list);
        recyclerView.setLayoutManager (new LinearLayoutManager(this));

     //   edittext();



        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findpeopeactivity = new Intent(homepage.this, FindPeopleActivity.class);
                startActivity(findpeopeactivity);
            }
        });
           LoadFriends("");



    }
    protected void  onStart(){
        super.onStart();
        checkForReceivingCall();
    }

    private void checkForReceivingCall() {
        usersRef.child (currentUserID)
                .child ("Ringing")
                .addValueEventListener (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("ringing"))
                        {
                         //   calledBy = dataSnapshot.child("ringing").getValue().toString();
                            Intent callingIntent = new Intent(homepage.this, CallingActivity.class);
                            callingIntent.putExtra("visit_user_id",calledBy);
                            startActivity(callingIntent);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
    }

    private void LoadFriends(String s) {
        Query query =mRef.child(mUser.getUid()).orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        options =new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query,Friends.class).build();
        adapter=new FirebaseRecyclerAdapter<Friends, FriendMyViewHlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendMyViewHlder holder, int position, @NonNull Friends model) {
                // listUserId= getRef(position).getKey();
                Picasso.get().load(model.getImage()).into(holder.profile);
                holder.bio.setText(model.getStatus());
                holder.name.setText(model.getName());
                visituserid=model.getUid();
                holder.videocallbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callingIntent = new Intent(homepage.this, CallingActivity.class);
                        callingIntent.putExtra("visit_user_id",visituserid);
                        startActivity(callingIntent);
                    }
                });

            }

            @NonNull
            @Override
            public FriendMyViewHlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list, parent, false);

                return new FriendMyViewHlder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                //   firebaseAuth.signOut();
                mAuth.signOut();
                // finish();
                startActivity(new Intent(homepage.this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
    private  BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            (BottomNavigationView.OnNavigationItemSelectedListener) item -> {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent mainIntent = new Intent(homepage.this, homepage.class);
                        startActivity(mainIntent);
                        break;

                    case R.id.navigation_dashboard:
                        Intent settingsIntent = new Intent(homepage.this, settings.class);
                        startActivity(settingsIntent);
                        break;

                    case R.id.navigation_notifications:
                        Intent notificationIntent = new Intent(homepage.this, notification.class);
                        startActivity(notificationIntent);
                        break;
                }
                return true;

            };
    private  void edittext(){
      //  final String getUserName = userNameET.getText().toString();
      //  final String getUserStatus = userBioET.getText().toString();

        final Map<String, Object> map= new HashMap<>();
//        map.put("name",userNameET.getText().toString());
//        map.put("bio",userBioET.getText().toString());
        map.put("FCM","FCM");

        mRef.child (String.valueOf(UserID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    mRef.child(String.valueOf(UserID)).updateChildren(map);

                } else {

                    Toast.makeText(homepage.this, "image is mandatory.", Toast.LENGTH_SHORT).show();
                    // userRef.child(String.valueOf(UserID)).updateChildren(map1);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

