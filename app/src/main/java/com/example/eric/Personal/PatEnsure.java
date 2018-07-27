package com.example.eric.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.eric.Core.InfSetting;
import com.example.eric.Core.MainActivity;
import com.example.eric.R;

/**
 * Created by Eric_ on 2018/7/27.
 */

public class PatEnsure extends AppCompatActivity {
    ImageButton ImaBtnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authon_patient);
        ImaBtnBack = findViewById(R.id.authon_imaBtm_backdpat);
        ImaBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                backIntent.setClass(PatEnsure.this,Authentication.class);
                setResult(RESULT_OK, backIntent);
                finish();
            }
        });
    }
}
