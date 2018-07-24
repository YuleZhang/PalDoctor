package com.example.eric.mylibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Handler;

/**
 * Created by Eric_ on 2018/7/20.
 */

public class Client {
    //需要将下面的IP改为服务器端IP
    private static final int TIMEOUT = 10000;// 10秒
    private static String txtUrl = "http://10.10.26.201:30004/AppServer/SynTxtDataServlet";
    private static String url = "http://10.10.26.201:30004/AppServer/SynDataServlet";
    private static String uploadUrl = "http://10.10.26.201:30004/AppServer/UploadFileServlet";
    private static String fileUrl = "http://10.10.26.201:30004/file.jpg";
    private static String txtFileUrl = "http://10.10.26.201:30004/AppServer/txtFile.txt";
    public static void main(String[] a) throws UnknownHostException, IOException{
        new Client().start();
//        System.out.print("我是用户");
//        InetAddress ia = null;
//        String ip = "";
//        try {
//            ia = ia.getLocalHost();
//            String localname = ia.getHostName();
//            String localip = ia.getHostAddress();
//            ip = localip;
//            System.out.println("本机名称是：" + localname);
//            System.out.println("本机的ip是 ：" + localip);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        try {
//            Socket socket = new Socket(ip, 30004);
//            //获取控制台输入的内容
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//            System.out.print("请输入发送的字符串：");
//            String str = bufferedReader.readLine();
//
//            //给服务端发送消息
//            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
//            printWriter.write(str + "\r\n");
//            printWriter.flush();
//
//            //关闭资源
//            bufferedReader.close();
//            printWriter.close();
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            sendFile(url,fileUrl,"a");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        sendPostRequest();
    }
    public void start() throws UnknownHostException, IOException
    {
        Socket socket=new Socket("192.168.43.118",30004);
        System.out.println("客户端连接成功");
        Scanner scanner=new Scanner(System.in);
        BufferedWriter write=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));     //可用PrintWriter
        BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String readline;
        while(true)                                                 //循环发消息
        {
            readline=scanner.nextLine();
            write.write(readline+'\n');                            //write()要加'\n'
            write.flush();
//			socket.shutdownOutput();
            System.out.println(in.readLine());
        }
//        write.close();
//        in.close();
//        socket.close();
    }
    /**
     * 上传文件
     */
    public static String sendFile(String urlPath, String filePath,
                                  String newName) throws Exception {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        URL url = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
		/* 允许Input、Output，不使用Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
		/* 设置传送的method=POST */
        con.setRequestMethod("POST");
		/* setRequestProperty */

        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary="
                + boundary);
		/* 设置DataOutputStream */
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; "
                + "name=\"file1\";filename=\"" + newName + "\"" + end);
        ds.writeBytes(end);

		/* 取得文件的FileInputStream */
        FileInputStream fStream = new FileInputStream(filePath);
		/* 设置每次写入1024bytes */
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int length = -1;
		/* 从文件读取数据至缓冲区 */
        while ((length = fStream.read(buffer)) != -1) {
			/* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

		/* close streams */
        fStream.close();
        ds.flush();

		/* 取得Response内容 */
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = is.read()) != -1) {
            b.append((char) ch);
        }
		/* 关闭DataOutputStream */
        ds.close();
        return b.toString();
    }
    /**
     * 通过Post方式提交参数给服务器,也可以用来传送json或xml文件
     */
    public static String sendPostRequest(String urlPath,
                                         Map<String, String> params, String encoding) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 如果参数不为空
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // Post方式提交参数的话，不能省略内容类型与长度
                sb.append(entry.getKey()).append('=').append(
                        URLEncoder.encode(entry.getValue(), encoding)).append(
                        '&');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        // 得到实体的二进制数据
        byte[] entitydata = sb.toString().getBytes();
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(TIMEOUT);
        // 如果通过post提交数据，必须设置允许对外输出数据
        conn.setDoOutput(true);
        // 这里只设置内容类型与内容长度的头字段
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        // conn.setRequestProperty("Content-Type", "text/xml");
        conn.setRequestProperty("Charset", encoding);
        conn.setRequestProperty("Content-Length", String
                .valueOf(entitydata.length));
        OutputStream outStream = conn.getOutputStream();
        // 把实体数据写入是输出流
        outStream.write(entitydata);
        // 内存中的数据刷入
        outStream.flush();
        outStream.close();
        // 如果请求响应码是200，则表示成功
        if (conn.getResponseCode() == 200) {
            // 获得服务器响应的数据
            BufferedReader in = new BufferedReader(new InputStreamReader(conn
                    .getInputStream(), encoding));
            // 数据
            String retData = null;
            String responseData = "";
            while ((retData = in.readLine()) != null) {
                responseData += retData;
            }
            in.close();
            return responseData;
        }
        return "sendText error!";
    }

}
