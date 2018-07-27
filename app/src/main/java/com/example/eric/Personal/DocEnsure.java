package com.example.eric.Personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eric.Core.LisDiagnosis;
import com.example.eric.Core.MainActivity;
import com.example.eric.Land.Login;
import com.example.eric.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by Eric_ on 2018/7/27.
 */

public class DocEnsure extends AppCompatActivity {
    private static final String TAG = "DocEnsure";
    ImageButton ImaBtnBack;
    Button BtnUpload;
    ImageView ImaDocSure;
    private String Path;
    private Bitmap head;// 从业资格证Bitmap

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authen_doctor);
        ImaBtnBack = findViewById(R.id.authon_imaBtm_backdoc);
        BtnUpload = findViewById(R.id.authen_btnupLoad);
        ImaDocSure = findViewById(R.id.authen_ImaDocSure);
        ImaBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                backIntent.setClass(DocEnsure.this,Authentication.class);
                setResult(RESULT_OK, backIntent);
                finish();
            }
        });
        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendMsg();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 3);
            }
        });

    }
    /*
    * 回调方法，文件上传
     */
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                Path = uri.getPath();
                Toast.makeText(this,Path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                Path = new LisDiagnosis().getPath(this, uri);
                Log.v(TAG,"path:" + Path);
            }
        }
        LayoutInflater factory = LayoutInflater.from(this);
//        final View textEntryView = factory.inflate(R.layout.personal_modifysecret, null);
        final AlertDialog.Builder ad1 = new AlertDialog.Builder(DocEnsure.this);
        ad1.setTitle("确定上传？");
//        ad1.setView(textEntryView);
        ad1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                Bitmap bm = BitmapFactory.decodeFile(Path); // 解码文件
                ImaDocSure.setBackgroundResource(0);
                ImaDocSure.setImageBitmap(bm);// 用ImageButton显示出来
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                ad1.setCancelable(true);
            }
        });
        ad1.show();// 显示对话框
        super.onActivityResult(requestCode, resultCode, data);
    }

}
