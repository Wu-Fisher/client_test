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

    // 用于双方对战
    String name = "p0";
    String score = "0";

    // 储存数据
    String USER_PATH = "user.txt";
    String SCORE_PATH = "score.txt";

    // 读写socket
    BufferedReader reader;
    PrintWriter writer;

    // 程序中的信息
    List<User> userlist = new ArrayList<User>();
    List<RankListData> ranklist = new ArrayList<RankListData>();

    public Service(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while ((content = reader.readLine()) != null) {
                System.out.println(content);

                if (content.equals("discoonnect")) {
                    System.out.println("Client disconnected");
                    MyServer.socketlist.remove(socket);
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } else if (content.equals("requestpk")) {
                    netGame();
                } else if (content.equals("register")) {
                    synchronized (MyServer.lock) {
                        register();
                    }
                } else if (content.equals("login")) {
                    synchronized (MyServer.lock) {
                        login();
                    }

                } else if (content.equals("getdata")) {
                    getScoreList();
                } else if (content.equals("adddata")) {
                    addScore();
                } else if (content.equals("deletedata")) {
                    deleteScore();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void sendMessage(Socket socket, String context) {
        try {
            writer.println(context);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String recieveMessage(Socket socket) {
        try {
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 进行联机游戏阶段
    public void netGame() throws IOException {
        MyServer.map.clear();
        checkPlayer();
        while ((content = reader.readLine()) != null) {
            System.out.println(content);
            if (content.equals("requestpk")) {
                checkPlayer();
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
            } else if (content.equals("exit")) {
                System.out.println("本次游戏结束");
                MyServer.map.clear();
                Arrays.fill(MyServer.isOVER, 0);
                this.name="p0";
                this.score="0";
                
                break;
            } else {
                if (this.name == "p1") {
                    MyServer.map.put("p1", content);
                } else {
                    MyServer.map.put("p2", content);
                }
            }

        }

    }

    public void checkPlayer() {
        synchronized (MyServer.lock) {
            switch (MyServer.map.size()) {
                case 0:
                    sendMessage(this.socket, "p1");
                    this.name = "p1";
                    this.score = "0";
                    MyServer.map.put("p1", "0");
                case 1:
                    if (this.name == "p0") {
                        this.name = "p2";
                        for (Socket s : MyServer.socketlist) {
                            sendMessage(s, "p2");
                        }
                        MyServer.map.put("p2", "0");
                        this.score = "0";
                    }

                case 2:
                    if(this.name=="p0")
                    {
                    sendMessage(this.socket, "busy");
                    }
                    else
                    {
                        sendMessage(this.socket, "p2");
                    }
                }
        }
    }

    // 登陆注册部分

    public void UserListToFile() {
        try {
            FileWriter fw = new FileWriter(USER_PATH);
            BufferedWriter bw = new BufferedWriter(fw);
            for (User user : userlist) {
                String content = user.getName() + "," + user.getAccount() + "," + user.getPassword();
                bw.write(content);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {

        }
    }

    public void FileToUserList() {
        try {
            userlist.clear();
            FileReader fr = new FileReader(USER_PATH);
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

            String name = reader.readLine();
            String account = reader.readLine();
            String password = reader.readLine();
            User user = getUser(name, account, password);
            if (user == null && name != null && password != null && account != null) {
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
            String account = reader.readLine();
            String password = reader.readLine();
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

    // 排行榜部分

    public void ScoreListToFile() {
        try {
            FileWriter fw = new FileWriter(SCORE_PATH);
            BufferedWriter bw = new BufferedWriter(fw);
            for (RankListData score : ranklist) {
                String content = score.getName() + "," + score.getScore() + ","
                        + TimeUnit.calenderToString(score.getDate());
                bw.write(content);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {

        }
    }

    public void FileToScoreList() {
        synchronized (MyServer.lock) {
            try {
                ranklist.clear();
                FileReader fr = new FileReader(SCORE_PATH);
                BufferedReader br = new BufferedReader(fr);
                String str = null;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split(",");
                    RankListData score = new RankListData(0, Integer.parseInt(parts[1]), parts[0],
                            TimeUnit.stringToCalendar(parts[2]));
                    ranklist.add(score);
                }
                br.close();
            } catch (Exception e) {
            }
        }

    }

    public void getScoreList() {
        try {
            FileToScoreList();
            for (RankListData score : ranklist) {
                String content = score.getName() + "," + score.getScore() + ","
                        + TimeUnit.calenderToString(score.getDate());
                sendMessage(this.socket, content);
            }
            sendMessage(this.socket, "listsendover");
        } catch (Exception e) {

        }
    }

    public void addScore() {
        synchronized (MyServer.lock) {
            try {
                FileToScoreList();
                String str = reader.readLine();
                String[] parts = str.split(",");
                RankListData score = new RankListData(0, Integer.parseInt(parts[1]), parts[0],
                        TimeUnit.stringToCalendar(parts[2]));
                ranklist.add(score);
                ScoreListToFile();
                sendMessage(this.socket, "success");
            } catch (Exception e) {

            }
        }
    }

    public void deleteScore() {
        synchronized (MyServer.lock) {
            try {
                FileToScoreList();
                String str = reader.readLine();
                String[] parts = str.split(",");
                RankListData score = new RankListData(0, Integer.parseInt(parts[1]), parts[0],
                        TimeUnit.stringToCalendar(parts[2]));
                for (RankListData s : ranklist) {
                    if (s.getName().equals(score.getName()) && s.getScore() == score.getScore()
                            && TimeUnit.calenderToString(s.getDate())
                                    .equals(TimeUnit.calenderToString(score.getDate()))) {
                        ranklist.remove(s);
                        break;
                    }
                }
                ScoreListToFile();
                sendMessage(this.socket, "success");
            } catch (Exception e) {

            }
        }
    }

}
