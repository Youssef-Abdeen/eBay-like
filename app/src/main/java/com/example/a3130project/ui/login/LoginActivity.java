package com.example.a3130project.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3130project.LenoraUS5.LandingPage;
import com.example.a3130project.R;
import com.example.a3130project.registration.RegistrationActivity;
import com.example.a3130project.ui.login.LoginViewModel;
import com.example.a3130project.ui.login.LoginViewModelFactory;
import com.example.a3130project.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Code Refactored by breaking store info storeinfo() into three new methods storeinfo(),
 * getuserData(), putDataInSharePref() so that each of them follows the Single Responsibility Principle
 *
 * Code Refactored by using email id instead of username to retrieve the first name and last name of the user
 */

public class LoginActivity extends AppCompatActivity {



    FirebaseDatabase database = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/");
    DatabaseReference rootRef = database.getReference();
    //DatabaseReference emailRef = database.getReference("email");
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private boolean inDB;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Checking if user is Logged In
        startupLoginCheck();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final Button registerButton = binding.registrationButton;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess(), usernameEditText.getText().toString());
                    storeInfo("Logged In", usernameEditText.getText().toString());
                    switchToLandingPage();
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
                //switchToLandingPage();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = binding.username.getText().toString();
                String enteredPassword = binding.password.getText().toString();

                if(!enteredUsername.isEmpty()) {
                    if (userExists(enteredUsername, enteredPassword)) {
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        loginViewModel.login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    }else{
                        usernameEditText.setError("Please create an account");
                    }
                }else{
                    usernameEditText.setError("Please enter an email");
                }

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this,com.example.a3130project.registration.RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
    private void updateUiWithUser(LoggedInUserView model, String user) {

        // TODO : initiate successful logged in experience
        String welcome = "Welcome " + user + "!";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    //Once the user is logged in, they should see the landing page
    protected void switchToLandingPage(){
        Intent landingPageIntent = new Intent(LoginActivity.this, LandingPage.class);
        startActivity(landingPageIntent);
        finish();
    }

    private void storeInfo(String status, String user){

        SharedPreferences.Editor editor;
        editor = getSharedPreferences("login_info", Context.MODE_PRIVATE).edit();
        editor.putString("LoginStatus",status);
        editor.putString("User",user);
        editor.apply();
        getUserData(user);

    }

    private void getUserData(String User){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");

        dbRef.orderByChild("username").equalTo(User).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String fname = dataSnapshot.child("fname").getValue(String.class);
                    String lname = dataSnapshot.child("lname").getValue(String.class);

                    String fullname = fname +" "+lname;

                    putDataInSharePref(fullname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putDataInSharePref(String fullname){
        SharedPreferences.Editor editor;
        editor = getSharedPreferences("login_info", Context.MODE_PRIVATE).edit();
        editor.putString("FullName", fullname);
        editor.apply();
    }

    /**
     * SharedPreferences have been used to store the login info
     * which helps the use stayed lgeed in unless logout button is pressed
     *
     * @author(s): Aashay Raj and Tanisha Dabas
     */
    protected void startupLoginCheck(){
        SharedPreferences loginInfo = getSharedPreferences("login_info",Context.MODE_PRIVATE);
        String loginStatus = loginInfo.getString("LoginStatus", null);
        String user = loginInfo.getString("User", null);

        if (loginStatus != null && user != null){
            String welcome = "Welcome " + user + "!";
            Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
            switchToLandingPage();
        }
    }


    private Boolean userExists(String username, String password) {
        rootRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(username)){
                    String usersPass = snapshot.child(username).child("password").getValue().toString();
                    if(usersPass.equals(password)){
                        inDB = true;
                    }else{
                        Toast.makeText(getApplicationContext(), "Incorrect password.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    inDB = false;
                    Toast.makeText(getApplicationContext(), "Please create an account.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // on cancalled, nothing will happen
            }
        });
        return inDB;
    }




}