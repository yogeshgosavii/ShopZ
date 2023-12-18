package com.offical.shopz;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseError;

import java.util.List;

public class FirebaseViewModel extends ViewModel implements CartActivity.OnRealTimeTaskCompelete{

    public MutableLiveData<List<com.offical.shopz.CartShopList>> getCartShopList() {
        return CartShopList;
    }

    public void setCartShopList(MutableLiveData<List<com.offical.shopz.CartShopList>> cartShopList) {
        CartShopList = cartShopList;
    }

    public MutableLiveData<DatabaseError> getDatabaseError() {
        return databaseError;
    }

    public void setDatabaseError(MutableLiveData<DatabaseError> databaseError) {
        this.databaseError = databaseError;
    }

    private MutableLiveData<List<CartShopList>> CartShopList = new MutableLiveData<>();
    private MutableLiveData<DatabaseError> databaseError = new MutableLiveData<>();
    private CartActivity cartActivity;
    FirebaseViewModel(){
        cartActivity = new CartActivity();
    }
    public  void getShopList(){
        cartActivity.getShopList();
    }
    @Override
    public void onSuccess(List<com.offical.shopz.CartShopList> cartShopLists) {
        CartShopList.setValue(cartShopLists);
    }

    @Override
    public void onFailure(DatabaseError error) {
        databaseError.setValue(error);
    }
}
