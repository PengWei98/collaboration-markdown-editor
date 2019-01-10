import java.util.concurrent.ArrayBlockingQueue;

public class ServerBuffer {
    static ArrayBlockingQueue<Change> buffer =
            new ArrayBlockingQueue(200);
}
