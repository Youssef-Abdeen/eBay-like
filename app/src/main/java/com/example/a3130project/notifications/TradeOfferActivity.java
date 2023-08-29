package com.example.a3130project.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3130project.R;
import com.example.a3130project.products.ProductDetailsPage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * To send the offer for a trade to a specific user
 * Refactoring is done by breaking a method into two or more smaller methods so that each of the
   method follows single responsibility principle
 */
public class TradeOfferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_offer);

        Intent intent = getIntent();
        String nameText, categoryText, ImageFile, value, fromUser, productId, notificationId;

        if (intent.getExtras() != null){
            nameText = intent.getExtras().getString("product");
            categoryText = intent.getExtras().getString("category");
            ImageFile = intent.getExtras().getString("imageFile");
            value = intent.getExtras().getString("value");
            fromUser = intent.getExtras().getString("fromUser");
            productId = intent.getExtras().getString("productId");
            notificationId = intent.getExtras().getString("notificationId");
        }
        else {
            nameText = "#########";
            categoryText = "############";
            value = "##########################";
            ImageFile = "####";
            fromUser = "#####";
            productId = "########";
            notificationId = "#######";
        }

        TextView to_name = findViewById(R.id.to_name);
        TextView to_value = findViewById(R.id.to_value);
        TextView to_category = findViewById(R.id.to_category);
        TextView to_fromUser = findViewById(R.id.to_fromUser);

        loadImage(ImageFile);

        to_name.setText(nameText);
        to_value.setText(value);
        to_category.setText(categoryText);
        to_fromUser.setText(fromUser);

        Button to_accept = findViewById(R.id.to_accept);
        to_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResponse("Accepted", fromUser, productId, notificationId);
            }
        });

        Button to_decline = findViewById(R.id.to_decline);
        to_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResponse("Declined", fromUser, productId, notificationId);
            }
        });

        Button to_chat = findViewById(R.id.to_chat);
        to_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToUserChat(fromUser);
            }
        });

    }

    private void loadImage(String imageFile) {
        final ImageView to_image = findViewById(R.id.to_image);

        if (!imageFile.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://project-f3d47.appspot.com/");
            StorageReference storageReference = storage.getReference().child("images/" + imageFile);
            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    to_image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("Image Load Error on Product", exception.getMessage());
                    to_image.setImageResource(R.drawable.placeholder_image);
                }
            });
        } else {
            to_image.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void sendResponse(String response, String toUser, String productId, String notificationId) {
        toUser = toUser.replace(".", ",");

        final String toUserFinal = toUser;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("notifications");
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);
        NotificationDetails notification = new NotificationDetails(response, productId, user);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Trade Product");

        switch (response) {
            case "Accepted":
                builder.setMessage("Confirm accept Trade Offer");
                break;
            case "Declined":
                builder.setMessage("Confirm decline Trade Offer");
                break;
        }

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(toUserFinal).push().setValue(notification);
                removeNotification(response, user, toUserFinal, notificationId, productId);
                switchToNotificationsPage("Trade Requested");
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void switchToNotificationsPage(String Message){
        Toast.makeText(this, Message, Toast.LENGTH_LONG).show();
        Intent postIntent = new Intent(TradeOfferActivity.this, com.example.a3130project.notifications.NotificationsActivity.class);
        startActivity(postIntent);
        finish();
    }

    private void removeNotification(String response, String user, String toUser, String notificationId, String productId){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/");
        database.getReference("notifications").child(user).child(notificationId).removeValue();

        switch (response){
            case "Accepted":
                TradedProduct traded = new TradedProduct(toUser, productId, "Trader");
                database.getReference("Traded").child(user).push().setValue(traded);

                TradedProduct traded2 = new TradedProduct(user, productId, "Receiver");
                database.getReference("Traded").child(toUser).push().setValue(traded2);

                switchToNotificationsPage("Trade Accepted");
                break;

            case "Declined":
                switchToNotificationsPage("Trade Declined");
                break;
        }
    }

    private void switchToUserChat(String otherUser){
        Intent postIntent = new Intent(this, com.example.a3130project.chat.ChatActivity.class);

        postIntent.putExtra("otherUser", otherUser);
        startActivity(postIntent);
        finish();
    }
}