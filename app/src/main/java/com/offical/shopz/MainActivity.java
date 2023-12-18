package com.offical.shopz;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {

    private DatabaseReference user_database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        user_database= FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = mAuth.getCurrentUser();
        final Button searchBtn = findViewById(R.id.btn_shop);
        final Button logoutBtn = findViewById(R.id.btn_logout);
        final ImageView profile = findViewById(R.id.profile);



        //go to search
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchShopActivity.class));
            }
        });

        //go to profile
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, profile_activity.class));
            }
        });

        //logout the user
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                user_database.child(currentUser.getUid()).child("online_status").setValue(ServerValue.TIMESTAMP);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                Toast.makeText(MainActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    //checked if user is already logged in or not
    public void onStart()
    {
        super.onStart();
        if (currentUser == null)
        {
            Intent startIntent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(startIntent);
            finish();
        }
        else
        {
            user_database.child(currentUser.getUid()).child("online_status").setValue("online");
        }
        if (checkLocationEnable()){
            Toast.makeText(this, "locationcheck", Toast.LENGTH_SHORT).show();
            Intent startIntent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(startIntent);
            finish();
        }
    }

    boolean checkLocationEnable(){
        LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if(gps_enabled && network_enabled){
            return true;
        }
        return false;
    }
}