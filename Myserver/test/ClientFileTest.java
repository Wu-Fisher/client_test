package test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClientFileTest {
    static String acc = "10.249.8.149";
    static int port = 9999;

    public static void main(String[] args) throws UnknownHostException, IOException {
        PlayerClient pc = new PlayerClient(acc, port);
        RankListData a = new RankListData(0, 1000, "A", Calendar.getInstance());
        RankListData b = new RankListData(1, 2000, "B", Calendar.getInstance());
        RankListData c = new RankListData(2, 3000, "C", Calendar.getInstance());
        List<RankListData> list = new ArrayList<RankListData>();

        System.out.println("测试发送数据");
        pc.doAddDate(a);
        pc.doAddDate(b);
        pc.doAddDate(c);
        System.out.println("测试发送数据完成");
        System.out.println("测试接收数据");
        list = pc.updDatas();
        printList(list);
        System.out.println("测试接收数据完成");
        System.out.println("测试删除数据");
        pc.doDeleteDate(a);
        System.out.println("接受新数据");
        list = pc.updDatas();
        printList(list);
        System.out.println("测试删除数据完成");

    }

    static void printList(List<RankListData> list) {
        for (RankListData r : list) {
            System.out.println(r.getName() + " " + r.getScore());
        }
    }
}
