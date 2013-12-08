import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pablo on 12/7/13.
 */
public interface Task extends Serializable {

    public Task executeTask();
    public ArrayList<Task> getNextTasks(Object[] currentResults);
    public  int getAnswerCount();

}
