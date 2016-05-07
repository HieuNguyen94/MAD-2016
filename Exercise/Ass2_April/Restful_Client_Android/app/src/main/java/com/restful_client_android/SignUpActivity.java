package com.restful_client_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity {

    private final String WS_URL = Utils.WS_SIGNUP_URL;
    private EditText usernameET;
    private EditText passwordET;
    private EditText password2ET;
    private EditText emailET;
    private EditText nameET;
    private EditText addressET;
    private EditText professionET;
    private EditText companyET;
    private EditText priorityET;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        usernameET = (EditText) findViewById(R.id.username);
        passwordET = (EditText) findViewById(R.id.password);
        password2ET = (EditText) findViewById(R.id.password2);
        emailET = (EditText) findViewById(R.id.email);
        nameET = (EditText) findViewById(R.id.name);
        addressET = (EditText) findViewById(R.id.address);
        professionET = (EditText) findViewById(R.id.profession);
        companyET = (EditText) findViewById(R.id.company);
        priorityET = (EditText) findViewById(R.id.priority);
        signupBtn = (Button) findViewById(R.id.signup);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameET.getText().toString().equals("") || passwordET.getText().toString().equals("") || password2ET.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Username/Password required", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (!passwordET.getText().toString().equals(password2ET.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Retype password wrongly", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    new RequestWebService().execute(WS_URL, Utils.WS_CHECKUSERNAME_URL);
                }
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
        private String email = emailET.getText().toString();
        private String name = nameET.getText().toString();
        private String profession = professionET.getText().toString();
        private String company = companyET.getText().toString();
        private String priority = priorityET.getText().toString();

        private ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Processing ...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                URL checkUrl = new URL(urls[1]);
                HttpURLConnection urlCheckConnection = (HttpURLConnection) checkUrl.openConnection();
                urlCheckConnection.setRequestMethod("POST");
                urlCheckConnection.setRequestProperty("Content-Type", "application/json");
                urlCheckConnection.setDoInput(true);
                urlCheckConnection.connect();

                JsonObject msgBodyCheck = new JsonObject();
                msgBodyCheck.addProperty("username", username);

                OutputStream osRequestCheck = urlCheckConnection.getOutputStream();
                osRequestCheck.write(msgBodyCheck.toString().getBytes());

                if (urlCheckConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    URL url = new URL(urls[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();

                    JsonObject msgBody = new JsonObject();
                    msgBody.addProperty("username", username);
                    msgBody.addProperty("password", password);
                    msgBody.addProperty("email", email);
                    msgBody.addProperty("name", name);
                    msgBody.addProperty("avatar", "");
                    msgBody.addProperty("address", "");
                    msgBody.addProperty("profession", profession);
                    msgBody.addProperty("company", company);
                    msgBody.addProperty("priority", priority);
                    Log.e("Body String", msgBody.toString());

                    OutputStream osRequest = urlConnection.getOutputStream();
                    osRequest.write(msgBody.toString().getBytes());

                    Log.e("Response code", String.valueOf(urlConnection.getResponseCode()));

                    byte[] msgResponseByte = new byte[1];
                    urlConnection.getInputStream().read(msgResponseByte);
                    String msgResponse = new String(msgResponseByte);
                    Log.e("Response msg", msgResponse);

                    if (msgResponse.equals("Y")){
                        result = 1;
                    }
                    else if (urlConnection.getResponseMessage().equals("N")){
                        result = 0;
                    }
                }
                else{
                    result = 2;
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
                Toast.makeText(SignUpActivity.this, "Sign up successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
            }
            else if (result == 0){
                Toast.makeText(SignUpActivity.this, "Something wrong with you", Toast.LENGTH_LONG).show();
            }
            else if (result == 2){
                Toast.makeText(SignUpActivity.this, "Username exists", Toast.LENGTH_LONG).show();
            }
        }
    }
}
