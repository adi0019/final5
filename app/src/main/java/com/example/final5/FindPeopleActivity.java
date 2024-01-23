package com.example.final5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendList;
    private EditText searchET;
    private  String str="";
    private DatabaseReference usersRef;
    ArrayList<Contacts> list;
    MyAdapter myAdapter;
    CardView cardView;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);
//        cardView = findViewById(R.id.card_view1);
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FindPeopleActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });


        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchET= findViewById (R.id.search_user_text);
        findFriendList = findViewById (R.id.find_friends_list);
        findFriendList.setLayoutManager (new LinearLayoutManager(getApplicationContext ()));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this,list);
        findFriendList.setAdapter(myAdapter);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                  Contacts contacts = dataSnapshot.getValue(Contacts.class);
                  list.add(contacts);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if(searchET.getText().toString().equals("")){
                        Toast.makeText(FindPeopleActivity.this, "please write name to search", Toast.LENGTH_SHORT).show();

                    }
                    else{
                     str = charSequence.toString();
                        onStart();
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>  options = null;
        if (str.equals("")) {
            options =
                    new FirebaseRecyclerOptions.Builder<Contacts>()
                            .setQuery(usersRef, Contacts.class)
                            .build();
        }
        else {
            options =
                    new FirebaseRecyclerOptions.Builder<Contacts>()
                            .setQuery(usersRef
                                            .orderByChild("name").startAt(str).endAt(str + "\uf8ff")
                                    , Contacts.class)
                            .build();
        }
    }

//        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> firebaseRecyclerAdapter
//                = new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Contacts model) {
//                    //   holder.bindData(model);
//                       holder.userNameTxt.setText(model.getName());
            //    Picasso.get().load(model.getImage()).into(holder.profileImageView);
//
//                holder.itemView.setOnClickListener(v -> {
//                    String visit_user_id = getRef(position).getKey ();
//                    Intent intent = new Intent(FindPeopleActivity.this, ProfileActivity.class);
//                    intent.putExtra (  "visit_user_id", visit_user_id) ;
//                 //   intent.putExtra ( "profile_image", model.getImage ());
//                    intent.putExtra ( "profile_name", model.getName ());
//                    startActivity(intent);
//                });
//            }
//
//            @NonNull
//            @Override
//            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
//                View view = LayoutInflater.from(p.getContext()).inflate(R.layout.contact_design, p,false);
//                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
//                return viewHolder;
//            //    return  new FindFriendsViewHolder(view);
//
//            }
////            public int getItemCount() {
////               return getSnapshots().size();
////          }
//
//        };
//         findFriendList.setAdapter(firebaseRecyclerAdapter);
//         firebaseRecyclerAdapter.startListening();
//
//
//
//
//    }
//
//
//
//    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
//        TextView userNameTxt;
//        Button videoCallBtn, cancelBtn;
//        ImageView profileImageView;
//
//        RelativeLayout cardView;
//
//        public FindFriendsViewHolder(@NonNull View itemview) {
//            super(itemview);
//
//            userNameTxt = itemview.findViewById(R.id.name_notification);
//            videoCallBtn = itemview.findViewById(R.id.call_btn);
//            cancelBtn = itemview.findViewById(R.id.request_decline_btn);
//            profileImageView = itemview.findViewById(R.id.image_notification);
//            cardView = itemview.findViewById(R.id.card_view);
//
//            videoCallBtn.setVisibility(View.GONE);
//        }
////        public void bindData(Contacts contacts) {
////            // Bind data to views
////          //  userNameTxt.setText(contacts.getName());
////          //  textViewEmail.setText(Contacts.getUserEmail());
////        }
//
//    }

}