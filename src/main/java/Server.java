
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    static void openServer() {
        new Thread(new WaitForConnect()).start();
        new Thread(new UpdateServerText()).start();


    }
}

class WaitForConnect implements Runnable {
    public void run() {
        try {
            ServerSocket server = new ServerSocket(4618);
            Socket client = null;
            while (true) {
                //等待客户端的连接，如果没有获取连接
                client = server.accept();
                System.out.println("与客户端连接成功！");
                JOptionPane.showMessageDialog(null, "有一名参与者进入了协同编辑", "提示", JOptionPane.INFORMATION_MESSAGE);
                //为每个客户端连接开启一个线程
                new Thread(new ServerThread(client)).start();
                new Thread(new SendText(client)).start();
            }
        } catch (Exception ex) {
            System.out.println("fail to connect!");
        }
    }
}

class ServerThread implements Runnable {

    private Socket client = null;

    public ServerThread(Socket client) {
        this.client = client;
    }

    public void run() {
        ObjectInputStream is = null;
        while (true) {
            try {
                InputStream in = client.getInputStream();
                if (in.available() > 0) {
                    is = new ObjectInputStream(new BufferedInputStream(in));
                    Object obj = is.readObject();
                    Change change = (Change) obj;
                    try {
                        ServerBuffer.buffer.put(change);
                    } catch (InterruptedException error) {
                        error.printStackTrace();
                    }
                    System.out.println("Server read:" + change.retain);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.getException();
            }
        }
    }
}

class UpdateServerText implements Runnable {

    public void run() {
        while (true) {
            OperationalTransformation.textTransformation();
        }
    }
}


class SendText implements Runnable {

    private Socket client;

    public SendText(Socket client) {
        this.client = client;
    }


    public void run() {
        try {
//            //获得输出流
            OutputStream os = client.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            while (true) {
                TextBuffer.buffer.take();
                String text = GUI.editorPane.getText();
                if (!text.equals("")) {
                    System.out.println("I will send:" + text);
                    writer.write(text.replace('\n','~'));
//                    writer.write("hello\r\n\r\n");
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }
}
