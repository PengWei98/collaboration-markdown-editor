import java.util.concurrent.ArrayBlockingQueue;

public class ClientBuffer {

    static ArrayBlockingQueue<Change> buffer =
            new ArrayBlockingQueue(200);
}
