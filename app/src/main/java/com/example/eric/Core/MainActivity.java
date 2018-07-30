package com.example.eric.Core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eric.Personal.AboutUs;
import com.example.eric.Personal.Authentication;
import com.example.eric.Personal.MeasureRecord;
import com.example.eric.Personal.MedicalRecord;
import com.example.eric.Personal.SelfInfo;
import com.example.eric.R;
import com.example.eric.View.BottomNavigationViewHelper;

import java.io.File;

public class MainActivity extends AppCompatActivity{
    Context mConetxt;
    TextView Headline;
    ImageButton btnUpload;
    ImageButton btnDownload;
    ImageButton btnSetting;
    ImageButton btnMessage;
    ImageButton btnSwitch;
    ImageButton btnPortrait;
    LinearLayout contentListen;
    LinearLayout contentPulse;
    LinearLayout contentFoodTte;
    LinearLayout contentPersonal;
    LinearLayout contentLocate;
    TextView tvUserMes;
    TextView tvPath;
    String user;   //用户名/手机号
    String password;
    Bitmap bitmap;//头像
    public static MainActivity instance = null;
    private static final String TAG = "MainActivity";
    String upLoadPath;
    private String Portraitpath= "/sdcard/myHead/head.jpg";//sd卡路径
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
        tvUserMes.setText(user);
        contentListen.setVisibility(View.GONE);
        contentPulse.setVisibility(View.GONE);
        contentFoodTte.setVisibility(View.GONE);
        contentPersonal.setVisibility(View.VISIBLE);
        contentLocate.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.GONE);
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
        contentLocate.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.GONE);
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
        contentLocate.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.GONE);
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
        contentLocate.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.VISIBLE);
    }

    /**
     * 入口函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mConetxt = this;
        instance = this;
//        mTextMessage = findViewById(R.id.tvtest);
        Intent mesIntent = getIntent();
        user = mesIntent.getStringExtra("user");
        password = mesIntent.getStringExtra("password");
        contentListen = findViewById(R.id.main_LinearListen);
        contentPulse = findViewById(R.id.main_LinearPulse);
        contentFoodTte = findViewById(R.id.main_LinearFootTte);
        contentPersonal = findViewById(R.id.main_LinearPersonal);
        contentLocate = findViewById(R.id.main_LinearLocate);
        tvUserMes = findViewById(R.id.main_tvUserMes);
        btnPortrait = findViewById(R.id.main_image_portrait);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Headline = findViewById(R.id.main_tvHead);
        btnDownload = findViewById(R.id.main_bTDownload);
        tvPath = findViewById(R.id.main_tvPath);
        btnUpload = findViewById(R.id.main_bTUpload);
        btnSwitch = findViewById(R.id.main_bTSwitch);
        btnSetting = findViewById(R.id.main_Iamge_Setting);
        btnMessage = findViewById(R.id.main_Image_Mails);
        File file=new File(Portraitpath);
        if (file.canRead()) {
            Log.v("TAG", "可读");
            if (file.exists()) {
                try {
                    ReadBmpFromSd();
//                    bitmap= BitmapFactory.decodeFile(path);
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
//                    btnPortrait.setImageBitmap(bitmap);
                }
                catch (Exception ex){
                    Toast.makeText(this,"错误："+ex,Toast.LENGTH_SHORT);
                }
            }
        }
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intMess=  new Intent();
                intMess.setClass(MainActivity.this,Message.class);
                startActivityForResult(intMess,6);
            }
        });
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noteIntentConnect(mConetxt)){
                    return;//检测联网信息
                }
//                sendMsg();
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
        //切换到诊断图界面
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                contentLocate.setVisibility(View.VISIBLE);
//                contentListen.setVisibility(View.GONE);
                contentPulse.setVisibility(View.GONE);
                contentFoodTte.setVisibility(View.GONE);
                contentPersonal.setVisibility(View.GONE);
                btnSwitch.setVisibility(View.VISIBLE);
                Log.v(TAG, "部位getVisibility"+String.valueOf(contentLocate.getVisibility()));
                Log.v(TAG, "比较View.VISIBL"+String.valueOf(View.VISIBLE));
                if(contentLocate.getVisibility() == View.VISIBLE){
                    contentListen.setVisibility(View.VISIBLE);
                    contentLocate.setVisibility(View.GONE);
                }
                else {
                    contentListen.setVisibility(View.GONE);
                    contentLocate.setVisibility(View.VISIBLE);
                }
            }
        });
        //切换到设置界面
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSetting = new Intent();
                intentSetting.setClass(MainActivity.this,InfSetting.class);
                startActivityForResult(intentSetting,6);
            }
        });
        //蓝牙音频下载
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new BlueToothTest().TestBT();
            }
        });
    }
    /*
    * 回调方法，文件上传
     */
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1://上传
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                        upLoadPath = uri.getPath();
                        tvPath.setText(upLoadPath);
                        Toast.makeText(this,upLoadPath+"11111",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        upLoadPath = new  LisDiagnosis().getPath(this, uri);
                        Log.v(TAG,"file path :"+upLoadPath);
                        tvPath.setText(upLoadPath);
                    }
                    upLoadPath = tvPath.getText().toString();
                    Log.v(TAG,upLoadPath);
                    String pathSplit[] = upLoadPath.split("/");
                    final String fileName = pathSplit[pathSplit.length-1];
                    Log.v(TAG,fileName);
                    //实例化进度条对话框（ProgressDialog）
