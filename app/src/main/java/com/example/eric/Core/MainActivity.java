package com.example.eric.Core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eric.Personal.Authentication;
import com.example.eric.Personal.MeasureRecord;
import com.example.eric.Personal.MedicalRecord;
import com.example.eric.R;
import com.example.eric.View.BottomNavigationViewHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    int mActivityResultChoice = 0;
    TextView Headline;
    ImageButton btnUpload;
    ImageButton btnDownload;
    LinearLayout contentListen;
    LinearLayout contentPulse;
    LinearLayout contentFoodTte;
    LinearLayout contentPersonal;
    TextView tvUserMes;
    TextView tvPath;
    String user;   //用户名/手机号
    String path;
    private static final String TAG = "MainActivity";
    private Bitmap head;// 头像Bitmap
    private ImageButton imagePotrait;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Listen();
                    return true;
                case R.id.navigation_dashboard:
                    Pulse();
                    return true;
                case R.id.navigation_notifications:
                    FootTte();
                    return true;
                case R.id.navigation_person:
                    Personal();
                    return true;
            }
            return false;
        }
    };
    /*
    * 个人信息界面
     */
    private void Personal(){
        Headline.setText("个人");
        tvUserMes.setText("用户名："+user);
        contentListen.setVisibility(View.GONE);
        contentPulse.setVisibility(View.GONE);
        contentFoodTte.setVisibility(View.GONE);
        contentPersonal.setVisibility(View.VISIBLE);
    }
    /*
    * 足底检测界面
     */
    private void FootTte() {
        Headline.setText("足底检测");
        contentListen.setVisibility(View.GONE);
        contentPulse.setVisibility(View.GONE);
        contentFoodTte.setVisibility(View.VISIBLE);
        contentPersonal.setVisibility(View.GONE);
    }
    /*
    * 把脉界面
     */
    private void Pulse() {
        Headline.setText("把脉");
        contentListen.setVisibility(View.GONE);
        contentPulse.setVisibility(View.VISIBLE);
        contentFoodTte.setVisibility(View.GONE);
        contentPersonal.setVisibility(View.GONE);
    }

    /**
     * 听诊界面
     */
    private void Listen() {
        Headline.setText("听诊");
        contentListen.setVisibility(View.VISIBLE);
        contentPulse.setVisibility(View.GONE);
        contentFoodTte.setVisibility(View.GONE);
        contentPersonal.setVisibility(View.GONE);
    }

    /**
     * 入口函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        mTextMessage = findViewById(R.id.tvtest);
        Intent mesIntent = getIntent();
        user = mesIntent.getStringExtra("user");
        contentListen = findViewById(R.id.main_LinearListen);
        contentPulse = findViewById(R.id.main_LinearPulse);
        contentFoodTte = findViewById(R.id.main_LinearFootTte);
        contentPersonal = findViewById(R.id.main_LinearPersonal);
        tvUserMes = findViewById(R.id.main_tvUserMes);
        imagePotrait = findViewById(R.id.main_image_portrait);//头像
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Headline = findViewById(R.id.main_tvHead);
        btnDownload = findViewById(R.id.main_bTDownload);
        tvPath = findViewById(R.id.main_tvPath);
        btnUpload = findViewById(R.id.main_bTUpload);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        imagePotrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.main_image_portrait:
                        mActivityResultChoice = 1;
                        showTypeDialog();
                        break;
                }
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendMsg();
                mActivityResultChoice = 2;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
    }
    private void showTypeDialog() {
        //显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                //打开文件
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }
    /*
    * 回调方法，包含头像选择以及文件上传
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mActivityResultChoice == 1){
            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK) {
                        cropPhoto(data.getData());// 裁剪图片
                    }

                    break;
                case 2:
                    if (resultCode == RESULT_OK) {
                        File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                        cropPhoto(Uri.fromFile(temp));// 裁剪图片
                    }

                    break;
                case 3:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        head = extras.getParcelable("data");
                        if (head != null) {
                            /**
                             * 上传服务器代码
                             */
//                        setPicToView(head);// 保存在SD卡中
                            imagePotrait.setImageBitmap(head);// 用ImageButton显示出来
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        else {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                    path = uri.getPath();
                    tvPath.setText(path);
                    Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                    return;
                    }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = getPath(this, uri);
                    tvPath.setText(path);
                    Toast.makeText(this, path+"发送成功", Toast.LENGTH_SHORT).show();
                }
//                    } else {//4.4以下下系统调用方法
//                    path = getRealPathFromURI(uri);
//                    tvPath.setText(path);
//                    Toast.makeText(MainActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
//                    }
                }
            path = tvPath.getText().toString();
            Log.v(TAG,path);
            String pathSplit[] = path.split("/");
            final String fileName = pathSplit[pathSplit.length-1];
            Log.v(TAG,fileName);
            Thread fileSendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG,FileSend(fileName, path, "39.105.20.186", 9999)+"");
//                    Toast.makeText(MainActivity.this,FileSend(fileName, path, "192.168.43.118", 30004)+"",Toast.LENGTH_LONG).show();
                }
            });
            fileSendThread.start();
            //信息测试
//            Socket socket = null;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取文件路径
     * @param context
     * @param uri
     * @return path
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                Log.v(TAG,"docId:"+docId);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                Log.v(TAG,"Media");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
                 return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public String getDataColumn(Context context, Uri uri, String selection,String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public String FileSend(String fileName, String path, String ipAddress, int port){
        try {
            Log.v(TAG,"开始发送");
            Socket name = new Socket(ipAddress, port);
            Log.v(TAG,"开始获取NAME信息");
            OutputStream outputName = name.getOutputStream();
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputName,"UTF-8");
            BufferedWriter bwName = new BufferedWriter(outputWriter);
            bwName.write(fileName);
            bwName.close();
            outputWriter.close();
            outputName.close();
            name.close();
            Socket data = new Socket(ipAddress, port);
            OutputStream outputData = data.getOutputStream();
            FileInputStream fileInput = new FileInputStream(path);
            int size = -1;
            byte[] buffer = new byte[1024];
            while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
                outputData.write(buffer, 0, size);
            }
            outputData.close();
            fileInput.close();
            data.close();
            Log.v(TAG,"内容发送成功");
            Log.v(TAG,"发送成功");
            return fileName + " 发送完成";
        } catch (Exception e) {
            return "发送错误:\n" + e.getMessage();
        }
    }


    /**
     * 调用系统的裁剪功能
     *
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 个人信息-> 就诊记录
     *
     * @param view
     */
    public void MedRecordClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MedicalRecord.class);
        startActivity(intent);
    }

    /**
     * 身份验证
     *
     * @param view
     */
    public void AuthenticationClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Authentication.class);
        startActivity(intent);
    }

    /**
     * 测量总汇
     *
     * @param view
     */
    public void measureClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MeasureRecord.class);
        startActivity(intent);
    }
}