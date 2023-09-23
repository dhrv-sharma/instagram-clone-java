package com.example.instagram.model;

public class Notification {
    private  String userId;
    private  String text;
    private String postId;
    private boolean isPost;



    public Notification(String userId, String text, String postId, boolean isPost) {
        this.userId = userId;
        this.text = text;
        this.postId = postId;
        this.isPost = isPost;
    }
    // after setting this we need to create a notification item for recycler view

    public Notification() {
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String getPostId() {
        return postId;
    }

    public boolean isPost() {
        return isPost;
    }
}
