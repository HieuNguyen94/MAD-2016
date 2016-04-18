package com.restful_client_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by TRONGNGHIA on 4/4/2016.
 */
public class UserDetailActivity extends AppCompatActivity {
    private TextView id;
    private TextView username;
    private TextView email;
    private TextView priority;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_user);
        id = (TextView) findViewById(R.id.id);
        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        priority = (TextView) findViewById(R.id.status);
        name = (TextView) findViewById(R.id.name);

        Intent intent = this.getIntent();
        Bundle userDetail = intent.getBundleExtra("DETAIL");
        id.setText(userDetail.getString("id"));
        username.setText(userDetail.getString("username"));
        email.setText(userDetail.getString("email"));
        priority.setText(userDetail.getString("priority"));
        name.setText(userDetail.getString("name"));
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

}
