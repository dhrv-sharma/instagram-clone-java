package com.example.instagram.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.R;
import com.example.instagram.UserAdapter.PostAdapter;
import com.example.instagram.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    // variables for the layout connection
    private RecyclerView recyclerView;
    private PostAdapter  postAdapter;
    private List<Post> postList;
    private  List<String> followingList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home,container,false);

        // linking the variables
        recyclerView=view.findViewById(R.id.recycler_view_posts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);// so that the latest post can come from top
        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(postAdapter);

        followingList=new ArrayList<>();

        checkFollowingUsers();



        return view;// return change to view
    }

    // add into the following list which is followed by the user
    public void checkFollowingUsers(){

        // referencing the followers of the current user and adding value add listener
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    followingList.add(shot.getKey());
                }
                // reading post of all the followers
//                Log.i("list",followingList.toString());
                // seeing our self post
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // retrieving the posts from all the followers
    private  void readPost(){

        // get reference to the post section
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    Post post=shot.getValue(Post.class);

                    // checking the whether the following list have publisher
                    for (String id: followingList){
                        if (post.getPublisher().equals(id)){
                            postList.add(post);

                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}