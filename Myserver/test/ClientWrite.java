package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWrite extends Thread {
    public Socket socket;

    public ClientWrite(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            while (true) {
                String content = br.readLine();
                pw.println(content);
                pw.flush();
                if (content.equals("exit")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
