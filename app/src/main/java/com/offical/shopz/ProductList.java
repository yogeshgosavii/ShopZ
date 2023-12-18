package com.offical.shopz;

import java.util.List;

public class ProductList {
    public ProductList( )
    {

    }
    public String productPrice;
    public String productName;
    public String productQuantity;

    public ProductList(String productPrice, String productName, String productQuantity) {
        this.productPrice = productPrice;
        this.productName = productName;
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }
}
