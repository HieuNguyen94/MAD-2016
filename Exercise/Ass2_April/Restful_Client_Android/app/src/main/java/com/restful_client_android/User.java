package com.restful_client_android;

/**
 * Created by TRONGNGHIA on 3/27/2016.
 */
public class User {

    private int id;
    private String username;
    private String email;
    private String status;
    private String name;

    public User(int id, String username, String email, String status, String name){
        this.id = id;
        this.username = username;
        this.email = email;
        this.status = status;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
