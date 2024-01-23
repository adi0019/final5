package com.example.final5;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class notification extends AppCompatActivity {

    private  RecyclerView Notifications_list;
    private DatabaseReference friendRequestRef, contactsRef, userRef;

    private RecyclerView notifications_list;
    private FirebaseAuth mAuth;
    private  String currentUserId;

    Button accept_btn, decline_btn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notifications_list = findViewById(R.id.notification_list);
        notifications_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        currentUserId =mAuth.getCurrentUser().getUid();
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactsRef =FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef =FirebaseDatabase.getInstance().getReference().child("Users");

    }
    protected  void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(friendRequestRef.child(currentUserId), Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, NotificationViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Contacts, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull Contacts model) {
                holder.accept_btn.setVisibility (View.VISIBLE);
                holder.decline_btn.setVisibility (View.VISIBLE) ;
                final String listUserId =getRef(position).getKey();
                DatabaseReference requestTypeRef =getRef(position).child ("request_type").getRef ();
                requestTypeRef. addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String type= snapshot.getValue().toString();
                            if(type.equals("received")){
                             holder.cardView.setVisibility(View.VISIBLE);

                             userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild("image")){
                                        final  String imageStr= snapshot.child("imgae").getValue().toString();
                                        final  String nameStr= snapshot.child("imgae").getValue().toString();

                                        Picasso.get().load(imageStr).into(holder.profileImageView);
                                        holder.userNameTxt.setText(nameStr);
                                    }

                                        final  String nameStr = snapshot.child("name").getValue().toString();
                                        holder.userNameTxt.setText(nameStr);

                                    holder.accept_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {

                                 }
                             });
                            }
                            else{
                               holder.cardView.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_design, parent, false);
                notification.NotificationViewHolder viewHolder = new notification.NotificationViewHolder(view);
                return viewHolder;
            }
        };
        notifications_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
    @SuppressLint("ViewConstructor")
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt;
        Button videoCallBtn;
        ImageView profileImageView;
        Button accept_btn, decline_btn;

        RelativeLayout cardView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        //   public NotificationViewHolder(@NonNull View itemview) {
         //   super(itemview.getContext());
//
//            userNameTxt = itemview.findViewById(R.id.name_notification);
//            videoCallBtn = itemview.findViewById(R.id.call_btn);
//            profileImageView = itemview.findViewById(R.id.image_contact);
//            cardView = itemview.findViewById(R.id.card_view1);
//            accept_btn= itemview.findViewById(R.id.accept_btn);
//            decline_btn = itemview.findViewById(R.id.decline_btn);
//        }
    }
}
