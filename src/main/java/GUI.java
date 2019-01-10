import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;

import javax.swing.*;
import java.awt.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class GUI {
    //    static String text = "";
    static JEditorPane editorPane = new JEditorPane();
    static JScrollPane scrollPane = new JScrollPane(editorPane);
    static JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    static int status = 0;
    static boolean update = false;
    static boolean hasJumped = false;

//    public static void setText(String text){
//        editorPane.setText(text);
//        System.out.println(editorPane.getText());
//    }

    public static void showGUI() {
        //create the frame
        JFrame frame = new JFrame("Markdown Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(800, 500);
        // Center the frame
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        JButton button = new JButton("Left");

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu menu1 = new JMenu("功能");

        menuBar.add(menu1);
        JMenuItem menuItem1 = new JMenuItem("开启线程");
        menu1.add(menuItem1);
        menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (status != 0) {
                    System.out.println("this GUI is not idle!");
                    return;
                }
                try {
                    Server.openServer();
                    status = 1;
                } catch (Exception error) {
                    System.out.println("The port has been used!");
                }

                JOptionPane.showMessageDialog(null, "在对话框内显示的描述性的文字", "标题条文字串", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JMenuItem menuItem2 = new JMenuItem("连接服务器");
        menu1.add(menuItem2);
        menuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (status != 0) {
                    System.out.println("this GUI is not idle!");
                }
                try {
                    Client.openClient("127.0.0.1");
                    status = 2;
                } catch (Exception error) {
                    System.out.println("Can not connect!");
                }
            }
        });

        splitPane.setLeftComponent(button);
        splitPane.setRightComponent(scrollPane);

        frame.add(splitPane);
        frame.setVisible(true);

        editorPane.getDocument().addDocumentListener(new DocumentListener() { //Producer
            public void insertUpdate(DocumentEvent e) {
                System.out.println("insert");
                try {
                    Insert insert = new Insert(e.getOffset(), e.getDocument().getText(e.getOffset(), e.getLength()));
                    System.out.println(insert.retain);
                    System.out.println(insert.insert);
                    if (status == 2) { //It is client
                        try {
                            ClientBuffer.buffer.put(insert);
                        } catch (InterruptedException error) {
                            error.printStackTrace();
                        }
                    }
//                    if(status == 1){
//                        update = true;
//                        System.out.println("You can update");
//                    }
                } catch (BadLocationException error) {
                    error.printStackTrace();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                System.out.println("remove");
                Delete delete = new Delete(e.getOffset() + 1);
                System.out.println(delete.retain);
                if (status == 2) { //It is client
                    try {
                        ClientBuffer.buffer.put(delete);
                    } catch (InterruptedException error) {
                        error.printStackTrace();
                    }
                }
            }

            public void changedUpdate(DocumentEvent e) {
                System.out.println("change");
            }
        });

    }

    public static void main(String[] a) {
        showGUI();
    }
}