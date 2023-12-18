package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    Double latitude,longitude;
    String locate;
    FusedLocationProviderClient fusedLocationProviderClient;

    EditText address;
    Button current_location,update_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        address = findViewById(R.id.address);
        current_location = findViewById(R.id.current_address);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddressActivity.this);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                if(snapshot.child("Location").hasChild("address")){
                    address.setText(snapshot.child("Location").child("address").getValue().toString());
                }
            }
        });
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();

            }
        });
        update_address = findViewById(R.id.update_address);
        update_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

    }
    //Change the current address stored in the app
    private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if(location != null){
                                    Geocoder geocoder = new Geocoder(AddressActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        String addresstxt = addresses.get(0).getSubLocality().toString();
                                        String[] addressarr = addresstxt.split("\\s");
                                        locate = addressarr[0];
                                        longitude = addresses.get(0).getLongitude();
                                        latitude = addresses.get(0).getLatitude();
                                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        userReference.child("Location").child("location").setValue(locate);
                                        userReference.child("Location").child("address").setValue(address.getText().toString());
                                        userReference.child("Location").child("latitude").setValue(latitude);
                                        userReference.child("Location").child("longitude").setValue(longitude);
                                        Toast.makeText(AddressActivity.this,"Location updated successfully", Toast.LENGTH_SHORT).show();


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
                                resolvableApiException.startResolutionForResult(AddressActivity.this,REQUEST_CHECK_SETTINGS);
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
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(AddressActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},REQUEST_CODE);
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