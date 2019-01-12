import java.io.IOException;

public class OperationalTransformation {


    static String transformation() {
        try {
//            System.out.println("The text is " + text);
            Change change = ServerBuffer.buffer.take();
            String text = GUI.editorPane.getText();
            if (change.getClass() == Insert.class) {
                Insert insert = (Insert) change;
//                System.out.println("retain:" + insert.retain);
//                System.out.println("insert:" + insert.insert);

                text = text.substring(0, insert.retain) + insert.insert + text.substring(insert.retain);
                return text;
            } else {
                if (change.getClass() == Delete.class) {
                    Delete delete = (Delete) change;
                        text = text.substring(0, delete.retain - delete.length) + text.substring(delete.retain);
                        return text;
//                    }
                } else {
                    System.out.println("type error!");
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    static String textTransformation() {
        String text = transformation();

        GUI.editorPane.setText(text);
        return text;

    }
}
