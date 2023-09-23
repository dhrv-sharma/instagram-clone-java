package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class editProfile extends AppCompatActivity {
    private ImageView close;
    private CircleImageView imageProfile;
    private TextView save;
    private TextView changePhoto;
    private EditText userName;
    private EditText fullName;
    private EditText bio;
    private Uri imageUri;

    private FirebaseUser fuSer;

    // helps to get image uri
    private StorageTask uploadTask;
    private StorageReference storageReg;
    ProgressDialog pm;

    int gal_code=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
// FirebaseUser and FirebaseAuth are used for getting current user
        fuSer= FirebaseAuth.getInstance().getCurrentUser();

        close=findViewById(R.id.close);
        imageProfile=findViewById(R.id.image_profile);
        close=findViewById(R.id.close);
        save=findViewById(R.id.save);
        changePhoto=findViewById(R.id.change_photo);
        fullName=findViewById(R.id.fullname);
        userName=findViewById(R.id.username);
        bio=findViewById(R.id.bio);

        storageReg= FirebaseStorage.getInstance().getReference().child("uploads");


        // for query use FirebaseDatabase
        // setting the old data into edit profile
        FirebaseDatabase.getInstance().getReference().child("Users").child(fuSer.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // it is an important point here user  respective child get referenced to User class object
                User user=snapshot.getValue(User.class);
                fullName.setText(user.getName());
                userName.setText(user.getUsername());
                bio.setText(user.getBio());
                Picasso.get().load(user.getImageUrl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // automatically fragment will start
                finish();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when such intent executed they return some data so we have to create a function onActivity result
                Intent iGallery=new Intent(Intent.ACTION_PICK);

                // user internal storage here refer to external storage

                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,gal_code);
            }
        });
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery=new Intent(Intent.ACTION_PICK);

                // user internal storage here refer to external storage

                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,gal_code);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pm=new ProgressDialog(editProfile.this);
                pm.setMessage("updating");
                updateProfile();
            }
        });
    }
// updating the text items
    public void updateProfile(){
        HashMap<String ,Object > map=new HashMap<>();
        map.put("name",fullName.getText().toString());
        map.put("username",userName.getText().toString());
        map.put("bio",bio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(fuSer.getUid()).updateChildren(map);
        pm.dismiss();
        finish();
    }

    // activity result of gallery selected images
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // everything is ok and executed properly
        if (resultCode==RESULT_OK) {


//            galley code check
            if (requestCode ==gal_code){
                imageUri=data.getData();
                imageProfile.setImageURI(data.getData());
                uploadImage();

            }
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    // uploading the image from uri to fire base
    private void  uploadImage(){
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri!=null){
            StorageReference filePath=storageReg.child(System.currentTimeMillis()+"jpeg");
            uploadTask=filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<URI>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri dwnloadUri= (Uri) task.getResult();
                        String url=dwnloadUri.toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(fuSer.getUid()).child("imageUrl").setValue(url);
                        pd.dismiss();
                    }else{
                        Toast.makeText(editProfile.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            });
        }else{
            Toast.makeText(this, "No Image is Selected", Toast.LENGTH_SHORT).show();
        }
    }
}