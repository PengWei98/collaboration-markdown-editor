import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

public class Client {
    static void openClient(String IP) throws Exception {


        Socket server = null;
        server = new Socket(IP, 4618);
        System.out.println("与服务器连接成功！");
        //为接受服务器消息令开启一线程
//        new Thread(new ServerThread(client)).start();
        new Thread(new ClientThread(server)).start();
    }

}

class ClientThread implements Runnable {
    Socket server;

    public ClientThread(Socket server) {
        this.server = server;
    }

    public void run() {
        try {
            ObjectOutputStream os = null;
            try {

//                new Thread(new UpdateClientText(server)).start();
                while (true) {
                    Change change = ClientBuffer.buffer.take();
                    System.out.println("Consumer reads " + change.retain);
                    os = new ObjectOutputStream(server.getOutputStream());
                    os.writeObject(change);
                    os.flush();
                }
            } catch (IOException ex) {
                System.out.println("can not send!");
            } finally {
                try {
                    os.close();
                } catch (Exception ex) {
                }
                try {
                    server.close();
                } catch (Exception ex) {
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class UpdateClientText implements Runnable {

    Socket server;

    public UpdateClientText(Socket server) {
        this.server = server;
    }

    public void run() {
        ObjectInputStream is = null;

        try {

            while (true) {
                System.out.println("hello!");
                is = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));
                Text text = (Text) is.readObject();
                if (text != null) {
                    System.out.println("The text is:" + text.text);
                    GUI.editorPane.setText(text.text);
                }
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }
}




