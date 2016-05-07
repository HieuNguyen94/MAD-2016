package com.restful_client_android;

import android.annotation.TargetApi;
import android.os.Build;

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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    FeedCardData(String avatarUrl, String username, String cardImageUrl, String description, String likeNumber) {
        if (Objects.equals(avatarUrl, "")) {
            this.avatarUrl = Variables.defaultAvatarUrl;
        } else {
            this.avatarUrl = avatarUrl;
        }
        this.username = username;
        if (Objects.equals(cardImageUrl, "")) {
            this.cardImageUrl = Variables.defaultCardImageUrl;
        } else {
            this.cardImageUrl = cardImageUrl;
        }
        this.description = description;
        this.likeNumber = likeNumber;
    }
}
