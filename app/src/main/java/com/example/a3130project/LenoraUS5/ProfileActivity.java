package com.example.a3130project.LenoraUS5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130project.R;
import com.example.a3130project.ui.login.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences loginInfo, temp_values;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        temp_values = getSharedPreferences("temp_values", Context.MODE_PRIVATE);

        user = loginInfo.getString("User", null);

        TextView welcomeTV = findViewById(R.id.Welcome);
        welcomeTV.setText("Are you sure you want to log out? " + user);
        Button logoutBtn = findViewById(R.id.Logout);

        /**
         * SharedPreferences that deletes the current login activity
         * when the logout button is pressed
         */
        logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.clear();
            editor.apply();

            SharedPreferences.Editor editor2 = temp_values.edit();
            editor2.clear();
            editor2.apply();

            switchToLogin();
        });
    }

    @Override
    public void onBackPressed(){
        switchToLanding();
    }

    private void switchToLanding(){
        Intent i = new Intent(ProfileActivity.this, LandingPage.class);
        startActivity(i);
        finish();
    }

    private void switchToLogin(){
        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}