package com.example.a3130project.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3130project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * This class is to respond to a trade that has been offered by a user
 * The user has the option to either accept or decline the offer, and the result is then displayed
   on both of the user's end
 * Refactoring is done by breaking a method into two or more smaller methods by following single
   responsibility principle
 */
public class TradeOfferResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_offer_response);

        Intent intent = getIntent();
        String nameText, categoryText, ImageFile, value, fromUser, response, notificationId;

        if (intent.getExtras() != null){
            nameText = intent.getExtras().getString("product");
            categoryText = intent.getExtras().getString("category");
            ImageFile = intent.getExtras().getString("imageFile");
            value = intent.getExtras().getString("value");
            fromUser = intent.getExtras().getString("fromUser");
            response = intent.getExtras().getString("response");
            notificationId = intent.getExtras().getString("notificationId");
        }
        else {
            nameText = "#########";
            categoryText = "############";
            value = "##########################";
            ImageFile = "####";
            fromUser = "#####";
            response = "#######";
            notificationId = "#########";
        }

        TextView tor_name = findViewById(R.id.tor_name);
        TextView tor_value = findViewById(R.id.tor_value);
        TextView tor_category = findViewById(R.id.tor_category);
        TextView tor_fromUser = findViewById(R.id.tor_fromUser);
        TextView tor_response = findViewById(R.id.tor_response);

        loadImage(ImageFile);

        tor_name.setText(nameText);
        tor_value.setText(value);
        tor_category.setText(categoryText);
        tor_fromUser.setText(fromUser);

        if(response.equals("Accepted")) {
            tor_response.setTextColor(Color.rgb(15, 255, 80));
        }
        else {
            tor_response.setTextColor(Color.rgb(255, 0, 0));
        }

        Button tor_mar = findViewById(R.id.tor_mar);

        tor_mar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
                String user = loginInfo.getString("User", null);
                removeNotification(user, notificationId);
            }
        });

        Button tor_chat = findViewById(R.id.tor_chat);
        tor_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToUserChat(fromUser);
            }
        });
    }

    private void loadImage(String imageFile) {
        final ImageView tor_image = findViewById(R.id.tor_image);

        if (!imageFile.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://project-f3d47.appspot.com/");
            StorageReference storageReference = storage.getReference().child("images/" + imageFile);
            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    tor_image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("Image Load Error on Product", exception.getMessage());
                    tor_image.setImageResource(R.drawable.placeholder_image);
                }
            });
        } else {
            tor_image.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void removeNotification(String user, String notificationId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("notifications");
        databaseReference.child(user).child(notificationId).removeValue();

        switchToNotificationsPage("Notification Dismissed");
    }

    private void switchToNotificationsPage(String Message){
        Toast.makeText(this, Message, Toast.LENGTH_LONG).show();
        Intent postIntent = new Intent(TradeOfferResponseActivity.this, com.example.a3130project.notifications.NotificationsActivity.class);
        startActivity(postIntent);
        finish();
    }

    private void switchToUserChat(String otherUser){
        Intent postIntent = new Intent(this, com.example.a3130project.chat.ChatActivity.class);

        postIntent.putExtra("otherUser", otherUser);
        startActivity(postIntent);
        finish();
    }
}