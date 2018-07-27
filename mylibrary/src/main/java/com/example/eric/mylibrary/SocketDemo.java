package com.example.eric.mylibrary;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

/**
 * 发送文件
 * Created by Eric_ on 2018/7/22.
 */

public class SocketDemo {
    public static void main(String[] a){
        final String path = "D://";//文件路径
        final String fileName = "林俊杰 - 黑武士.mp3";//文件名称
        final String ipAddress = "10.10.214.89";//服务器ip地址
        final int port = 30004;//服务器开放端口
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(FileSend(fileName, path, ipAddress, port));
            }
        });
        sendThread.start();
    }
    public static String FileSend(String fileName, String path, String ipAddress, int port){
        try {
            Socket socket = new Socket(ipAddress, port);//设置socket，并进行连接connect
            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];//数据存储
            // 选择进行传输的文件
            File file = new File(path + fileName);
            System.out.println("文件长度:" + (int) file.length());
            DataInputStream input = new DataInputStream(new FileInputStream(path + fileName));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());//将socket设置为数据的传输出口
            DataInputStream getAck = new DataInputStream(socket.getInputStream());//设置socket数据的来源
            //将文件名传输过去
            output.writeUTF(file.getName());
            output.flush();
            //将文件长度传输过去
            output.writeLong((long) file.length());
            output.flush();

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

                if(!getAck.readUTF().equals("OK"))
                {
                    System.out.println("服务器"+ ipAddress + ":" + port + "失去连接！");
                    break;
                }
            }
            output.flush();// 注意关闭socket链接，不然客户端会等待server的数据过来，// 直到socket超时，导致数据不完整。
            input.close();
            output.close();
            socket.close();
            getAck.close();
            System.out.println("文件传输完成");
            return fileName + " 发送完成";
        } catch (Exception e) {
            return "发送错误:\n" + e.getMessage();
        }
    }
}
