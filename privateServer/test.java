import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class test {
    public static void main(String[] args) {
        System.out.println("java test");
        String path = "test.py";
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("python3 test.py");
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            int re = proc.waitFor();
            System.out.println(re);
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("java over\n");
    }

}
