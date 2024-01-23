package com.example.final5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Contacts> list;

    String visit_user_id;
    private FirebaseAuth mAuth;

    Context mycontext;
    private FirebaseUser mUser;




    public MyAdapter(Context context, ArrayList<Contacts> list) {
        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
        mUser =mAuth.getCurrentUser();
    }


    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.find_friend_design, parent,false);
        return new MyViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position ) {
         Contacts contacts =list.get(position);

//        Picasso.get().load(contacts.getImage()).into(holder.contactsimageview);
        holder.firstName.setText(contacts.getName());
     //   holder.firstName.setText(contacts.getUid());
//        holder.bio.setText(contacts.getbio());
//       visit_user_id = contacts.getUid();
      //   visit_user_id = contacts.getUid(position);
      //   visit_user_id = contacts.getUid(position);
        if(!mUser.getUid().equals(contacts.getUid())){
            Picasso.get().load(contacts.getImage()).into(holder.contactsimageview);
            holder.firstName.setText(contacts.getName());
            //   holder.firstName.setText(contacts.getUid());
            holder.bio.setText(contacts.getbio());
            visit_user_id = contacts.getUid();
        }


        else {
            String name = contacts.getName();
            String bio = contacts.getbio();
            String image = contacts.getImage();
            String Userid = contacts.getUid();
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra (  "image",image ) ;
            intent.putExtra ( "Userid", Userid);
            intent.putExtra ( "bio", bio);
            intent.putExtra ( "name", name);
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                     intent.putExtra (  "visit_user_id", visit_user_id) ;
                    intent.putExtra ( "profile_image", contacts.getImage ());
                   intent.putExtra ( "profile_name", contacts.getName ());
                   context.startActivity(intent);
            }
        });







    }




    @Override
    public int getItemCount() {
        return list.size();
    }
    public static  class MyViewHolder extends RecyclerView.ViewHolder{
        TextView firstName, bio;
        ImageView contactsimageview;
        CardView cardView;

        @SuppressLint("WrongViewCast")
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.name_notification);
            contactsimageview=itemView.findViewById(R.id.image_notification);
            bio = itemView.findViewById(R.id.user_bio);
            cardView= itemView.findViewById(R.id.card_view1);
        }
    }
}
