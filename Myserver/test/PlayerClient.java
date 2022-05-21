package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PlayerClient {
    public String name = "p0";
    public String score = "0";
    public String opscore = "0";

    public boolean isPlaying = false;
    public boolean isReady = false;
    public boolean isBusy = false;

    public Socket socket;
    public String acc = "10.249.9.101";

    public PlayerClient(int port) throws UnknownHostException, IOException {
        this.socket = new Socket(acc, port);
    }

    // 及时更新调用

    public void setYourScore(int score) {
        this.score = Integer.toString(score);
    }

    public int getOppScore() {
        return Integer.parseInt(this.opscore);
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
            }
        }
        return false;
    }

    public void callPK() {
        new Thread(new Runnable() {
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
        }).start();
        new Thread(new Runnable() {
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

        }).start();
    }

    public void sendYourScore() {
        if (isPlaying) {
            new Thread(new Runnable() {
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
            }).start();
        }
    }

    public void KeepsendYourScore() {
        if (isPlaying) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {

                        while (isPlaying) {
                            Thread.sleep(500);
                            PrintWriter pw = new PrintWriter(socket.getOutputStream());
                            String content = score;
                            pw.println(content);
                            pw.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void updateOppScore() {
        if (isPlaying) {
            new Thread(new Runnable() {
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
            }).start();
            new Thread(new Runnable() {

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
            }).start();

        }
    }

    public void KeepupdateOppScore() {
        if (isPlaying) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        while (isPlaying) {
                            Thread.sleep(1000);
                            PrintWriter pw = new PrintWriter(socket.getOutputStream());
                            String content = "otherscore";
                            pw.println(content);
                            pw.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        while (isPlaying) {
                            Thread.sleep(1000);
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String content = br.readLine();
                            opscore = content;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    public void overGame() {
        isPlaying = false;
    }

    public void sendExit() {
        if (isPlaying) {
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
        }
    }
}
