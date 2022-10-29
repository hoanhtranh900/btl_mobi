package com.sangnk.btl_mobi.Model;

public class Inbox {
    private String avatar;
    private Long numUnread;
    private String username;
    private String fullname;
    private String lastMessage;
    private String lastMessageTime;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getNumUnread() {
        return numUnread;
    }

    public void setNumUnread(Long numUnread) {
        this.numUnread = numUnread;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
