package com.example.a3130project.registration;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class User implements Serializable {

    public static final String TAG = "User";
    private String fname;
    private String lname;
    private String email;
    private String username;
    private String pass;
    private String address;
    private String dob;
    private double rating = 0.0;

    public User(){

    }

    public User(String fname, String lname, String email, String username, String pass, String address, String dob, double rating){
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.username = username;
        this.pass = pass;
        this.address = address;
        this.dob = dob;
        this.rating = rating;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return pass;
    }

    public void setPassword(String pass) {
        this.pass = pass;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
