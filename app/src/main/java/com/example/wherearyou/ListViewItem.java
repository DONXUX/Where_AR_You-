package com.example.wherearyou;


public class ListViewItem {
    private String friendIcon;
    private String friendName;

    public void setIcon(String icon){
        friendIcon = icon;
    }
    public void setFriendName(String friend){
        friendName = friend;
    }

    public String getIcon(){
        return this.friendIcon;
    }
    public String getFriendName(){
        return this.friendName;
    }
}
