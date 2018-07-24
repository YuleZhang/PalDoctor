package com.example.eric.Land;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eric.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 创建人：张宇
 * 注册
 * 创建时间：2018/7/16.
 */

public class Regist extends Activity {
    int countSeconds = 60;//倒计时秒数
    EditText eTphoneNum, eTPass,eTVerCode;
    Button getVerCode_btn;
    Button regist_btn;
    ImageView imageEyes;
    Context mContext;
    UserDatabase userDatabase;
    String phone;
    String content;//验证码
    String password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eric_registe);
        userDatabase = new UserDatabase(this);
        mContext = this;
        eTphoneNum = findViewById(R.id.regist_eTPhoneNum);
        eTVerCode = findViewById(R.id.regist_eTVerCode);
        imageEyes = findViewById(R.id.image_eye);
        eTPass = findViewById(R.id.regist_eTPass);
        regist_btn = (Button) findViewById(R.id.regist_bTRegist);
        getVerCode_btn = (Button) findViewById(R.id.regist_btGetVerCode);
        regist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vercode = eTVerCode.getText().toString();
                if(!vercode.equals(content)){
                    Toast.makeText(Regist.this,"验证码错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(null == eTPass.toString()|| "" == eTPass.toString().trim()){
                    Toast.makeText(Regist.this,"密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    phone = eTphoneNum.getText().toString();
                    password = eTPass.getText().toString();
                    userDatabase.insert(phone,password);
                    Toast.makeText(Regist.this,"注册成功",Toast.LENGTH_SHORT).show();
                    Intent intentLand = new Intent();
                    intentLand.putExtra("user",phone);
                    intentLand.setClass(Regist.this,Login.class);
                    startActivity(intentLand);
                    finish();
                }
            }
        });
        getVerCode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countSeconds == 60) {
                    String mobile = eTphoneNum.getText().toString();
                    Log.e("tag", "mobile==" + mobile);
                    getMobile(mobile);
                } else {
                    Toast.makeText(Regist.this, "不能重复发送验证码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCountBack() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getVerCode_btn.setText(countSeconds + "");
                mCountHandler.sendEmptyMessage(0);
            }
        });
    }

    //获取验证码信息，判断是否有手机号码
    public void getMobile(String mobile) {
        userDatabase = new UserDatabase(this);
        int isExist = userDatabase.detection(mobile,password);
        if ("".equals(mobile)) {
            Log.e("tag", "mobile=" + mobile);
            Toast.makeText(this,"手机号码不能为空",Toast.LENGTH_SHORT).show();
//            new AlertDialog.Builder(mContext).setTitle("提示").setMessage("手机号码不能为空").setCancelable(true).show();
        } else if (!isMobileNO(mobile)) {
            Toast.makeText(this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
//            new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请输入正确的手机号码").setCancelable(true).show();
        } else if(isExist != 0){
            Toast.makeText(Regist.this,"该手机号码已经存在，请直接登陆",Toast.LENGTH_SHORT).show();
        } else {
            Log.e("tag", "输入了正确的手机号");
            requestVerifyCode(mobile);
        }
    }
    //获取验证码信息，进行验证码请求
    public void requestVerifyCode(String mobile) {
        phone = mobile;
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 6)
        {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer vertiCode = new StringBuffer();
        for (Integer i : set)
        {
            vertiCode.append("" + i);
        }
        content =vertiCode.toString();//短信内容
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(mobile,null,content,null,null);
        eTVerCode.setText(vertiCode.toString());
        startCountBack();//这里是用来进行请求参数的
    }

    @SuppressLint("HandlerLeak")
    private Handler mCountHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countSeconds > 0) {
                --countSeconds;
                getVerCode_btn.setText("(" + countSeconds + ")后获取验证码");
                mCountHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                countSeconds = 60;
                getVerCode_btn.setText("请重新获取验证码");
            }
        }
    };
    //使用正则表达式判断电话号码
    public static boolean isMobileNO(String tel) {
        Pattern p = Pattern.compile("^(13[0-9]|15([0-3]|[5-9])|14[5,7,9]|17[1,3,5,6,7,8]|18[0-9])\\d{8}$");
        Matcher m = p.matcher(tel);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    public void ImageClick(View view) {
        int inputType = eTPass.getInputType();
        if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            //如果是隐藏状态就显示出来，图片也跟随状态变化
            imageEyes.setSelected(true);
            eTPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            //如果是显示状态就隐藏，图片也跟随状态变化
            imageEyes.setSelected(false);
            eTPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
}
