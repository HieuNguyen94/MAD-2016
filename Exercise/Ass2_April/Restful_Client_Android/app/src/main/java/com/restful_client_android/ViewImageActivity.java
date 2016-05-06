package com.restful_client_android;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.faradaj.blurbehind.BlurBehind;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {
    RelativeLayout blurBackground;
    com.restful_client_android.TouchImageView iv_ImageDetail;
    ImageButton ib_closeImageDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        blurBackground = (RelativeLayout) findViewById(R.id.blurBackground);
        iv_ImageDetail = (com.restful_client_android.TouchImageView) findViewById(R.id.iv_ImageDetail);
        ib_closeImageDetail = (ImageButton) findViewById(R.id.ib_closeImageDetail);
        BlurBehind.getInstance()
                .withAlpha(80)
                .withFilterColor(Color.parseColor("#FFFFFFFF"))
                .setBackground(this);
        String cardImageUrl = getIntent().getStringExtra(Constants.card_image_url);
        Picasso.with(this)
                .load(cardImageUrl)
                .into(iv_ImageDetail);
        ViewGroup.LayoutParams param = iv_ImageDetail.getLayoutParams();
        param.width = ViewGroup.LayoutParams.FILL_PARENT;
        param.height = ViewGroup.LayoutParams.FILL_PARENT;
        ib_closeImageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0); // remove exit animation
            }
        });
    }
}
