package client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadClient {
    public static void main(String[] args) throws IOException {
        Socket client = null;
        Scanner readFromServer = null;
        PrintStream writeMsgToserver = null;
        try {
            //尝试与服务器建立连接
            client = new Socket("127.0.0.1",7777);
            //获取此连接的输入、输出流
            readFromServer = new Scanner(client.getInputStream());
            writeMsgToserver = new PrintStream(client.getOutputStream());
            //进行数据的输入输出
            writeMsgToserver.println("我是客户端");
            if(readFromServer.hasNext()){
                System.out.println("服务端发来的消息为:"+readFromServer.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //基站关闭
            client.close();
            readFromServer.close();
            writeMsgToserver.close();
        }
    }
}


