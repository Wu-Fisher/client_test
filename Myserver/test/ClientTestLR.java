package test;

public class ClientTestLR {
    static String acc = "10.249.8.149";
    static int port = 9999;

    public static void main(String[] args) throws Exception {
        PlayerClient pc = new PlayerClient(acc, port);
        System.out.println("测试注册：");
        boolean a = pc.register("player1", "1234", "5678");
        boolean b = pc.register("player2", "3456", "5678");
        if (a) {

            System.err.println("注册成功");
        } else {
            System.out.println("a already exist");
            System.err.println("注册失败");
        }
        if (b) {
            System.err.println("注册成功");
        } else {
            System.out.println("b already exist");
            System.err.println("注册失败");
        }
        System.out.println("测试重复注册");
        a = pc.register("player1", "1234", "5678");
        if (a) {
            System.out.println("测试失败");
        } else {
            System.err.println("测试成功");
        }
        System.out.println("测试登陆");
        a = pc.login("1234", "5678");
        if (a) {
            System.out.println("测试成功");
            System.out.println("用户登录" + pc.playerName);
        } else {
            System.out.println("登陆失败");
        }

    }
}
