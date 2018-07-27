package com.example.eric.mylibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Eric_ on 2018/7/20.
 */

public class Server {
    static int c = 1;
    public void oneServer() throws IOException
    {
//        String receive;
//        ServerSocket server=new ServerSocket(30004);
//        System.out.println("服务器启动成功");
//        Socket socket=server.accept();
//        BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//        while(true)                                                    //一直等客户端的消息
//        {
//            receive=in.readLine();
//            System.out.println("客户端发来:"+receive);
//            writer.write("文件发送成功"+'\n');               //'\n'一定要加
//            writer.flush();
//        }
//        writer.close();
//        in.close();
//        socket.close();
//        server.close();
    }
    public static void main(String[] args) throws IOException
    {
        c = 2;
        new Server().oneServer();
    }

}
