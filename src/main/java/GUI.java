import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

import javax.swing.*;
import java.awt.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


class MyEditorPane extends JEditorPane {
    boolean lock1 = false;
    boolean lock2 = false;

    @Override
    public void setText(String text) {
        lock1 = true;
        lock2 = true;
        super.setText(text);
    }
}

public class GUI {
    static MyEditorPane editorPane = new MyEditorPane();
    static JScrollPane scrollPane = new JScrollPane(editorPane);
    static JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    static JTextPane textPane = new JTextPane();
    static int status = 0;
    static boolean preview = false;
    static String text = "";

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

        final JMenuItem menuItem3 = new JMenuItem("预览");
        menu1.add(menuItem3);

        final JMenuItem menuItem4 = new JMenuItem("取消预览");
        menu1.add(menuItem4);
        menuItem4.setEnabled(false);


        menuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text = editorPane.getText();
                editorPane.setContentType("text/html");
                MutableDataSet options = new MutableDataSet();
                options.setFrom(ParserEmulationProfile.MARKDOWN);
                options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[] { TablesExtension.create()}));
                Parser parser = Parser.builder(options).build();
                HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                System.out.println(editorPane.getText());
                Node document = parser.parse(text);
                String html = renderer.render(document);
                System.out.println(html);
                editorPane.setText(html);
                editorPane.setEditable(false);
                menuItem3.setEnabled(false);
                menuItem4.setEnabled(true);
            }
        });

        menuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editorPane.setContentType("text/plain");
                editorPane.setText(text);
                menuItem4.setEnabled(false);
                menuItem3.setEnabled(true);

            }
        });

        splitPane.setLeftComponent(textPane);
        splitPane.setRightComponent(scrollPane);

        frame.add(splitPane);
        frame.setVisible(true);

        editorPane.getDocument().addDocumentListener(new DocumentListener() { //Producer
            public void insertUpdate(DocumentEvent e) {
                System.out.println("insert");
                try {
                    new Thread(new Title()).start();
                    Insert insert = new Insert(e.getOffset(), e.getDocument().getText(e.getOffset(), e.getLength()));
//                    System.out.println(e.getOffset());
//                    System.out.println(e.getLength());

//                    System.out.println(insert.retain);
//                    System.out.println(insert.insert);
                    if (status == 2) { //It is client
                        if (editorPane.lock1) {
                            editorPane.lock1 = false;
                        } else {
                            try {
                                ClientBuffer.buffer.put(insert);
                            } catch (InterruptedException error) {
                                error.printStackTrace();
                            }
                        }
                    }
                    if (status == 1) {
                        try {
                            TextBuffer.buffer.put(new Text(editorPane.getText()));
                        } catch (InterruptedException error) {
                            error.printStackTrace();
                        }
                    }
                } catch (BadLocationException error) {
                    error.printStackTrace();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                new Thread(new Title()).start();
                Delete delete = new Delete(e.getOffset() + e.getLength(), e.getLength());
                System.out.println("Delete" + delete.retain);
                if (status == 2) { //It is client
                    if (editorPane.lock2) {
                        editorPane.lock2 = false;
                    } else {
                        try {
                            ClientBuffer.buffer.put(delete);
                        } catch (InterruptedException error) {
                            error.printStackTrace();
                        }
                    }
                }
                if (status == 1) {
                    try {
                        TextBuffer.buffer.put(new Text(editorPane.getText()));
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
