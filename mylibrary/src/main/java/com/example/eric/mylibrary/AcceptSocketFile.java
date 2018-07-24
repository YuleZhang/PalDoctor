package com.example.eric.mylibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Eric_ on 2018/7/22.
 */

public class AcceptSocketFile {
    static boolean isEnable;
    private static ServerSocket server;
    static int port = 30004;
    public static void main(String[] a) {
//        Handler mhandler = null;
        // 服务器端用于监听Socket的线程
        Thread listener = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log("port1:"+port);
                    server = new ServerSocket(port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (server != null) {
                    while (true) {
                        log("Finalport:"+port);
                        ReceiveFile();
                    }
                }
            }
        });
        listener.start();

    }
    public static String ReceiveFile() {
        log("Receiveport:"+port);
        try {
            // 接收文件名
//            ServerSocket serverSocket=new ServerSocket(30004);
//            Socket socket = serverSocket.accept();
            Socket name = server.accept();
            InputStream nameStream = name.getInputStream();
//            InputStream nameStream = socket.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(nameStream,"UTF-8");
            BufferedReader br = new BufferedReader(streamReader);
            String fileName = br.readLine();
            br.close();
            streamReader.close();
            nameStream.close();
            name.close();
//            socket.close();
            System.out.println("File 接受成功");
            // 接收文件数据
            Socket data = server.accept();
            InputStream dataStream = data.getInputStream();
            File dir = new File("C://Users//Eric_//Desktop//新建文件夹"); // 创建文件的存储路径
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String savePath = "C://Users//Eric_//Desktop//新建文件夹//" + fileName; // 定义完整的存储路径
            FileOutputStream file = new FileOutputStream(savePath, false);
            byte[] buffer = new byte[1024];
            int size = -1;
            while ((size = dataStream.read(buffer)) != -1) {
                file.write(buffer, 0, size);
            }
            System.out.println("data 接受成功");
            file.close();
            dataStream.close();
            data.close();
            System.out.println("接收完成");
            return fileName + "接收完成";
        } catch (Exception e) {
            return "接收错误:\n" + e.getMessage();
        }
    }
    private static void log(String text) {
        System.out.println(text);
    }
}
