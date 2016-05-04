package com.restful_client_android;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.faradaj.blurbehind.BlurBehind;

public class ViewImageActivity extends AppCompatActivity {
    RelativeLayout blurBackground;
    ImageView iv_ImageDetail;
    ImageButton ib_closeImageDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        blurBackground = (RelativeLayout) findViewById(R.id.blurBackground);
        iv_ImageDetail = (ImageView) findViewById(R.id.iv_ImageDetail);
        ib_closeImageDetail = (ImageButton) findViewById(R.id.ib_closeImageDetail);
        BlurBehind.getInstance()
                .withAlpha(80)
                .withFilterColor(Color.parseColor("#FFFFFFFF"))
                .setBackground(this);

        ib_closeImageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0); // remove exit animation
            }
        });
    }
}
