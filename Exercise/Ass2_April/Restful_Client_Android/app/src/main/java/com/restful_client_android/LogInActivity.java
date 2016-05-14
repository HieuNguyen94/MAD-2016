package com.restful_client_android;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LogInActivity extends AppCompatActivity {

    private EditText usernameET;
    private EditText passwordET;
    private AsyncHttpClient client;
    private ProgressDialog newsFeedProgressDialog;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameET = (EditText) findViewById(R.id.username);
        passwordET = (EditText) findViewById(R.id.password);
        Button loginBtn = (Button) findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameET.getText().toString().equals("") || passwordET.getText().toString().equals("")) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "All fields are required", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                login();
            }
        });
        TextView signupTV = (TextView) findViewById(R.id.signup);
        signupTV.setClickable(true);
        signupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences settings = getSharedPreferences("Login", 0);
        String un = settings.getString("username", "");
        String av = settings.getString("avatar", "");
        if (!Objects.equals(un, "") && !Objects.equals(av, "")) {
            Variables.currentLoginUserAvatar = av;
            Variables.currentLoginUsername = un;
            Intent intent = new Intent(LogInActivity.this, NewsFeedActivity.class);
            startActivity(intent);
            finish();
        }

        client = new AsyncHttpClient();
        newsFeedProgressDialog = new ProgressDialog(LogInActivity.this, R.style.AppTheme_Dark_Dialog);
        newsFeedProgressDialog.setMessage("Logging in");
        newsFeedProgressDialog.setCanceledOnTouchOutside(false);
        settingShowcase();
    }

    private void settingShowcase() {
        SharedPreferences settings = getSharedPreferences("Showcase", 0);
        if (settings.getBoolean(Variables.loginShowcase, true)) {
            ViewTarget signupTarget = new ViewTarget(R.id.signup, this);
            ShowcaseView showcase = new ShowcaseView.Builder(this)
                    .setTarget(signupTarget)
                    .setContentTitle("Do you know")
                    .setContentText("Is it the first time you're here? Just create a new account and then we can start")
                    .build();
            showcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    SharedPreferences settings = getSharedPreferences("Showcase", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Variables.loginShowcase, false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login() {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("username", usernameET.getText().toString());
            params.put("password", passwordET.getText().toString());
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        newsFeedProgressDialog.show();
        client.post(getApplicationContext(), Variables.loginApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                newsFeedProgressDialog.dismiss();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Login success", Snackbar.LENGTH_SHORT).show();;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("Login failed");
                Snackbar.make(getWindow().getDecorView().getRootView(), "Login failed. Please check your Internet connection and try again", Snackbar.LENGTH_SHORT).show();
                newsFeedProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                newsFeedProgressDialog.dismiss();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Login failed", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                newsFeedProgressDialog.dismiss();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Login failed", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                newsFeedProgressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    String message = response.getString(Variables.apiMessage);
                    String data = response.getString("data");
                    if (success.equals("true")) {
                        Variables.currentLoginUserAvatar = data;
                        Variables.currentLoginUsername = usernameET.getText().toString();
                        SharedPreferences settings = getSharedPreferences("Login", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username", Variables.currentLoginUsername);
                        editor.putString("avatar", Variables.currentLoginUserAvatar);
                        editor.commit();

                        Intent intent = new Intent(LogInActivity.this, NewsFeedActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        System.out.println("Login ERROR" + message);
                        Utils.showToast(getApplicationContext(), Variables.messageOperationFailed);
                        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    newsFeedProgressDialog.dismiss();
                }
            }
        });
    }
}
