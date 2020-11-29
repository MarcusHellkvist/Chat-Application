package com.example.chatapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;

public class Message {
    private String userName;;
    private String textMessage;
    private String friendId;
    private Bitmap photo;
    private long messageTime;



    public Message() {
    }

    public Message(String userName, String textMessage) {
        this.textMessage = textMessage;
        this.userName = userName;
        this.photo = photo;
        this.friendId = friendId;
        this.messageTime = new Date().getTime();
        // this.friendText = friendText;

    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }






}



