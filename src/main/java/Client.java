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
            new Thread(new UpdateText(server)).start();
            ObjectOutputStream os = null;

            try {
                while (true) {

                    Change change = ClientBuffer.buffer.take();
//                    System.out.println();
                    os = new ObjectOutputStream(server.getOutputStream());
                    os.writeObject(change);
                    os.flush();
                }
            } catch (IOException ex) {
                System.out.println("can not send!");
                ex.getStackTrace();
            }
//            catch (ClassNotFoundException ex){
//
//            }
            finally {
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

class UpdateText implements Runnable {

    Socket server;

    public UpdateText(Socket server) {
        this.server = server;
    }

    public void run() {
        try {

            InputStream is = server.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //接收服务器的相应
            while (true) {
                String text = "";
                // 开始接收服务端发送的数据
                text = br.readLine();
                text = text.replace('~', '\n');
                System.out.println("I read " + text);


                String str = null;
                boolean finishUpdate = true;
                String text1 = "";

                System.out.println("I recieved:" + text);
                if (text != null) {

                    text1 = GUI.editorPane.getText();

                    System.out.println("原来的txt:" + text1 + " 要更新为的txt:" + text);
                    if (!text1.equals(text)) {
                        GUI.editorPane.setText(text);
                    }
                }
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }
}





