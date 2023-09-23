package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
   private ImageView camera;
   private ImageView gallery;
   private ImageView cross;
   private TextView post;
   private EditText description;

   private Boolean check_image;

   private Uri imagePost;
   private String imageUrl;


// code for receiving data
   private final int cam_code=1;
   private final int gal_code=2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        gallery=findViewById(R.id.gallery);
        camera=findViewById(R.id.camera);
        cross=findViewById(R.id.cancel);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);
//        post=findViewById(R.id.)
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // implicit intent
                Intent iCamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // i hve to take data from the new activity so use
                startActivityForResult(iCamera,cam_code);
            }
        });

        // gallery function
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery=new Intent(Intent.ACTION_PICK);

                // user internal storage here refer to external storage

                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,gal_code);

            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostActivity.this,instapage.class);
                startActivity(intent);
                finish();
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description.getText().toString().isEmpty() || imagePost==null){
                    Toast.makeText(PostActivity.this, "Please fill out all details for the post ", Toast.LENGTH_SHORT).show();
                }else {
                    upload();
                }
            }
        });


    }



    // onActivityResult you get the result of that intent which will give definable a data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // everything is ok and executed properly
        if (resultCode==RESULT_OK) {

            // cam code
            if (requestCode == cam_code) {
                // handling the data
//                data.getExtras().get("data");
                // bitmap for image
                // converting bitmap for uri
                Bitmap bitmap=(Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                String path=MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
                Uri conUri=Uri.parse(path);
                gallery.setImageURI(conUri);
                imagePost=conUri;


            }
//            galley code check
            if (requestCode ==gal_code){
                imagePost=data.getData();
                gallery.setImageURI(data.getData());

            }
        }else{
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(PostActivity.this,MainActivity.class);
            startActivity(intent);
        }

    }


    public void upload(){
        // message for user
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        // uploading the photo
            StorageReference filepath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+getFileExtension(imagePost));
            StorageTask uploadTask=filepath.putFile(imagePost);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri download=task.getResult();
                    imageUrl=download.toString();
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
                    String postid=ref.push().getKey();// unique id creation
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("postid",postid);
                    map.put("imageurl",imageUrl);
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    ref.child(postid).setValue(map);
                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

    }

    public String getFileExtension(Uri image){
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(image));
    }
}