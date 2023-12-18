package com.offical.shopz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private DatabaseReference searched_shop_database;
    View mview;
    private ArrayList<String> searched_shops;

    public SearchAdapter(ArrayList<String> searched_shops){
        this.searched_shops = searched_shops;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_searched_shop_layout,parent,false);
        return new ViewHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        searched_shop_database = FirebaseDatabase.getInstance().getReference().child("Shops");
        searched_shop_database.child(searched_shops.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ShopDetails")){
                    holder.setShopname(snapshot.child("ShopDetails").child("name").getValue().toString());
                    holder.setShopAddress(snapshot.child("ShopDetails").child("address").getValue().toString());
                    holder.setShop_image_thumb(snapshot.child("ShopDetails").child("image").getValue().toString(),mview.getContext());
                    holder.search_shop_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent shop_intent = new Intent(mview.getContext(), ProductsActivity.class);
                            shop_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            shop_intent.putExtra("shop_name",snapshot.child("ShopDetails").child("name").getValue().toString());
                            Log.d("TG", "onClick: "+position);
                            shop_intent.putExtra("shop_id",searched_shops.get(position));
                            mview.getContext().startActivity(shop_intent);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return searched_shops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout search_shop_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            search_shop_view = itemView.findViewById(R.id.search_shop_view);
        }


        public void setShopname(String Shopname)
        {
            TextView shopnname_search=(TextView) itemView.findViewById(R.id.search_shopname);
            shopnname_search.setText(Shopname);
        }
        public void setShopAddress(String address)
        {
            TextView shopNameAddress=(TextView) itemView.findViewById(R.id.search_shop_address);
            shopNameAddress.setText(address);
        }
        public void setShop_image_thumb(String thumb_shop_image, Context context)
        {
            RoundedImageView Shop_image =(RoundedImageView) itemView.findViewById(R.id.search_shop_image);
            Picasso.get().load(thumb_shop_image).into(Shop_image);
        }
    }
}
