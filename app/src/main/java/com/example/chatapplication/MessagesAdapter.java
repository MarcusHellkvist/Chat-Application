package com.example.chatapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter <MessagesAdapter.MessageViewHolder>  {

    private ArrayList <Message> messagesList;
    private String userEmail;
    private String messageType;



    public MessagesAdapter (ArrayList<Message> messagesList, String email) {
        this.messagesList = messagesList;
        this.userEmail = email;

    }



    @Override
    public int getItemViewType(int position) {
        if(messagesList.get(position).getUserName().equals(userEmail)) {
            messageType = "user";
            return 1;
        }
        messageType = "another";
        return 0;
    }




    @NonNull
    @Override
    public MessagesAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.friends_list_items;
        if (messageType.equals("user")) layout = R.layout.user_list_items;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        MessagesAdapter.MessageViewHolder viewHolder = new MessagesAdapter.MessageViewHolder(view, messageType);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);

       // holder.imageViewPhoto.setImageBitmap(message.getPhoto());


        holder.textViewName.setText(message.getUserName());
        holder.textViewMessageText.setText(message.getTextMessage());
        holder.textViewTime.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", message.getMessageTime()));




    }
    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView textViewMessageText;
        public TextView textViewTime;


        //public ImageView userPhoto;


        public MessageViewHolder(@NonNull View itemView, String messageType) {
            super(itemView);

            if (messageType.equals("user")) {

                textViewName = itemView.findViewById(R.id.name);
                textViewMessageText = itemView.findViewById(R.id.message_text);
                textViewTime = itemView.findViewById(R.id.time);

            } else {

                textViewName = itemView.findViewById(R.id.friendName);
                textViewMessageText = itemView.findViewById(R.id.friend_sms_text);
                textViewTime = itemView.findViewById(R.id.friend_message_time);
            }
        }
    }
}

