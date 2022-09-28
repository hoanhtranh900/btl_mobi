package com.mrash.instagramclone.Model;

import java.util.List;

public class Post {
    private String description;
    private List<String> postImageUrls;
    private String postid;
    private String publisher;

    public Post() {
    }

    public Post(String description, List<String> postImageUrls, String postid, String publisher) {
        this.description = description;
        this.postImageUrls = postImageUrls;
        this.postid = postid;
        this.publisher = publisher;
    }

    public List<String> getPostImageUrls() {
        return postImageUrls;
    }

    public void setPostImageUrls(List<String> postImageUrls) {
        this.postImageUrls = postImageUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