//            final ProgressDialog pd = new ProgressDialog(this);
                    ProgressDialog progressDialog = null;
                    progressDialog = ProgressDialog.show(mConetxt, "请稍等...", "正在玩命上传中......", true);
                    //设置对话进度条样式为水平
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    //设置对话进度条显示在屏幕中央（方便截图）
                    progressDialog.getWindow().setGravity(Gravity.CENTER);
                    //使用匿名内部类实现线程并启动
                    final ProgressDialog finalProgressDialog = progressDialog;
                    new Thread(new Runnable() {
                        int initial = 0;//初始下载进度
                        @Override
                        public void run() {
//                    Log.v(TAG, new LisDiagnosis().FileSend(fileName, path, "39.105.20.186", 9999)+"");
                            int c = new LisDiagnosis().FileSend(fileName, upLoadPath, "39.105.20.186", 9999);
                            Looper.prepare();
                            if(c == -1){
                                Toast.makeText(getApplicationContext(),"上传失败，请打开服务器",Toast.LENGTH_SHORT).show();
                                finalProgressDialog.dismiss();//进度完成时对话框消失
                            }
                            else if(c == 0){
                                Toast.makeText(getApplicationContext(),"上传超时",Toast.LENGTH_SHORT).show();
                                finalProgressDialog.dismiss();//进度完成时对话框消失
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                                finalProgressDialog.dismiss();//进度完成时对话框消失
                            }
                            Looper.loop();

//                            Looper.prepare();
//                            Toast.makeText(getApplicationContext(),"上传成功",Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        }
                    }).start();
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case 2://跳转到个人信息
                if(resultCode == Activity.RESULT_OK){
                    Personal();
                }
                break;
            case 3://跳转到就诊记录
                if(resultCode == Activity.RESULT_OK){
                    Personal();
                }
                break;
            case 4://跳转到身份验证
                if(resultCode == Activity.RESULT_OK){
                    Personal();
                }
                break;
            case 5://跳转到测量总汇
                if(resultCode == Activity.RESULT_OK){
                    Personal();
                }
                break;
            case 6://跳转到消息界面
                if(resultCode == Activity.RESULT_OK){
                    Listen();
                }
                break;
        }
    }
    /**
     * 个人信息-> 就诊记录
     *
     * @param view
     */
    public void MedRecordClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MedicalRecord.class);
        startActivityForResult(intent,3);
    }

    /**
     * 身份验证
     *
     * @param view
     */
    public void AuthenticationClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Authentication.class);
        startActivityForResult(intent,4);
    }

    /**
     * 测量总汇
     *
     * @param view
     */
    public void measureClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MeasureRecord.class);
        startActivityForResult(intent,5);
    }
    /**
     * 检验网络连接 并toast提示
     * @return
     */
    public boolean noteIntentConnect(Context context) {
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            // 当前网络不可用
            Toast.makeText(context.getApplicationContext(), "请先连接Internet！",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if (!wifi) { // 提示使用WIFI
            Toast.makeText(context.getApplicationContext(), "建议您使用WIFI以减少流量！",
                    Toast.LENGTH_SHORT).show();
        }
        return true;

    }
    //跳转到个人信息界面
    public void toSelfInfo(View view) {
        if(null == user||user.equals("")){
            Toast.makeText(getApplicationContext(),"用户名错误",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent IntentSelfInfo = new Intent();
        IntentSelfInfo.setClass(MainActivity.this, SelfInfo.class);
        IntentSelfInfo.putExtra("userName",user);
        IntentSelfInfo.putExtra("password",password);
//        IntentSelfInfo.putExtra("Bitmap",bitmap);
        startActivityForResult(IntentSelfInfo,2);
//        startActivity(IntentSelfInfo);
    }
    /**
     * 读取并压缩BMP图像
     */
    public void ReadBmpFromSd(){
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(Portraitpath, options);
        int height = options.outHeight;
        int width= options.outWidth;
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width); // 原图的最小边长
        if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
            inSampleSize = (int)ratio;
        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        Bitmap bm = BitmapFactory.decodeFile(Portraitpath, options); // 解码文件
        Log.w("TAG", "size: " + bm.getByteCount() + " width: " + bm.getWidth() + " heigth:" + bm.getHeight()); // 输出图像数据
        btnPortrait.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnPortrait.setImageBitmap(bm);
    }

    public void AboutUs(View view) {
        Intent aboutUsIntent = new Intent();
        aboutUsIntent.setClass(MainActivity.this, AboutUs.class);
        startActivity(aboutUsIntent);
    }
}