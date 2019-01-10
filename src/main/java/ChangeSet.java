import java.util.LinkedList;
import java.util.Queue;

public class ChangeSet {
    Queue<Change> changes = new LinkedList();

    boolean enqueue(Change change){
        if (changes.offer(change)) {
            return true;
        }
        else{
            return false;
        }
    }

    void applyInsert(){

    }

    void applyDelete(){

    }

    void apply(String text){

    }

}
