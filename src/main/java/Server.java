
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    static void openServer() throws Exception {
//        ServerSocket server = new ServerSocket(4618);
//        Socket client = null;
//        while (true) {
//            //等待客户端的连接，如果没有获取连接
//            client = server.accept();
//            System.out.println("与客户端连接成功！");
//            //为每个客户端连接开启一个线程
//            new Thread(new ServerThread(client)).start();
//        }
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
                //为每个客户端连接开启一个线程
                new Thread(new ServerThread(client)).start();
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
        try {
//            new Thread(new PassText(client)).start();
            while (true) {
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
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {

        } finally {
            try {
                is.close();
            } catch (Exception ex) {
            }
            try {
                client.close();
            } catch (Exception ex) {
            }
        }
    }
}

class UpdateServerText implements Runnable {

    public void run() {
//        String text = GUI.editorPane.getText();
        while (true) {
            OperationalTransformation.textTransformation(GUI.editorPane.getText());
        }
    }
}

class PassText implements Runnable {

    private Socket client = null;

    public PassText(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            ObjectOutputStream os = null;
            while(true) {
                os = new ObjectOutputStream(client.getOutputStream());
                os.writeObject(new Text(GUI.editorPane.getText()));
                os.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}