package com.restful_client_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.baoyz.widget.PullRefreshLayout;
import com.melnykov.fab.FloatingActionButton;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewsFeedActivity extends AppCompatActivity {
    private RecyclerView rvFeed;
    FloatingActionButton fab;
    AsyncHttpClient client;
    AsyncHttpClient imageUploadClient;
    static final int PICK_IMAGE_CAMERY_REQUEST = 1;
    private int PICK_IMAGE_GALLERY_REQUEST = 2;
    private FeedAdapter feedAdapter;
    private ProgressDialog newsFeedProgressDialog;
    private MaterialDialog selectImageDialog;
    private MaterialDialog progressUploadDialog;
    private String pictureImagePath = "";
    private PullRefreshLayout pullToRefreshLayout;
    private View positiveAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        rvFeed = (RecyclerView) findViewById(R.id.rvFeed);
        fab = (FloatingActionButton) findViewById(R.id.fabnewPost);
        fab.attachToRecyclerView(rvFeed);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectImageDialog();
//                showProgressDeterminateDialog();
            }
        });
        newsFeedProgressDialog = new ProgressDialog(NewsFeedActivity.this, R.style.AppTheme_Dark_Dialog);
        newsFeedProgressDialog.setMessage(Variables.dialogMessageAlmostThere);
        newsFeedProgressDialog.setCanceledOnTouchOutside(false);
        client = new AsyncHttpClient();
        imageUploadClient = new AsyncHttpClient();
        imageUploadClient.addHeader("Authorization", "Client-ID 9806c7ef5d11150"); //TODO
        setupFeed();
        pullToRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullToRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
        pullToRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getListFeed();
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void dispatchTakePictureGalleryIntent() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST);
    }

    private void dispatchTakePictureIntent() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, PICK_IMAGE_CAMERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_CAMERY_REQUEST && resultCode == RESULT_OK) {
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                showNewPostPopup(myBitmap);
            }
        }

        if (requestCode == PICK_IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                showNewPostPopup(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(this, NewsFeedActivity.this, new ArrayList<FeedCardData>());
        rvFeed.setAdapter(feedAdapter);
        try {
            getListFeed();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        feedAdapter.updateItems();
    }

    public void showNewPostPopup(final Bitmap bitmap) {
        EditText et_description;
        MaterialDialog newPostDialog = new MaterialDialog.Builder(this)
                .title("New Post")
                .titleColor(Color.WHITE)
                .customView(R.layout.new_post_dialog, true)
                .positiveText("POST")
                .negativeText("Cancel")
                .widgetColorRes(R.color.material_teal_a400)
                .positiveColorRes(R.color.material_teal_a400)
                .negativeColorRes(R.color.material_teal_a400)
                .buttonRippleColorRes(R.color.white)
                .backgroundColorRes(R.color.primary_dark)
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText contentTextView = (EditText) dialog.getCustomView().findViewById(R.id.et_new_post_desc);
                        uploadImage(bitmap, contentTextView.getText().toString());
                    }
                }).build();
        et_description = (EditText)newPostDialog.getCustomView().findViewById(R.id.et_new_post_desc);
        ImageView iv_newPostImage = (ImageView) newPostDialog.getCustomView().findViewById(R.id.iv_newPostImage);
        iv_newPostImage.setImageBitmap(bitmap);
        positiveAction = newPostDialog.getActionButton(DialogAction.POSITIVE);
        et_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        newPostDialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void getListFeed() throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        params.put("username", Variables.currentLoginUsername); //TODO
        params.put("pageindex", "0");
        params.put("pagesize", Variables.defaultPageSize);
        StringEntity entity = new StringEntity(params.toString());

        // Only show newsfeed progress dialog in the first time, otherwise using pull to refresh
        if (Variables.isFirstLoad) {
            newsFeedProgressDialog.show();
            Variables.isFirstLoad = false;
        }

        client.post(getApplicationContext(), Variables.getNewsFeedApiUrl, entity, "application/json", new JsonHttpResponseHandler(){
            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Firstly remove all cards
                feedAdapter.removeAllCard();
                try {
                    for(int i = 0; i < response.length(); i++) {
                        JSONObject post = (JSONObject) response.get(i);
                        feedAdapter.insertCardJSON(post);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    feedAdapter.notifyDataSetChanged();
                    newsFeedProgressDialog.dismiss();
                    pullToRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                newsFeedProgressDialog.dismiss();
                pullToRefreshLayout.setRefreshing(false);
                Utils.showToast(getApplicationContext(), "Loading error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //TODO on failure handler
                newsFeedProgressDialog.dismiss();
                pullToRefreshLayout.setRefreshing(false);
                Utils.showToast(getApplicationContext(), "Loading error");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Utils.showToast(getApplicationContext(), "Success");
                // Firstly remove all cards
                feedAdapter.removeAllCard();
                try {
                    String success = response.getString(Variables.apiSuccess);
                    JSONArray cartList = response.getJSONArray(Variables.apiData);
                    if (success.equals("true")) {
                        for(int i = 0; i < cartList.length(); i++) {
                            JSONObject post = (JSONObject) cartList.get(i);
                            feedAdapter.insertCardJSON(post);
                        }
                    } else {
                        Utils.showToast(getApplicationContext(), "Loading error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    feedAdapter.notifyDataSetChanged();
                    newsFeedProgressDialog.dismiss();
                    pullToRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    public void showSelectImageDialog() {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(this);
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content("From gallery")
                .icon(R.drawable.gallery)
                .backgroundColor(Color.WHITE)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content("Using camera")
                .icon(R.drawable.camera)
                .backgroundColor(Color.WHITE)
                .build());

        selectImageDialog = new MaterialDialog.Builder(this)
                .title("Select your image")
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        MaterialSimpleListItem item = adapter.getItem(which);
                        Utils.showToast(getApplicationContext(), item.getContent().toString());
                        if (which == 0) { // Select from gallery
                            dispatchTakePictureGalleryIntent();
                        } else { // Using camera
//                            dispatchTakePictureIntent();
                            dispatchTakePictureIntent();
                        }
                        selectImageDialog.dismiss();
                    }
                })
                .show();
    }

    private void uploadImage(Bitmap image, final String content) {
        String myBase64Image = encodeToBase64(image, Bitmap.CompressFormat.JPEG, Variables.bitmapCompressQuality);
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("image", myBase64Image);
            params.put("album", "jF7fMpi2QZ1celm");
            params.put("type", "file");
            params.put("name", "first");
            params.put("title", "first");
            params.put("description", "first image");
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showIndeterminateProgressDialog(true);
        imageUploadClient.post(getApplicationContext(), Variables.uploadImageApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressUploadDialog.dismiss();
                System.out.println("Upload image failed");
                //TODO reshow new post dialog
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("Upload image successfully");
                progressUploadDialog.dismiss();
                try {
                    String success = response.getString(Variables.apiSuccess);
                    JSONObject data = response.getJSONObject(Variables.apiData);
                    if (success.equals("true")) {
                        System.out.println("url " + data.getString(Variables.apiLink));
                        createNewPost(data.getString(Variables.apiLink), content);
                    } else {
                        Utils.showToast(getApplicationContext(), "Loading error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private void showIndeterminateProgressDialog(boolean horizontal) {
        progressUploadDialog = new MaterialDialog.Builder(this)
                .title("Uploading image")
                .content("Please wait")
                .progress(true, 0)
                .widgetColorRes(R.color.material_teal_a400)
                .progressIndeterminateStyle(horizontal)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void createNewPost(String imageUrl, String content) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("username", Variables.currentLoginUsername);
            params.put("content", content);
            params.put("cardimage", imageUrl);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        imageUploadClient.post(getApplicationContext(), Variables.createPostApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressUploadDialog.dismiss();
                Utils.showToast(getApplicationContext(), "Create new post failed");
                System.out.println("Create new post failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Utils.showToast(getApplicationContext(), "Create new post successfully");
                System.out.println("Create new post successfully");
                progressUploadDialog.dismiss();
                try {
                    String success = response.getString(Variables.apiSuccess);
                    String message = response.getString(Variables.apiMessage);
                    if (success.equals("true")) {
                        getListFeed();
                    } else {
                        Utils.showToast(getApplicationContext(), "Loading error" + message);
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {
                    progressUploadDialog.dismiss();
                }
            }
        });
    }
}
