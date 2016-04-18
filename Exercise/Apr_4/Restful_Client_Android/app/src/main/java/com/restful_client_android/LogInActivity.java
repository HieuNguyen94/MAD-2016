package com.restful_client_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LogInActivity extends AppCompatActivity {

    private final String WS_URL = Util.WS_LOGIN_URL;
    private EditText usernameET;
    private EditText passwordET;

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
                new RequestWebService().execute(WS_URL);
            }
        });
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

    private class RequestWebService extends AsyncTask<String, Void, Void> {

        private int result = -1;
        private String username = usernameET.getText().toString();
        private String password = passwordET.getText().toString();
        private ProgressDialog dialog = new ProgressDialog(LogInActivity.this, R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Logging in ...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                JsonObject msgBody = new JsonObject();
                msgBody.addProperty("username", username);
                msgBody.addProperty("password", password);
                Log.e("Body String", msgBody.toString());

                OutputStream osRequest = urlConnection.getOutputStream();
                osRequest.write(msgBody.toString().getBytes());

                Log.e("Response code", String.valueOf(urlConnection.getResponseCode()));

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = 1;
                }
                else if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
                    result = 0;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            if (result == 1){
                Log.e("result", "1");
                Toast.makeText(LogInActivity.this, "Login successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else if (result ==0){
                Toast.makeText(LogInActivity.this, "Wrong username or password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
