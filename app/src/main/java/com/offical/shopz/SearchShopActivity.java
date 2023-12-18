package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchShopActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private RoundedImageView mainprofile;
    FirebaseUser currentUser;
    private RecyclerView.Adapter nearest_shop_adapter;
    private RecyclerView shopList;
    private LinearLayout search_bar;
    TextView location,address;
    HashMap<String,Double> nearst_shop_list;
    DatabaseReference shopDatabaseReference,userDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop);
        shopList =findViewById(R.id.shop_list);
        location = findViewById(R.id.user_location);
        address = findViewById(R.id.user_address);
        LinearLayoutManager shopListLayoutManager = new LinearLayoutManager(SearchShopActivity.this);
        shopList.setLayoutManager(shopListLayoutManager);
        nearst_shop_list = new HashMap<String,Double>();

        shopDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Shops");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.child("Shops").getChildren()) {
                        Location user = new Location("User");
                        if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
                            user.setLatitude((Double) snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location").child("latitude").getValue());
                            user.setLongitude((Double) snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location").child("longitude").getValue());

                            Location shop = new Location("Shop");
                            shop.setLatitude((Double) dataSnapshot.child("ShopDetails").child("location").child("latitude").getValue());
                            shop.setLongitude((Double) dataSnapshot.child("ShopDetails").child("location").child("longitude").getValue());

                            Double distance = Double.valueOf(user.distanceTo(shop));
                            nearst_shop_list.put(dataSnapshot.getKey(), distance);
                        }
                    }
                    nearest_shop_adapter = new NearestShopAdapter(sortByValue(nearst_shop_list));
                    shopList.setAdapter(nearest_shop_adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("Location").hasChild("location") && snapshot.child("Location").hasChild("address")){
                        location.setText(snapshot.child("Location").child("location").getValue().toString());
                        address.setText(snapshot.child("Location").child("address").getValue().toString());
                        Log.d("TAG", "onCreate: "+sortByValue(nearst_shop_list));
                    }
                    else{
                        Intent shop_intent = new Intent(SearchShopActivity.this, LocationActivity.class);
                        shop_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(shop_intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        mainprofile = findViewById(R.id.main_profile);
        mainprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchShopActivity.this, profile_activity.class));

            }
        });
        search_bar = findViewById(R.id.shop_to_search);
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shop_intent = new Intent(SearchShopActivity.this, SearchShopBarActivity.class);
                startActivity(shop_intent);
            }
        });

    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            if(!checkLocationEnable()){
                Intent intent = new Intent(SearchShopActivity.this, LocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }
        else{
            Intent intent = new Intent(SearchShopActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
    public static ArrayList<String> sortByValue(HashMap<String, Double> hm) {

        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list
                = new LinkedList<Map.Entry<String,Double > >(
                hm.entrySet());

        //Sorting the list based on distance
        Collections.sort(
                list,
                (i1,
                 i2) -> i1.getValue().compareTo(i2.getValue()));


        // Put the data in ArrayList
        ArrayList<String> temp
                = new ArrayList<String>();
        for (Map.Entry<String, Double> aa : list) {
            temp.add(aa.getKey());
        }

        //return the sorted list
        return temp;
    }
    //created setImage function to get the image from database easily.
    void setImage(ImageView view,String shopname){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref =database.getReference("Shopnames");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Picasso.get().load(snapshot.child(shopname).child("image").getValue(String.class)).into(view);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //failed to read value
            }
        });

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
        if(gps_enabled ){
            return true;
        }
        return false;
    }
}