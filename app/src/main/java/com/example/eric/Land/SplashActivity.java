package com.example.eric.Land;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.eric.R;

/**
 * 创建人张宇
 * APP缓冲欢迎界面
 * 创建时间：2018/7/16.
 */

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 3000;  //延迟3秒

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eric_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Entrance.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
