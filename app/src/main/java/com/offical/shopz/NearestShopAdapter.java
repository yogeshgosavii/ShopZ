package com.offical.shopz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class NearestShopAdapter extends RecyclerView.Adapter<NearestShopAdapter.ViewHolder> {
    private DatabaseReference searched_shop_database;
    DatabaseReference shopDatabaseReference,userDatabaseReference;
    View mview;
    FirebaseAuth auth;
    private ArrayList<String> nearest_shops;
    public NearestShopAdapter(ArrayList<String> nearest_shops){
        this.nearest_shops = nearest_shops;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_shop_layout,parent,false);
        return new ViewHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        shopDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Shops");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        shopDatabaseReference.keepSynced(true);
        auth = FirebaseAuth.getInstance();
        final String shop_id = nearest_shops.get(position).toString();
        Log.d("TAG", "onBindViewHolder: "+shop_id);
        shopDatabaseReference.child(shop_id).child("ShopDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String shopName = (String) snapshot.child("name").getValue().toString();
                holder.setShop_name(shopName);
                String shopAddress = (String) snapshot.child("address").getValue().toString();
                holder.setShop_address(shopAddress);
                String shopContact = (String) snapshot.child("mobile").getValue().toString();
                holder.setShop_contact(shopContact);
                String shopImage = (String) snapshot.child("image").getValue().toString();
                holder.setShopImage(shopImage,mview.getContext());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.enter_shop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("data","Shopid :"+shop_id);


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Shops").child(shop_id).child("ShopDetails").child("name");
                databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        String shop_name = task.getResult().getValue().toString();
                        Log.d("data","onComplete: "+shop_name);
                        Intent shop_intent = new Intent(mview.getContext(), ProductsActivity.class);
                        shop_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        shop_intent.putExtra("shop_name",shop_name);
                        shop_intent.putExtra("shop_id",shop_id);
                        mview.getContext().startActivity(shop_intent);
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return nearest_shops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout enter_shop_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            enter_shop_btn = itemView.findViewById(R.id.enter_shop_btn);
        }


        public void setShop_address(String shopAddress) {
            TextView shop_address = itemView.findViewById(R.id.shop_address);
            shop_address.setText(shopAddress);
        }

        public void setShop_contact(String shopContact) {
            TextView shop_contact = itemView.findViewById(R.id.shop_contact);
            shop_contact.setText(shopContact);

        }

        public void setShop_name(String shopName) {
            TextView shop_name = itemView.findViewById(R.id.shop_name);
            shop_name.setText(shopName);

        }

        public void setShopImage(String shopImage, Context context) {
            ImageView shop_image =(ImageView) itemView.findViewById(R.id.shop_image);
            Picasso.get().load(shopImage).into(shop_image);
        }
    }
}
