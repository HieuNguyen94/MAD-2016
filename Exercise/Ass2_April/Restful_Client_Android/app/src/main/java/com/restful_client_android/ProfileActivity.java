package com.restful_client_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    ImageButton ib_editProfile;
    AsyncHttpClient client;
    private ProgressDialog loadingDialog;
    private TextView tv_username;
    private CircleImageView iv_avatar;
    private TextView tv_email;
    private TextView tv_address;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        tv_username = (TextView) findViewById(R.id.tvUsernameProfile);
        iv_avatar = (CircleImageView) findViewById(R.id.ivAvatarProfile);
        tv_email = (TextView) findViewById(R.id.tvEmailProfile);
        tv_address = (TextView) findViewById(R.id.tvAddressProfile);
        ib_editProfile = (ImageButton) findViewById(R.id.ib_EditProfile);
        username = intent.getStringExtra(Variables.apiUsername);
        ib_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This feature is not available at the moment", Snackbar.LENGTH_SHORT).show();
            }
        });
        if (Utils.isCurrentUser(username)) {
            ib_editProfile.setVisibility(View.VISIBLE);
        }

        loadingDialog = new ProgressDialog(ProfileActivity.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setMessage(Variables.dialogMessageAlmostThere);
        loadingDialog.setCanceledOnTouchOutside(false);
        client = new AsyncHttpClient();
        tv_username.setText(username);
        getUserInfo(username);
    }

    private void getUserInfo(String username) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("username", username); //TODO
            entity = new StringEntity(params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        loadingDialog.show();
        client.post(getApplicationContext(), Variables.getUserInfoApiUrl, entity, "application/json", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    JSONObject userInfo = response.getJSONObject(Variables.apiData);
                    if (success.equals("true")) {
                        loadUserInfoIntoView(userInfo);
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Loading error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

        });
    }

    private void loadUserInfoIntoView(JSONObject data) {
        try {
            String email = data.getString(Variables.apiEmail);
            String address = data.getString(Variables.apiAddress);
            String avatarUrl = data.getString("avatar");
            if (!email.equals("")) {
                tv_email.setText(email);
            }
            if (!address.equals("")) {
                tv_address.setText(address);
            }
            if (!avatarUrl.equals("")) {
                Picasso.with(getApplicationContext()).load(avatarUrl).into(iv_avatar);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
