package com.example.a3130project.registration;
import com.example.a3130project.R;
import com.example.a3130project.ui.login.LoginActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Used CSCI3130 A2 and some tutorial 6 code as refrence
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference UsersRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        database = FirebaseDatabase.getInstance();
        UsersRef = database.getReference("users");

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    protected String getFirstName() {
        EditText firstName = findViewById(R.id.editTextFirstName);
        return firstName.getText().toString().trim();
    }

    protected String getLastName() {
        EditText lastName = findViewById(R.id.editTextLastName);
        return lastName.getText().toString().trim();
    }
    protected double getRating() {
        return 0.0;
    }

    protected String getEmail() {
        EditText email = findViewById(R.id.editTextEmail);
        return email.getText().toString().trim();
    }

    protected String getUsername() {
        EditText username = findViewById(R.id.editTextUsername);
        return username.getText().toString().trim();
    }

    protected String getPassword() {
        EditText password = findViewById(R.id.editTextPassword);
        return password.getText().toString().trim();
    }

    protected String getAddress() {
        EditText address = findViewById(R.id.editTextAddress);
        return address.getText().toString().trim();
    }

    protected String getDOB() {
        EditText dob = findViewById(R.id.editTextDOB);
        return dob.getText().toString().trim();
    }

    protected static boolean isValidEmailAddress(String emailAddress) {
        if (emailAddress.contains("@")) {
            return true;
        }
        return false;
    }

    protected static boolean isValidPassword(String password) {
        if (password.length() >= 5) {
            return true;
        }
        return false;
    }

    protected boolean anyEmpty() {
        if (getFirstName().isEmpty() || getLastName().isEmpty() || getEmail().isEmpty() || getUsername().isEmpty() || getAddress().isEmpty() || getDOB().isEmpty() || getPassword().isEmpty()) {
            return true;
        }
        return false;
    }


    protected void switchToLogin(String username, String password) {
        if (anyEmpty() == false) {
            Intent welcomeIntent = new Intent(this, LoginActivity.class);
            welcomeIntent.putExtra("username", username);
            welcomeIntent.putExtra("password", password);
            startActivity(welcomeIntent);
        }
    }


    @Override
    public void onClick(View view) {
        String emailAddress = getEmail();
        String username = getUsername();
        String password = getPassword();
        boolean error = false;

        if (isValidPassword(password)==false && anyEmpty()==false){
            Toast toast = Toast.makeText(getApplicationContext(),"Invalid Password", Toast.LENGTH_LONG);
            toast.show();
            error = true;
        }


        if (isValidEmailAddress(emailAddress)==false && anyEmpty()==false){
            Toast toast = Toast.makeText(getApplicationContext(),"Invalid Email", Toast.LENGTH_LONG);
            toast.show();
            error = true;
        }


        if (anyEmpty()==true){
            Toast toast = Toast.makeText(getApplicationContext(),"Please fill all of the required fields ", Toast.LENGTH_LONG);
            toast.show();
            error = true;
        }


        if (error == false){
            User newUser = new User(getFirstName(),getLastName(),getEmail(),getUsername(),getPassword(),getAddress(),getDOB(), getRating());
            UsersRef.child(username.replace(".",",")).setValue(newUser);
            Toast toast = Toast.makeText(getApplicationContext(),"Registration Succesful!", Toast.LENGTH_LONG);
            toast.show();
            switchToLogin(getUsername(),getPassword());
        }


    }
}