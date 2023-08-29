package com.example.a3130project.LenoraUS5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.a3130project.LenoraUS9ProfilePage.ProfilePage;
import com.example.a3130project.R;
import com.example.a3130project.chat.ChatUsersActivity;
import com.example.a3130project.search.SearchPage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LandingPage extends AppCompatActivity {

    private String count = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        //here we add the textview welcome to change to
        //Welcome User!
        //this will be implemented later once we get firebase figured

        /**
         * Switch To Page(s)
         * Calls the appropriate methods to switch to requested page after pressing button.
         *
         * @author Lenora Tairova
         */

        SharedPreferences loginInfo = getSharedPreferences("temp_values", Context.MODE_PRIVATE);
        count = loginInfo.getString("NotificationsCount", null);

        if(count == null) {
            count = "0";
        }

        checkNotifications();

        Button notificationsBar = (Button) findViewById(R.id.notificationsButton);
        notificationsBar.setText(String.format("you have %s notifications", count));
        notificationsBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToNotificationsPage();
            }
        });

        ImageButton goToSearchPage = (ImageButton) findViewById(R.id.searchButton);
        goToSearchPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSearchPage();
            }
        });

        // Go to ChatPage
        ImageButton goToChatPage = (ImageButton) findViewById(R.id.chatButton);
        goToChatPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToChatPage();
            }
        });

        ImageButton goToPostPage = (ImageButton) findViewById(R.id.postButton);
        goToPostPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToPostPage();
            }
        });

        //Go to MapsPage
        ImageButton goToMapsPage = (ImageButton) findViewById(R.id.mapButton);
        goToMapsPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){switchToMapsPage();}
        });

        ImageButton goToProfilePage = (ImageButton) findViewById(R.id.profileButton);
        goToProfilePage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){switchToProfilePage();}
        });
    }
    /**
     * Switch to Page(s) Intents
     *
     * upon button being bushed, the appropriate methods are called respective to
     * button functionality (post button pressed -> go to post page)
     *
     * @author Lenora Tairova
     */

    //Switch to SearchPage
    protected void switchToSearchPage(){
        Intent searchIntent = new Intent(LandingPage.this, SearchPage.class);
        startActivity(searchIntent);
        finish();
    }

    //Switch to PostPage
    protected void switchToPostPage(){
        Intent postIntent = new Intent(LandingPage.this, com.example.a3130project.products.ProductsPage.class);
        startActivity(postIntent);
        finish();
    }

    //Switch to MapsPage
    protected void switchToMapsPage(){
        Intent mapsIntent = new Intent(LandingPage.this, MapsActivity.class);
        startActivity(mapsIntent);
        finish();
    }

    protected void switchToProfilePage(){
        Intent profileIntent = new Intent(LandingPage.this, ProfilePage.class);
        startActivity(profileIntent);
        finish();
    }

    //Switch to ChatPage
    protected void switchToChatPage(){
        Intent chatIntent = new Intent(LandingPage.this, ChatUsersActivity.class);
        startActivity(chatIntent);
        finish();
    }

    protected void switchToNotificationsPage(){
        Intent chatIntent = new Intent(LandingPage.this, com.example.a3130project.notifications.NotificationsActivity.class);
        startActivity(chatIntent);
        finish();
    }

    private void checkNotifications(){
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);
        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("notifications");
        dbRef.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long c = snapshot.getChildrenCount();

                //System.out.println("dfdfssfdfsfd "+c);

                SharedPreferences.Editor editor;
                editor = getSharedPreferences("temp_values", Context.MODE_PRIVATE).edit();
                editor.putString("NotificationsCount",Long.toString(c));
                editor.apply();

                count = Long.toString(c);

                Button notificationsBar = (Button) findViewById(R.id.notificationsButton);
                notificationsBar.setText(String.format("you have %s notifications", count));

                /*for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String c = dataSnapshot.getValue(String.class);

                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

            });

    }

}