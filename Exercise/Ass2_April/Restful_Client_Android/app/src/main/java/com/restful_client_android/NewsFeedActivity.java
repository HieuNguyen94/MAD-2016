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
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.baoyz.widget.PullRefreshLayout;
import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;
import com.melnykov.fab.FloatingActionButton;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int PICK_IMAGE_REQUEST = 2;
    private FeedAdapter feedAdapter;
    private ProgressDialog newsFeedProgressDialog;
    private MaterialDialog selectImageDialog;
    private Toast mToast;
    private String pictureImagePath = "";
    private PullRefreshLayout pullToRefreshLayout;


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
            }
        });
        newsFeedProgressDialog = new ProgressDialog(NewsFeedActivity.this, R.style.AppTheme_Dark_Dialog);
        newsFeedProgressDialog.setMessage(Variables.dialogMessageAlmostThere);
        newsFeedProgressDialog.setCanceledOnTouchOutside(false);
        client = new AsyncHttpClient();
        setupFeed();
        pullToRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullToRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
        pullToRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getListFeed();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                showNewPostPopup(myBitmap);
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
        ArrayList<FeedCardData> data = new ArrayList<FeedCardData>();
        feedAdapter = new FeedAdapter(this, NewsFeedActivity.this, new ArrayList<FeedCardData>());
        rvFeed.setAdapter(feedAdapter);
        try {
            getListFeed();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        feedAdapter.updateItems();
    }


    public void showImage() {
        BlurBehind.getInstance().execute(NewsFeedActivity.this, new OnBlurCompleteListener() {
            @Override
            public void onBlurComplete() {
                Intent intent = new Intent(NewsFeedActivity.this, ViewImageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    private View positiveAction;
    private EditText passwordInput;

    public void showNewPostPopup(Bitmap bitmap) {
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
                        //TODO click post button
                    }
                }).build();
        EditText et_description = (EditText)newPostDialog.getCustomView().findViewById(R.id.et_new_post_desc);
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
        params.put("username", "nghia"); //TODO
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
                        feedAdapter.insertCard(post);
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
                showToast("Loading error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //TODO on failure handler
                newsFeedProgressDialog.dismiss();
                pullToRefreshLayout.setRefreshing(false);
                showToast("Loading error");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                showToast("Success");
                // Firstly remove all cards
                feedAdapter.removeAllCard();
                try {
                    String success = response.getString(Variables.apiSuccess);
                    JSONArray cartList = response.getJSONArray(Variables.apiData);
                    if (success.equals("true")) {
                        for(int i = 0; i < cartList.length(); i++) {
                            JSONObject post = (JSONObject) cartList.get(i);
                            feedAdapter.insertCard(post);
                        }
                    } else {
                        showToast("Loading error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    feedAdapter.notifyDataSetChanged();
                    newsFeedProgressDialog.dismiss();
                    pullToRefreshLayout.setRefreshing(false);
                }
//                try {
//                    for(int i = 0; i < response.length(); i++) {
//                        JSONObject post = (JSONObject) response.get(i);
//                        feedAdapter.insertCard(post);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } finally {
//                    feedAdapter.notifyDataSetChanged();
//                    newsFeedProgressDialog.dismiss();
//                    pullToRefreshLayout.setRefreshing(false);
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    public void showInputDialog() {
        new MaterialDialog.Builder(this)
                .title("Input")
                .content("Enter your message here")
                .customView(R.layout.new_post_dialog, true)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(0, Variables.commentMaxLength)
                .positiveText("Submit")
                .contentColor(Color.BLACK)
                .widgetColorRes(R.color.material_teal_a400)
                .positiveColorRes(R.color.material_teal_a400)
                .input("John Appleseed", "John Appleseed", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        showToast("Hello, " + input.toString() + "!");
                    }
                }).show();
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
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
                        showToast(item.getContent().toString());
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
}
