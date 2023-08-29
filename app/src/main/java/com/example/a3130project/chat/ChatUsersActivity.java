package com.example.a3130project.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a3130project.R;
import com.example.a3130project.notifications.NotificationsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);

        LinearLayout linearLayout = findViewById(R.id.usersContainer);
        linearLayout.removeAllViews();
        addCards();

        Button newChat = findViewById(R.id.newChat);
        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewChat();
            }
        });

    }

    private void addCards(){
        LinearLayout linearLayout = findViewById(R.id.usersContainer);
        linearLayout.removeAllViews();

        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("chats");

        databaseReference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //linearLayout.removeAllViews();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String otherUser = dataSnapshot.getKey();
                    //System.out.println("ddddddddddddddddddddddddd " + otherUser);
                    addListItem(otherUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addListItem(String otherUser){
        View card = getLayoutInflater().inflate(R.layout.item_user, null);

        TextView usernameTV = card.findViewById(R.id.usernameTV);
        usernameTV.setText(otherUser);

        usernameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToUserChat(otherUser);
            }
        });

        LinearLayout scrollContainer = findViewById(R.id.usersContainer);
        scrollContainer.addView(card);
    }

    private void startNewChat(){
        final EditText taskEditText = new EditText(this);
        //taskEditText.setWidth(300);
        Resources resources = this.getResources();

        taskEditText.setPadding(
                resources.getDimensionPixelOffset(R.dimen.dp_19),
                resources.getDimensionPixelOffset(R.dimen.dp_15),
                resources.getDimensionPixelOffset(R.dimen.dp_19),
                resources.getDimensionPixelOffset(R.dimen.dp_15)
        );
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New chat")
                .setMessage("Enter username")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = String.valueOf(taskEditText.getText());

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(username);

                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue() == null) {
                                    taskEditText.setText("");
                                    errorAlertBox();
                                }
                                else {
                                    switchToUserChat(username);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void errorAlertBox(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("ERROR");
        builder.setMessage("User does not exist");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
            }
        });

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void switchToUserChat(String otherUser){
        Intent postIntent = new Intent(ChatUsersActivity.this, com.example.a3130project.chat.ChatActivity.class);

        postIntent.putExtra("otherUser", otherUser);
        startActivity(postIntent);
        finish();
    }

    @Override
    public void onBackPressed(){
        switchToLanding();
    }

    private void switchToLanding(){
        Intent postIntent = new Intent(this, com.example.a3130project.LenoraUS5.LandingPage.class);
        startActivity(postIntent);
        //finish();
    }
}