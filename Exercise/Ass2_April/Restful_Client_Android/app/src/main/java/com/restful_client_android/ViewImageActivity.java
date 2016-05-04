package com.restful_client_android;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.faradaj.blurbehind.BlurBehind;

public class ViewImageActivity extends AppCompatActivity {
    RelativeLayout blurBackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        blurBackground = (RelativeLayout) findViewById(R.id.blurBackground);
        BlurBehind.getInstance()
                .withAlpha(80)
                .withFilterColor(Color.parseColor("#FF777777"))
                .setBackground(this);
        blurBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0); // remove exit animation
            }
        });
    }
}
