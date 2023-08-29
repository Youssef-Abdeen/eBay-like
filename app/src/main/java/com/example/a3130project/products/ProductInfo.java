package com.example.a3130project.products;


public class ProductInfo {
    String productName, productCategory, productDesc, productImage, user;
    double value, latitude, longitude;

    /**
     * Constructors to assign thevalues to the product fields
     */
    public ProductInfo(){
    }
    public ProductInfo(String productName, String productCategory, String productDesc, String productImage, String user,
                       double value, double latitude, double longitude){
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDesc = productDesc;
        this.productImage = productImage;
        this.user = user;
        this.value = value;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Getters and Setters Method
     */

    public String getProductName() {
        return productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getUser() {
        return user;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getValue() {
        return value;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductImage() {
        return productImage;
    }
}
