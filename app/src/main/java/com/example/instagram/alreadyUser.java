package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class alreadyUser extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button login;

    private FirebaseAuth mAuth;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_user);
        TextView mess=findViewById(R.id.newUser);
        mess.setPaintFlags(mess.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        login=findViewById(R.id.loginGo);
        pd =new ProgressDialog(this);


        mAuth=FirebaseAuth.getInstance();

        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(alreadyUser.this,loginactivity.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_Email=loginEmail.getText().toString();
                String login_Pass=loginPassword.getText().toString();

                if (login_Pass.isEmpty() || login_Email.isEmpty()){

                    Toast.makeText(alreadyUser.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("credentials",login_Email+"\n"+login_Pass);
                    pd.setMessage("Signing In");
                    pd.show();
                    checkUser(login_Email,login_Pass);


                }

            }
        });



    }

    public void checkUser(String email,String pass){
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // checks weather mail is verified or not by user
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        Intent intent = new Intent(alreadyUser.this, instapage.class);
                        startActivity(intent);
                        pd.dismiss();
                        finish();
                    }
                    else {
                        Toast.makeText(alreadyUser.this, "Email is not verified Verification link have been shared", Toast.LENGTH_SHORT).show();
                        pd.setMessage("Sending a Mail");
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent=new Intent(alreadyUser.this,alreadyUser.class);
                                    startActivity(intent);
                                    pd.dismiss();
                                    finish();

                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {// this runs on the failure of listener
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(alreadyUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        mAuth.signOut();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {// this runs on the failure of listener
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(alreadyUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                loginPassword.getText().clear();
                loginEmail.getText().clear();

            }
        });

    }
}