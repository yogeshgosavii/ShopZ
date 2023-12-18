package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth mAuth;
    private ProgressDialog login_Progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_Progress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        final EditText email = findViewById(R.id.login_email);
        final EditText password = findViewById(R.id.login_password);
        final Button loginBtn = findViewById(R.id.btn_login);
        final TextView registerNowBtn = findViewById(R.id.registerNowBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email_txt = email.getText().toString();
                final String passwordtxt = password.getText().toString();

                if(email_txt.isEmpty() || passwordtxt.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please Email or Password", Toast.LENGTH_SHORT).show();
                }
                else{

                    login_Progress.setMessage("Connecting...");
                    login_Progress.setCanceledOnTouchOutside(false);
                    login_Progress.show();
                    login_user(email_txt,passwordtxt);
                }
            }
        });

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, Register.class));

            }
        });

    }
    private void login_user(String email,String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    login_Progress.dismiss();
                    Intent intent = new Intent(LoginActivity.this, SearchShopActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    login_Progress.dismiss();
                    Toast.makeText(LoginActivity.this, "Please check your email and password try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}