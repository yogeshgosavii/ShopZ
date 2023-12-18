package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class ProductsActivity extends AppCompatActivity {
    FirebaseAuth auth;
    NestedScrollView insideShopLayout;
    private RecyclerView productList;
    private LinearLayout cart_view,products_to_search;
    private ConstraintLayout cart_layout;
    private TextView shop_location,shop_distance,item_amount,shop_name,shop_name_header,itemCount,itemAmount;
    private Button view_cart;
    DatabaseReference productDatabaseReference,userDatabaseReference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        final String shop_id=getIntent().getStringExtra("shop_id");
        final String shopName=getIntent().getStringExtra("shop_name");
        Log.d("data","Shopid:"+shop_id+" Shop1");
        insideShopLayout = findViewById(R.id.inside_shop_layout);
        shop_name_header = findViewById(R.id.shop_name_header);
        itemAmount = findViewById(R.id.item_amount);
        itemCount = findViewById(R.id.item_count);
        products_to_search = findViewById(R.id.products_to_search);

        products_to_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shop_intent = new Intent(ProductsActivity.this, ProductSearchBarActivity.class);
                shop_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shop_intent.putExtra("shop_id",shop_id);
                startActivity(shop_intent);
            }
        });
        shop_location = findViewById(R.id.shop_location);
        shop_distance = findViewById(R.id.shop_distance_time);
        DatabaseReference shoplocation = FirebaseDatabase.getInstance().getReference();
        shoplocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shop_location.setText(snapshot.child("Shops").child(shop_id).child("ShopDetails").child("address").getValue().toString());
                Location user = new Location("User");
                if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
                    user.setLatitude((Double) snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location").child("latitude").getValue());
                    user.setLongitude((Double) snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location").child("longitude").getValue());

                    Location shop = new Location("Shop");
                    shop.setLatitude((Double) snapshot.child("Shops").child(shop_id).child("ShopDetails").child("location").child("latitude").getValue());
                    shop.setLongitude((Double) snapshot.child("Shops").child(shop_id).child("ShopDetails").child("location").child("longitude").getValue());

                    float distance = (float) (user.distanceTo(shop))/1000;
                    DecimalFormat df = new DecimalFormat("0.00");
                    shop_distance.setText(df.format(distance)+" km away");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(shopName.length()>13){
            shop_name_header.setText(shopName.substring(0,13)+"...");
        }
        else{
            shop_name_header.setText(shopName);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insideShopLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    int totalScroll = insideShopLayout.getMaxScrollAmount()-insideShopLayout.getScrollY();
                    Log.d("data", "onScrollChange: "+totalScroll);
                    if (totalScroll <= 842){
                        shop_name_header.setVisibility(View.VISIBLE);
                    } else {
                        shop_name_header.setVisibility(View.GONE);
                    }
                }
            });
        }
        productList =findViewById(R.id.product_list);
        cart_layout = findViewById(R.id.cart_layout);
        cart_view = findViewById(R.id.cart_view);
//        cart_name = findViewById(R.id.cart_name);
        shop_name = findViewById(R.id.shopname);
        if(shopName.length()>30){
            shop_name.setText(shopName.substring(0,30)+"...");
        }
        else{
            shop_name.setText(shopName);
        }
//        view_cart = findViewById(R.id.view_cart);
        cart_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shop_intent = new Intent(ProductsActivity.this, CartActivity.class);
                shop_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shop_intent.putExtra("shop_name",shopName);
                shop_intent.putExtra("shop_id",shop_id);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                startActivity(shop_intent);
                finish();
            }
        });

