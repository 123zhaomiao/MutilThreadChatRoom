package multi;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 读线程
 * 获取服务器发来了什么
 */
class ReadDataToServer implements Runnable{
    private  final Socket client;

    public ReadDataToServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
            try {
                Scanner scanner = new Scanner(client.getInputStream());
                scanner.useDelimiter("\n");
                while(scanner.hasNext()){
                    String message = scanner.nextLine();
                    System.out.println("服务器的消息："+message);
                    if(message.equals("bye")){
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}

/**
 * 写线程
 */
class WriteDataToServer implements Runnable{
    private  final Socket client;

    public WriteDataToServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            PrintStream printStream = new PrintStream(client.getOutputStream());
            //获取键盘输入
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            while(true){
                System.out.println("请输入消息:");
                String message = scanner.nextLine();
                printStream.println(message);
                if(message.equals("bye")){
                    //表示客户端要关闭
                    client.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class MultiThreadClient {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("127.0.0.1",7777);
            // 1.向服务器发数据
            new Thread(new WriteDataToServer(client)).start();
            // 2.从服务器读取数据
            new Thread(new ReadDataToServer(client)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
