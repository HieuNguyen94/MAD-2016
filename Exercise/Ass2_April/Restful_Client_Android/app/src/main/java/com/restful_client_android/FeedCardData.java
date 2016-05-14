package com.restful_client_android;

import android.annotation.TargetApi;
import android.os.Build;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Created by hieunguyen on 5/6/16.
 */
public class FeedCardData {
    public String avatarUrl;
    public String username;
    public String cardImageUrl;
    public String description;
    public String likeNumber;
    public String postId;
    public String likePost;
    public String commentNumber;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    FeedCardData(String avatarUrl, String username, String cardImageUrl, String description, String likeNumber, String postId, String likePost, String commentNumber) {
        if (avatarUrl.equals("")) {
            this.avatarUrl = Variables.defaultAvatarUrl;
        } else {
            this.avatarUrl = avatarUrl;
        }
        if (cardImageUrl.equals("")) {
            this.cardImageUrl = Variables.defaultCardImageUrl;
        } else {
            this.cardImageUrl = cardImageUrl;
        }
        this.username = username;
        this.description = description;
        this.likeNumber = likeNumber;
        this.postId = postId;
        this.likePost = likePost;
        this.commentNumber = commentNumber;
    }
}
