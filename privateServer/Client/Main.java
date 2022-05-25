package Client;

public class Main {
    public static void main(String[] args) {
        try {
            PriClient client = new PriClient("10.249.8.149", 9999);
            client.sendMessage("hello \n world");
            client.sendRun();
            client.waitOver();
            client.sendGet();
            client.getFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
