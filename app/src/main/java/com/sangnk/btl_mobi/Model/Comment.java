package com.sangnk.btl_mobi.Model;

public class Comment {
    private String body;
    private String createTimeStr;
    private User user;

    public Comment() {
    }

    public Comment(String body, String createTimeStr, User user) {
        this.body = body;
        this.createTimeStr = createTimeStr;
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
