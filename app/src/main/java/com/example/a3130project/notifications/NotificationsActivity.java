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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a3130project.R;
import com.example.a3130project.products.ProductInfo;
import com.example.a3130project.products.ProductsPage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * This class is to get the notifications tab in the form of a card
 * The card is formed on both the buyer and the seller's end but with appropriate toast messages
 * When the trade is accepted or rejected, then the notification is removed automatically
 * Refactoring is done by marking a notification as "Mark as read" to remove the specific card from
   the notifications tab
 */
public class NotificationsActivity extends AppCompatActivity {
    String count;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private String imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        SharedPreferences loginInfo = getSharedPreferences("temp_values", Context.MODE_PRIVATE);
        count = loginInfo.getString("NotificationsCount", null);

        if(count == null) {
            count = "0";
        }

        TextView notificationBox = findViewById(R.id.notification_box);
        notificationBox.setText(String.format("You have %s notifications", count));

        checkNotifications();


        LinearLayout linearLayout = findViewById(R.id.notificationsContainer);
        linearLayout.removeAllViews();
        addCards();
    }

    public void addCards(){
        LinearLayout linearLayout = findViewById(R.id.notificationsContainer);
        linearLayout.removeAllViews();

        View card = getLayoutInflater().inflate(R.layout.notification_card, null);

        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);

        databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("notifications");


        databaseReference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String notificationId = dataSnapshot.getKey();

                    NotificationDetails notification;
                    notification = dataSnapshot.getValue(NotificationDetails.class);

                    getNotification(notification.getFromUser(),
                            notification.getNotificationType(),
                            notification.getProductId(),
                            notificationId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNotification(String fromUser, String notificationType, String productId, String notificationId) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("products");
        dbRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //DataSnapshot dataSnapshot = snapshot;
                    ProductInfo product;
                    product = snapshot.getValue(ProductInfo.class);

                    addProduct(product.getProductName(),
                            product.getProductCategory(),
                            product.getProductImage(),
                            product.getProductDesc(),
                            product.getValue(),
                            fromUser,
                            productId,
                            notificationType,
                            notificationId);

                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addProduct(String nameText, String categoryText, String imageFile, String descText, double value,
                            String fromUser, String productId, String notificationType, String notificationId){

        String imageLoc = "images/" + imageFile;
        storageReference = FirebaseStorage.getInstance("gs://project-f3d47.appspot.com/").getReference(imageLoc);

        View card = getLayoutInflater().inflate(R.layout.notification_card, null);
        ImageView image = card.findViewById(R.id.notificationImage);
        TextView user = card.findViewById(R.id.fromUser);
        TextView type = card.findViewById(R.id.notificationType);
        TextView product = card.findViewById(R.id.notificationProduct);

        LinearLayout scrollContainer = findViewById(R.id.notificationsContainer);
        Button showNotification = card.findViewById(R.id.showNotification);


        storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                image.setImageResource(R.drawable.placeholder_image);
            }
        });

        showNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notificationType.equals("Trade")) {
                    switchToTradeOffer(nameText, categoryText, imageFile, value, fromUser, productId, notificationId);
                }
                else {
                    switchToTradeOfferResponse(nameText, categoryText, notificationType, imageFile, value, fromUser, notificationId);
                }
            }
        });

        user.setText(fromUser);

        switch (notificationType) {
            case "Trade":
                type.setText("Wants to Trade");
                break;
            case "Accepted":
                type.setText("Accepted Trade");
                type.setTextColor(Color.rgb(15, 255, 80));
                break;
            case "Declined":
                type.setText("Declined Trade");
                type.setTextColor(Color.rgb(255, 0, 0));
                break;
        }

        product.setText(nameText);
        scrollContainer.addView(card);
    }

    @Override
    public void onBackPressed(){
        switchToLanding();
    }

    private void switchToLanding(){
        Intent postIntent = new Intent(NotificationsActivity.this, com.example.a3130project.LenoraUS5.LandingPage.class);
        startActivity(postIntent);
    }

    private void switchToProductDetailsPage(String nameText, String categoryText, String descText, String ImageFile, double value){
        Intent postIntent = new Intent(NotificationsActivity.this, com.example.a3130project.products.ProductDetailsPage.class);

        postIntent.putExtra("nameText", nameText);
        postIntent.putExtra("categoryText", categoryText);
        postIntent.putExtra("descText", descText);
        postIntent.putExtra("ImageFile", ImageFile);
        postIntent.putExtra("productValue", Double.toString(value));
        startActivity(postIntent);
        //finish();
    }

    private void switchToTradeOffer(String product, String category, String imageFile, double value,
                                    String fromUser, String productId, String notificationId) {
        Intent postIntent = new Intent(NotificationsActivity.this, com.example.a3130project.notifications.TradeOfferActivity.class);

        postIntent.putExtra("product", product);
        postIntent.putExtra("category", category);
        postIntent.putExtra("imageFile", imageFile);
        postIntent.putExtra("value", Double.toString(value));
        postIntent.putExtra("fromUser", fromUser);
        postIntent.putExtra("productId", productId);
        postIntent.putExtra("notificationId", notificationId);

        startActivity(postIntent);
        finish();
    }

    private void switchToTradeOfferResponse(String product, String category, String response, String imageFile,
                                            double value, String fromUser, String notificationId) {
        Intent postIntent = new Intent(NotificationsActivity.this, com.example.a3130project.notifications.TradeOfferResponseActivity.class);

        postIntent.putExtra("product", product);
        postIntent.putExtra("category", category);
        postIntent.putExtra("response", response);
        postIntent.putExtra("imageFile", imageFile);
        postIntent.putExtra("value", Double.toString(value));
        postIntent.putExtra("fromUser", fromUser);
        postIntent.putExtra("notificationId", notificationId);


        startActivity(postIntent);
        finish();
    }

    private void checkNotifications() {
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
                editor.putString("NotificationsCount", Long.toString(c));
                editor.apply();

                count = Long.toString(c);

                TextView notificationBox = findViewById(R.id.notification_box);
                notificationBox.setText(String.format("You have %s notifications", count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }
}