package com.offical.shopz;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    public ArrayList<CartProductList> getProductLists() {
        return productLists;
    }

    public void setProductLists(ArrayList<CartProductList> productLists) {
        this.productLists = productLists;

        this.productLists.removeAll(Collections.singleton(null));
    }

    public ArrayList<CartShopList> getCartShopLists() {
        return cartShopLists;
    }

    public void setCartShopLists(ArrayList<CartShopList> cartShopLists) {
        this.cartShopLists = cartShopLists;
    }

    private Activity activity;
    ArrayList<CartProductList> productLists;
    ArrayList<CartShopList> cartShopLists;

    public CartAdapter() {
        this.activity = activity;
        this.productLists = productLists;
        this.cartShopLists = cartShopLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_cart_products,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartProductList cartProductList = productLists.get(position);
        holder.productPrice.setText(cartProductList.productPrice);
        holder.productName.setText(cartProductList.productName);
        holder.productQuantity.setText(cartProductList.productQuantity);



    }

    @Override
    public int getItemCount() {
        return productLists.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView productName,productPrice,productQuantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productQuantity = itemView.findViewById(R.id.item_count);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }
}
