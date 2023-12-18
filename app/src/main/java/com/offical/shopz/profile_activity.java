package com.offical.shopz;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.offical.shopz.databinding.ActivityProfileBinding;
import com.squareup.picasso.Picasso;


public class profile_activity extends AppCompatActivity {
    FirebaseStorage storage;
    FirebaseDatabase database;
    ImageView image;
    ActivityResultLauncher<String> profile;
    TextView fullname, phone;
    FirebaseAuth fAuth;
    String userId;
    Uri imageUri;

    private DatabaseReference user_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = FirebaseStorage.getInstance();
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user_database = FirebaseDatabase.getInstance().getReference();
        TextView your_order = findViewById(R.id.your_orders);
        your_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_activity.this,YourOrdersActivity.class);
                startActivity(intent);
            }
        });

        TextView logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_database.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online_status").setValue(ServerValue.TIMESTAMP);
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(profile_activity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                Toast.makeText(profile_activity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        //to get name and mobile from database
        fullname = findViewById(R.id.name);
        phone = findViewById(R.id.mobile);
        userId = fAuth.getCurrentUser().getUid();

       user_database= FirebaseDatabase.getInstance().getReference().child("Users");
       user_database.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.hasChild(userId)){
                   fullname.setText(snapshot.child(userId).child("username").getValue().toString());
                   phone.setText(snapshot.child(userId).child("number").getValue().toString());
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }
}

