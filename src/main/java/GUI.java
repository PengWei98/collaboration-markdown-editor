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
import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    static JSplitPane splitPane2 = new JSplitPane();
    static JEditorPane editorPane2 = new JEditorPane();
    static int status = 0;
    static String text = "";

    public static void showGUI() {
        //create the frame
        JFrame frame = new JFrame("Markdown Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(1000, 500);
        // Center the frame
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);


        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu menu1 = new JMenu("功能");

        menuBar.add(menu1);
        JMenuItem menuItem1 = new JMenuItem("邀请协同编辑");
        menu1.add(menuItem1);
        menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (status != 0) {
                    JOptionPane.showMessageDialog(null, "提示：您目前已经参与到协同编辑中，请勿重复", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    Server.openServer();
                    status = 1;
                } catch (Exception error) {
                    System.out.println("The port has been used!");
                }
                try {
                    InetAddress addr = InetAddress.getLocalHost();
                    String ip = addr.getHostAddress();
                    JOptionPane.showMessageDialog(null, "参与者输入'" + ip + "'后即可参与协同编辑", "邀请协同编辑", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    System.out.println("network error!");


                }
            }
        });

        JMenuItem menuItem2 = new JMenuItem("参与协同编辑");
        menu1.add(menuItem2);
        menuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (status != 0) {
                    JOptionPane.showMessageDialog(null, "提示：您目前已经参与到协同编辑中，请勿重复", "提示", JOptionPane.WARNING_MESSAGE);
                }
                try {
                    String ip = JOptionPane.showInputDialog( null, "请输入邀请码：", "参与协同编辑", JOptionPane.QUESTION_MESSAGE );
                    Client.openClient(ip);
                    status = 2;
                } catch (Exception error) {
                    System.out.println("Can not connect!");
                }
            }
        });

        JMenu menu2 = new JMenu("文件");
        menuBar.add(menu2);

        JMenuItem menuItem5 = new JMenuItem("保存文件");
        menu2.add(menuItem5);
        menuItem5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //弹出文件选择框
                JFileChooser chooser = new JFileChooser();

                //后缀名过滤器
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "(*.md)", "md");

                chooser.setFileFilter(filter);


                //下面的方法将阻塞，直到【用户按下保存按钮且“文件名”文本框不为空】或【用户按下取消按钮】
                int option = chooser.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {    //假如用户选择了保存
                    File file = chooser.getSelectedFile();

                    String fname = chooser.getName(file);    //从文件名输入框中获取文件名

                    //假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
                    if (fname.indexOf(".md") == -1) {
                        file = new File(chooser.getCurrentDirectory(), fname + ".md");
//                        System.out.println("renamed");
                        System.out.println(file.getName());
                    }
                    //写文件操作……

                    String sourceString = editorPane.getText();    //待写入字符串
                    byte[] sourceByte = sourceString.getBytes();
                    if (null != sourceByte) {
                        try {
//                                File file = new File(path);        //文件路径（路径+文件名）
                            if (!file.exists()) {    //文件不存在则创建文件，先创建目录
                                File dir = new File(file.getParent());
                                dir.mkdirs();
                                file.createNewFile();
                            }
                            FileOutputStream outStream = new FileOutputStream(file);    //文件输出流用于将数据写入文件
                            outStream.write(sourceByte);
                            outStream.close();    //关闭文件输出流
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            }
        });

        JMenuItem menuItem6 = new JMenuItem("打开文件");
        menu2.add(menuItem6);
        menuItem6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //弹出文件选择框
                String fileName = "";
                JFileChooser fileChooser = new JFileChooser("./");
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(fileChooser);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fileName = fileChooser.getSelectedFile().getAbsolutePath();
                }
                if (!fileName.equals("")) {
                    StringBuilder result = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(fileName));//构造一个BufferedReader类来读取文件
                        String s = null;
                        while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                            result.append(System.lineSeparator() + s);
                        }
                        br.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    editorPane.setText(result.toString());


                }

            }
        });

        JMenuItem menuItem7 = new JMenuItem("导出html");
        menu2.add(menuItem7);
        menuItem7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();

                //后缀名过滤器
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "(*.html)", "html");

                chooser.setFileFilter(filter);


                //下面的方法将阻塞，直到【用户按下保存按钮且“文件名”文本框不为空】或【用户按下取消按钮】
                int option = chooser.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {    //假如用户选择了保存
                    File file = chooser.getSelectedFile();

                    String fname = chooser.getName(file);    //从文件名输入框中获取文件名

                    //假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
                    if (fname.indexOf(".html") == -1) {
                        file = new File(chooser.getCurrentDirectory(), fname + ".html");
//                        System.out.println("renamed");
                        System.out.println(file.getName());
                    }
                    //写文件操作……

//                    String sourceString = editorPane.getText();    //待写入字符串
                    text = editorPane.getText();
//                    editorPane.setContentType("text/html");
                    MutableDataSet options = new MutableDataSet();
                    options.setFrom(ParserEmulationProfile.MARKDOWN);
                    options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[]{TablesExtension.create()}));
                    Parser parser = Parser.builder(options).build();
                    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                    System.out.println(editorPane.getText());
                    Node document = parser.parse(text);
                    String html = renderer.render(document);


                    byte[] sourceByte = html.getBytes();
                    if (null != sourceByte) {
                        try {
//                                File file = new File(path);        //文件路径（路径+文件名）
                            if (!file.exists()) {    //文件不存在则创建文件，先创建目录
                                File dir = new File(file.getParent());
                                dir.mkdirs();
                                file.createNewFile();
                            }
                            FileOutputStream outStream = new FileOutputStream(file);    //文件输出流用于将数据写入文件
                            outStream.write(sourceByte);
                            outStream.close();    //关闭文件输出流
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        textPane.setEditable(false);
        splitPane.setLeftComponent(textPane);
        splitPane.setRightComponent(scrollPane);
        splitPane.setDividerLocation(150);
        splitPane2.setLeftComponent(splitPane);
        splitPane2.setRightComponent(editorPane2);
        splitPane2.setDividerLocation(600);
        editorPane2.setEditable(false);
//        editorPane2.setE
        editorPane2.setContentType("text/html");
//        frame.add(splitPane);
        frame.add(splitPane2);
        frame.setVisible(true);

        editorPane.getDocument().addDocumentListener(new DocumentListener() { //Producer
            public void insertUpdate(DocumentEvent e) {
                try {
                    new Thread(new Title()).start();
                    new Thread(new Preview()).start();
                    Insert insert = new Insert(e.getOffset(), e.getDocument().getText(e.getOffset(), e.getLength()));

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
                new Thread(new Preview()).start();
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
