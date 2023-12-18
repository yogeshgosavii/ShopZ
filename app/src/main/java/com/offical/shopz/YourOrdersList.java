package com.offical.shopz;

public class YourOrdersList {
    public YourOrdersList(){

    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public YourOrdersList(String orderId, String orderStatus, String orderTime, String orderTotal) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
        this.orderTotal = orderTotal;
    }

    String orderId;
    String orderStatus;
    String orderTime;
    String orderTotal;
}
