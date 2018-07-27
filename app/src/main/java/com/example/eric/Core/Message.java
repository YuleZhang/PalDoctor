package com.example.eric.Core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.eric.R;

/**
 * Created by Eric_ on 2018/7/27.
 */

public class Message extends AppCompatActivity {
    ImageButton ImaBtnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_message);
        ImaBtnBack = findViewById(R.id.message_imaBtm_back);
        ImaBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                setResult(RESULT_OK,backIntent);
                finish();
            }
        });
    }
}
