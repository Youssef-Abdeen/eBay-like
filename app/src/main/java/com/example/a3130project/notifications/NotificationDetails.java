package com.example.a3130project.notifications;


/**
 * This class is to get the notifications when the trade is requested or is accepted
 * Refactoring is done by doing constructor overloading
 */
public class NotificationDetails {
    String notificationType, productId, fromUser;

    NotificationDetails(){
    }

    public NotificationDetails(String notificationType, String productId, String fromUser) {
        this.notificationType = notificationType;
        //this.product = product;
        this.productId = productId;
        this.fromUser = fromUser;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getProductId() {
        return productId;
    }

    public String getFromUser() {
        return fromUser;
    }
}
