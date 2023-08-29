package com.example.a3130project.LenoraUS5;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a3130project.R;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.a3130project.products.ProductsPage;
import com.example.a3130project.products.ProductInfo;

public class PostPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     *    Implemented with the help of
     *    Coding Cafe: https://www.youtube.com/watch?v=FWsB4Q_ViZU
     *    Code Refactored by displaying the full name and last name on the profile page instead of the
     *    Username and address
     *    Code Refactored by using email id instead of username to retrieve the first name and last name
     */

    //creating variables for each element in the xml
    private ImageButton selectImgPost;
    private Button uploadPost;
    private EditText descPost;
    //photo chosen from gallery
    private static final int Photo_Chosen = 1;

    Bitmap bitmap;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Uri imageUri;
    String categoryValue;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    double latitude, longitude;

    /**
     * On Create
     * When the user switches to this activity, they will see the ability
     * to select a photo, and add a description.
     * In future iterations, they will be able to Upload the Post to
     * a secure database.
     *
     * @param savedInstanceState
     * @author Lenora Tairova
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);
        //creating buttons so its easier to refer to by their IDs
        selectImgPost = (ImageButton)findViewById(R.id.imageButton2);
        uploadPost = (Button)findViewById(R.id.uploadPostButton);
        descPost = (EditText)findViewById(R.id.postDesc);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Spinner spinner = (Spinner) findViewById(R.id.productCat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.product_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        /**
         * OnClick - OpenGallery
         *
         * when the user presses the button to upload an image,
         * their default photo gallery will be opened
         * They may select a photo there.
         *
         * @author Lenora Tairova
         */
        selectImgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call for method to be opened
                OpenGallery();
            }
        });

        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(getLocation())
                {
                    uploadImage();
                }
            }
        });

    }
    //method to open users gallery
    private void OpenGallery() {
        //creating new intent
        Intent photoGalleryIntent = new Intent(Intent.ACTION_PICK);
        //ACTION_GET_CONTENT allows user to browse through specified content type
        //photoGalleryIntent.setAction(Intent.ACTION_PICK);
        photoGalleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //photoGalleryIntent.setType("image/*");
        //startActivityForResult(photoGalleryIntent, Photo_Chosen);

        //ActivityResult activity = new ActivityResult();
        activityLauncher.launch(photoGalleryIntent);
    }

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<androidx.activity.result.ActivityResult>() {
                @Override
                public void onActivityResult(androidx.activity.result.ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent imageData = result.getData();
                        imageUri = imageData.getData();

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            selectImgPost.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance("gs://project-f3d47.appspot.com").getReference("images/"+fileName);
        databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("products");

        EditText name = (EditText) findViewById(R.id.productName);
        EditText desc = (EditText) findViewById(R.id.postDesc);
        EditText value = (EditText) findViewById(R.id.productValue);

        Double prodValue = Double.parseDouble(value.getText().toString());

        String userName = getCurrentUser();

        ProductInfo product = new ProductInfo(name.getText().toString(), categoryValue, desc.getText().toString(), fileName,
                userName, prodValue, latitude, longitude);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        selectImgPost.setImageURI(null);
                        Toast.makeText(PostPage.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        databaseReference.push().setValue(product);
                        switchToProductsPage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(PostPage.this,"Failed to Upload",Toast.LENGTH_SHORT).show();


                    }
                });
    }

    private String getCurrentUser(){
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);

        return user;
    }

    @SuppressLint("MissingPermission")
    private boolean getLocation() {
        if(locationPermissionCheck()) {
            if(isLocationEnabled()) {
                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationGPS != null) {
                    latitude = locationGPS.getLatitude();
                    longitude = locationGPS.getLongitude();

                    Toast.makeText(this, latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                    return true;

                } else {
                    Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Please enable location to post.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(this, "Please give location permission to post.", Toast.LENGTH_LONG).show();
            getLocationPermission();
            //getLocation();
        }

        return false;
    }

    private boolean locationPermissionCheck() {
        return (ActivityCompat.checkSelfPermission(
                PostPage.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                PostPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void getLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
        //switchToProductsPage();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                //dialog.dismiss();
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
        Intent postIntent = new Intent(PostPage.this, com.example.a3130project.products.ProductsPage.class);
        startActivity(postIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        categoryValue = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}