package com.restful_client_android;

/**
 * Created by hieunguyen on 5/6/16.
 */
public class FeedCardData {
    public String avatarUrl;
    public String username;
    public String cardImageUrl;
    public String description;
    public String likeNumber;

    FeedCardData(String avatarUrl, String username, String cardImageUrl, String description, String likeNumber) {
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.cardImageUrl = cardImageUrl;
        this.description = description;
        this.likeNumber = likeNumber;
    }
}
