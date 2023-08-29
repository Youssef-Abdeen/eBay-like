package com.example.a3130project.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130project.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {
    String otherUser;
    DatabaseReference chatDbRef = FirebaseDatabase.getInstance("https://project-f3d47-default-rtdb.firebaseio.com/").getReference("chats");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            otherUser = intent.getExtras().getString("otherUser");
        }

        Button chatSendBtn = findViewById(R.id.chatSendBtn);
        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendClick(otherUser);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        attachRecyclerViewAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void attachRecyclerViewAdapter() {
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(chatDbRef.child(user).child(otherUser), Chat.class)
                        .setLifecycleOwner(this)
                        .build();

        final RecyclerView.Adapter adapter = new ChatAdapter(options, user);

        RecyclerView messagesList = findViewById(R.id.chatRecyclerView);

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messagesList.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        messagesList.setAdapter(adapter);
    }

    public void onSendClick(String otherUser) {
        //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //String name = "User";    //.substring(0, 6);
        EditText messageEdit = findViewById(R.id.chatMessageET);

        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);

        onAddMessage(new Chat(user, messageEdit.getText().toString()), otherUser);
        messageEdit.setText("");
    }

    protected void onAddMessage(@NonNull Chat chat, String otherUser) {
        SharedPreferences loginInfo = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String user = loginInfo.getString("User", null);
        chatDbRef.child(user).child(otherUser).push().setValue(chat);
        chatDbRef.child(otherUser).child(user).push().setValue(chat);
    }

    //Making sure we can go back
    @Override
    public void onBackPressed(){
        switchToLanding();
    }

    private void switchToLanding(){
        Intent postIntent = new Intent(ChatActivity.this, com.example.a3130project.chat.ChatUsersActivity.class);
        startActivity(postIntent);
        //finish();
    }
}
