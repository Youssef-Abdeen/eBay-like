package com.example.a3130project.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3130project.R;
import com.example.a3130project.chat.ChatUsersActivity;
import com.example.a3130project.registration.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {
    String username;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            username = intent.getExtras().getString("productUser");
        }
        Button profileChat = findViewById(R.id.profileChat);
        Button updateRating = findViewById(R.id.updateRating);
        final RatingBar profileStarRating = findViewById(R.id.profileStarRating);
        profileChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToUserChat(username);
            }
        });

        setFields(username);

        updateRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.child(username).child("rating").setValue(String.valueOf(profileStarRating.getRating()));
                String newRating = "New rating : " + String.valueOf(profileStarRating.getRating());
                Toast.makeText(UserProfile.this, newRating, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setFields(String username){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("users");
        TextView profileUsername = findViewById(R.id.profileUsername);
        TextView profileFullName = findViewById(R.id.profileFullName);
        TextView profileEmail = findViewById(R.id.profileEmail);


        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String fname = dataSnapshot.child("fname").getValue(String.class);
                    String lname = dataSnapshot.child("lname").getValue(String.class);
                    String fullname = fname +" "+lname;
                    String email = dataSnapshot.child("email").getValue(String.class);

                    profileUsername.setText(username);
                    profileFullName.setText(fullname);
                    profileEmail.setText(email);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //on cancelled, nothing happens
            }
        });
    }

    private void switchToUserChat(String otherUser){
        Intent postIntent = new Intent(this, com.example.a3130project.chat.ChatActivity.class);

        postIntent.putExtra("otherUser", otherUser);
        startActivity(postIntent);
        finish();
    }


}