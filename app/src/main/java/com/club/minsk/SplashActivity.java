package com.club.minsk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.club.minsk.db.Devices;
import com.karumi.dexter.Dexter;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dexter.initialize(this);
        new Devices().insert();

        Intent intent = new Intent(this, StartActivity.class);
        if (getIntent().getExtras() != null)
            intent.putExtras(getIntent().getExtras());
        startActivity(intent);
        finish();
    }
}
