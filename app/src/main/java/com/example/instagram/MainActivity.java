package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.instagram.Fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// for firebase add authentication dependency and data base dependency
// for button color theme setting style name="Theme.Instagram" parent="Theme.AppCompat.DayNight.NoActionBar"
public class MainActivity extends AppCompatActivity {

    private ImageView iconImage;
    private LinearLayout linearLayout;
    private Button login;
    private Button register;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iconImage=findViewById(R.id.logo);
        linearLayout=findViewById(R.id.linearLayout);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);

        mAuth=FirebaseAuth.getInstance();

        linearLayout.animate().alpha(0f).setDuration(1);
//        An animation that controls the position of an object.
        TranslateAnimation animation=new TranslateAnimation(0,0,0,-1000);
        animation.setDuration(1500);
        animation.setFillAfter(false);// animation will not exist after completion
        animation.setAnimationListener(new MyAnimationListener());// listener
        iconImage.setAnimation(animation);// whole above animation is set on icon image



    }
// which handles the animation time wise
    private class MyAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            iconImage.clearAnimation();// clears the animation and move to the end
            iconImage.setVisibility(View.INVISIBLE);
            //alpha is for visibility
            linearLayout.animate().alpha(1f).setDuration(500);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
    public void onLogin(View view){
        Intent intent=new Intent(this,alreadyUser.class);
        startActivity(intent);

    }


    public void onRegister(View view){
        Intent intent=new Intent(this,loginactivity.class);

        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // this is used to keep whether login have been done or not
// if user have already login then it will directly direct to instaPage activity
        Bundle mintent=getIntent().getExtras();
        if (mintent !=null){
            String profileId=mintent.getString("publisherId");
            SharedPreferences settings = getSharedPreferences("profile",MODE_PRIVATE);
            settings.edit().putString("profileId",profileId).apply();
            Intent intent=new Intent(this,instapage.class);
            intent.putExtra("check",0);
            startActivity(intent);
        };
        FirebaseUser mFirebaseUSer=mAuth.getCurrentUser();
        if (mFirebaseUSer!=null){
            // code runs when login have done
            Intent intent=new Intent(this,instapage.class);
            startActivity(intent);
            finish();
        }



    }
}