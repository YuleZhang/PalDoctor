package com.example.eric.Core;

import android.annotation.SuppressLint;
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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.text.Format;
import java.util.Date;

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
    @SuppressLint("SdCardPath")
    public int FileSend(String oper, String user, String path, String ipAddress, int port){
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, port),5000);
            socket.setSoTimeout(30000);
            long filelen = 0;
            int buffersize = 8192;
            byte[] buf = new byte[buffersize];
            DataInputStream input;
            DataOutputStream output=  new DataOutputStream(socket.getOutputStream());
            DataInputStream getAck = new DataInputStream(socket.getInputStream());
            output.writeUTF(user);
            output.flush();
            //将要执行的操作+文件名传过去传过去
            output.writeUTF(oper);
            System.out.println("The operation is: "+oper);
            output.flush();
            if(oper.equals("UpLoad")) {
                File file = new File(path);
                System.out.println(path);
                if(!file.exists()){
                    return -1;
                }
                input = new DataInputStream(new FileInputStream(path));
                output.writeUTF(path);
                output.flush();
                //将文件长度传过去
                output.writeLong((long) file.length());
                output.flush();
                int readSize = 0;
                while ((readSize = input.read(buf))!=-1){
                    output.write(buf,0,readSize);
                }
                output.flush();
                input.close();
                output.close();
                socket.close();
                getAck.close();
                return 1;
            }
            else {
                path = File.separator+ "sdcard"+File.separator;
//                String sdStatus = Environment.getExternalStorageState();
//                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//                    return 0;
//                }
                input = new DataInputStream(socket.getInputStream());
                String downFileName = input.readUTF();
                System.out.println("文件名称为: "+downFileName);
                File file = new File(path+downFileName);
                if(!file.exists()){
                    file.createNewFile();
                }
                filelen = input.readLong();//读取文件长度
                System.out.println("文件长度为: "+filelen);
                output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                int readSize = 0;
                while ((readSize = getAck.read(buf))!=-1){
                    output.write(buf,0,readSize);
                }
                output.flush();
                input.close();
                output.close();
                socket.close();
                getAck.close();
                return 1;
            }
        }
        catch (SocketTimeoutException se){
            return 0;//超时
        }catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
