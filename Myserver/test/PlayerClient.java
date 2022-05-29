package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerClient {
    public String name = "p0";
    public String score = "0";
    public String opscore = "0";

    public boolean isPlaying = false;
    public boolean isReady = false;
    public boolean isBusy = false;
    public boolean isWaiting = false;
    public boolean isAllOver = false;

    public void resetButNotExit() {
        isPlaying = false;
        isReady = false;
        isBusy = false;
        isWaiting = false;
        isAllOver = false;

        name = "p0";
        score = "0";
        opscore = "0";

    }

    public String playerName = "testplayer";

    public Socket socket;
    public String acc = "10.249.8.149";
    public boolean isConnected = false;

    public List<RankListData> ranklist = new ArrayList<RankListData>();

    ExecutorService ReadThreadExecutor;
    ExecutorService WriteThreadExecutor;
    BufferedReader br;
    PrintWriter pw;;

    public PlayerClient(String acc, int port) {
        isConnected = Connect(acc, port);
    }

    public boolean Connect(String acc, int port) {
        try {
            this.acc = acc;
            this.socket = new Socket(acc, port);
            ReadThreadExecutor = Executors.newSingleThreadExecutor();
            WriteThreadExecutor = Executors.newSingleThreadExecutor();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {

            e.printStackTrace();
            isConnected = false;
            return false;
        }
        return true;
    }

    public void resetExecuter()

    {
        try {
            if (ReadThreadExecutor != null)
                ReadThreadExecutor.shutdownNow();
            if (WriteThreadExecutor != null)
                WriteThreadExecutor.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();

        }
        ReadThreadExecutor = Executors.newSingleThreadExecutor();
        WriteThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public void resetWriterAndReader() {
        // try {
        // if (br != null)
        // br.close();
        // if (pw != null)
        // pw.close();
        // } catch (Exception e) {
        // e.printStackTrace();

        // }
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 及时更新调用

    public void setYourScore(int score) {
        this.score = Integer.toString(score);
    }

    public int getOppScore() {
        int ans = 0;
        try {
            ans = Integer.parseInt(this.opscore);
        } catch (Exception e) {
            ans = 0;
        }
        return ans;
    }

    // 阻塞三秒查看是否ready
    public boolean checkReady() {
        for (int i = 0; i < 15; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isReady) {
                isPlaying = true;
                return true;
            } else if (isBusy) {
                isBusy = true;
                return true;
            }
        }
        return false;
    }

    public void callPK() {

        WriteThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {

                sendContent("requestpk", socket);
            }
        });

        ReadThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String content = br.readLine();
                    if (content.equals("p1")) {
                        name = "p1";
                    } else if (content.equals("p2")) {

                        if (name.equals("p1")) {
                            isReady = true;
                        } else {
                            name = "p2";
                            isReady = true;
                        }
                    } else if (content.equals("busy")) {
                        isBusy = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void sendYourScore() {

        WriteThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                sendContent(score, socket);
            }
        });
    }

    public void updateOppScore() {

        WriteThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                sendContent("otherscore", socket);
            }
        });

        ReadThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                opscore = getContent(socket);
            }
        });

    }

    public void sendOver() {

        sendContent("over", socket);
    }

    public void gameOver(int score) {
        setYourScore(score);
        sendContent(this.score, socket);
        sendOver();
    }

    public void waitOppGameOver() {
        while (true) {
            try {
                System.out.println("st wait");
                String content = "wait";
                sendContent(content, socket);
                String reply = getContent(socket);
                if (reply.equals("over")) {
                    isAllOver = true;
                    break;
                } else {
                    System.out.println("wait opp");
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int oopDataFinal() {
        String content = "0";
        sendContent("otherscore", socket);
        do {
            content = getContent(socket);
        } while (!content.matches("[0-9]+"));
        opscore = content;
        return getOppScore();

    }

    public void sendExit() {
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // // TODO Auto-generated method stub
        // try {
        // Thread.sleep(1000);
        // String content = "exit";
        // sendContent(content, socket);
        // } catch (Exception e) {
        // e.printStac
        try {
            Thread.sleep(1000);
            String content = "exit";
            sendContent(content, socket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void discoonnect() {
        sendContent("disconnect", socket);
    }

    public void sendContent(String content, Socket socket) {
        try {
            pw.println(content);
            pw.flush();
            System.out.println("send:" + content);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getContent(Socket socket) {
        String content = "";
        try {

            content = br.readLine();
            System.out.println("收到" + content);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return content;
    }

    // 登陆部分

    public boolean register(String name, String account, String password) {
        try {
            pw.println("register");
            pw.flush();
            pw.println(name);
            pw.flush();
            pw.println(account);
            pw.flush();
            pw.println(password);
            pw.flush();
            String content = br.readLine();
            if (content.equals("success")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String accout, String password) {
        try {
            pw.println("login");
            pw.println(accout);
            pw.println(password);
            pw.flush();
            String content = br.readLine();
            if (content.equals("success")) {
                try {
                    playerName = br.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<RankListData> updDatas() {
        try {
            ranklist.clear();
            pw.println("getdata");
            pw.flush();
            while (true) {
                String content = br.readLine();
                if (content.equals("listsendover")) {
                    break;
                }
                String[] parts = content.split(",");
                RankListData score = new RankListData(0, Integer.parseInt(parts[1]), parts[0],
                        TimeUnit.stringToCalendar(parts[2]));
                ranklist.add(score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ranklist;
    }
}
