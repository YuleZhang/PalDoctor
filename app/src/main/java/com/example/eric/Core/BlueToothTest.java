package com.example.eric.Core;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by Eric_ on 2018/7/30.
 */

public class BlueToothTest  extends Activity{
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private AudioManager mAudioManager = null;
    public static final String TAG = "BlueToothTest";
    void TestBT(){
        BluetoothAdapter mAdapter= BluetoothAdapter.getDefaultAdapter();
        if(!mAdapter.isEnabled()) {
//弹出对话框提示用户是后打开
            // 请求打开 Bluetooth
            Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
            requestBluetoothOn.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            // 设置 Bluetooth 设备可见时间
//            requestBluetoothOn.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000);
            // 请求开启 Bluetoot
            this.startActivityForResult(requestBluetoothOn, 0);
        }
    }
    private void startRecording() {
        //获得文件保存路径。记得添加android.permission.WRITE_EXTERNAL_STORAGE权限
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/btrecorder.3gp";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();//如果文件打开失败，此步将会出错。
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        if(!mAudioManager.isBluetoothScoAvailableOffCall()){
            Log.d(TAG, "系统不支持蓝牙录音");
            return;
        }
//蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
        mAudioManager.startBluetoothSco();
        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);

                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
                    mRecorder.start();//开始录音
                    unregisterReceiver(this);  //别遗漏
                }else{//等待一秒后再尝试启动SCO
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
    }
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if(mAudioManager.isBluetoothScoOn()){
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
        if (requestCode == 0){
            BroadcastReceiver mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();//找到设备
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                            Log.v(TAG, "find device:" + device.getName() + device.getAddress());
                        }
                    }
                    else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        setTitle("搜索完成");
                    }
                }
            };
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiver, filter);
            final AlertDialog.Builder ad1 = new AlertDialog.Builder(BlueToothTest.this);
            ad1.setTitle("录音:");
            ad1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    startRecording();
//                Log.i("111111", ModifyCurSec.getText().toString());
                }
            });
            ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    stopRecording();
                }
            });
            ad1.show();// 显示对话框
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
