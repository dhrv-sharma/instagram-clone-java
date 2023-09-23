package com.example.instagram.UserAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Fragments.Postdetailed;
import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

// step 2 extend class
public class photoAdapter extends RecyclerView.Adapter<photoAdapter.ViewHolder> {

    // step 3
    private Context mContext;
    private List<Post> mPosts;
// important step for taking data from the user here the user is fragment
    public photoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    // step 4
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.phott_layout,parent,false);
        return new photoAdapter.ViewHolder(view);
    }

//    step 6
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post=mPosts.get(position);
        // place holder gives default image
        Picasso.get().load(post.getImageurl()).placeholder(R.drawable.ic_action_gallery).into(holder.postImage);
// to access any data inside the BinderHolder use post class object
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling post detailed fragment
                mContext.getSharedPreferences("prefs",Context.MODE_PRIVATE).edit().putString("postId",post.getPostid()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Postdetailed()).commit();
            }
        });


    }

    // step 5
    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // step 1
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView postImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage=itemView.findViewById(R.id.post_image);
        }
    }
}
