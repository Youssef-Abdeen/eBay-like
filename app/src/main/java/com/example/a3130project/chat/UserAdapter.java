package com.example.a3130project.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130project.R;
import com.example.a3130project.registration.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.UserViewHolder> {

    //    onUserClickListener is the interface reference

    private final OnUserClickListener onUserClickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options Firebase recycler options
     */

//passing the interface variable ref from the activity
    public UserAdapter(@NonNull FirebaseRecyclerOptions<User> options, OnUserClickListener onUserClickListener) {
        super(options);
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        while inflating a view, passing the interface ref variable in that view as well
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view, onUserClickListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
        holder.usernameTV.setText(user.getUsername());
//        to go to that person's chats
        holder.usernameTV.setOnClickListener(view ->
                holder.onUserClickListener.onUserClick(user.getUsername()));
//        passing the username(that is clicked) to the activity
    }

    public interface OnUserClickListener {
        //        when you click a username, you want to pass that username to the activity, from that activity you want to start that activity to a different activity
//        interface to pass the username
        void onUserClick(String username);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTV;
        public OnUserClickListener onUserClickListener;

        public UserViewHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.usernameTV);
            itemView.setOnClickListener(view -> onUserClickListener.onUserClick(usernameTV.getText().toString()));
        }
    }

}