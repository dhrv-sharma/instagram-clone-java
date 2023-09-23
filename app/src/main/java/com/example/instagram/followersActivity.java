package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.instagram.UserAdapter.userAdapter;
import com.example.instagram.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class followersActivity extends AppCompatActivity {

    private String id;// whose foll0wing list
    private String title;// which is getting show followers ,following or likes
    private List<String> id_list;// it will have data of id

    private RecyclerView recyclerView;
    private com.example.instagram.UserAdapter.userAdapter userAdapter;
    private List<User> mUsers; // here we are using  the layout of Users





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        // checking which activity will call followers ,following
        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        // setting tool bar
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar();
        getSupportActionBar().setTitle(title);

        // it will have back arrow which is pointing to the parent activity which invoked this activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // when we click on above button this part of the code will run
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers=new ArrayList<>();
        // false for the it is activity not fragment
        userAdapter=new userAdapter(this,mUsers,false);
        recyclerView.setAdapter(userAdapter);

        id_list=new ArrayList<>();

        switch (title){
            case "followers":
                getFollowers();
                break;

            case "followings":
                getFollowings();
                break;

            case "likes":
                getLikes();
                break;
        }


    }

    public  void showUsers(){
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    User user=shot.getValue(User.class);

                    // checking weather this particular user is in its list or not
                    for (String id: id_list){
                        if (user.getId().equals(id)){
                            mUsers.add(user);
                        }
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_list.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    id_list.add(shot.getKey());
                }
                Log.i("size",Integer.toString(id_list.size()));

                showUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowings() {
        FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_list.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    id_list.add(shot.getKey());
                }

                showUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes() {

        // query
        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_list.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    id_list.add(shot.getKey());
                }

                Log.i(" like id",id);
                Log.i("size post likes",Integer.toString(id_list.size()));

                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}