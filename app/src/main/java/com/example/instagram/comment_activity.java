package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.UserAdapter.CommentAdapter;
import com.example.instagram.model.Comment;
import com.example.instagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class comment_activity extends AppCompatActivity {
    private EditText addComment;
    private CircleImageView imageProfile;
    private TextView post;

    private String postId;
    private  String authorId;
    FirebaseUser fUser;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this ));
        commentList=new ArrayList<>();
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");
        commentAdapter=new CommentAdapter(this,commentList,postId);
        recyclerView.setAdapter(commentAdapter);

        addComment=findViewById(R.id.add_comment);
        imageProfile=findViewById(R.id.image_profile);
        post =findViewById(R.id.post);


        fUser= FirebaseAuth.getInstance().getCurrentUser();


        getUserImage();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(comment_activity.this, "No Comment added", Toast.LENGTH_SHORT).show();
                }else{
                    putComment();
                    addComment.setText("");

                }
            }
        });

        getComment();
    }

    // posting a comment on the post

    public void putComment(){
        HashMap<String ,Object> map=new HashMap<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Comment").child(postId);
        // id is an comment id
        String id=ref.push().getKey();
        map.put("id",id);
        map.put("comment",addComment.getText().toString());
        map.put("publisher",fUser.getUid());

// generating a unique comment id and saving on database  through push() so that multiple comments can be visible of same user
        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(comment_activity.this, "Comment added", Toast.LENGTH_SHORT).show();
                }else{


                    Toast.makeText(comment_activity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // setting the image profile of user
    public void getUserImage(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user =snapshot.getValue(User.class);
                if (!user.getImageUrl().equals("default")) {
                    Picasso.get().load(user.getImageUrl()).into(imageProfile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // getting comments from the databse
    public void getComment(){
        FirebaseDatabase.getInstance().getReference().child("Comment").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot shot:snapshot.getChildren()){
                    Comment comment=shot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}