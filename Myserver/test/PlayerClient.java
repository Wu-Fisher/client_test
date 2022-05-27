package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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

    public String playerName = "testplayer";

    public Socket socket;
    public String acc = "10.249.8.149";

    ExecutorService ReadThreadExecutor;
    ExecutorService WriteThreadExecutor;

    public PlayerClient(int port) throws UnknownHostException, IOException {
        this.socket = new Socket(acc, port);
        ReadThreadExecutor = Executors.newSingleThreadExecutor();
        WriteThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public PlayerClient(String acc, int port) throws UnknownHostException, IOException {
        this.acc = acc;
        this.socket = new Socket(acc, port);
        ReadThreadExecutor = Executors.newSingleThreadExecutor();
        WriteThreadExecutor = Executors.newSingleThreadExecutor();
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
                String content = "wait";
                sendContent(content, this.socket);
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String content = br.readLine();
                if (content.equals("over")) {
                    isAllOver = true;
                    break;
                }
                System.out.println("wait opp");
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int oopDataFinal() {
        sendContent("otherscore", socket);
        String content = getContent(socket);
        opscore = content;
        return getOppScore();

    }

    public void sendExit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(1000);
                    String content = "exit";
                    sendContent(content, socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ReadThreadExecutor.shutdown();
        WriteThreadExecutor.shutdown();
    }

    public void sendContent(String content, Socket socket) {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println(content);
            pw.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getContent(Socket socket) {
        String content = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            content = br.readLine();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return content;
    }

    // 登陆部分

    public boolean register(String name, String account, String password) {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println("register");
            pw.println(name);
            pw.println(account);
            pw.println(password);
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println("login");
            pw.println(accout);
            pw.println(password);
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String content = br.readLine();
            if (content.equals("success")) {
                try {
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    playerName = br2.readLine();
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

}
