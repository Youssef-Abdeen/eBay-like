package com.example.a3130project.search;

import com.example.a3130project.LenoraUS5.LandingPage;
import com.example.a3130project.R;
import com.example.a3130project.products.ProductDetailsPage;
import com.example.a3130project.products.ProductInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;
import android.widget.Spinner;

public class SearchPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SearchView searchView;
    ListView listView;
    List<ProductInfo> dataList;
    ArrayAdapter<ProductInfo> adapter;
    Spinner categoryFilter;
    TextView noResultsMessage;
    String productId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        Button savedButton = findViewById(R.id.saved_button);
        savedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchPage.this, SavedSearch.class);
                startActivity(intent);
            }
        });

        noResultsMessage = findViewById(R.id.no_results_message);

        // Set up the category filter spinner
        categoryFilter = findViewById(R.id.categoryFilter);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories_filter, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilter.setAdapter(spinnerAdapter);
        categoryFilter.setOnItemSelectedListener(this);

        /**
         * Good way to organize structure for search
         * -Lenora
         */
        // Gets references to the SearchView and ListView in the layout
        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.list_view);
        searchView.setIconified(false);
        initDataList(); // Initialize the data list

        // Set up the search view to filter the data list based on the search query
        // and display the results in the ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ProductInfo product = adapter.getItem(position);
            if (product != null) {
                switchToProductDetailsPage(product, productId);
            }
        });

        // Initialize the adapter and set it to the ListView and binds the dataList to it
        adapter = new ArrayAdapter<ProductInfo>(this, android.R.layout.simple_list_item_1, dataList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(dataList.get(position).getProductName());
                return view;
            }
            // Override the getFilter method to return a custom filter that filters the data list based on the search query
            @Override
            public Filter getFilter() {
                return new Filter() {
                    // Perform the filtering operation in a background thread
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        String[] constraints = constraint.toString().split(";", 2);
                        String category = constraints[0];
                        String query = constraints[1].toLowerCase();

                        FilterResults results = new FilterResults();
                        ArrayList<ProductInfo> filteredList = new ArrayList<>();

                        // If the search query is empty return all the products
                        //Fixed bug where if you open search nothing would appear
                        // Refactored to make it more readable and understandable
                        // based on Lenora's comments.
                        for (ProductInfo product : dataList) {
                            boolean matchesCategory = category.equals("All") || category.equals(product.getProductCategory());
                            boolean matchesQuery = query.isEmpty() || product.getProductName().toLowerCase().contains(query);
                            if (matchesCategory && matchesQuery) {
                                filteredList.add(product);
                            }
                        }

                        results.values = filteredList;
                        results.count = filteredList.size();

                        return results;
                    }
                    // Publish the results of the filtering operation in the UI thread
                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        List<ProductInfo> filteredList = (List<ProductInfo>) results.values;
                        clear();
                        addAll(filteredList);
                        notifyDataSetChanged();

                        if (filteredList.isEmpty()) {
                            noResultsMessage.setVisibility(View.VISIBLE);
                        } else {
                            noResultsMessage.setVisibility(View.GONE);
                        }
                    }
                };
            }
        };
        listView.setAdapter(adapter);

        // Set a listener for changes in the SearchView's text
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // Good check for both submit and change taking into account both
            // potential cases -Lenora
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the event when the user submits a search query
                // In this case, we don't do anything, so return false
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle the event when the search text changes
                // Filter the dataList based on the newText and update the ListView
                // to display the filtered data and check if the search query is empty
                // and if it is it goes back to the original data list
                String selectedCategory = categoryFilter.getSelectedItem().toString();
                if (newText.isEmpty()) {
                    adapter.clear();
                    adapter.addAll(dataList);
                    adapter.notifyDataSetChanged();
                    noResultsMessage.setVisibility(View.GONE);
                } else {
                    adapter.getFilter().filter(selectedCategory + ";" + newText);
                }
                return true;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Update the filter when the selected category changes
        String selectedCategory = parent.getItemAtPosition(position).toString();
        adapter.getFilter().filter(selectedCategory + ";" + searchView.getQuery().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //Not sure what this does
    // -Lenora
    private void initDataList() {
        dataList = new ArrayList<ProductInfo>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    productId = snapshot.getKey();
                    ProductInfo product = dataSnapshot.getValue(ProductInfo.class);
                    dataList.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }

    // Good to remember to have a back button to go back to the landing page
    // -Lenora
    protected void switchToLandingPage(){
        Intent landingIntent = new Intent(SearchPage.this, LandingPage.class);
        startActivity(landingIntent);
        finish();
    }
    @Override
    public void onBackPressed(){
        switchToLandingPage();
    }

    /**
     * Switches to the product details page
     * @param product the product to display
     */
    private void switchToProductDetailsPage(ProductInfo product, String productId) {
        Intent intent = new Intent(SearchPage.this, ProductDetailsPage.class);
        intent.putExtra("nameText", product.getProductName());
        intent.putExtra("categoryText", product.getProductCategory());
        intent.putExtra("descText", product.getProductDesc());
        intent.putExtra("ImageFile", product.getProductImage());
        intent.putExtra("productValue", Double.toString(product.getValue()));
        intent.putExtra("productId", productId);
        intent.putExtra("productUser", product.getUser());
        startActivity(intent);
    }
}