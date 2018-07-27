package com.example.eric.mylibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        final String path = "C://Users//Eric_//Desktop//新建文件夹//"; //接受文件路径
//         服务器端用于监听Socket的线程
        Thread listener = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server = new ServerSocket(port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (server != null) {
                    while (true) {
                        ReceiveFile(path);
                    }
                }
            }
        });
        listener.start();
    }
    public static String ReceiveFile(String path) {
        try {
            Socket socket = server.accept();
            System.out.println("客户端"+ socket.getInetAddress() +"已连接");
            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];//数据存储
            long donelen = 0;//传输完成的数据长度
            long filelen = 0;//文件长度
            //将socket数据作为数据输入流
            DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //以客户端的IP地址作为存储路径
            String fileDir = path + "\\" + socket.getInetAddress().toString().substring(1, socket.getInetAddress().toString().length());;
            File file = new File(fileDir);
            //判断文件夹是否存在，不存在则创建
            if(!file.exists())
            {
                file.mkdir();
            }

            String fileName = input.readUTF();//读取文件名

            //设置文件路径
            String filePath = fileDir + "\\" + fileName;


            file = new File(filePath);

            if(!file.exists())
            {
                file.createNewFile();
            }

            DataOutputStream fileOut = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(file)));


            filelen = input.readLong();//读取文件长度

            System.out.println("文件的长度为:" + filelen + "\n");
            System.out.println("开始接收文件!" + "\n");
            DataOutputStream ack = new DataOutputStream(socket.getOutputStream());

            while (true) {
                int read = 0;
                if (input != null) {
                    read = input.read(buf);
                    ack.writeUTF("OK");//结束到数据以后给client一个回复
                }

                if (read == -1) {
                    break;
                }
                donelen += read;
                // 下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
                System.out.println("文件接收了" + (donelen * 100 / filelen)
                        + "%\n");
                fileOut.write(buf, 0, read);
            }

            if(donelen == filelen)
                System.out.println("接收完成，文件存为" + file + "\n");
            else
            {
                System.out.printf("IP:%s发来的%s传输过程中失去连接\n",socket.getInetAddress(),fileName);
                file.delete();
            }
            ack.close();
            input.close();
            fileOut.close();
            return fileDir+"接受完成";
        } catch (Exception e) {
            return "接收错误:\n" + e.getMessage();
        }
    }
}
