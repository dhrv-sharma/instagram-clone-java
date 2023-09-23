package com.example.instagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.UserAdapter.PostAdapter;
import com.example.instagram.UserAdapter.photoAdapter;
import com.example.instagram.editProfile;
import com.example.instagram.followersActivity;
import com.example.instagram.model.Post;
import com.example.instagram.model.User;
import com.example.instagram.optionsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView post;
    private TextView following;
    private  TextView fullname;
    private  TextView folllowers;
    private  TextView bio;
    private  TextView username;

    private  ImageView myPictures;
    private  ImageView savedPictures;

    private Button editProfile;


    private FirebaseUser fuser;

    String profileId;

//    for my photos
    private RecyclerView recyclerView;
    private photoAdapter photoAdapter;
    private List<Post> myPhotoList;

    // for saved photos
    private  RecyclerView recyclerViewSaves;
    private photoAdapter postAdapterSaves;
    private  List<Post> mySavedPost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
// getUid() Returns a string used to uniquely identify your user in your Firebase project's user database.
        fuser= FirebaseAuth.getInstance().getCurrentUser();

        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        editProfile=view.findViewById(R.id.edit_profile);

        // getting the access of the shared preferences
        String data=getContext().getSharedPreferences("profile", Context.MODE_PRIVATE).getString("profileId","none");
        if (data.equals("none")){
            profileId=fuser.getUid();
        }
        else{
            profileId=data;
            getContext().getSharedPreferences("profile", Context.MODE_PRIVATE).edit().clear().commit();


        }
        imageProfile=view.findViewById(R.id.image_profile);
        options=view.findViewById(R.id.options);
        folllowers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        post=view.findViewById(R.id.posts);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bio);
        username=view.findViewById(R.id.username);
        myPictures=view.findViewById(R.id.my_pictures);
        savedPictures=view.findViewById(R.id.saved_pictures);
        recyclerView=view.findViewById(R.id.recucler_view_pictures);
        recyclerView.setHasFixedSize(true);
        // creating a grid layout by setLayoutManager
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        myPhotoList=new ArrayList<>();
        photoAdapter=new photoAdapter(getContext(),myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        // both recycler view have same adapter photo adapter
        // adapter can be used for set on click listener properties for there
        // go on bindView holder function

        recyclerViewSaves =view.findViewById(R.id.recucler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext() ,3));
        mySavedPost=new ArrayList<>();
        postAdapterSaves=new photoAdapter(getContext(),mySavedPost);
        recyclerViewSaves.setAdapter(postAdapterSaves);


        // setting user info fetching my photos and saved photos
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();

        // for another profile edit profile should not be there
        if (profileId.equals(fuser.getUid())){
            editProfile.setText("Edit Profile");
        }else{
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText= editProfile.getText().toString();
                if (btnText.equals("Edit Profile")){
                    // go to edit profile
                    startActivity( new Intent(getContext(), com.example.instagram.editProfile.class));
                }else{
                    if (btnText.equals("follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fuser.getUid())
                                .child("following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(fuser.getUid()).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fuser.getUid())
                                .child("following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(fuser.getUid()).removeValue();

                    }
                }

            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);
        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
                savedPictures.setImageResource(R.drawable.ic_action_saved);
                myPictures.setImageResource(R.drawable.ic_picture);
                mySavedPost.clear();
                myPhotos();


            }
        });
        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
                savedPictures.setImageResource(R.drawable.ic_saved_done);
                myPictures.setImageResource(R.drawable.ic_app_not);
                myPhotoList.clear();
                getSavedPost();


            }
        });

        folllowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), followersActivity.class);
                // we are getting profile id already
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });

        folllowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), followersActivity.class);
                // we are getting profile id already in this fragment
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), followersActivity.class);
                // we are getting profile id already in this fragment
                intent.putExtra("id",profileId);
                intent.putExtra("title","followings");
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), optionsActivity.class));
            }
        });




        return view ;
    }

    private void getSavedPost() {
        // in database in Saves we are storing only the post id so first we extract post id then image from posts
        List<String> savedPostId=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot:snapshot.getChildren()){
                    savedPostId.add(shot.getKey());// return the post id
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        mySavedPost.clear();
                        for (DataSnapshot shot2:snapshot2.getChildren()){
                            Post post=shot2.getValue(Post.class);
// checking the user have saved this post or not
                            for (String id: savedPostId){
                                if (post.getPostid().equals(id)){
                                    mySavedPost.add(post);

                                }
                            }


                        }

                        postAdapterSaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // bringing
    private void myPhotos() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    Post post=shot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){
                        myPhotoList.add(post);
                    }
                }
                // keeping latest images on top
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fuser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()){
                    editProfile.setText("following");
                }
                else{
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter=0;
                for (DataSnapshot shot:snapshot.getChildren()){
                    Post post=shot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        counter++;
                    }
                }

                post.setText(String.valueOf(counter));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getFollowersAndFollowingCount() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        // followers counting
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                folllowers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
// following counting
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void userInfo(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (!user.getImageUrl().equals("default") ){
                    Picasso.get().load(user.getImageUrl()).into(imageProfile);
                }
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}