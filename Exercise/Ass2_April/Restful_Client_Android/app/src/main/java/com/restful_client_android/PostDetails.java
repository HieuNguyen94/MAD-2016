package com.restful_client_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PostDetails extends AppCompatActivity {
    CommentAdapter commentAdapter;
    RecyclerView commentRecylerView;
    AsyncHttpClient client;
    EditText commentEditText;
    Button postButton;
    private ProgressDialog loadingDialog;
    private String postId;
    InputMethodManager inputManager;
    private ImageView iv_ImageDetailPostDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        client = new AsyncHttpClient();

        loadingDialog = new ProgressDialog(PostDetails.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setMessage(Variables.dialogMessageAlmostThere);
        loadingDialog.setCanceledOnTouchOutside(false);
        iv_ImageDetailPostDetail = (ImageView) findViewById(R.id.iv_ImageDetailPostDetail);
        String url = getIntent().getStringExtra("cardImageUrl");
        System.out.println(url);
        Picasso.with(getApplicationContext())
                .load(url)
                .into(iv_ImageDetailPostDetail);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
        setTitle(getIntent().getStringExtra("description"));
        postButton = (Button) findViewById(R.id.postCommentButton);
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                postComment();
            }
        });

        commentRecylerView = (RecyclerView) findViewById(R.id.commentRecylerView);
        commentRecylerView.setAdapter(commentAdapter);

//        commentAdapter.add(new Comment(Variables.defaultAvatarUrl, "Hieu Nguyen", "hahaha", "Jun 26"));
//        commentAdapter.notifyDataSetChanged();
        postId = getIntent().getStringExtra("postId");
//        getComment(postId);
        commentInit();
    }

    private void commentInit() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        commentRecylerView.setLayoutManager(linearLayoutManager);
        commentAdapter = new CommentAdapter(getApplicationContext(), PostDetails.this);
        commentRecylerView.setAdapter(commentAdapter);
        getComment(postId);
        commentAdapter.updateItems();
    }

    private void getComment(String postId) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("postid", postId);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        loadingDialog.show();
        client.post(getApplicationContext(), Variables.getCommentApiUrl, entity, "application/json", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    JSONArray commentArray = response.getJSONArray(Variables.apiData);
                    if (success.equals("true")) {
                        for (int i = 0; i < commentArray.length(); i++) {
                            commentAdapter.insertCommentJSON((JSONObject)commentArray.get(i));
                        }
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        Utils.showToast(getApplicationContext(), "Get comments failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "Get comments failed");
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
                Utils.showToast(getApplicationContext(), "Get comments failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Get comments failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Utils.showToast(getApplicationContext(), "Get comments failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postComment() {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("username", Variables.currentLoginUsername);
            params.put("content", commentEditText.getText());
            params.put("postid", postId);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        loadingDialog.show();
        client.post(getApplicationContext(), Variables.postCommentApiUrl, entity, "application/json", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    if (success.equals("true")) {
                        commentAdapter.removeAllComment();
                        getComment(postId);
                        commentEditText.setText("");
                    } else {
                        Utils.showToast(getApplicationContext(), "Post comment failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "Post comment failed");
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
                Utils.showToast(getApplicationContext(), "Post comment failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Post comment failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Utils.showToast(getApplicationContext(), "Post comment failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

        });
    }
}
