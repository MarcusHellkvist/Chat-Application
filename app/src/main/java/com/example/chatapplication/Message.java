package com.example.chatapplication;

import android.widget.ImageView;

import java.text.DateFormat;
import java.util.Date;


import android.widget.TextView;

public class Message {
    private String userName;;
    private String textMessage;
    private String friendId;
    // private ImageView photo;
    private long messageTime;



    public Message() {
    }

    public Message(String userName, String textMessage) {
        this.textMessage = textMessage;
        this.userName = userName;
        //this.photo = photo;
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

 /*   public User getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(User messageUser) {
        this.messageUser = messageUser;
        }

  */

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

  /*  public ImageView getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(ImageView userPhoto) {
        this.userPhoto = userPhoto;
    }



    public String getFriendText() {
        return friendText;
    }

    public void setFriendText(String friendText) {
        this.friendText = friendText;
    }
    */



    /*
    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
*/

}



