import java.util.ArrayList;

/**
 * Created by pablo on 12/7/13.
 */
public class Manager extends Worker {

    private int answerCount = 0;

    private ArrayList<Worker> neighbors = new ArrayList<Worker>();
    private ArrayList<Worker> networkGraph = new ArrayList<Worker>();

    public Manager(int workerId, int portNumber, int[] neighbors) {
        super(workerId, portNumber, neighbors);
    }

    @Override
    protected void start() {
        
    }
}
