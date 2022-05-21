package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRead extends Thread {
    public Socket socket;
    public String content;

    public ClientRead(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((content = br.readLine()) != null) {
                System.out.println(content);
                if (content.equals(socket.getPort() + "exit")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
