package test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class ClientTest {
    static String acc = "10.249.8.149";
    static int port = 9999;

    public static void main(String[] args) throws UnknownHostException, IOException {
        PlayerClient pc = new PlayerClient(acc, port);
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
            for (int i = 0; i < 10; i++) {
                try {
                    score += 100;
                    pc.setYourScore(score);
                    pc.sendYourScore();
                    pc.updateOppScore();
                    oppscore = pc.getOppScore();
                    System.out.println("your score: " + score + " opp score: " + oppscore);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 以下方法回阻塞主线程
            try {
                System.out.println("over");
                pc.gameOver(score);
                System.out.println("start wait");
                pc.waitOppGameOver();
                oppscore = pc.oopDataFinal();
                pc.sendExit();
                System.out.println("最终得分:" + score + "对手得分:" + oppscore);
                pc.discoonnect();
            } catch (Exception e) {

            }
        }

    }
}
