package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.util.ElementScanner14;

public class Service extends Thread {
    Socket socket;
    String content;
    String name = "p0";
    String score = "-1";
    BufferedReader reader;

    public Service(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while ((content = reader.readLine()) != null) {
                System.out.println(content);
                if (content.equals("exit")) {
                    System.out.println("Client disconnected");
                    // PrintWriter writer = new PrintWriter(socket.getOutputStream());
                    // writer.println(socket.getPort() + content);
                    // writer.flush();
                    MyServer.socketlist.clear();
                    MyServer.map.clear();
                    Arrays.fill(MyServer.isOVER, 0);
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } else if (content.equals("requestpk")) {
                    switch (MyServer.socketlist.size()) {
                        case 1:
                            sendMessage(this.socket, "p1");
                            this.name = "p1";
                            break;
                        case 2:
                            for (Socket s : MyServer.socketlist) {
                                sendMessage(s, "p2");

                            }
                            break;
                        default:
                            sendMessage(this.socket, "busy");
                            break;
                    }
                } else if (content.equals("otherscore")) {
                    if (this.name == "p1") {
                        sendMessage(this.socket, MyServer.map.getOrDefault("p2", "0"));
                    } else {
                        sendMessage(this.socket, MyServer.map.getOrDefault("p1", "0"));
                    }
                } else if (content.equals("over")) {
                    if (this.name == "p1") {
                        MyServer.isOVER[0] = 1;
                    } else {
                        MyServer.isOVER[1] = 1;
                    }
                } else if (content.equals("wait")) {
                    {
                        if (this.name == "p1") {
                            if (MyServer.isOVER[1] == 1) {
                                sendMessage(this.socket, "over");
                            } else {
                                sendMessage(this.socket, "wait");
                            }
                        } else {
                            if (MyServer.isOVER[0] == 1) {
                                sendMessage(this.socket, "over");
                            } else {
                                sendMessage(this.socket, "wait");
                            }
                        }
                    }
                } else {
                    if (this.name == "p1") {
                        MyServer.map.put("p1", content);
                    } else {
                        MyServer.map.put("p2", content);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void sendMessage(Socket socket, String context) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream());
            writer.println(context);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
