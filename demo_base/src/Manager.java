import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by pablo on 12/7/13.
 */
public class Manager extends Worker {

    private int answerCount = 0;

    private ArrayList<Worker> neighbors = new ArrayList<Worker>();
    private ArrayList<Worker> networkGraph = new ArrayList<Worker>();

    public Manager(int workerId, int portNumber, int[] neighbors) {
        super(workerId, portNumber, neighbors);

        System.out.println("Creating manager");

    }

    @Override
    protected void start() {

        int[] array = new int[] { 6, 2, 9, 0, 7, 3, 8, 4, 5, 1 }; 			// Array to sort
        int[] result = new int[array.length];								// Array to store the sorted array
        int arrayLength = array.length;										// length of the array to sort
        Queue<QuicksortTask> taskQueue = new LinkedList<QuicksortTask>();	// Task FIFO list
        System.out.println("Start manager");


        // Creates first task and puts it in the task queue:
        QuicksortTask firstTask = new QuicksortTask(0, 1, array);
        taskQueue.add(firstTask);

        try {
            mClientSocket = new Socket((String) null, mPortNumber);
            while(answerCount < arrayLength){
                // Main process sends a message to neighbor of id 1
                // ------------------------------------------------------------------------------------
                QuicksortTask task = taskQueue.remove();
                System.out.println("Before send message");
                sendMessage(1, task);
                System.out.println("After send message");
                task = receiveMessage();
                System.out.println("After receive message");
                // Obtained the result, we then proceed to get its result:
                int resultPivotPos = task.getPivotPos();
                int resultStartIndex = task.getStartIndex();
                int[] resultArray =  task.getArray();

                // Store the resulting pivot in the array:
                result[resultStartIndex + resultPivotPos] = resultArray[resultPivotPos];
                answerCount++;

                // Then we proceed to split the remaining array in 2 subtasks:
                if(resultPivotPos < resultArray.length - 1){
                    int newLength = resultArray.length - 1 - resultPivotPos;
                    int newStartIndex = resultStartIndex + resultPivotPos + 1;
                    int[] newArray = new int[newLength];

                    for(int originalIndex = resultPivotPos + 1, index = 0; originalIndex < resultArray.length; originalIndex++, index++)
                        newArray[index] = resultArray[originalIndex];

                    QuicksortTask newTask = new QuicksortTask(newStartIndex, -1, newArray);
                    taskQueue.add(newTask);
                }
                if(resultPivotPos > 0){
                    int newLength = resultPivotPos;
                    int newStartIndex = resultStartIndex;
                    int[] newArray = new int[newLength];

                    for(int i = 0; i < resultPivotPos; i++)
                        newArray[i] = resultArray[i];

                    QuicksortTask newTask = new QuicksortTask(newStartIndex, -1, newArray);
                    taskQueue.add(newTask);
                }
            }

            // Once ended, print result and send null task to children (in this case, process 1):
            // ------------------------------------------------------------------------------------
           sendMessage(1, null);
            System.out.println("RESULT");
            System.out.println("==========================================================");
            System.out.print("[");

            for(int i = 0; i < result.length; i++)
                System.out.print(result[i] + ", ");
            System.out.println("EOT]");
            System.out.println("==========================================================");


        }
        catch(IOException ioException) {
            System.out.println("Socket exception");
            ioException.printStackTrace();
        }
        catch(java.lang.Exception langException) {
            System.out.println("Error serializing");
            langException.printStackTrace();
        }

    }
}
