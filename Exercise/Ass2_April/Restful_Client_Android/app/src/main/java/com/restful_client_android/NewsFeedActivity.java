package com.restful_client_android;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class NewsFeedActivity extends AppCompatActivity {
    private RecyclerView rvFeed;
    FloatingActionButton fab;

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
                Snackbar.make(view, Constants.message_no_feature, Snackbar.LENGTH_SHORT).show();
            }
        });
        setupFeed();
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
        //TODO remove debug
        FeedCardData card = new FeedCardData(
                "http://www.megaicons.net/static/img/icons_sizes/189/462/256/young-lion-icon.png",
                "Hieu Nguyen",
                "https://pbs.twimg.com/profile_images/633782900077408256/F541mrSs_400x400.jpg",
                "lorem ipsum dolor sit amet consectetuer adipiscing elit",
                "2.5k");
        //TODO remove debug
        for (int i = 0; i < 10; i++) {
            data.add(card);
        }
        FeedAdapter feedAdapter = new FeedAdapter(this, NewsFeedActivity.this, data);
        rvFeed.setAdapter(feedAdapter);
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
}
