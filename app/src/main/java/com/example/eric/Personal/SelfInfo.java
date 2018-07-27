package com.example.eric.Personal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eric.Core.MainActivity;
import com.example.eric.Land.Login;
import com.example.eric.Land.UserDatabase;
import com.example.eric.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Eric_ on 2018/7/26.
 */

public class SelfInfo extends AppCompatActivity {
    ImageButton ImaBtnPortrait;
    ImageButton ImaBtnBack;
    TextView tvPersonName;
    TextView tvPersonBirth;
    TextView tvHeight;
    TextView tvWeight;
    Spinner SpiSex;
    Spinner SpiRegion;
    String user;
    String password;
    public static final String TAG = "SelfInfo";
    private Bitmap head;// 头像Bitmap
    UserDatabase userdb;
    String sex;
    String region;
    Bitmap bmpPortrait;
    private static String path = "/sdcard/myHead/";// sd路径


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);
        Intent infoIntent = getIntent();
        Bundle b=infoIntent.getExtras();
        bmpPortrait=(Bitmap) b.getParcelable("bitmap");
        user = infoIntent.getStringExtra("userName");
        password = infoIntent.getStringExtra("password");
        ImaBtnPortrait = findViewById(R.id.person_portrait);
        tvPersonBirth = findViewById(R.id.person_Birth);
        tvPersonName = findViewById(R.id.person_userName);
        tvHeight = findViewById(R.id.person_Height);
        tvWeight = findViewById(R.id.person_Weight);
        ImaBtnBack = findViewById(R.id.person_imaBtm_back);
        SpiSex = findViewById(R.id.person_SpiSex);
        SpiSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                             @Override
                                             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                 sex = (String)SpiSex.getSelectedItem();
                                             }

                                             @Override
                                             public void onNothingSelected(AdapterView<?> parent) {

                                             }
                                         });
                SpiRegion = findViewById(R.id.person_SpiRegion);
        SpiRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region = (String)SpiRegion.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        userdb = new UserDatabase(this);
        final File file=new File(path);
        if (file.canRead()) {
            Log.v("TAG", "可读");
            if (file.exists()) {
                try {
                    ReadBmpFromSd();
//                    ImaBtnPortrait.setImageBitmap(bitmap);
                }
                catch (Exception ex){
                    Toast.makeText(this,"错误："+ex,Toast.LENGTH_SHORT);
                }
            }
        }
        initInfo();
        ImaBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                backIntent.setClass(SelfInfo.this, MainActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
        ImaBtnPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });
    }

    /**
     * 初始化用户信息
     */
    private void initInfo() {
        String[] userInfo = userdb.getInfo(user);
//        Log.v(TAG,userInfo[0]);
        if(userInfo==null || (userInfo!=null && userInfo.length==0)  ){
            return;
        }
//        Log.v(TAG,userInfo[2]);
        if(null != userInfo[3]&&!userInfo[3].isEmpty()) {
            tvPersonName.setText(userInfo[3]);
        }else {
            String str = "";
            for (int i = 0; i < 5; i++) {
                str = str + (char) (Math.random() * 26 + 'a');
            }
            tvPersonName.setText(str);
        }
        if(null != userInfo[4]){
            tvHeight.setText(userInfo[4]);
        }
        if(null != userInfo[5]){
            tvWeight.setText(userInfo[5]);
        }
        if(null != userInfo[6]){
            tvPersonBirth.setText(userInfo[6]);
        }
        if(null != userInfo[7]){
            setSpinnerItemSelectedByValue(SpiRegion,userInfo[7]);
        }
        if(null != userInfo[8]){
            setSpinnerItemSelectedByValue(SpiSex,userInfo[8]);
        }
    }
     /** 根据值, 设置spinner默认选中:
      *     @param spinner
     *      @param value
     */
    public  void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
