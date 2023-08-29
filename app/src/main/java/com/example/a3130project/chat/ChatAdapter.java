package com.example.a3130project.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130project.R;
import com.example.a3130project.registration.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import com.firebase.ui.database.FirebaseRecyclerOptions;


public class ChatAdapter extends FirebaseRecyclerAdapter<Chat, ChatAdapter.ChatViewHolder> {
    //Getting Firebase Ref of Users category to retrieve username

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
//    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
    //private User currentUser;

    private String currentUser;
    public ChatAdapter(@NonNull FirebaseRecyclerOptions<Chat> options, String currentUser) {
        super(options);
        // add the current user to the adapter
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        inflates the item chat
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Chat chat) {

        //Use firebase to retrieve username and compare to chat message username
        if (currentUser.equals(chat.getUsername())) {
            holder.anyUserLL.setVisibility(View.GONE);
            holder.currentUserLL.setVisibility(View.VISIBLE);
            holder.currentUserNameTV.setText("Me");
            holder.currentUserMessageTV.setText(chat.getChatMessage());
        } else {
            holder.currentUserLL.setVisibility(View.GONE);
            holder.anyUserLL.setVisibility(View.VISIBLE);
            holder.anyUserNameTV.setText(chat.getUsername());
            holder.anyUserMessageTV.setText(chat.getChatMessage());
        }
//        if the user is logged into the app, the session username is equal to the chat message username,

        //Use firebase to retrieve username
        /*if (Objects.equals(User.getUsername(), chat.getUsername())) {
//            hiding the any user layout and displaying current user layout
            holder.anyUserLL.setVisibility(View.GONE);
            holder.currentUserLL.setVisibility(View.VISIBLE);
            holder.currentUserNameTV.setText(chat.getUsername());
            holder.currentUserMessageTV.setText(chat.getChatMessage());
        } else {
//            hiding the current user layout and displaying the any user layout
            holder.currentUserLL.setVisibility(View.GONE);
            holder.anyUserLL.setVisibility(View.VISIBLE);
            holder.anyUserNameTV.setText(chat.getUsername());
            holder.anyUserMessageTV.setText(chat.getChatMessage());
        }*/
    }
    //contains all the ids of all of the layouts and ui elements from the recycler view
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout anyUserLL;
        private final TextView anyUserNameTV;
        private final TextView anyUserMessageTV;

        private final LinearLayout currentUserLL;
        private final TextView currentUserNameTV;
        private final TextView currentUserMessageTV;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            anyUserLL = itemView.findViewById(R.id.anyUserLL);
            anyUserNameTV = itemView.findViewById(R.id.anyUserNameTV);
            anyUserMessageTV = itemView.findViewById(R.id.anyUserMessageTV);
            currentUserLL = itemView.findViewById(R.id.currentUserLL);
            currentUserNameTV = itemView.findViewById(R.id.currentUserNameTV);
            currentUserMessageTV = itemView.findViewById(R.id.currentUserMessageTV);
        }
    }
}