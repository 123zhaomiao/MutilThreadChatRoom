package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket  = null;
        Scanner readFromClient = null;
        PrintStream sendMegToClient = null;
        try {
            serverSocket  = new ServerSocket(7000);
            System.out.println("等待客户端连接....");
            //2、一直阻塞，直到有客户端连接---就可以与客户端连接
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为"+client.getPort());
            //3、获取此连接的输入流和输出流 输入使用Scanner 输出使用PrintStream
            //3.1、获取此连接的输入流、用户读取客户端消息
            //3.2、获取此连接的输出流、用户给客户端发消息
            readFromClient = new Scanner(client.getInputStream());
            sendMegToClient = new PrintStream(
                    //autoFlush 自动刷新缓冲区
                    //encoding 指定通信编码
                    client.getOutputStream(),true,"UTF-8"
            );
            //表示有数据的输入
            if(readFromClient.hasNext()){
                System.out.println("客户端说:"+readFromClient.nextLine());
            }
            sendMegToClient.println("我是服务器");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //基站关闭
            serverSocket.close();
            readFromClient.close();
            sendMegToClient.close();
        }
    }
}
