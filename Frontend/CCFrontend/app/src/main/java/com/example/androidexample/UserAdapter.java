package com.example.androidexample;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<SimpleUser> usersList;
    private List<SimpleUser> filteredList; // <-- for filtering
    private OnDeleteClickListener deleteClickListener;

    private OnMakeAdminClickListener makeAdminClickListener;

    private String currentUsernameQuery = "";
    private String currentRoleFilter = "All";

    public interface OnDeleteClickListener {
        void onDeleteClick(SimpleUser user);
    }

    public UserAdapter(List<SimpleUser> usersList, OnDeleteClickListener deleteClickListener, OnMakeAdminClickListener makeAdminClickListener) {
        this.usersList = usersList;
        this.filteredList = new ArrayList<>(usersList);
        this.deleteClickListener = deleteClickListener;
        this.makeAdminClickListener = makeAdminClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adminviewuserpage_usercard, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        SimpleUser user = filteredList.get(position);
        holder.firstLastText.setText(user.getDisplayName()); // use displayName now
        holder.usernameText.setText(user.getUsername());
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(user));

        holder.makeAdminButton.setVisibility(
                (!user.isAdmin() && !user.isTutor()) ? View.VISIBLE : View.GONE
        );

        holder.makeAdminButton.setOnClickListener(v -> {
            if (makeAdminClickListener != null) {
                makeAdminClickListener.onMakeAdminClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String usernameQuery, String roleFilter) {
        currentUsernameQuery = usernameQuery;
        currentRoleFilter = roleFilter;

        filteredList.clear();
        for (SimpleUser user : usersList) {
            boolean matchesUsername = user.getUsername().toLowerCase().contains(usernameQuery.toLowerCase());
            boolean matchesRole = roleFilter.equals("All") ||
                    (roleFilter.equals("Admin") && user.isAdmin()) ||
                    (roleFilter.equals("Tutor") && user.isTutor());

            if (matchesUsername && matchesRole) {
                filteredList.add(user);
            }
        }
        notifyDataSetChanged();
    }

    // Call this whenever data changes asynchronously
    public void reapplyFilter() {
        filter(currentUsernameQuery, currentRoleFilter);
    }

    public void setFilteredList(List<SimpleUser> newList) {
        this.filteredList = newList;
        notifyDataSetChanged();
    }

    public interface OnMakeAdminClickListener {
        void onMakeAdminClick(SimpleUser user);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView firstLastText, usernameText;
        Button deleteButton;
        Button makeAdminButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            firstLastText = itemView.findViewById(R.id.first_last_txt);
            usernameText = itemView.findViewById(R.id.username);
            deleteButton = itemView.findViewById(R.id.delete_btn);
            makeAdminButton = itemView.findViewById(R.id.make_admin_btn);
        }
    }
}

