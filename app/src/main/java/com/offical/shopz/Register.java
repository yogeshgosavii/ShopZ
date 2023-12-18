package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private DatabaseReference userdatabase;
    private ProgressDialog login_Progress;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText gmail = findViewById(R.id.gmail);
        final EditText phone = findViewById(R.id.register_phone);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText conpassword = findViewById(R.id.conpassword);

        final Button registerBtn = findViewById(R.id.btnregister);
        final TextView loginNowBtn =findViewById(R.id.register_to_login);
        login_Progress=new ProgressDialog(this);
        userdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNotEmpty(gmail,phone,username,password,conpassword)){
                    if(checkGmail(gmail.getText().toString()) && checkNumber(phone.getText().toString()) )
                    {
                        if(register_user(username.getText().toString(),gmail.getText().toString(),password.getText().toString(),conpassword.getText().toString(),phone.getText().toString())){
                            Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                    else{
                        Toast.makeText(Register.this, "Please check your mail or number", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Register.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    private boolean register_user(final String user_name, String user_email, final String password,String conPassword,String phone)
    {
        final boolean[] isCreated = {false};
        if(!checkPasswordMatch(password,conPassword)){
            Toast.makeText(this, "Password are not same", Toast.LENGTH_SHORT).show();
            return isCreated[0];
        }
        if (!user_email.equals("") && !password.equals("") && !conPassword.equals("")&& !user_name.equals("")&& !phone.equals("")){
            login_Progress.setMessage("Creating your userId...");
            login_Progress.setCanceledOnTouchOutside(false);
            login_Progress.show();

            mAuth.createUserWithEmailAndPassword(user_email.trim(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        isCreated[0] = true;
                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        String current_userid = current_user.getUid();

                        Map<String, Object> new_user_map = new HashMap<String, Object>();
                        new_user_map.put("id", current_userid);
                        new_user_map.put("online_status", "online");
                        new_user_map.put("password", password);
                        new_user_map.put("username", user_name);
                        new_user_map.put("number", phone);
                        Map<String, Object> register_user_map = new HashMap<String, Object>();
                        register_user_map.put(current_userid, new_user_map);

                        userdatabase.updateChildren(register_user_map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error != null) {
                                    Log.d("user_registered", error.getMessage().toString());
                                } else {
                                    login_Progress.dismiss();
                                    Intent password_intent = new Intent(Register.this, SearchShopActivity.class);
                                    password_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(password_intent);
                                    Register.this.finishAffinity();
                                }
                            }
                        });


                    } else {

                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show();
        }

        return isCreated[0];
    }
    boolean checkGmail(String gmail){

        if(gmail.contains("@gmail.com")){
            return true;
        }
        return false;
    }
    boolean checkNumber(String number){
        if(number.length()==10){
            return true;
        }
        return false;
    }
    boolean isNotEmpty(EditText gmail, EditText phone, EditText username, EditText password, EditText conpassword){
        return !(gmail.getText().equals("") || phone.getText().equals("") || username.getText().equals("") || password.getText().equals("") || conpassword.getText().equals(""));
    }
    boolean checkPasswordMatch(String password, String conpassword){
        if(password.equals(conpassword))
        {
            return true;
        }
        return false;
    }
}