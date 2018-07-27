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
import android.widget.TextView;
import android.widget.Toast;

/**
 * 创建人：张宇
 * 登陆界面
 * 创建时间：2018/7/16.
 */
import com.example.eric.Core.MainActivity;
import com.example.eric.Land.Regist;
import com.example.eric.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Login extends Activity{
    int countSeconds = 60;//倒计时秒数
    Context mContext;
    Button btLoad;
    Button btGetVerCode;
    EditText eTUser;
    EditText eTPass;
    TextView tvPass;
    TextView tvLandWay;
    UserDatabase userDatabase;
    Regist regist;
    String content;//验证码
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eric_login);
        mContext = this;
        userDatabase = new UserDatabase(this);
        btLoad = findViewById(R.id.login_btLoad);
        btGetVerCode = findViewById(R.id.login_btGetVerCode);
        eTUser = findViewById(R.id.login_eTUser);
        eTPass = findViewById(R.id.login_eTPass);
        tvPass = findViewById(R.id.login_tvPass);
        tvLandWay = findViewById(R.id.login_tvLandWay);
        Intent registIntent = getIntent();
        eTUser.setText(registIntent.getStringExtra("user"));
        regist = new Regist();
        //账号密码登陆
        btLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = eTUser.getText().toString();
                String password = eTPass.getText().toString();
                if("".equals(user))
                {
                    Toast.makeText(Login.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else if("".equals(password)){
                    if(btGetVerCode.getVisibility() == View.VISIBLE) {
                        Toast.makeText(Login.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Login.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(userDatabase.detection(user,password)==1 || password.equals(content)){
                        Toast.makeText(Login.this,"登陆成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("user",user);
                        intent.putExtra("password",password);
                        intent.setClass(Login.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Login.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //验证码登陆
        btGetVerCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countSeconds == 60) {
                    String mobile = eTUser.getText().toString();
                    Log.e("tag", "mobile==" + mobile);
                    getMobile(mobile);
                } else {
                    Toast.makeText(Login.this, "不能重复发送验证码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //获取验证码信息，判断是否有手机号码
    public void getMobile(String mobile) {
        userDatabase = new UserDatabase(this);
        int isExist = userDatabase.detection(mobile,null);
        if ("".equals(mobile)) {
            Log.e("tag", "mobile=" + mobile);
            Toast.makeText(this,"手机号码不能为空",Toast.LENGTH_SHORT).show();
//            new AlertDialog.Builder(mContext).setTitle("提示").setMessage("手机号码不能为空").setCancelable(true).show();
        } else if (!regist.isMobileNO(mobile)) {
            Toast.makeText(this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
//            new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请输入正确的手机号码").setCancelable(true).show();
        } else if(isExist == 3){
            requestVerifyCode(mobile);
        }else{
            Toast.makeText(this,"请先注册",Toast.LENGTH_SHORT).show();
        }
    }
    //获取验证码信息，进行验证码请求
    public void requestVerifyCode(String mobile) {
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
        SmsManager sm = SmsManager.getDefault();
        content = vertiCode.toString();
        sm.sendTextMessage(mobile,null,content,null,null);
        eTPass.setText(vertiCode.toString());
        startCountBack();//这里是用来进行请求参数的
    }

    private void startCountBack() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btGetVerCode.setText(countSeconds + "");
                mCountHandler.sendEmptyMessage(0);
            }
        });
    }

    public void messageClick(View view) {
        if(btGetVerCode.getVisibility() == View.VISIBLE){
            btGetVerCode.setVisibility(View.GONE);
            eTPass.setHint("请输入密码");
            tvPass.setText("密码：");
            tvLandWay.setText("短信验证码登陆");
            eTPass.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            eTPass.setText("");
        }
        else{
            eTPass.setHint("请输入验证码");
            tvPass.setText("验证码：");
            eTPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btGetVerCode.setVisibility(View.VISIBLE);
            tvLandWay.setText("返回账号密码登陆");
            eTPass.setText("");
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler mCountHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countSeconds > 0) {
                --countSeconds;
                btGetVerCode.setText("(" + countSeconds + ")后获取验证码");
                mCountHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                countSeconds = 60;
                btGetVerCode.setText("请重新获取验证码");
            }
        }
    };

}
