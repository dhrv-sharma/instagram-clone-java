package com.example.instagram.UserAdapter;

import android.content.Context;
import android.content.Intent;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Fragments.Postdetailed;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.MainActivity;
import com.example.instagram.R;
import com.example.instagram.model.Notification;
import com.example.instagram.model.Post;
import com.example.instagram.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

// step 2 extension of class and implementation of the method
public class notificationAdapter  extends RecyclerView.Adapter<notificationAdapter.ViewHolder> {
    // step 3 data members declaration
    private Context mContext;
    private List<Notification> mNotifications;

    public notificationAdapter(Context mContext,List<Notification> mNotifications){
        this.mContext=mContext;
        this.mNotifications=mNotifications;
    }

    // step 4
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new notificationAdapter.ViewHolder(view);
    }
// we can add setOn item click listener on holder through bind holder
    // we can also pass image view and can be set by different function outside bindHolder
    // step 6
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // basically this code set the item value for each each recycler view
        Notification notification=mNotifications.get(position);


        // passing the view for setting the items
        // notification is an object which is of others users
        // getting information of the user
        getUser(holder.imageProfile,holder.username,notification.getUserId());

        // this helps to get the comment of the post
        holder.comment.setText(notification.getText());

        // checking weather it is a post
        if (!notification.getPostId().equals("")){
            holder.postImage.setVisibility(View.VISIBLE);
// bringing the post images
            // here we pass the holder.postImage which will get settled by the function
            getPostImage(holder.postImage,notification.getPostId());
        }else{
            holder.postImage.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!notification.getPostId().equals("")){
                    // apply() is very important
                    mContext.getSharedPreferences("prefs",Context.MODE_PRIVATE).edit().putString("postId",notification.getPostId()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Postdetailed()).commit();
//                    Intent intent=new Intent(mContext,MainActivity.class);
//                    mContext.startActivity(intent);
                }else{
                    mContext.getSharedPreferences("profile",Context.MODE_PRIVATE).edit().putString("profileId",notification.getUserId()).apply();
                    Intent intent=new Intent(mContext,MainActivity.class);
                    mContext.startActivity(intent);
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
            }
        });
    }

    private void getPostImage(ImageView postImage, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // we have to check weather a single item is there or whole class is received
                // you have to mention the Post.class in getValue
                Post post=snapshot.getValue(Post.class);
                // place holder is used to set temporary image
                Picasso.get().load(post.getImageurl()).placeholder(R.drawable.ic_action_gallery).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(ImageView imageView, TextView textView,String userId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (!user.getImageUrl().equals("default")) {
                    Picasso.get().load(user.getImageUrl()).into(imageView);
                }
                textView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // step 5
    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    // step 1
    public class ViewHolder extends RecyclerView.ViewHolder{
        // components of notification item

        public ImageView imageProfile;
        public ImageView postImage;
        public TextView username;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
// linking the variables with the xml components
            imageProfile=itemView.findViewById(R.id.image_profile);
            postImage=itemView.findViewById(R.id.post_image);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
        }
    }
}
