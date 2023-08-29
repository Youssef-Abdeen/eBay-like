package com.example.a3130project.search;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.a3130project.R;
import com.example.a3130project.products.ProductInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SavedSearch extends AppCompatActivity {

    private ListView favoriteSearchListView;
    private List<ProductInfo> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_search);

        // Find the ListView in the layout
        favoriteSearchListView = findViewById(R.id.favorite_search_list);

        // Get the dataList from SearchPage
        dataList = (List<ProductInfo>) getIntent().getSerializableExtra("dataList");

        // Get the saved searches from the dataList
        List<String> savedSearches = getSavedSearches(dataList);

        // Create an ArrayAdapter to display the saved searches in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, savedSearches);

        // Set the adapter for the ListView
        favoriteSearchListView.setAdapter(adapter);
    }

    private List<String> getSavedSearches(List<ProductInfo> dataList) {
        List<String> savedSearches = new ArrayList<>();

        // Check if dataList is null
        if (dataList != null) {

            // Loop through dataList and add each product category to savedSearches list
            for (ProductInfo product : dataList) {
                String category = product.getProductCategory();
                if (!savedSearches.contains(category)) {
                    savedSearches.add(category);
                }
            }
        }

        return savedSearches;
    }
}