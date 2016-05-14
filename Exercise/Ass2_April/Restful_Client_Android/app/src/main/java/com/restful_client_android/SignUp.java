package com.restful_client_android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.dd.processbutton.iml.ActionProcessButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {
    static final int PICK_IMAGE_CAMERY_REQUEST = 1;
    private int PICK_IMAGE_GALLERY_REQUEST = 2;
    private String pictureImagePath = "";
    private EditText et_username;
    private EditText et_email;
    private EditText et_address;
    private EditText et_password;
    private EditText et_confirmPassword;
    AsyncHttpClient client;
    private CircleImageView ib_avatar;
    private ActionProcessButton btnSingup;
    private MaterialDialog selectImageDialog;
    AsyncHttpClient imageUploadClient;
    private Bitmap avatarBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_CAMERY_REQUEST && resultCode == RESULT_OK) {
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                File imageFile = new File(pictureImagePath);
                beginCrop(Uri.fromFile(imageFile));
            }
        }

        if (requestCode == PICK_IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            beginCrop(uri);
        }
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            ib_avatar.setImageURI(Crop.getOutput(result));
            try {
                avatarBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_username = (EditText) findViewById(R.id.singupUsername);
        et_email = (EditText) findViewById(R.id.signupEmail);
        et_address = (EditText) findViewById(R.id.signupAddress);
        et_password = (EditText) findViewById(R.id.signupPassword);
        et_confirmPassword = (EditText) findViewById(R.id.signupConfirmPassword);
        ib_avatar = (CircleImageView) findViewById(R.id.avatarSignup);
        ib_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectImageDialog();
            }
        });
        client = new AsyncHttpClient();
//        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        btnSingup = (ActionProcessButton) findViewById(R.id.btnSignIn);
        btnSingup.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkingMissingInfo()) {
                    btnSingup.setProgress(50);
                   enableInput(false);
//                    signup();
                    //TODO
                    if (avatarBitmap != null) {
                        uploadImage(avatarBitmap);
                    } else {
                        signup(Variables.defaultAvatarUrl);
                    }
                }
            }
        });
        imageUploadClient = new AsyncHttpClient();
        imageUploadClient.addHeader("Authorization", "Client-ID 9806c7ef5d11150"); //TODO
    }

    private void enableInput(boolean flag) {
        btnSingup.setEnabled(flag);
        et_username.setEnabled(flag);
        et_email.setEnabled(flag);
        et_address.setEnabled(flag);
        et_password.setEnabled(flag);
        et_confirmPassword.setEnabled(flag);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean checkingMissingInfo() {
        if (et_username.getText().length() == 0 || et_email.getText().length() == 0 || et_address.getText().length() == 0 || et_password.getText().length() == 0) {
            Snackbar.make(getWindow().getDecorView().getRootView(), Variables.message_all_fealds_are_required, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        System.out.println(Objects.equals(et_password.getText().toString(), et_confirmPassword.getText().toString()));
        if (!(Objects.equals(et_password.getText().toString(), et_confirmPassword.getText().toString()))) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Password does not match", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private JSONObject getInfoHandler(String url) {
        JSONObject params = new JSONObject();
        try {
            params.put("username", et_username.getText());
            params.put("password", et_password.getText());
            params.put("email", et_email.getText());
            params.put("name", et_username.getText());
            params.put("avatar", url);
            params.put("profession", "");
            params.put("address", et_address.getText());
            params.put("company", "");
            params.put("priority", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    private void signup(final String url) {
        JSONObject params = getInfoHandler(url);
        StringEntity entity = null;
        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(getApplicationContext(), Variables.signupApiUrl, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    String message = response.getString("message");
                    if (success.equals("true")) {
//                        loadUserInfoIntoView(userInfo); //TODO
                        btnSingup.setProgress(100);
                        Variables.currentLoginUsername = et_username.getText().toString();
                        Variables.currentLoginUserAvatar = url;

                        SharedPreferences settings = getSharedPreferences("Login", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username", Variables.currentLoginUsername);
                        editor.putString("avatar", Variables.currentLoginUserAvatar);
                        editor.commit();

                        Intent intent = new Intent(SignUp.this, NewsFeedActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
                        btnSingup.setProgress(-1);
                        enableInput(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    btnSingup.setProgress(-1);
                    btnSingup.setEnabled(true);
                    enableInput(true);
                    Utils.showToast(getApplicationContext(), "Sign up failed");
                } finally {
//                    loadingDialog.dismiss(); //TODO
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Sign up failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Sign up failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.showToast(getApplicationContext(), "Sign up failed");
                btnSingup.setProgress(-1);
                enableInput(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
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

    private void beginCrop(Uri source) {
        ib_avatar.setImageDrawable(null);
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void uploadImage(Bitmap image) {
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
//        showIndeterminateProgressDialog(true); //TODO
        imageUploadClient.post(getApplicationContext(), Variables.uploadImageApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                progressUploadDialog.dismiss(); //TODO
                System.out.println("Upload image failed");
                btnSingup.setProgress(-1);
                btnSingup.setEnabled(true);
                enableInput(true);
                Utils.showToast(getApplicationContext(), "Uploage image failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Uploage image failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Uploage image failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("Upload image successfully");
//                progressUploadDialog.dismiss(); //TODO
                try {
                    String success = response.getString(Variables.apiSuccess);
                    JSONObject data = response.getJSONObject(Variables.apiData);
                    if (success.equals("true")) {
                        System.out.println("url " + data.getString(Variables.apiLink));
                        signup(data.getString(Variables.apiLink));

                    } else {
                        Utils.showToast(getApplicationContext(), "Uploage image failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    btnSingup.setProgress(-1);
                    btnSingup.setEnabled(true);
                    enableInput(true);
                    Utils.showToast(getApplicationContext(), "Uploage image failed");
                }
            }
        });
    }

    private static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}

