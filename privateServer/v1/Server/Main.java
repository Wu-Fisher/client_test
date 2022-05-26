package Server;

public class Main {
    public static void main(String[] args) {
        MyServer server = new MyServer(9999);
        server.work();
    }
}
