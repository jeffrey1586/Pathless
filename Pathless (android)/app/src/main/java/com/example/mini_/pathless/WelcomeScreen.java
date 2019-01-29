package com.example.mini_.pathless;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class WelcomeScreen extends AppCompatActivity {

    private static int delayTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        // hide the actionbar
        getSupportActionBar().hide();

        // set the delay between showing the welcome screen and map activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeScreen.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        }, delayTime);
    }
}
