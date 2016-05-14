package com.restful_client_android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Objects;

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

    private ArrayList<ActivityData> activityList;
    private AppAdapter activityAdapter;
    private SwipeMenuListView activityListView;
    private View positiveAction;
    AsyncHttpClient imageUploadClient;
    private MaterialDialog progressUploadDialog;
    private String password;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, 0); // remove exit animation
    }

    @Override
    protected void onResume() {
        if (Variables.refreshFlag) {
            getUserInfo(Variables.currentLoginUsername);
        }
        super.onResume();
    }

    private void settingShowcase() {
        SharedPreferences settings = getSharedPreferences("Showcase", 0);
        if (settings.getBoolean(Variables.profileShowcase, true)) {
            ViewTarget editProfileTarget = new ViewTarget(R.id.ib_EditProfile, this);
            ShowcaseView showcase = new ShowcaseView.Builder(this)
                    .setTarget(editProfileTarget)
                    .setContentTitle(Variables.showcaseTitle)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setContentText("Don't forget to update your profile regularly")
                    .build();
            final Activity that = this;
            showcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    ViewTarget swipe = new ViewTarget(R.id.recentActivities, that);
                    new ShowcaseView.Builder(that)
                            .setTarget(swipe)
                            .setContentTitle(Variables.showcaseTitle)
                            .withMaterialShowcase()
                            .setStyle(R.style.CustomShowcaseTheme)
                            .setContentText("Swipe left to show more actions")
                            .build();

                    SharedPreferences settings = getSharedPreferences("Showcase", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Variables.profileShowcase, false);
                    editor.commit();
                }

                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                }

                @Override
                public void onShowcaseViewShow(ShowcaseView showcaseView) {

                }

                @Override
                public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
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
                Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                intent.putExtra("email", tv_email.getText().toString());
                intent.putExtra("address", tv_address.getText().toString());
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });
        if (Utils.isCurrentUser(username)) {
            ib_editProfile.setVisibility(View.VISIBLE);
        }
        loadingDialog = new ProgressDialog(ProfileActivity.this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setMessage(Variables.dialogMessageAlmostThere);
        loadingDialog.setCanceledOnTouchOutside(false);
        client = new AsyncHttpClient();
        imageUploadClient = new AsyncHttpClient();
        imageUploadClient.addHeader("Authorization", "Client-ID 9806c7ef5d11150"); //TODO
        tv_username.setText(username);
        getUserInfo(username);

        if (Objects.equals(username, Variables.currentLoginUsername)) {
            settingShowcase();
        }

        activityList = new ArrayList<ActivityData>();
        activityListView = (SwipeMenuListView) findViewById(R.id.listView);
        activityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                BlurBehind.getInstance().execute(ProfileActivity.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                        intent.putExtra(Variables.cardImageUrl, activityList.get(i).imageUrl);
                        intent.putExtra(Variables.apiContent, activityList.get(i).fullDescription);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
            }
        });
        activityAdapter = new AppAdapter(getApplicationContext());
        getActivityList(username);
        activityListView.setAdapter(activityAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "edit" item
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
//                openItem.setBackground(new ColorDrawable(Color.);
                editItem.setBackground(R.color.edit_background);
                // set item width
                editItem.setWidth(dp2px(90));
                // set item title
                editItem.setTitle("Edit");
                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(R.color.delete_background);
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        activityListView.setMenuCreator(creator);

        // step 2. listener item click event
        activityListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ActivityData item = activityList.get(position);
                switch (index) {
                    case 0:
                        edit(item);
                        break;
                    case 1:
                        // delete
					delete(item, position);
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        activityListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // set MenuStateChangeListener
        activityListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
            }

            @Override
            public void onMenuClose(int position) {
            }
        });
    }

    private void getUserInfo(String username) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("username", username); //TODO
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
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
            password = data.getString("password");
            if (!email.equals("")) {
                tv_email.setText(email);
            }
            if (!address.equals("")) {
                tv_address.setText(address);
            }
            if (!avatarUrl.equals("")) {
                Picasso.with(getApplicationContext()).load(avatarUrl).into(iv_avatar);
            }

            SharedPreferences settings = getSharedPreferences("Login", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("avatar", avatarUrl);
            if (avatarUrl != Variables.defaultAvatarUrl) {
                Variables.defaultAvatarUrl = avatarUrl;
                Variables.refreshFlag = true;
            }
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class AppAdapter extends BaseSwipListAdapter {
        private Context context;
        public AppAdapter(Context context) {
            this.context = context;
        }

        public void insertActivityJSON(JSONObject object) {
            try {
                ActivityData post = new ActivityData(
                        object.getString("id"),
                        object.getString("cardImage"),
                        object.getString("content"));
                insertActivity(post);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void insertActivity(ActivityData activity) {
            activityList.add(activity);
        }

        @Override
        public int getCount() {
            return activityList.size();
        }

        @Override
        public ActivityData getItem(int position) {
            return activityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_activity, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ActivityData item = getItem(position);
            Picasso.with(context).load(item.imageUrl).resize(50, 50).centerCrop().into(holder.iv_icon);
            holder.tv_name.setText(item.description);
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(this);
            }
        }

        @Override
        public boolean getSwipEnableByPosition(int position) {
            return Utils.isCurrentUser(username);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void edit(ActivityData item) {
        showEditPostPopup(item);
    }

    private void delete(ActivityData item, final int position) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("id", item.postId);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.put(getApplicationContext(), Variables.deletePostApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("Delete activities failed");
                Utils.showToast(getApplicationContext(), "Delete Post failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Delete Post failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Delete Post failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    String message = response.getString(Variables.apiMessage);
                    if (success.equals("true")) {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Delete post successfully", Snackbar.LENGTH_SHORT).show();
                        activityList.remove(position);
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        System.out.println("Delete activities ERROR" + message);
                        Utils.showToast(getApplicationContext(), Variables.messageOperationFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showEditPostPopup(final ActivityData activity) {
        EditText et_description;
        MaterialDialog newPostDialog = new MaterialDialog.Builder(this)
                .title("Edit Post")
                .titleColor(Color.WHITE)
                .customView(R.layout.new_post_dialog, true)
                .positiveText("SAVE")
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
                        savePost(activity.postId, contentTextView.getText().toString());
                    }
                }).build();
        et_description = (EditText)newPostDialog.getCustomView().findViewById(R.id.et_new_post_desc);
        ImageView iv_newPostImage = (ImageView) newPostDialog.getCustomView().findViewById(R.id.iv_newPostImage);
        Picasso.with(getApplicationContext()).load(activity.imageUrl).into(iv_newPostImage);
        et_description.setText(activity.fullDescription);
//        iv_newPostImage.setImageBitmap(bitmap);
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
        positiveAction.setEnabled(true); // disabled by default
    }

    private void showIndeterminateProgressDialog(boolean horizontal) {
        progressUploadDialog = new MaterialDialog.Builder(this)
                .title("Update your changes")
                .content("Please wait")
                .progress(true, 0)
                .widgetColorRes(R.color.material_teal_a400)
                .progressIndeterminateStyle(horizontal)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void savePost(String id, String content) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("id", id);
            params.put("content", content);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showIndeterminateProgressDialog(true);
        client.put(getApplicationContext(), Variables.updatePostConentApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("Update activities failed");
                progressUploadDialog.dismiss();
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
                try {
                    String success = response.getString(Variables.apiSuccess);
                    String message = response.getString(Variables.apiMessage);
                    if (success.equals("true")) {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Update post successfully", Snackbar.LENGTH_SHORT).show();
                        activityList.clear();
                        getActivityList(username);
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        System.out.println("Update activities ERROR" + message);
                        Utils.showToast(getApplicationContext(), Variables.messageOperationFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    progressUploadDialog.dismiss();
                }
            }
        });
    }

    private void getActivityList(String username) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("username", username);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getApplicationContext(), Variables.getPostOfUserApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("Get activities failed");
                Utils.showToast(getApplicationContext(), "Get posts failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Get posts failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(getApplicationContext(), "Get posts failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    String message = response.getString(Variables.apiMessage);
                    JSONArray activityList = response.getJSONArray(Variables.apiData);
                    if (success.equals("true")) {
                        for(int i = 0; i < activityList.length(); i++) {
                            JSONObject post = (JSONObject) activityList.get(i);
                            activityAdapter.insertActivityJSON(post);
                        }
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        System.out.println("Get activities ERROR" + message);
                        Utils.showToast(getApplicationContext(), Variables.messageOperationFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "Get posts failed");
                }
            }
        });
    }

    class ActivityData {
        public String postId;
        public String imageUrl;
        public String description;
        public String fullDescription;

        public ActivityData(String postId, String url, String desc) {
            this.postId = postId;
            if (url.equals("")) {
                this.imageUrl = Variables.defaultCardImageUrl;
            } else {
                this.imageUrl = url;
            }
            this.fullDescription = desc;
            if (desc.length() > Variables.activityDescriptionMaxLength) {
                this.description = desc.substring(0, Variables.activityDescriptionMaxLength) + "...";
            } else {
                this.description = desc;
            }
        }
    }
}
