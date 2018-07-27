package com.example.eric.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.eric.Core.MainActivity;
import com.example.eric.Land.Login;
import com.example.eric.Land.Regist;
import com.example.eric.R;

/**
 * Created by Eric_ on 2018/7/27.
 */

public class AboutUs extends AppCompatActivity {
    ImageButton ImaBtnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_aboutus);
        ImaBtnBack = findViewById(R.id.aboutUs_imaBtm_back);
        ImaBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLand = new Intent();
                intentLand.setClass(AboutUs.this,MainActivity.class);
                setResult(RESULT_OK, intentLand);
                finish();
            }
        });
    }
}
