import java.io.IOException;
import java.io.OutputStream;
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


    }

    @Override
    protected void start() {

        int[] array = new int[] { 6, 2, 9, 0, 7, 3, 8, 4, 5, 1 }; 			// Array to sort
        int[] result = new int[array.length];								// Array to store the sorted array
        int arrayLength = array.length;										// length of the array to sort
        Queue<QuicksortTask> taskQueue = new LinkedList<QuicksortTask>();	// Task FIFO list

        // Creates first task and puts it in the task queue:
        QuicksortTask firstTask = new QuicksortTask(0, 1, array);
        taskQueue.add(firstTask);

        try {

            while(answerCount < arrayLength){
                // Main process sends a message to neighbor of id 1
                // ------------------------------------------------------------------------------------
                QuicksortTask task = taskQueue.remove();

                byte[] payload = SerializationUtilities.serialize(task);
                Message m = new Message(0, 1, payload);

                // Now we serialize the message:
                byte[] data = SerializationUtilities.serialize(m);

                OutputStream out = mClientSocket.getOutputStream();
                // two first bytes indicate the length in big endian format
                byte a = (byte)(data.length / 256);
                byte b = (byte)(data.length % 256);

                out.write(a);
                out.write(b);
                out.write(data);


                // Now we read the answer:
                // ------------------------------------------------------------------------------------
                a = (byte)mClientSocket.getInputStream().read();
                b = (byte)mClientSocket.getInputStream().read();


                int length = (256 * a + b);


                byte[] buffer = new byte[length];

                mClientSocket.getInputStream().read(buffer, 0, length);

                m = (Message) SerializationUtilities.deserialize(buffer, 0, length);

                // Now we get the task:
                payload = m.getPayload();
                task = (QuicksortTask)SerializationUtilities.deserialize(payload, 0, payload.length);

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

                    for(int originalIndex = resultPivotPos + 1, index = 0; originalIndex < newArray.length; originalIndex++, index++)
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

                // Once ended, print result and send null task to children (in this case, process 1):
                // ------------------------------------------------------------------------------------
                System.out.println("RESULT");
                System.out.println("==========================================================");
                System.out.print("[");

                for(int i = 0; i < result.length; i++)
                    System.out.print(result[i] + ", ");
                System.out.println("EOT]");
                System.out.println("==========================================================");

                payload = SerializationUtilities.serialize(null);
                m = new Message(0, 1, payload);

                // Now we serialize the message:
                data = SerializationUtilities.serialize(m);

                out = mClientSocket.getOutputStream();
                // two first bytes indicate the length in big endian format
                a = (byte)(data.length / 256);
                b = (byte)(data.length % 256);

                out.write(a);
                out.write(b);
                out.write(data);
            }
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
