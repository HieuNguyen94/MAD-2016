package com.restful_client_android;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.targets.Target;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.melnykov.fab.ScrollDirectionListener;

//import com.github.amlcurran.showcaseview.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class MainActivity extends AppCompatActivity {
    private CircleRefreshLayout mRefreshLayout;
    private final String WS_URL = Util.WS_GET_ALL_URL;
    private TextView titleTV;
    private ListView usersListLV;
    private RequestWebService requestWebService;
    private boolean isFirstLoad = true;
    FloatingActionButton fab;
    ShowcaseView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleTV = (TextView) findViewById(R.id.url);
        usersListLV = (ListView) findViewById(R.id.users_list);
        titleTV.setText("GET FROM " + WS_URL);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(usersListLV);
        mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void refreshing() {
                new RequestWebService().execute(WS_URL);
//                usersListLV.setAdapter(null);
            }

            @Override
            public void completeRefresh() {
                // do something when refresh complete
            }
        });
        requestWebService = new RequestWebService();
        requestWebService.execute(WS_URL);

        Target viewTarget = new ViewTarget(R.id.fab, this);
        Button customButton = (Button) getLayoutInflater().inflate(R.layout.view_custom_button, null);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sv = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .replaceEndButton(customButton)
                .setTarget(viewTarget)
                .setContentTitle("Why you're so hurry :)")
                .setContentText("This feature is not available at this time")
//                .singleShot(42)
                .build();
        sv.setButtonPosition(param);
        sv.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sv.isShown()) {
                    sv.setStyle(com.github.amlcurran.showcaseview.R.style.ShowcaseView);
                } else {
                    sv.show();
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

        private String jsonResponse;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (isFirstLoad){
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Please wait for getting result");
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString("admin:admin".getBytes(), Base64.DEFAULT));
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String myLine = "";
                StringBuilder strBuilder = new StringBuilder();
                while ((myLine = responseBuffer.readLine()) != null) {
                    strBuilder.append(myLine);
                }
                //show response (JSON encoded data)
                jsonResponse = strBuilder.toString();
                Log.e("RESPONSE", jsonResponse);

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
            Gson gson = new Gson();
            if (isFirstLoad) { // If isFirstLoad we use default loading, otherwise -> pull to request
                dialog.dismiss();
                isFirstLoad = false;
            } else {
                mRefreshLayout.finishRefreshing();
            }
            Type listType = new TypeToken<ArrayList<User>>() {
            }.getType();
            ArrayList<User> usersList = gson.fromJson(jsonResponse, listType);
            try {
                JsonElement jelement = new JsonParser().parse(jsonResponse);
                JsonArray jarray = jelement.getAsJsonArray();
                for (int i = 0; i < jarray.size(); i++) {
                    JsonObject jobject = jarray.get(i).getAsJsonObject();
                    User user = new User(Integer.parseInt(jobject.get("id").toString()),
                            jobject.get("username").toString(),
                            jobject.get("email").toString(),
                            jobject.get("priority").toString(),
                            jobject.get("name").toString());
                    usersList.add(user);
                }

                Log.e("User", "User");
                usersListLV.setAdapter(new UserAdapter(MainActivity.this, usersList));
            } catch (Exception e) {
                Log.e("PARSING", e.getMessage());
            }
        }
    }
}
