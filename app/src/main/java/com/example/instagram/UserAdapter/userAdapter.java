package com.example.instagram.UserAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.MainActivity;
import com.example.instagram.R;
import com.example.instagram.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// user adapter is used for linking the recycler list with the list item design which we have design
public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUSer;// created a list of objects of class user

    // as we are using this inside the fragments so light change in the code
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public userAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // settled our layout
        View view= LayoutInflater.from(mContext).inflate(R.layout.userlayout,parent,false);
        return new userAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //This method is called for each ViewHolder to bind it to the adapter. This is where we will pass our data to our ViewHolder
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        User user=mUSer.get(position);// setting a user through position
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());

        // for image set we need  picasso
        Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.ic_action_person).into(holder.imageProfile);
        // here remember the user is class

        // checking whether user have followed that list_item_account

        isFollowed(user.getId(),holder.btnFollow);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }

        // follow request sending
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnFollow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(firebaseUser.getUid())
                            .child("following")
                            .child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(user.getId())
                            .child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
//                    holder.btnFollow.setText("following");

                    addNotification(user.getId());
                } else{
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(firebaseUser.getUid())
                            .child("following")
                            .child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(user.getId())
                            .child("followers")
                            .child(firebaseUser.getUid()).removeValue();

//                    holder.btnFollow.setText("follow");


                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragment){
                    mContext.getSharedPreferences("profile",Context.MODE_PRIVATE).edit().putString("prfoileId",user.getId()).apply();
                    Intent intent=new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });

    }

    private void addNotification(String userId){
        HashMap<String,Object> notf=new HashMap<>();
        notf.put("userId",userId);
        notf.put("text","started following you");
        notf.put("postId","");
        notf.put("isPost",false);
// push add the content into the data base with the unique id
        FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(notf);
    }

    public  void isFollowed(String id,Button btnFollow){
        // this checks weather this account is followed by user or not
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");

        // when user follow or unfollow an account this part of code runs
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()){
                    btnFollow.setText("following");
                }
                else{
                    btnFollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public int getItemCount() {
        return mUSer.size();
    }

    // to take inputs of first three values we have to make constructor
    public userAdapter(Context mContext, List<User> mUSer, boolean isFragment) {
        this.mContext = mContext;
        this.mUSer = mUSer;
        this.isFragment = isFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView username;
        public  TextView fullname;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            btnFollow =itemView.findViewById(R.id.btn_follow);


        }
    }
}
