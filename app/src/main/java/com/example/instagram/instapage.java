package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.instagram.Fragments.HomeFragment;
import com.example.instagram.Fragments.NotificationFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class instapage extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instapage);

        bottomNavigationView=findViewById(R.id.bottom_navigation);`
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        selectorFragment=new HomeFragment();
                        break;
                    case R.id.search:
                        selectorFragment=new SearchFragment();
                        break;
                    case R.id.add:
                        selectorFragment=null;
                        startActivity(new Intent(instapage.this,PostActivity.class));
                        finish();
                        break;
                    case R.id.heart:
                        selectorFragment=new NotificationFragment();
                        break;
                    case R.id.person:
                        selectorFragment=new ProfileFragment();
                        break;
                }

                if (selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }
                return true;
            }
        });
        // this fragment will be executed on on creation of this activity

        String data=getSharedPreferences("profile", Context.MODE_PRIVATE).getString("profileId","none");


        if (data.equals("none") ){
            selectorFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }else{
            // this line also select that fragment
            bottomNavigationView.setSelectedItemId(R.id.person);
        }




    }
}