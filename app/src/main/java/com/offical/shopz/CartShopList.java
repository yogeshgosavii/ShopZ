package com.offical.shopz;

import java.util.List;

public class CartShopList {
    public CartShopList(){

    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public List<ProductList> getProductLists() {
        return productLists;
    }

    public void setProductLists(List<ProductList> productLists) {
        this.productLists = productLists;
    }

    public CartShopList(String shopname, List<ProductList> productLists) {
        this.shopname = shopname;
        this.productLists = productLists;
    }

    String shopname;


    private List<ProductList> productLists;
}
