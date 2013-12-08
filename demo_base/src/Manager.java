import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by pablo on 12/7/13.
 */
public class Manager extends Worker {

    private int answerCount = 0;

    private ArrayList<Worker> networkGraph = new ArrayList<Worker>();

    public Manager(int workerId, int portNumber, int[] neighbors) {
        super(workerId, portNumber, neighbors);
    }

    @Override
    protected void start() {


       				// Array to store the sorted array
        Queue<Task> taskQueue = new LinkedList<Task>();	// Task FIFO list
        System.out.println("Start manager");

        // Initialize hashmap of availability:
        HashMap<Integer, Boolean> busyNeighbors = new HashMap<Integer, Boolean>();
        for(int i = 0; i < mNeighbors.length; i++)
        	busyNeighbors.put(mNeighbors[i], false);

        // Creates first task and puts it in the task queue:
        QuicksortTask firstTask = new QuicksortTask();
        int answerCount = 0;
        Object[] result = new Object[firstTask.getAnswerCount()];
        taskQueue.add(firstTask);

        try {
            mClientSocket = new Socket((String) null, mPortNumber);
            while(answerCount < firstTask.getAnswerCount()){

            	// Main process sends a message to neighbors
                // ------------------------------------------------------------------------------------                
            	Task task;
            	
            	for(int i = 0; i < mNeighbors.length && taskQueue.size() > 0; i++){                
                    task = taskQueue.remove();
                	sendMessage(mNeighbors[i], task);
                }
                
            	//Message m = receiveMessage();

                System.out.println("Answer count: " + answerCount);
            	task = receiveTask();

                ArrayList<Task> nextTasks = task.getNextTasks(result);
                System.out.println("Message received");
                for(int i = 0; i < nextTasks.size(); ++i) {
                    taskQueue.add(nextTasks.get(i));
                }
                answerCount++;
                System.out.println("Number of tasks: " + nextTasks.size());
            }

            // Once ended, print result and send null task to children:
            // ------------------------------------------------------------------------------------
            for(int workerId : mNeighbors){
            	QuicksortTask t = null;
            	sendMessage(workerId, t);
            }
            System.out.println("RESULT");
            System.out.println("==========================================================");
            System.out.print("[");

            for(int i = 0; i < result.length; i++)
                System.out.print((Integer)result[i] + ", ");
            System.out.println("EOT]");
            System.out.println("==========================================================");


        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
