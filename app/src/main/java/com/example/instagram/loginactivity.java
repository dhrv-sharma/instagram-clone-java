package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class loginactivity extends AppCompatActivity {

    private EditText userName,name,email,password;
    private TextView message;
    private Button register;


    // using this we are creating an email user account in database
    private DatabaseReference myRootRef;
    private FirebaseAuth mAuth;




// adding data in database
//    private FirebaseDatabase db=FirebaseDatabase.getInstance();
//    private  DatabaseReference root=db.getReference();


    // this gives a pop up which used to show loading just a moment
    ProgressDialog pd;

// we want to mail the new user as a welcome message so for that go to tools -> fire base -> cloud functions -> do step 1 and step 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        userName=findViewById(R.id.username);
        name=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        message=findViewById(R.id.textView);
        message.setPaintFlags(message.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register=findViewById(R.id.registerGo);

        // initialize progress dialogue
        pd =new ProgressDialog(this);

        // getting initialized
        myRootRef=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginactivity.this,alreadyUser.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String textUser=userName.getText().toString();
                String textMail=email.getText().toString();
                String textName=name.getText().toString();
                String textPass=password.getText().toString();

                if (textMail.isEmpty() || textUser.isEmpty() || textName.isEmpty() || textPass.isEmpty()){
                    Toast.makeText(loginactivity.this, "Please Fill Out The All Credentials", Toast.LENGTH_SHORT).show();
                }else{
                    if (textPass.length() <6 ){
                        Toast.makeText(loginactivity.this, "Password length is too short", Toast.LENGTH_SHORT).show();
                    }else {
                        // registering user
                        registeringUser(textUser,textName,textMail,textPass);
                    }
                }


            }

        });





    }



    public void registeringUser(String username,String name,String email,String password){

        // this create users wth email and password with  email id
        // remember if one mail id account is created then then it will not accept the another account with same mail id
        // message set
        pd.setMessage("Please Wait  ");
        // from here this pop start
        pd.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String ,Object> user=new HashMap<>();
                user.put("username",username);
                user.put("name",name);
                user.put("email",email);
                user.put("password",password);
                user.put("id",mAuth.getCurrentUser().getUid());
                // these data will be update later on
                user.put("bio","");

                user.put("imageUrl","default");

                // adds the data through hash map bu creating an child under users
                myRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    // as soon as child completes this code runs
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(loginactivity.this, "Please Verify Your Email Address", Toast.LENGTH_SHORT).show();
// used for email verification
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                // on success of sending mail
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(loginactivity.this, "Update Your Profile "+"For Better Experience", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(loginactivity.this,MainActivity.class);
                                        startActivity(intent);
                                        // here the pop up ends
                                        pd.dismiss();
                                        mAuth.signOut();
                                        finish();// so the user hits the back button he does not come here again
                                    }
                                    else{
                                        Toast.makeText(loginactivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                });



            }

        }).addOnFailureListener(new OnFailureListener() {// in case if something error occurred during account creation it will tell the
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(loginactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





    }
}