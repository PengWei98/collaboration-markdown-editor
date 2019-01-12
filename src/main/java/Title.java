public class Title implements Runnable{
    public void run(){
        String text = GUI.editorPane.getText();
        String[] lines = text.split("\n");
        String titles = "";

        for (String line : lines) {
            StringBuffer title = new StringBuffer();
            if (line.length() > 1 && line.charAt(0) == '#') {
                int i = 0;
                int length = line.length();

                for (i = 0; i < length - 1; i++) {
                    if (line.charAt(i) == '#') {
                        if (i != 0) {
                            title.append(" ");
                        }
                    } else {
                        break;
                    }
                }
                title.append(line.substring(i));
            }
            if (!title.toString().equals("")) {
                titles = titles + title.toString() + "\n";
            }
            GUI.textPane.setText(titles);
            try {
                Thread.sleep(100);
            } catch (Exception ex) {

            }
        }
    }
}
