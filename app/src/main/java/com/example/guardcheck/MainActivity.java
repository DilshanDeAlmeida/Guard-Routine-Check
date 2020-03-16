package com.example.guardcheck;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIMEOUT =500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView splashGif = (ImageView)findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.tsimg).into(splashGif);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIMEOUT);

    }
}
