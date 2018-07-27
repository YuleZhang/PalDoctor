package com.example.eric.Core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.eric.Land.Login;
import com.example.eric.R;

/**
 * Created by Eric_ on 2018/7/25.
 */

public class InfSetting extends Activity{
    ImageButton ImabtnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_setting);
        ImabtnBack = findViewById(R.id.setting_imaBtm_back);
        ImabtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                backIntent.setClass(InfSetting.this,MainActivity.class);
                setResult(RESULT_OK,backIntent);
                finish();
            }
        });
    }
    public void setting_ExcUser(View view) {
        Intent excIntent = new Intent();
        excIntent.setClass(InfSetting.this,Login.class);
        startActivity(excIntent);
        MainActivity.instance.finish();
        finish();
    }

    public void setting_exitUser(View view) {
        Intent excIntent = new Intent();
        excIntent.setClass(InfSetting.this,Login.class);
        startActivity(excIntent);
        MainActivity.instance.finish();
        finish();
    }
}
