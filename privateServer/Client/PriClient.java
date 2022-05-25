package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

public class PriClient {
    Socket socket;
    String acc;
    int port;

    public PriClient(String acc, int port) throws UnknownHostException, IOException {
        this.acc = acc;
        this.port = port;
        this.socket = new Socket(acc, port);
    }

    public void sendRun() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("发送运行指令");
            out.println("run");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void waitOver() {
        try {
            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            System.out.println("等待结束指令");
            String content = in.readLine();
            if (content.equals("over")) {
                System.out.println("运行结束");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGet() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("发送获取指令");
            out.println("get");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendExit() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("发送退出指令");
            out.println("exit");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataInputStream dis;

    private FileOutputStream fos;

    public void getFile() {
        try {
            dis = new DataInputStream(socket.getInputStream());

            // 文件名和长度
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();
            File directory = new File("recieve");
            if (!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
            fos = new FileOutputStream(file);

            // 开始接收文件
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
            }
            System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength)
                    + "] ========");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (dis != null)
                    dis.close();
                socket.close();
            } catch (Exception e) {
            }
        }
    }

    private static DecimalFormat df = null;

    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    private String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if (size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if (size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if (size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }

}
