package com.offical.shopz;

public class ShopList {
    ShopList(){

    }

    public ShopList(String shopName, String shopAddress, String shopContact, String shopImage) {
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopContact = shopContact;
        this.shopImage = shopImage;
    }

    public String shopName;
    public String shopAddress;
    public String shopContact;
    public String shopImage;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopContact() {
        return shopContact;
    }

    public void setShopContact(String shopContact) {
        this.shopContact = shopContact;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

}
