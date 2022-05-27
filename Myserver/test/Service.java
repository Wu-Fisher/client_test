package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Service extends Thread {
    Socket socket;
    String content;
    String name = "p0";
    String score = "-1";

    BufferedReader reader;
    List<User> userlist = new ArrayList<User>();

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
                    MyServer.socketlist.remove(socket);
                    MyServer.map.remove(name);
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
                            if (MyServer.isOVER[1] == 1 && MyServer.isOVER[0] == 1) {
                                sendMessage(this.socket, "over");
                            } else {
                                sendMessage(this.socket, "wait");
                            }
                        } else {
                            if (MyServer.isOVER[0] == 1 && MyServer.isOVER[1] == 1) {
                                sendMessage(this.socket, "over");
                            } else {
                                sendMessage(this.socket, "wait");
                            }
                        }
                    }
                } else if (content.equals("register")) {
                    synchronized (MyServer.isOVER) {
                        register();
                    }
                } else if (content.equals("login")) {
                    synchronized (MyServer.lock) {
                        login();
                    }
                }

                else {
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

    public String recieveMessage(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void UserListToFile() {
        try {
            FileWriter fw = new FileWriter("user.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (User user : userlist) {
                String content = user.getName() + "," + user.getAccount() + "," + user.getPassword();
                bw.write(content);
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {

        }
    }

    public void FileToUserList() {
        try {
            userlist.clear();
            FileReader fr = new FileReader("user.txt");
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            while ((str = br.readLine()) != null) {
                String[] parts = str.split(",");
                User user = new User(parts[0], parts[1], parts[2]);
                userlist.add(user);
            }
            br.close();
        } catch (Exception e) {
        }

    }

    public void register() {
        try {
            FileToUserList();
            String name = recieveMessage(this.socket);
            String account = recieveMessage(this.socket);
            String password = recieveMessage(this.socket);
            User user = getUser(name, account, password);
            if (user == null) {
                userlist.add(new User(name, account, password));
                UserListToFile();
                sendMessage(this.socket, "success");
            } else {
                sendMessage(this.socket, "failure");
            }
        } catch (Exception e) {

        }

    }

    public void login() {
        try {
            FileToUserList();
            String account = recieveMessage(this.socket);
            String password = recieveMessage(this.socket);
            User user = getUser(account, password);
            if (user != null) {
                sendMessage(this.socket, "success");
                sendMessage(this.socket, user.getName());
            } else {
                sendMessage(this.socket, "failure");
            }

        } catch (Exception e) {

        }
    }

    public User getUser(String account, String password) {
        for (User user : userlist) {
            if (account.equals(user.getAccount()) && password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public User getUser(String name, String account, String password) {
        for (User user : userlist) {
            if (name.equals(user.getName()) || account.equals(user.getAccount())) {
                return user;
            }

        }
        return null;
    }

}
