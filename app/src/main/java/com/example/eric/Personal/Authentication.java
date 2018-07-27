package com.example.eric.Personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.eric.Core.InfSetting;
import com.example.eric.Core.MainActivity;
import com.example.eric.R;

/**
 * Created by Eric_ on 2018/7/20.
 */

public class Authentication extends Activity {
    ImageButton ImaBtnBack;
    Button BtnDoctor;
    Button BtnPatient;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == Activity.RESULT_OK){

                }
                break;
            case 2:
                if(resultCode == Activity.RESULT_OK){

                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_authentication);
        ImaBtnBack = findViewById(R.id.authen_imaBtm_back);
        BtnDoctor = findViewById(R.id.authon_BtnDoctor);
        BtnPatient = findViewById(R.id.authon_BtnPatient);
        ImaBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                //设置返回数据
                Authentication.this.setResult(RESULT_OK, backIntent);
                finish();
            }
        });
        BtnDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ensureIntent = new Intent();
                ensureIntent.setClass(Authentication.this,DocEnsure.class);
                startActivityForResult(ensureIntent,1);//医生
            }
        });
        BtnPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ensureIntent = new Intent();
                ensureIntent.setClass(Authentication.this,PatEnsure.class);
                startActivityForResult(ensureIntent,2);//患者
            }
        });
    }
}
