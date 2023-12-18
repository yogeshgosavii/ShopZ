package com.offical.shopz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView bill_items;
    TextView bill_subtotal,deliveryFee,bill_total;

    TextView username,usernumber,useraddress;
    FirebaseAuth auth;
    FirebaseViewModel firebaseViewModel;
    private OnRealTimeTaskCompelete realTimeTaskCompelete;
    private DatabaseReference cartshopDatabaseReference,cartproductDatabaseReference ;
    private DatabaseReference userDatabaseReference;
    String shopName;
    String shop_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        shop_id=getIntent().getStringExtra("shop_id");
        shopName=getIntent().getStringExtra("shop_name");
        Log.d("TAG", "shopid:"+shop_id+"  shopname:"+shopName);
        bill_items =findViewById(R.id.bill_items);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();

        LinearLayoutManager shopListLayoutManager = new LinearLayoutManager(CartActivity.this);
        bill_items.setLayoutManager(shopListLayoutManager);
        bill_items.setHasFixedSize(true);
        bill_subtotal = findViewById(R.id.subtotal_amount);
        deliveryFee = findViewById(R.id.dilevery_amount);
        bill_total = findViewById(R.id.total_amount);
        TextView user_info_error = findViewById(R.id.user_info_error);
        cartshopDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cart");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Button payBill = findViewById(R.id.cart_paybill);
        //to proceed to bill payment
        payBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        if (snapshot.child("Location").hasChild("address")){
                            if(!useraddress.getText().equals("")){
                                String bill_id = userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("yourOrders").push().getKey();
                                userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("yourOrders").child(bill_id).child("orderStatus").setValue("Processing");
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM d','H:mm a");
                                userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("yourOrders").child(bill_id).child("orderTime").setValue(sdf.format(new Date()));
                                userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("yourOrders").child(bill_id).child("orderTotal").setValue(bill_total.getText());
                                cartshopDatabaseReference.child("cartProducts").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot s) {
                                        String shopname="";
                                        for (DataSnapshot snapshot1 : s.getChildren()){
                                            userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("yourOrders").child(bill_id).child("orderProducts").child(snapshot1.getKey()).setValue(snapshot1.getValue());
                                        }
                                        userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("yourOrders").child(bill_id).child("orderId").setValue("#"+bill_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                cartshopDatabaseReference.removeValue();
                                                Intent intent = new Intent(CartActivity.this, YourOrdersActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                                    }
                                });
                            }
                            else{
                                user_info_error.setVisibility(View.VISIBLE);

                            }


                        }
                        else{
                            user_info_error.setVisibility(View.VISIBLE);
                        }

                    }
                });

            }
        });
        RoundedImageView edit_address = findViewById(R.id.edit_address);
        edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this,AddressActivity.class);
                startActivity(intent);
            }
        });
        username = findViewById(R.id.user_name);
        usernumber = findViewById(R.id.user_number);
        useraddress = findViewById(R.id.user_address);
        userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                username.setText(task.getResult().child("username").getValue().toString()+",");
                usernumber.setText(task.getResult().child("number").getValue().toString());
                if (task.getResult().child("Location").hasChild("address")){
                    useraddress.setText(task.getResult().child("Location").child("address").getValue().toString());
                }
                else{
                    useraddress.setText("No address found");

                }

            }
        });
        userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username.setText(snapshot.child("username").getValue().toString()+",");
                usernumber.setText(snapshot.child("number").getValue().toString());
                if (snapshot.child("Location").hasChild("address")){
                    useraddress.setText(snapshot.child("Location").child("address").getValue().toString());
                }
                else{
                    useraddress.setText("No address found");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = cartshopDatabaseReference.child("cartProducts");
        FirebaseRecyclerOptions<CartShopList> options = new FirebaseRecyclerOptions.Builder<CartShopList>()
                .setQuery(query, CartShopList.class)
                .build();

        FirebaseRecyclerAdapter<CartShopList,CartProductListViewHolder> shopListCartAdapter = new FirebaseRecyclerAdapter<CartShopList, CartProductListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartProductListViewHolder holder, int position, @NonNull CartShopList model) {
                cartshopDatabaseReference.child("cartProducts").keepSynced(true);
                final long[] subtotalAmount = {0};
                final String cart_product_id = getRef(position).getKey();
                Log.d("data",cart_product_id);

                DatabaseReference shopnameDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cart");

                shopnameDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("data","productid:"+cart_product_id);
                        if (snapshot.child("cartProducts").hasChild(cart_product_id)){
                            String cartShopName = null;
                            if(snapshot.child("cartProducts").child(cart_product_id).hasChild("productShopname")){
                                cartShopName = (String) snapshot.child("cartProducts").child(cart_product_id).child("productShopname").getValue().toString();
                            }
                            holder.setCartShopName(cartShopName);
                            String productname = snapshot.child("cartProducts").child(cart_product_id).child("productName").getValue().toString();
                            if(productname.length()>20){
                                productname = productname.substring(0,20)+"...";
                            }
                            holder.setProductName(productname);
                            long productPrice  = 0;
                            long productQuantity = 0;

                            if(snapshot.child("cartProducts").child(cart_product_id).hasChild("productPrice")){
                                productPrice = (long) snapshot.child("cartProducts").child(cart_product_id).child("productPrice").getValue();
                            }
                            if(snapshot.child("cartProducts").child(cart_product_id).hasChild("productQuantity")){
                                productQuantity = (long)snapshot.child("cartProducts").child(cart_product_id).child("productQuantity").getValue();
                            }
                            long subtotalAmount = 0;
                            if(snapshot.hasChild("cartTotal")){

                                subtotalAmount = (long) snapshot.child("cartTotal").getValue();
                                Log.d("TAG", "cartTotal:"+subtotalAmount);
                            }

                            holder.setProductQuantity(String.valueOf(productQuantity));
                            holder.setAllProductPrice("₹"+(productPrice*productQuantity));
                            holder.setProductPrice("₹"+productPrice);
                            bill_subtotal.setText("₹"+ subtotalAmount);
                            long deliveryfee = 0;
                            if(snapshot.hasChild("DeliveryFee")){
                                deliveryfee = (long) snapshot.child("DeliveryFee").getValue();
                            }
                            deliveryFee.setText("₹"+deliveryfee);
                            bill_total.setText("₹"+(deliveryfee+ subtotalAmount));

                        }

                        holder.cartAddItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        long currentQuantity = (long) task.getResult().child("cartProducts").child(cart_product_id).child("productQuantity").getValue();
                                        Log.d("data","long:"+currentQuantity);
                                        userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(cart_product_id).child("productQuantity").setValue( currentQuantity +1);
                                        long subTotal = 0;
                                        long productPrice = 0;
                                        if(task.getResult().child("cartProducts").child(cart_product_id).hasChild("productPrice")){
                                            productPrice = (long) task.getResult().child("cartProducts").child(cart_product_id).child("productPrice").getValue();
                                        }
                                        if(task.getResult().hasChild("cartTotal")){
                                            subTotal = (long) task.getResult().child("cartTotal").getValue();
                                            userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartTotal").setValue(subTotal+productPrice);
                                        }


                                    }
                                });
                            }
                        });
                        holder.cartRemoveItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        long currentQuantity = (long) task.getResult().child("cartProducts").child(cart_product_id).child("productQuantity").getValue();
                                        long deliveryfee = (long) task.getResult().child("DeliveryFee").getValue();
                                        Log.d("data","long:"+currentQuantity);
                                        int count =0;
                                        boolean flag = false;
                                        for (DataSnapshot dataSnapshot:task.getResult().child("cartProducts").getChildren()){
                                            String listShopname = dataSnapshot.child("productShopname").getValue().toString();
                                            String currentShopname = task.getResult().child("cartProducts").child(cart_product_id).child("productShopname").getValue().toString();

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
                                        if(currentQuantity >1){
                                            userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(cart_product_id).child("productQuantity").setValue( currentQuantity -1);
                                            userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(cart_product_id).child("productQuantity").setValue( currentQuantity -1);


                                        }
                                        else{
                                            userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartProducts").child(cart_product_id).removeValue();
                                        }
                                        long productPrice;
                                        long cartTotal = (long) task.getResult().child("cartTotal").getValue();
                                        productPrice = (long) task.getResult().child("cartProducts").child(cart_product_id).child("productPrice").getValue();
                                        userDatabaseReference.child(auth.getCurrentUser().getUid()).child("cart").child("cartTotal").setValue(cartTotal-productPrice);
                                        DatabaseReference emptyCart = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cart");
                                        emptyCart.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                            @Override
                                            public void onSuccess(DataSnapshot snapshot) {
                                                if (snapshot.child("cartTotal").getValue().toString().equals("0")){
                                                    Intent intent = new Intent(CartActivity.this,ProductsActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("shop_name",shopName);
                                                    intent.putExtra("shop_id",shop_id);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                                                    finish();
                                                }
                                            }
                                        });

                                    }
                                });

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                shopnameDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public CartProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_cart_products,parent,false);
                return new CartProductListViewHolder(view);
            }
        };
        shopListCartAdapter.startListening();
        bill_items.setAdapter(shopListCartAdapter);
    }
    private class CartProductListViewHolder extends RecyclerView.ViewHolder{
        Button cartRemoveItem,cartAddItem;
        public CartProductListViewHolder(@NonNull View itemView) {
            super(itemView);
            cartRemoveItem = itemView.findViewById(R.id.cart_remove_item_btn);
            cartAddItem = itemView.findViewById(R.id.cart_add_item_btn);
        }
        public void setProductName(String productName) {
            TextView product_name = itemView.findViewById(R.id.product_name);
            product_name.setText(productName);
        }
        public void setProductQuantity(String productQuantity) {
            TextView product_quantity = itemView.findViewById(R.id.cart_item_count);
            product_quantity.setText(productQuantity);
        }
        public void setProductPrice(String productPrice) {
            TextView single_product_amount = itemView.findViewById(R.id.single_product_amount);
            single_product_amount.setText(productPrice);
        }
        public void setAllProductPrice(String productAllPrice) {
            TextView all_product_amount = itemView.findViewById(R.id.all_product_amount);
            all_product_amount.setText(productAllPrice);
        }
        public void setCartShopName(String cartShopname){
            TextView cartshopname = itemView.findViewById(R.id.single_product_shopname);
            cartshopname.setText(cartShopname);
        }
    }

    @Override
    public void onBackPressed() {
        Intent shop_intent = new Intent(CartActivity.this, ProductsActivity.class);
        shop_intent.putExtra("shop_name",shopName);
        shop_intent.putExtra("shop_id",shop_id);
        startActivity(shop_intent);
        finish();
        super.onBackPressed();
    }

    public void getShopList(){
        cartshopDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CartShopList> cartShopLists = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CartShopList cartShopList = new CartShopList();
                    cartShopList.setShopname(dataSnapshot.child("sname").getValue(String.class));

                    GenericTypeIndicator<ArrayList<ProductList>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<ProductList>>() {
                    };
                    cartShopList.setProductLists(dataSnapshot.child("Products").getValue(arrayListGenericTypeIndicator));
                    cartShopLists.add(cartShopList);
                    realTimeTaskCompelete.onSuccess(cartShopLists);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                realTimeTaskCompelete.onFailure(error);

            }
        });
    }

    public interface OnRealTimeTaskCompelete{
        void onSuccess(List<CartShopList> cartShopLists);
        void onFailure(DatabaseError error);
    }

}