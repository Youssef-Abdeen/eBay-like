package com.example.a3130project.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3130project.LenoraUS5.PostPage;
import com.example.a3130project.R;
import com.example.a3130project.notifications.NotificationDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetailsPage extends AppCompatActivity {

    /**
     * Method to display the details of the product that has been posted
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_page);
        Intent intent = getIntent();
        String nameText, categoryText, descText, ImageFile, productId, productUser;
        double valueNum;

        if (intent.getExtras() != null){
            nameText = intent.getExtras().getString("nameText");
            categoryText = intent.getExtras().getString("categoryText");
            descText = intent.getExtras().getString("descText");
            ImageFile = intent.getExtras().getString("ImageFile");
            valueNum = Double.parseDouble(intent.getExtras().getString("productValue"));
            productId = intent.getExtras().getString("productId");
            productUser = intent.getExtras().getString("productUser");
        }
        else{
            nameText = "#########";
            categoryText = "############";
            descText = "##########################";
            ImageFile = "";
            valueNum = -999.99;
            productId = "##########";
            productUser = "###########";
        }

        final TextView pdp_name = findViewById(R.id.pdp_name);
        final TextView pdp_cat = findViewById(R.id.pdp_category);
        final TextView pdp_desc = findViewById(R.id.pdp_desc);

        final TextView pdp_value = findViewById(R.id.pdp_value);

        pdp_name.setText(nameText);
        pdp_cat.setText(categoryText);
        pdp_desc.setText(descText);
        pdp_value.setText(Double.toString(valueNum));

        Button trade = findViewById(R.id.pdp_trade);
        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tradeProduct(productId, productUser);
            }
        });

        Button userProfile = findViewById(R.id.pdp_userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserProfile(productUser);
            }
        });


        loadImage(ImageFile);
    }

    /**
     * Loads the image from Firebase Storage and sets it to the ImageView
     *
     * @param imageFile The name of the image file in Firebase Storage
     */
    private void loadImage(String imageFile) {
        final ImageView pdp_image = findViewById(R.id.pdp_image);

        if (!imageFile.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://project-f3d47.appspot.com/");
            StorageReference storageReference = storage.getReference().child("images/" + imageFile);
            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    pdp_image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("Image Load Error on Product", exception.getMessage());
                    pdp_image.setImageResource(R.drawable.placeholder_image);
                }
            });
        } else {
            pdp_image.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void tradeProduct(String productId, String toUser) {
        toUser = toUser.replace(".", ",");

        final String toUserFinal = toUser;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("notifications");
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);
        NotificationDetails notification = new NotificationDetails("Trade", productId, user);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Trade Product");
        builder.setMessage("Do you confirm?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(toUserFinal).push().setValue(notification);
                switchToProductsPage();
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

    private void switchToProductsPage(){
        Toast.makeText(this, "Trade Requested", Toast.LENGTH_LONG).show();
        Intent postIntent = new Intent(ProductDetailsPage.this, com.example.a3130project.products.ProductsPage.class);
        startActivity(postIntent);
        finish();
    }

    private void goToUserProfile(String productUser) {
        Intent postIntent = new Intent(ProductDetailsPage.this, com.example.a3130project.profile.UserProfile.class);

        postIntent.putExtra("productUser", productUser);

        startActivity(postIntent);
    }

}