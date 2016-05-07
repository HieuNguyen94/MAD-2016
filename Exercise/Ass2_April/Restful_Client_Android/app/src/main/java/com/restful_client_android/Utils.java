package com.restful_client_android;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Objects;

/**
 * Created by TRONGNGHIA on 4/4/2016.
 */
public class Utils {
    public static String WS_URL = "http://mad-ass1-2016.herokuapp.com";
    public static String WS_LOGIN_URL = "http://mad-ass1-2016.herokuapp.com/user/login";
    public static String WS_SIGNUP_URL = "http://mad-ass1-2016.herokuapp.com/user/create";
    public static String WS_CHECKUSERNAME_URL = "http://mad-ass1-2016.herokuapp.com/user/checkusername";
    public static String WS_GET_ALL_URL = "http://mad-ass1-2016.herokuapp.com/user/all";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isCurrentUser(String username) {
        return (Objects.equals(username, Variables.currentLoginUsername));
    }
}
