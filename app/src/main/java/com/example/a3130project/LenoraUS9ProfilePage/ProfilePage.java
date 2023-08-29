package com.example.a3130project.LenoraUS9ProfilePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3130project.LenoraUS5.LandingPage;
import com.example.a3130project.LenoraUS5.ProfileActivity;
import com.example.a3130project.R;
import com.example.a3130project.notifications.TradedProduct;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This page displays the user's first name, last name and email id
 * This page also displays the user's overall rating
 * This page also displays the total trade in and trade out value the user has done overall
 * Refactoring is done by fixing the issue in the total trade in value
 */
public class ProfilePage extends AppCompatActivity{
    TextView displayUserName;
    TextView displayName;
    String currRating;
    String listItems;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
    long numChildren;
    int goodCount = 1;

    double tradeInValue = 0, tradeOutValue = 0;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        TextView displayUserName = findViewById(R.id.displayUserName);
        TextView fullName = findViewById(R.id.fullName);
        Button getRating = findViewById(R.id.getRating);

        SharedPreferences loginInfo = getSharedPreferences("login_info",Context.MODE_PRIVATE);
        String User = loginInfo.getString("User", null);
        String fullname = loginInfo.getString("FullName", null);

        displayUserName.setText(User);
        fullName.setText(fullname);

        createNotificationChannel();
        //testing
        System.out.println(goodCount);

        /*dbRef.child("clubs")
                .orderByChild("name")
                .equalTo("efg")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            String clubkey = childSnapshot.getKey();*/



        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numChildren = snapshot.getChildrenCount();
                goodCount = (int)numChildren;
                //displaying on console but not in notifications
                System.out.println(numChildren);
                System.out.println(goodCount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listItems = "Error";
            }
        });

        //Display Users Info on Screen
        //TODO
        //Good way to clean up code - Dave
        initializeFields();
        getTradeValues();
        //making our buttons functional
        Button goToLogoutPage = (Button) findViewById(R.id.goToLogOutPageButton);
        Button goToLandingPage = (Button) findViewById(R.id.goToLandingPageButton);
        Button checkGoodsNearMe = (Button) findViewById(R.id.notifGood);
        //If there are goods near us, change the listItems string value
        //using a dummy value for now until the database of items is ready
        //numChildren = 7;
        if(goodCount != 0){
            listItems = (goodCount+" products found!");
        }
        else{
            listItems = "None found :(";
        }
        //Notification - How it will appear in notification centre (pull down)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "US7")
                .setSmallIcon(R.drawable.baseline_add_alert_24)
                .setContentTitle("Products Near You:")
                .setContentText(listItems)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        //When we click the button, we will get a notification listing items near us
        checkGoodsNearMe.setOnClickListener(v -> {
            notificationManager.notify(100, builder.build());
        });

        /**
         * Switch To Page(s)
         * Calls the appropriate methods to switch to requested page after pressing button.
         *
         * @author Lenora Tairova
         */

        goToLogoutPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){switchToLogOutPage();}
        });

        goToLandingPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){switchToLandingPage();}
        });

        getRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rate = currentRating(User);
            }
        });


    }

    private String currentRating(String username) {
        DatabaseReference usersRating = dbRef.child(username).child("rating");
        usersRating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currRating = snapshot.getValue(String.class);
                Toast.makeText(ProfilePage.this, currRating, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return currRating;
    }


    /**
     * Switch to Page(s) Intents
     *
     * upon button being bushed, the appropriate methods are called respective to
     * button functionality (post button pressed -> go to post page)
     *
     * @author Lenora Tairova
     */


    protected void switchToLogOutPage(){
        Intent logoutIntent = new Intent(ProfilePage.this, ProfileActivity.class);
        startActivity(logoutIntent);
        finish();
    }
    protected void switchToLandingPage(){
        Intent landingIntent = new Intent(ProfilePage.this, LandingPage.class);
        startActivity(landingIntent);
        finish();
    }
    @Override
    public void onBackPressed(){
        switchToLandingPage();
    }

    //Method to initialize user info
    @SuppressLint("WrongViewCast")
    private void initializeFields(){
        displayUserName = (TextView) findViewById(R.id.displayUserName);
        displayName = (TextView) findViewById(R.id.fullName);
    }

    private void getTradeValues(){
        TextView tradeinvalue = findViewById(R.id.tradeinvalue);
        TextView tradeoutvalue = findViewById(R.id.tradeoutvalue);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("Traded");
        DatabaseReference prodReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("products");
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);

        databaseReference.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                long i = 0;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    i += 1;
                    TradedProduct traded;
                    traded = dataSnapshot.getValue(TradedProduct.class);

                    String productId = traded.getProductId();
                    String tradeType = traded.getTradeType();

                    if(i==count) {
                        getProductValue(productId, tradeType, 1);
                    }
                    else {
                        getProductValue(productId, tradeType, 0);
                    }


                }
                tradeinvalue.setText(Double.toString(tradeInValue));
                tradeoutvalue.setText(Double.toString(tradeOutValue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProductValue(String productId, String tradeType, int setFlag) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("products");

        databaseReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double value = snapshot.child("value").getValue(Double.class);
                //System.out.println("okkkkkkkkk" + value[0]);

                switch (tradeType){
                    case "Trader" :
                        tradeOutValue += value;
                        //System.out.println("innnnnnnnnnnnnnn "+ tradeOutValue);
                        break;
                    case "Receiver" :
                        tradeInValue += value;
                        //System.out.println("outttttttttttttt "+ tradeInValue);
                        break;
                }

                TextView tradeinvalue = findViewById(R.id.tradeinvalue);
                TextView tradeoutvalue = findViewById(R.id.tradeoutvalue);

                if(setFlag == 1){
                    tradeinvalue.setText(Double.toString(tradeInValue));
                    tradeoutvalue.setText(Double.toString(tradeOutValue));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Create channel for notifications
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notifChannel";
            String description = "Channel for notifications if a good is found in the area";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("US7", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}