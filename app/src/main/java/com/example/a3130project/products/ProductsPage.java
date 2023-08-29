package com.example.a3130project.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.a3130project.LenoraUS5.PostPage;
import com.example.a3130project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ProductsPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    StorageReference storageReference;
    DatabaseReference databaseReference;
    public Bitmap bitmap;
    String categoryValue;

    /**
     * This method is to have the landing page for the Add posts page.
     * This method also is to create the Upload and show my products button
     * Here we also connect to the database to store the items info in the firebase
     *
     * Code Refactored by fixing the bug on the items page
     * Code Refactored by breaking the database into two different databases, one to store the images
     * and one to store the items info
     *
     * Please note: The code will crash if you do not provide an image of the product while posting an item.
     * Please do post the image also otherwise the item would not be uploaded
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_page);

        Spinner spinner = (Spinner) findViewById(R.id.categoryFilter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container);
        linearLayout.removeAllViews();
        addCards();

        final Button uploadButton = (Button) findViewById(R.id.uploadButton);
        final Button addButton = (Button) findViewById(R.id.show);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( ProductsPage.this, PostPage.class);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addCards();
            }
        });
    }

    public void addCards(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container);
        linearLayout.removeAllViews();

        CheckBox checkBox = findViewById(R.id.myPostsCheckbox);

        View card = getLayoutInflater().inflate(R.layout.product_card, null);

        final String username;
        if (checkBox.isChecked()) {
            username = getCurrentUser();
        }
        else {
            username = "";
        }

        databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ProductInfo product;
                    product = dataSnapshot.getValue(ProductInfo.class);

                    String productId = dataSnapshot.getKey();

                    if (!Objects.equals(username, ""))
                    {
                        if(!Objects.equals(username, product.getUser()))
                        {
                            continue;
                        }
                    }

                    if (Objects.equals(categoryValue,"All") || Objects.equals(product.getProductCategory(), categoryValue)) {
                        addProduct(product.getProductName(),
                                product.getProductCategory(),
                                product.getProductImage(),
                                product.getProductDesc(),
                                product.getValue(),
                                productId,
                                product.getUser());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addProduct(String nameText, String categoryText, String imageFile, String descText, double value,
                            String productId, String productUser){
        String imageLoc = "images/" + imageFile;
        storageReference = FirebaseStorage.getInstance("gs://project-f3d47.appspot.com/").getReference(imageLoc);

        View card = getLayoutInflater().inflate(R.layout.product_card, null);
        ImageView image = card.findViewById(R.id.productImage);
        TextView name = card.findViewById(R.id.name);
        TextView category = card.findViewById(R.id.categoryField);
        TextView val = card.findViewById(R.id.showValue);

        LinearLayout scrollContainer = findViewById(R.id.container);
        Button showProduct = card.findViewById(R.id.showProduct);

        /*String tempFilePath;

        try {
            final File tempFile = File.createTempFile(imageFile, "jpg");
            tempFilePath = tempFile.getAbsolutePath();
            storageReference.getFile(tempFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                            image.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

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

        showProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToProductDetailsPage(nameText, categoryText, descText, imageFile, value, productId, productUser);
            }
        });

        name.setText(nameText);
        category.setText(categoryText);
        val.setText(Double.toString(value));

        scrollContainer.addView(card);
    }

    private String getCurrentUser(){
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);

        return loginInfo.getString("User", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        switchToLanding();
    }

    private void switchToLanding(){
        Intent postIntent = new Intent(ProductsPage.this, com.example.a3130project.LenoraUS5.LandingPage.class);
        startActivity(postIntent);
        //finish();
    }

    private void switchToProductDetailsPage(String nameText, String categoryText, String descText, String ImageFile, double value,
                    String productId, String productUser){
        Intent postIntent = new Intent(ProductsPage.this, com.example.a3130project.products.ProductDetailsPage.class);

        postIntent.putExtra("nameText", nameText);
        postIntent.putExtra("categoryText", categoryText);
        postIntent.putExtra("descText", descText);
        postIntent.putExtra("ImageFile", ImageFile);
        postIntent.putExtra("productValue", Double.toString(value));
        postIntent.putExtra("productId", productId);
        postIntent.putExtra("productUser", productUser);
        startActivity(postIntent);
        //finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        categoryValue = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        categoryValue = "All";
    }
}