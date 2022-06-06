package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static String context = "";
    // StringBuilder sb = new StringBuilder();
    List<String> list = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Main a = new Main();
            a.readToList();
            a.listToContext();
            PriClient client = new PriClient("10.249.8.149", 9999);
            client.sendMessage(context);
            client.sendRun();
            client.waitOver();
            client.sendGet();
            client.getFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readToList() {
        try {
            list = new ArrayList<>();
            FileReader fr = new FileReader("privateServer/Client/sample2.html");
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listToContext() {
        try {
            context = "";
            for (String s : list) {
                context += s + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contextToFile() {
        try {
            FileWriter fw = new FileWriter("privateServer/Client/sample2.html");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(context);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
