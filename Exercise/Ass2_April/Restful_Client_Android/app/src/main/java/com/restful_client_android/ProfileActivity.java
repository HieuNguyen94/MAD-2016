package com.restful_client_android;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ProfileActivity extends AppCompatActivity {
    ImageButton ib_editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        ib_editProfile = (ImageButton) findViewById(R.id.ib_EditProfile);
        ib_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This feature is not available at the moment", Snackbar.LENGTH_SHORT).show();
            }
        });
        if (Utils.isCurrentUser(intent.getStringExtra(Variables.apiUsername))) {
            ib_editProfile.setVisibility(View.VISIBLE);

        }
    }
}
