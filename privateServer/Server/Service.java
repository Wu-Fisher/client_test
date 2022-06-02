package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Service extends Thread {
    Socket socket;
    String content;
    String content2;
    BufferedReader reader;
    List<String> lines;
    String out_txt_path = "Server/pyout.txt";
    String in_txt_path = "Server/pyin.txt";
    String send_file_path = "Server/ttt.csv";
    String python_exec = "python Server/test.py";

    public Service(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while ((content = reader.readLine()) != null) {
                System.out.println(content);
                if (content.equals("run")) {
                    runPython();
                    writeToFile(this.out_txt_path, false);
                    sendMessage(socket, "over");
                } else if (content.equals("exit")) {
                    System.out.println("用户" + socket.getPort() + "下线啦！");
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                    break;
                } else if (content.equals("get")) {
                    sendFile(this.send_file_path);
                } else if (content.equals("Mstart")) {
                    MgetMessage();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void MgetMessage() throws IOException {
        System.out.println("Mstart");
        lines = new ArrayList<String>();
        while ((content2 = reader.readLine()) != null) {
            if (content2.equals("Mend")) {
                System.out.println("Mend");
                writeToFile(this.in_txt_path, false);
                break;
            }
            System.out.println(content2);
            lines.add(content2);
        }
    }

    public void sendMessage(Socket socket, String context) {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream());) {
            writer.println(context);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public String getContent(Socket socket) {
    // String content = "";
    // try {
    // BufferedReader br = new BufferedReader(new
    // InputStreamReader(socket.getInputStream()));
    // content = br.readLine();
    // } catch (Exception e) {

    // e.printStackTrace();
    // }
    // return content;
    // }

    public void runPython() {
        System.out.println("java test");
        Process proc;
        try {
            proc = Runtime.getRuntime().exec(python_exec);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            lines = new ArrayList<String>();
            while ((line = in.readLine()) != null) {
                lines.add(line);
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

    public void writeToFile(String path, boolean isHead) throws IOException {
        FileWriter fw = new FileWriter(path, isHead);
        BufferedWriter bw = new BufferedWriter(fw);
        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }
        bw.close();
    }

    public void sendFile(String path) throws IOException {
        try {
            File file = new File(path);
            FileInputStream fis;
            DataOutputStream dos;
            if (file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(socket.getOutputStream());

                // 文件名和长度
                dos.writeUTF(file.getName());
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();

                // 开始传输文件
                System.out.println("======== 开始传输文件 ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100 * progress / file.length()) + "% |");
                }
                System.out.println();
                System.out.println("======== 文件传输成功 ========");
                if (fis != null)
                    fis.close();
                if (dos != null)
                    dos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
