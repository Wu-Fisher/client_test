package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    }

    public void resetNetData() {
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
        realyConnect(acc, port);
    }

    public void realyConnect(String acc,int port){
        new Thread(){
            public void run(){
                isConnected = Connect(acc, port);     
            }
        }.start();
        try{
            int i =0 ;
            while(i<10 && !isConnected){
                Thread.sleep(100);
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
        resetNetData();
        sendContent("requestpk", socket);

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
        resetExecuter();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
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
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String content;
                if ((content = br.readLine()) != null) {
                    if (content.equals("over")) {
                        isAllOver = true;
                        break;
                    } else {
                        System.out.println("wait opp");
                        Thread.sleep(100);
                    }
                } else {
                    System.err.println("error");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int oopDataFinal() {
        String content = "0";

        do {
            sendContent("otherscore", socket);
            content = getContent(socket);
        } while (!content.matches("[0-9]+"));
        opscore = content;
        return getOppScore();

    }

    public void sendExit() {
        try {
            Thread.sleep(1000);
            String content = "exit";
            sendContent(content, socket);
            resetButNotExit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void discoonnect() {
        sendContent("disconnect", socket);
    }

    public void sendContent(String content, Socket socket) {
        try {
            // PrintWriter pw = new PrintWriter(socket.getOutputStream());
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
            // BufferedReader br = new BufferedReader(new
            // InputStreamReader(socket.getInputStream()));
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
            Collections.sort(ranklist, new Comparator<RankListData>() {
                @Override
                public int compare(RankListData o1, RankListData o2) {
                    return o2.getScore() - o1.getScore();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ranklist;
    }

    public boolean doAddDate(RankListData r) {
        try {
            sendContent("adddata", socket);
            String str = r.getName() + "," + r.getScore() + "," + TimeUnit.calenderToString(r.getDate());
            sendContent(str, socket);

            String reply = getContent(socket);
            if (reply.equals("success")) {
                System.out.println("添加成功");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doDeleteDate(RankListData r) {
        try {
            sendContent("deletedata", socket);
            String str = r.getName() + "," + r.getScore() + "," + TimeUnit.calenderToString(r.getDate());
            sendContent(str, socket);
            String reply = getContent(socket);
            if (reply.equals("success")) {
                System.out.println("delete success");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
