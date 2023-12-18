package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Button btn_manual_location, btn_device_location;
    private TextView address;
    private DatabaseReference user_database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mAuth = FirebaseAuth.getInstance();
        user_database = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = mAuth.getCurrentUser();
        address = findViewById(R.id.address);
        btn_device_location = findViewById(R.id.btn_device_location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationActivity.this);
        btn_device_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();



            }
        });
    }
    public void onStart()
    {
        super.onStart();
        if (currentUser == null)
        {
            Intent startIntent = new Intent(LocationActivity.this,LoginActivity.class);
            startActivity(startIntent);
            finish();
        }
        else
        {
            user_database.child(currentUser.getUid()).child("online_status").setValue("online");
        }
    }
    //get the last location of the user
    public void getLastLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //checks if the user location in on
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                            @Override
                            public void onSuccess(android.location.Location location) {

                                if(location != null){
                                    Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        String addresstxt = addresses.get(0).getSubLocality().toString();
                                        String[] addressarr = addresstxt.split("\\s");
                                        user_database.child(currentUser.getUid()).child("Location").child("location").setValue(addressarr[0]);
                                        user_database.child(currentUser.getUid()).child("Location").child("address").setValue(addresses.get(0).getAddressLine(0));
                                        user_database.child(currentUser.getUid()).child("Location").child("latitude").setValue(addresses.get(0).getLatitude());
                                        user_database.child(currentUser.getUid()).child("Location").child("longitude").setValue(addresses.get(0).getLongitude());

                                        Intent startIntent = new Intent(LocationActivity.this,SearchShopActivity.class);
                                        startActivity(startIntent);
                                        finish();


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                                else
                                {
                                    getLastLocation();

                                }
                            }
                        });
            }
            else
            {
                //if location is of gives a message to turn on location
                LocationRequest locationRequest;
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(2000);
                locationRequest.setNumUpdates(1);



                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);
                SettingsClient settingsClient = LocationServices.getSettingsClient(this);
                Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
                task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        getLastLocation();
                    }
                });
                task.addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ResolvableApiException){
                            try{
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(LocationActivity.this,REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

            }

        }
        else
        {
            //if location permission is not given ask for permission
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(LocationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_CODE){
            if(grantResults.length>0&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();
            }
            else
            {
                askPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS)
        {
            if(resultCode == RESULT_OK)
            {
                getLastLocation();
            }
            if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "request denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}