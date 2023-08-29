package com.example.a3130project.LenoraUS5;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.a3130project.R;
import com.example.a3130project.products.ProductInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.a3130project.databinding.ActivityMapsBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int REQUEST_LOCATION = 1;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/");
    DatabaseReference rootRef = database.getReference();
    DatabaseReference productsRef = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("products");






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed(){
        switchToLanding();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng userPos = getLocation();
        mMap.addMarker(new MarkerOptions().position(userPos).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPos));
        addMarkers();


    }
    //switching to landing page
    private void switchToLanding(){
        Intent postIntent = new Intent(MapsActivity.this, LandingPage.class);
        startActivity(postIntent);
        finish();
    }

    private void addMarkers() {

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ProductInfo product = dataSnapshot.getValue(ProductInfo.class);
                    String productId = dataSnapshot.getKey();
                    LatLng location = new LatLng(product.getLatitude(), product.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(location).title(productId)). setTag(product);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ProductInfo product = (ProductInfo) marker.getTag(); // Get the attached item
                if (product != null) {
                    switchToProductDetailsPage(product, marker.getTitle()); // Launch the item page
                    return true;
                }
                return false;
            }
        });
    }

    private void switchToProductDetailsPage(ProductInfo product, String id){
        Intent postIntent = new Intent(this, com.example.a3130project.products.ProductDetailsPage.class);
        String value = product.getValue() +"";

        postIntent.putExtra("nameText", product.getProductName());
        postIntent.putExtra("categoryText", product.getProductCategory());
        postIntent.putExtra("descText", product.getProductDesc());
        postIntent.putExtra("ImageFile", product.getProductImage());
        postIntent.putExtra("productValue", value);
        postIntent.putExtra("productId", id);
        postIntent.putExtra("productUser", product.getUser());
        startActivity(postIntent);
    }


    private void locationPermission(){
        if(locationPermissionCheck()) {
            if(!isLocationEnabled()) {
                Toast.makeText(this, "Please enable location to post.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(this, "Please give location permission to post.", Toast.LENGTH_LONG).show();
            getLocationPermission();
        }
    }

    private boolean locationPermissionCheck() {
        return (ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void getLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private LatLng getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        double latitude, longitude;
        if (locationPermissionCheck()) {
            if (isLocationEnabled()) {
                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationGPS != null) {
                    latitude = locationGPS.getLatitude();
                    longitude = locationGPS.getLongitude();
                    LatLng location = new LatLng(latitude, longitude);
                    return location;
                }
            }
        }
        return null;
    }

}