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

    public Socket socket;
    public String acc = "10.249.9.101";

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
                // TODO Auto-generated method stub
                try {
                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    String content = "requestpk";
                    pw.println(content);
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                // TODO Auto-generated method stub
                try {
                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    String content = score;
                    pw.println(content);
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateOppScore() {

        WriteThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    String content = "otherscore";
                    pw.println(content);
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ReadThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String content = br.readLine();
                    opscore = content;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void sendOver() {

        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String content = "over";
            pw.println(content);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gameOver(int score) {
        setYourScore(score);
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String content = this.score;
            pw.println(content);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendOver();
    }

    public void waitOppGameOver() {
        while (true) {
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                String content = "wait";
                pw.println(content);
                pw.flush();
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                // BufferedReader br = new BufferedReader(new
                // InputStreamReader(socket.getInputStream()));
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
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String content = "otherscore";
            pw.println(content);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String content = br.readLine();
            opscore = content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getOppScore();

    }

    public void sendExit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(1000);
                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    String content = "exit";
                    pw.println(content);
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ReadThreadExecutor.shutdown();
        WriteThreadExecutor.shutdown();
    }
}
