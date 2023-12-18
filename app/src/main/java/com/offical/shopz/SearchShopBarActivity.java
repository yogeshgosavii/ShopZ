package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchShopBarActivity extends AppCompatActivity {
    private ArrayList<String> sorted_search_list,search_shop_list;
    private RecyclerView search_list;
    private EditText search_input;
    private RecyclerView.Adapter search_adapter;

    private DatabaseReference search_database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop_bar);
        search_list = findViewById(R.id.search_list);
        search_input = findViewById(R.id.search_input);
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(SearchShopBarActivity.this);
        search_list.setLayoutManager(searchLayoutManager);
        search_shop_list = new ArrayList<String>();
        search_database = FirebaseDatabase.getInstance().getReference().child("Shops");
        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search_shop_list.clear();
                if(!search_input.getText().toString().equals("")){
                    search_database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(dataSnapshot.hasChild("ShopDetails")){
                                    if((dataSnapshot.child("ShopDetails").child("name").getValue().toString().toLowerCase()).contains(search_input.getText().toString().toLowerCase())  ){
                                        Log.d("data", "onDataChange: "+search_input.getText().toString());
                                        search_shop_list.add(dataSnapshot.getKey());
                                    }
                                }

                            }
                            Log.d("data", "onDataChange: "+search_shop_list);
                            search_adapter=new SearchAdapter(search_shop_list);
                            search_list.setAdapter(search_adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    search_shop_list.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}