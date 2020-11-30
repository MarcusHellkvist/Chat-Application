package com.example.chatapplication;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.FriendsViewHolder> {

    private ArrayList<User> friendsList;
    private ArrayList<Bitmap> imageListOfFriends;

    interface OnItemClickListener {
        void OnItemClicked(int position, View view);
    }

    public OnItemClickListener friendOnItemClickListener;

    public void setFriendOnItemClickListener(OnItemClickListener friendOnItemClickListener) {
        this.friendOnItemClickListener = friendOnItemClickListener;
    }

    @NonNull
    @Override
    public MyFriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myfriends, parent, false);
        MyFriendsAdapter.FriendsViewHolder FriendsViewHolder = new MyFriendsAdapter.FriendsViewHolder(view, friendOnItemClickListener);
        return FriendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        User item = friendsList.get(position);
        Log.d("TAG", "onBindViewHolder: "+ position);
        holder.myFriendsName.setText(item.getName());
        if (imageListOfFriends.size() == 0) {
            Log.d("TAG", "onBindViewHolder:  if log ");
            holder.myFriendImage.setImageResource(R.drawable.defaultavatar);
            return;
        }
        for (int i = 0; i <imageListOfFriends.size() ; i++) {
            holder.myFriendImage.setImageBitmap(imageListOfFriends.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public MyFriendsAdapter(ArrayList<User> friendsList, ArrayList<Bitmap> imageListOfFriends) {
        this.friendsList = friendsList;
        this.imageListOfFriends = imageListOfFriends;
        //notifyDataSetChanged();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        public ImageView myFriendImage;
        public TextView myFriendsName;
        public TextView myFriendMessage;

        public FriendsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            myFriendImage = itemView.findViewById(R.id.my_friend_imageView);
            myFriendsName = itemView.findViewById(R.id.my_friend_name);
            myFriendMessage = itemView.findViewById(R.id.my_friend_last_message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnItemClicked(position, v);
                        }
                    }
                }
            });
        }
    }
}