//                spinner.setSelection(i,true);// 默认选中项
                spinner.setSelection(i);// 默认选中项

                break;
            }
        }
    }


    /*
    * 回调方法，包含头像选择以及文件上传
     */
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
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
                        setPicToView(head);// 保存在SD卡中
                        ImaBtnPortrait.setImageBitmap(head);// 用ImageButton显示出来
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";// 图片名字
        Log.v(TAG,"存储的路径"+path+"head.jpg");
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
     * 修改头像
     */
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

    //性别修改
    public void sexModify(View view) {

    }

    //用户名修改
    public void userModify(View view) {
        final EditText eTuser=new EditText(this);
        eTuser.setSingleLine(true);
        new AlertDialog.Builder(this)
                .setTitle("用户名")
                .setView(eTuser)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获得dialog 中 edittext的值
                        tvPersonName.setText(eTuser.getText());
                    }
                })
                .show();
    }
    //出生年月
    public void birthDate(View view) {
        // Calendar 需要这样来得到
         Calendar calendar = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(this,
                // 绑定监听器(How the parent is notified that the date is set.)
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // 此处得到选择的时间，可以进行你想要的操作
                        tvPersonBirth.setText(year + "年" + (monthOfYear+1) + "月" + dayOfMonth + "日");
                    }
                }
                // 设置初始日期
                ,calendar.get(Calendar.YEAR)
                ,calendar.get(Calendar.MONTH)
                ,calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    //出生地
    public void birthLocation(View view) {

    }
    //修改身高
    public void MofdifyHeight(View view) {
        final EditText eTuser=new EditText(this);
        eTuser.setSingleLine(true);
        new AlertDialog.Builder(this)
                .setTitle("身高")
                .setView(eTuser)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获得dialog 中 edittext的值
                        tvHeight.setText(eTuser.getText()+"厘米");
                    }
                })
                .show();
    }
    //修改体重
    public void MofdifyWeight(View view) {
        final EditText eTuser=new EditText(this);
        eTuser.setSingleLine(true);
        new AlertDialog.Builder(this)
                .setTitle("体重")
                .setView(eTuser)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获得dialog 中 edittext的值
                        tvWeight.setText(eTuser.getText()+"千克");
                    }
                })
                .show();
    }
    //修改密码
    public void MofdifySecret(View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.personal_modifysecret, null);
        final EditText ModifyCurSec = (EditText) textEntryView.findViewById(R.id.Edt_Modify_CurSec);
        final EditText Modify_NewSec = (EditText)textEntryView.findViewById(R.id.Edt_Modify_NewSec);
        final EditText Modify_NewSecAga = (EditText)textEntryView.findViewById(R.id.Edt_Modify_NewSecAga);
        ModifyCurSec.setSingleLine(true);
        Modify_NewSec.setSingleLine(true);
        Modify_NewSecAga.setSingleLine(true);
        final AlertDialog.Builder ad1 = new AlertDialog.Builder(SelfInfo.this);
        ad1.setTitle("修改密码:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                try
                {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    if(ModifyCurSec.getText().toString().isEmpty()||Modify_NewSec.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                        field.set(dialog, false);
                    }
                    else if (!ModifyCurSec.getText().toString().equals(password)){
                        Log.v(TAG,"password:"+password);
                        Toast.makeText(getApplicationContext(),"当前密码输入错误",Toast.LENGTH_SHORT).show();
                        field.set(dialog, false);
                    }
                    else if(!Modify_NewSec.getText().toString().equals(Modify_NewSecAga.getText().toString())){
                        Toast.makeText(getApplicationContext(),"请检查两次输入密码是否一致",Toast.LENGTH_SHORT).show();
                        field.set(dialog, false);
                    }
                    else {
                        try {
                            userdb.modifyPass(user,Modify_NewSec.getText().toString());
                            Toast.makeText(getApplicationContext(),"修改成功，请重新登陆",Toast.LENGTH_SHORT).show();
                            MainActivity.instance.finish();
                            Intent backIntent = new Intent();
                            backIntent.setClass(SelfInfo.this, Login.class);
                            startActivity(backIntent);
                            finish();
                        }
                        catch (Exception ex){
                            Toast.makeText(getApplicationContext(),"修改失败"+ex,Toast.LENGTH_SHORT).show();
                        }
                        field.set(dialog, true);
                    }

                    //设置mShowing值，欺骗android系统

                }catch(Exception e) {
                    e.printStackTrace();
                }
                Log.i("111111", ModifyCurSec.getText().toString());
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                ad1.setCancelable(true);
            }
        });
        ad1.show();// 显示对话框

    }

    public void MofdifySave(View view) {
        String pickName = tvPersonName.getText().toString();
        String birth = tvPersonBirth.getText().toString();
        String height = tvHeight.getText().toString();
        String weight = tvWeight.getText().toString();
        try {
            userdb = new UserDatabase(this);
            userdb.saveInfo(user,password,pickName,sex,birth,region,height,weight);
            Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
        }catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "保存失败" + ex, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * 读取并压缩图片
     */
    public void ReadBmpFromSd(){
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(path+"head.jpg", options);
        Log.v(TAG,"初始化的路径"+path+"head.jpg");
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
        Bitmap bm = BitmapFactory.decodeFile(path+"head.jpg", options); // 解码文件
        Log.w("TAG", "size: " + bm.getByteCount() + " width: " + bm.getWidth() + " heigth:" + bm.getHeight()); // 输出图像数据
        ImaBtnPortrait.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ImaBtnPortrait.setImageBitmap(bm);
    }
}
