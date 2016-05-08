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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    FeedCardData(String avatarUrl, String username, String cardImageUrl, String description, String likeNumber) {
        if (avatarUrl.equals("")) {
            this.avatarUrl = Variables.defaultAvatarUrl;
        } else {
            this.avatarUrl = avatarUrl;
        }
        try {
            URI iurl = new URI(cardImageUrl);
            this.cardImageUrl = cardImageUrl;
        } catch (URISyntaxException e) {
            this.cardImageUrl = Variables.defaultCardImageUrl;
        }
        this.username = username;
        this.description = description;
        this.likeNumber = likeNumber;
    }
}
