package com.example.eric.mylibrary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Eric_ on 2018/7/22.
 */

public class SocketDemo {
    static final String path = "D://root.txt";
    static final String fileName = "root.txt";
    static final String ipAddress = "39.105.20.186";
    static final int port = 9999;
    static Socket socket = null;
//    private static final int HOST_PORT = 9999;
    public static void main(String[] a){
//        SocketDemo socketDemo = new SocketDemo()

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(FileSend(fileName, path, ipAddress, port));
            }
        });
        sendThread.start();
//        try {
//            socket = new Socket("10.10.214.89",30004);
//            System.out.println("连接成功");
//        } catch (UnknownHostException e) {
//            System.out.println("连接失败");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("连接失败");
//            e.printStackTrace();
//        }
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public static void sendMsg() {

        try {
            // 创建socket对象，指定服务器端地址和端口号
            socket = new Socket("10.10.214.89", 30004);
            // 获取 Client 端的输出流
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
            // 填充信息
            out.println("妈的，我是客户端");
            System.out.println("msg=" + "妈的，我是客户端");
            // 关闭

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //    public static void sendFile(){
//        Socket s = null;
//        try {
//            ServerSocket ss = new ServerSocket(HOST_PORT);
//            while (true) {
//                String filePath = "leno.jpg";
//                File file = new File(filePath);
//                System.out.println("文件长度:" + (int) file.length());
//                s = ss.accept();
//                log("建立Socket连接");
//                DataInputStream dis = new DataInputStream(
//                        new BufferedInputStream(s.getInputStream()));
//                dis.readByte();
//                DataInputStream fis = new DataInputStream(
//                        new BufferedInputStream(new FileInputStream(filePath)));
//                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//                dos.writeUTF(file.getName());
//                dos.flush();
//                dos.writeLong((long) file.length());
//                dos.flush();
//                int bufferSize = 8192;
//                byte[] buf = new byte[bufferSize];
//                while (true) {
//                    int read = 0;
//                    if (fis != null) {
//                        read = fis.read(buf);
//                    }
//                    if (read == -1) {
//                        break;
//                    }
//                    dos.write(buf, 0, read);
//                }
//                dos.flush();
//                // 注意关闭socket链接哦，不然客户端会等待server的数据过来，
//                // 直到socket超时，导致数据不完整。
//                fis.close();
//                s.close();
//                log("文件传输完成");
//            }
//        }catch (IOException e){
//
//        }
//    }
    public static String FileSend(String fileName, String path, String ipAddress, int port){
        try {
            System.out.println("******");
            Socket name = new Socket(ipAddress, port);
            System.out.println("客户端连接成功");
            OutputStream outputName = name.getOutputStream();
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputName,"UTF-8");
            BufferedWriter bwName = new BufferedWriter(outputWriter);
            bwName.write(fileName);
            bwName.close();
            outputWriter.close();
            outputName.close();
            name.close();
            System.out.println("NAME 发送完成");
            Socket data = new Socket(ipAddress, port);
            OutputStream outputData = data.getOutputStream();
            FileInputStream fileInput = new FileInputStream(path);
            int size = -1;
            byte[] buffer = new byte[1024];
            while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
                outputData.write(buffer, 0, size);
            }
            System.out.println("DATA 发送完成");

            outputData.close();
            fileInput.close();
            data.close();

//            Socket socket = new Socket(ipAddress,port);
//
//            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));     //可用PrintWriter
//            sendFinished=in.readLine();
//            System.out.println(sendFinished);
//            socket.close();
            return fileName + " 发送完成";
        } catch (Exception e) {
            return "发送错误:\n" + e.getMessage();
        }
    }
    private static void log(String text) {
        System.out.println(text);
    }
}
