import java.util.concurrent.ArrayBlockingQueue;

public class Text implements java.io.Serializable{
    String text;
    public Text(String text){
        this.text = text;
    }
}

class TextBuffer{
    static ArrayBlockingQueue<Text> buffer =
            new ArrayBlockingQueue(100);
}
