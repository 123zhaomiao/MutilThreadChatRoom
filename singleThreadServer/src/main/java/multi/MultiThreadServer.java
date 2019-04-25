package multi;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    public static void main(String[] args) {
        //线程池 创建方式有四种
        //无限制 执行周期短 任务数量大 客户端任务时间我们决定不了
        //单线程池 谁先来先执行谁 第一个人不结束后面人没戏
        //所以我们创建固定线程的线程池
        final ExecutorService executorService =
                Executors.newFixedThreadPool(10);
        try {
            ServerSocket serverSocket = new ServerSocket(7777);
            System.out.println("等待服务端连接...");
            while(true){
                //阻塞函数 直到有连接
                Socket client = serverSocket.accept();
                //客户连接之后处理任务
                executorService.submit(new ExecuteClient(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 注册
 * 私聊
 * 群聊
 * 退出
 * 显示当前在线用户
 * 统计活跃度
 */
class ExecuteClient implements Runnable{
    private final Socket client;
    //存放当前的所有注册用户 new了很多ExecuteClient 将Map设置为静态 ---> 成员共享
    //多线程会有线程安全问题、所有需要一个安全的Map
    private static  final Map<String ,Socket> ONLINE_USER_MAP = new ConcurrentHashMap<>();

    public ExecuteClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //首先获取客户端输入
            Scanner scanner = new Scanner(client.getInputStream());
            scanner.useDelimiter("\n");
            while(true){
                String message = scanner.nextLine();
                //约定格式 按照格式解析
                /**
                 * 1、注册: userName:<name>
                 * 2、私聊：private:<name>:<message>
                 * 3、群聊：group:<message>
                 * 4、退出: bye
                 */
                if(message.startsWith("userName")){
                    String userName = message.split("\\:")[1];
                    register(userName,client);
                    continue;
                }
                if(message.startsWith("private")){
                    String userName = message.split("\\:")[1];
                    String mes = message.split("\\:")[2];
                    privateChat(userName,mes);
                    continue;
                }
                if(message.startsWith("group")){
                    String mes = message.split("\\:")[1];
                    groupChat(mes);
                    continue;
                }
                if(message.equals("bye")){
                    quit();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //注册
    private void register(String userName, Socket client) throws IOException {
        System.out.println(userName + "加入聊天室"+client.getRemoteSocketAddress());
        ONLINE_USER_MAP.put(userName,client);
        //服务端告诉客户端注册成功
        PrintStream sendToClient = new PrintStream(client.getOutputStream());
        sendToClient.println(userName+"注册成功!");
        //打印在线人数
        onLineUser();
    }
    //私聊
    private void privateChat(String userName, String mes) throws IOException {
        //谁给你说
        String currentUserName = getCurrentCuerName();
        //给谁说
        Socket target = ONLINE_USER_MAP.get(userName);
        if(client!=null){
            PrintStream sentToClient = new PrintStream(target.getOutputStream());
            //发送消息
            sentToClient.println(currentUserName + " 对你说："+ mes);
        }
    }
    //群聊
    private void groupChat(String mes) throws IOException {
        for(Socket socket : ONLINE_USER_MAP.values()){
            PrintStream sentToClient = new PrintStream(socket.getOutputStream());
            if(socket.equals(client)){
                continue;
            }
            String currentUserName = getCurrentCuerName();
            //发送消息
            sentToClient.println(currentUserName + " 说："+ mes);
        }
    }

    //退出
    private void quit() throws IOException {
        String currentUserName = getCurrentCuerName();
        System.out.println("用户:"+currentUserName+"下线");
        PrintStream sentToClient = new PrintStream(client.getOutputStream());
        sentToClient.println("bye");
        ONLINE_USER_MAP.remove(currentUserName);
        onLineUser();
    }


    private void onLineUser(){
        System.out.println("当前在线人数："+ONLINE_USER_MAP.size()+"  用户列表如下:");
        for(Map.Entry<String,Socket> entry:ONLINE_USER_MAP.entrySet()){
            System.out.println(entry.getKey());
        }
    }
    private String getCurrentCuerName(){
        String currentUserName = "";
        for(Map.Entry<String,Socket> entry:ONLINE_USER_MAP.entrySet()){
            if(this.client.equals(entry.getValue())){
                currentUserName = entry.getKey();
                break;
            }
        }
        return currentUserName;
    }
}