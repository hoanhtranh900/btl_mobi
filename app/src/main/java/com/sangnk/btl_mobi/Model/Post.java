package com.sangnk.btl_mobi.Model;

public class Post {
    private String description;
    private String postImageUrl;
    private String postid;
    private String publisher;
    private User user;
    private String datecreate;
    private Long totalLike;

    public Long getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(Long totalLike) {
        this.totalLike = totalLike;
    }

    public Post(String description, String postImageUrl, String postid, String publisher, User user) {
        this.description = description;
        this.postImageUrl = postImageUrl;
        this.postid = postid;
        this.publisher = publisher;
        this.user = user;
    }

    public Post() {
    }

    public String getDatecreate() {
        return datecreate;
    }

    public void setDatecreate(String datecreate) {
        this.datecreate = datecreate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
