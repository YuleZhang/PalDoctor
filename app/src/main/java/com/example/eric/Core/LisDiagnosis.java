package com.example.eric.Core;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Eric_ on 2018/7/24.
 */

public class LisDiagnosis {
    private static final String TAG = "LisDiagnosis";
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
    public int FileSend(String fileName, String path, String ipAddress, int port){
        try {
            Socket socket = new Socket(ipAddress, port);//设置socket，并进行连接connect
            int bufferSize = 8192;
            int timeCounter = 0;
            byte[] buf = new byte[bufferSize];//数据存储
            // 选择进行传输的文件
            File file = new File(path);
            Log.v(TAG,"文件长度:" + (int) file.length());
            DataInputStream input = new DataInputStream(new FileInputStream(path));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());//将socket设置为数据的传输出口
            DataInputStream getAck = new DataInputStream(socket.getInputStream());//设置socket数据的来源
            //将文件名传输过去
            output.writeUTF(file.getName());
            output.flush();
            //将文件长度传输过去
            output.writeLong((long) file.length());
            output.flush();
            Log.v(TAG,"begintimeCounter:"+timeCounter);
            int readSize = 0;

            while(true)
            {
                if(input != null)
                {
                    readSize = input.read(buf);
                }
                if(readSize == -1)
                    break;
                output.write(buf, 0, readSize);
                timeCounter++;
                Log.v(TAG,"timeCounter:"+timeCounter);
                if(!getAck.readUTF().equals("OK"))
                {
                    Log.v(TAG,"服务器"+ ipAddress + ":" + port + "失去连接！");
                    break;
                }
            }
            Log.v(TAG,"FinaltimeCounter:"+timeCounter);
            output.flush();
            // 注意关闭socket链接哦，不然客户端会等待server的数据过来，
            // 直到socket超时，导致数据不完整。
            input.close();
            output.close();
            socket.close();
            getAck.close();
            Log.v(TAG,"文件传输完成");
            return 1;
        } catch (Exception e) {
            Log.v(TAG,e.getMessage());
            return -1;
        }
    }
}
