/**
 * Created by pablo on 12/7/13.
 */
public class Manager extends Worker {

    private int answerCount = 0;

    public Manager(int workerId, int portNumber, int numNeighbors) {
        super(workerId, portNumber, numNeighbors);
    }

}