//        cart_name.setText(shopName);
        LinearLayoutManager productListLayoutManager = new LinearLayoutManager(ProductsActivity.this);
        productList.setLayoutManager(productListLayoutManager);
        productList.setHasFixedSize(false);
        productDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Shops").child(shop_id).child("Products");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = productDatabaseReference;
        FirebaseRecyclerOptions<ProductList> options = new FirebaseRecyclerOptions.Builder<ProductList>()
                .setQuery(query, ProductList.class)
                .build();
        FirebaseRecyclerAdapter<ProductList,ProductListViewHolder> productList_Adapter = new FirebaseRecyclerAdapter<ProductList, ProductListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductListViewHolder holder, int position, @NonNull ProductList model) {
                productDatabaseReference.keepSynced(true);
                auth = FirebaseAuth.getInstance();
                final String product_id = getRef(position).getKey();
                Log.d("data",product_id);
                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean cart_flag = false;
                        for(DataSnapshot dataSnapshot : snapshot.child("cartProducts").getChildren()){
                            if (dataSnapshot.hasChild("productShopname")){
                                if((dataSnapshot.child("productShopname").getValue()).equals(shopName)){
                                    cart_flag = true;
                                    break;
                                }
                            }

                        }
                        if (cart_flag){
                            if(snapshot.child("cartProducts").getChildrenCount() >1){
                                itemCount.setText((int) snapshot.child("cartProducts").getChildrenCount() + " items");
                            }
                            else{
                                itemCount.setText((int) snapshot.child("cartProducts").getChildrenCount() + " item");
                            }
                            if(snapshot.hasChild("cartTotal")){
                                itemAmount.setText("₹"+(long) snapshot.child("cartTotal").getValue());
                            }
                            cart_view.setVisibility(View.VISIBLE);
                        }
                        else{
                            cart_view.setVisibility(View.GONE);

                        }
                        if (snapshot.child("cartProducts").hasChild(product_id)){
                            holder.add_to_cart.setVisibility(View.GONE);
                            holder.order_manage_btn.setVisibility(View.VISIBLE);
                        }
                        else{
                            holder.add_to_cart.setVisibility(View.VISIBLE);
                            holder.order_manage_btn.setVisibility(View.GONE);
                        }
                        if(snapshot.child("cartProducts").child(product_id).hasChild("productQuantity")){
                            long quantity = (long) snapshot.child("cartProducts").child(product_id).child("productQuantity").getValue();
                            Log.d("data","quantity:"+quantity);
                            if(quantity > 0) {
                                holder.item_count.setText(Integer.toString(Math.toIntExact(quantity)));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.add_to_cart.setVisibility(View.GONE);
                        holder.order_manage_btn.setVisibility(View.VISIBLE);
                        final long[] productPrice = new long[1];
                        productDatabaseReference.child(product_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d("data","vice:"+product_id);
                                productPrice[0] = (long) snapshot.child("price").getValue();
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.getResult().hasChild("DeliveryFee")){
                                            boolean flag = false;
                                            for(DataSnapshot snapshot1 :task.getResult().child("cartProducts").getChildren()){
                                                String listShopname = snapshot1.child("productShopname").getValue().toString();
//                                                String currentShopname =task.getResult().child("cartProducts").child(product_id).child("productShopname").getValue().toString();
                                                Log.d("data", "listShopname: "+listShopname);
                                                Log.d("data", "currentShopname: "+shopName);
                                                if(listShopname.equals(shopName)){
                                                    flag = true;
                                                    break;
                                                }

                                            }
                                            if(!flag){
                                                long deliveryFee = (long) task.getResult().child("DeliveryFee").getValue();
                                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("DeliveryFee").setValue(deliveryFee+30);
                                            }
                                        }
                                        else{

                                            userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("DeliveryFee").setValue(30);
                                        }
                                        if (task.getResult().hasChild("cartTotal")){
                                            long cartTotal = (long) task.getResult().child("cartTotal").getValue();
                                            userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartTotal").setValue(cartTotal+ productPrice[0]);
                                        }
                                        else{
                                            userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartTotal").setValue(productPrice[0]);

                                        }
                                    }
                                });
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(product_id).child("productName").setValue(snapshot.child("name").getValue().toString());
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(product_id).child("productPrice").setValue(productPrice[0]);
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(product_id).child("productQuantity").setValue(1);
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(product_id).child("productShopname").setValue(shopName);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




                    }
                });

                holder.add_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                long currentQuantity = (long) task.getResult().child("cartProducts").child(product_id).child("productQuantity").getValue();
                                long productPrice = (long) task.getResult().child("cartProducts").child(product_id).child("productPrice").getValue();
                                long subTotal = (long) task.getResult().child("cartTotal").getValue();
                                Log.d("data","long:"+currentQuantity);
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(product_id).child("productQuantity").setValue( currentQuantity +1);
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartTotal").setValue(subTotal+productPrice);

                            }
                        });

                    }
                });
                //remove item from cart
                holder.remove_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                long currentQuantity = (long) task.getResult().child("cartProducts").child(product_id).child("productQuantity").getValue();
                                Log.d("data","long:"+currentQuantity);
                                if(currentQuantity >1){

                                    userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(product_id).child("productQuantity").setValue( currentQuantity -1);
                                }
                                else{
                                    userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(product_id).removeValue();
                                    if(!task.getResult().hasChild("cartProducts")){
                                        userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("DeliveryFee").removeValue();
                                    }
                                }
                                long deliveryfee = (long) task.getResult().child("DeliveryFee").getValue();
                                int count =0;
                                boolean flag = false;
                                for (DataSnapshot dataSnapshot:task.getResult().child("cartProducts").getChildren()){
                                    String listShopname = dataSnapshot.child("productShopname").getValue().toString();
                                    String currentShopname = task.getResult().child("cartProducts").child(product_id).child("productShopname").getValue().toString();

                                    if(listShopname.equals(currentShopname)){
                                        count++;
                                        if(count>1){
                                            flag = true;
                                            break;
                                        }
                                    }

                                }
                                if(!flag){
                                    userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("DeliveryFee").setValue( deliveryfee-30);
                                }
                                long productPrice = 0;
                                long cartTotal = (long) task.getResult().child("cartTotal").getValue();
                                productPrice = (long) task.getResult().child("cartProducts").child(product_id).child("productPrice").getValue();
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartTotal").setValue(cartTotal-productPrice);
                                cartTotal = (long) task.getResult().child("cartTotal").getValue();
                                if(cartTotal == 0){
                                    userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartTotal").removeValue();
                                }


                            }
                        });
                        userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.hasChild("cartProducts")){
                                    userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("DeliveryFee").removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                assert product_id != null;
                productDatabaseReference.child(product_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String productName = (String) snapshot.child("name").getValue().toString();
                        holder.setProductName(productName);
                        String productPrice = (String) snapshot.child("price").getValue().toString();
                        holder.setProductPrice("₹"+productPrice);
                        String productImage = (String) snapshot.child("image").getValue().toString();
                        holder.setProductImage(productImage,ProductsActivity.this);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_product_layout,parent,false);
                return new ProductListViewHolder(view);
            }
        };
        productList_Adapter.startListening();
        productList.setAdapter(productList_Adapter);
    }
    private class ProductListViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout order_manage_btn,cart_layout;
        TextView item_count;
        ConstraintLayout add_to_cart;
        Button add_item,remove_item,view_cart;
        public ProductListViewHolder(@NonNull View itemView) {
            super(itemView);
            order_manage_btn = itemView.findViewById(R.id.order_manage_btn);
            item_count = itemView.findViewById(R.id.item_count);
            add_to_cart = itemView.findViewById(R.id.add_btn);
            add_item = itemView.findViewById(R.id.add_item_btn);
            remove_item = itemView.findViewById(R.id.remove_item_btn);
        }
        public void setProductName(String ProductName)
        {
            TextView product_name=(TextView) itemView.findViewById(R.id.product_name);
            product_name.setText(ProductName);
        }
        public void setProductPrice(String ProductPrice)
        {
            TextView product_price=(TextView) itemView.findViewById(R.id.product_price);
            product_price.setText(ProductPrice);
        }
        public void setProductImage(String productImage, Context context)
        {
            RoundedImageView product_image =(RoundedImageView) itemView.findViewById(R.id.product_image);
            Picasso.get().load(productImage).into(product_image);

        }
    }
}