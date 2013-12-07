import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Principal {
    // This simulation assumes the existence of two processes of ids 0 and 1
    // where the one with id 0 is the main process.


    public static void main(String[] args) {
        // Study the received arguments to know what to parse
        for (String a : args) {
            System.out.println(a);
        }

        Socket socket = null;
        try {
            // Port number is included in args[5].
            socket = new Socket((String) null, Integer.parseInt(args[5]));

            if (args.length > 6 && args[6].equals("-main")) {
                // Initialize variables:
            	int[] array = new int[] { 6, 2, 9, 0, 7, 3, 8, 4, 5, 1 }; 			// Array to sort
            	int[] result = new int[array.length];								// Array to store the sorted array
            	int arrayLength = array.length;										// length of the array to sort
            	int answerCount = 0;												// Answer counter
            	Queue<QuicksortTask> taskQueue = new LinkedList<QuicksortTask>();	// Task FIFO list

            	// Creates first task and puts it in the task queue:
            	QuicksortTask firstTask = new QuicksortTask(0, 1, array);
            	taskQueue.add(firstTask);

            	while(answerCount < arrayLength){
                	// Main process sends a message to neighbor of id 1
                    // ------------------------------------------------------------------------------------
            		QuicksortTask task = taskQueue.remove();

                	byte[] payload = SerializationUtilities.serialize(task);
                	Message m = new Message(0, 1, payload);

                	// Now we serialize the message:
                    byte[] data = SerializationUtilities.serialize(m);

                    OutputStream out = socket.getOutputStream();
                    // two first bytes indicate the length in big endian format
                    byte a = (byte)(data.length / 256);
                    byte b = (byte)(data.length % 256);

                    out.write(a);
                    out.write(b);
                    out.write(data);


                    System.out.println("Message length = " + data.length);
                    System.out.println("Message a = " + a);
                    System.out.println("Message b = " + b);
                    System.out.println("Message length2 = " + (a * 256 + b));

                    // Now we read the answer:
                    // ------------------------------------------------------------------------------------
                    a = (byte)socket.getInputStream().read();
                    b = (byte)socket.getInputStream().read();

                    System.out.println("Message a = " + a);
                    System.out.println("Message b = " + b);

                    int length = (256 * a + b);

                    System.out.println("Message length3 = " + length);

                    byte[] buffer = new byte[length];

                    socket.getInputStream().read(buffer, 0, length);

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

                    out = socket.getOutputStream();
                    // two first bytes indicate the length in big endian format
                    a = (byte)(data.length / 256);
                    b = (byte)(data.length % 256);

                    out.write(a);
                    out.write(b);
                    out.write(data);
            	}
            } else {
            	QuicksortTask task = null;

            	do{
                    byte a = (byte)socket.getInputStream().read();
                    byte b = (byte)socket.getInputStream().read();

                    System.out.println("Message a = " + a);
                    System.out.println("Message b = " + b);

                    int length = (256 * a + b);

                    System.out.println("Message length3 = " + length);

                    byte[] buffer = new byte[length];

                    socket.getInputStream().read(buffer, 0, length);

                    Message m = (Message) SerializationUtilities.deserialize(
                            buffer, 0, length);

                    // Now we get the task:
                    byte[] payload = m.getPayload();
                    task = (QuicksortTask)SerializationUtilities.deserialize(payload, 0, payload.length);

                    if(task != null){
	                    task.printTask();
	                    QuicksortTask result = task.executeTask();
	                    result.printTask();

	                    // Now we send the result back to
	                    payload = SerializationUtilities.serialize(result);

	                    m = new Message(1, 0, payload);
	                    byte[] data = SerializationUtilities.serialize(m);

	                    OutputStream out = socket.getOutputStream();
	                    // two first bytes indicate the length in big endian format
	                    a = (byte)(data.length / 256);
	                    b = (byte)(data.length % 256);

	                    out.write(a);
	                    out.write(b);
	                    out.write(data);
                    }
                    // He then sends another message.
            	}while(task != null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
