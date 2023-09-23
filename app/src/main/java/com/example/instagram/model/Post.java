package com.example.instagram.model;

public class Post {

    // exact same as in post data base
    // in home fragment we have post item
    // we create class Post for post item
    // now we need a Post adapter for our post recycler view
    private String description;
    private String imageurl;
    private String postid;
    private String publisher;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
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

    public Post() {
    }

    public Post(String description, String imageurl, String postid, String publisher) {
        this.description = description;
        this.imageurl = imageurl;
        this.postid = postid;
        this.publisher = publisher;
    }
}
