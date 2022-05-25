package Server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    public static List<Socket> socketlist = new ArrayList<Socket>();

    public static int[] isOVER = new int[2];
    public int port = 9999;

    public MyServer(int port) {
        this.port = port;
    }

    public void work() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("Server IP address: " + address.getHostAddress());

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("对战房已开启！");
            while (true) {
                System.out.println("Waiting for client...");
                Socket socket = serverSocket.accept();
                System.out.println("上线通知： 用户" + socket.getPort() + "上线啦！");
                socketlist.add(socket);
                new Thread(new Service(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
