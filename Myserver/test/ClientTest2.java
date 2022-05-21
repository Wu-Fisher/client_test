package test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest2 {
    static String acc = "10.249.9.101";
    static int port = 9999;

    public static void main(String[] args) throws UnknownHostException, IOException {
        PlayerClient pc = new PlayerClient(9999);
        int score = 0;
        int oppscore = 0;
        while (true && !pc.isBusy) {
            try {
                pc.callPK();
                Thread.sleep(300);
                if (pc.checkReady()) {
                    System.out.println("ready");
                    break;
                } else {
                    System.out.println("not ready");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (pc.isBusy) {
            System.out.println("busy");
        } else {
            System.out.println("开始游戏");
            for (int i = 0; i < 40; i++) {
                try {
                    score += 120;
                    pc.setYourScore(score);
                    pc.sendYourScore();
                    Thread.sleep(100);
                    pc.updateOppScore();
                    oppscore = pc.getOppScore();
                    System.out.println("your score: " + score + " opp score: " + oppscore);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                pc.gameOver(score);
                pc.waitOppGameOver();
                oppscore = pc.oopDataFinal();
                System.out.println("最终得分:" + score + "对手得分:" + oppscore);
            } catch (Exception e) {

            }
        }

    }
}
