package com.example.instagram.model;

// create a package in java folder where fragment them a java class and enable also in data base
public class User {

    // we are bringing the data from the database here
    // so remember to mention name exactly as in database
    // we also have to create a java adapter for recyclerview

    private String name;
    private String username;
    private String bio;
    private String email;
    private String id;
    private String imageUrl;

    public  User(){
    }

    public User(String name,String email, String username, String bio, String imageUrl,  String id) {
        this.name = name;
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setname(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
