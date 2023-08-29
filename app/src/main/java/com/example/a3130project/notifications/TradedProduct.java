package com.example.a3130project.notifications;

/**
 * Normal getters and setters
 */
public class TradedProduct {
    String userTraded, productId, tradeType;

    public TradedProduct() {

    }

    public TradedProduct(String user, String productId, String tradeType) {
        this.userTraded = user;
        this.productId = productId;
        this.tradeType = tradeType;
    }

    public String getUserTraded() {
        return userTraded;
    }

    public String getTradeType() {
        return tradeType;
    }

    public String getProductId() {
        return productId;
    }

}
