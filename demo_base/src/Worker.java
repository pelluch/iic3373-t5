import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.OutputStream;

/**
 * Created by pablo on 12/7/13.
 */
public class Worker {

    protected int mWorkerId;
    protected int mPortNumber;
    protected int[] mNeighbors;
    protected final static int MANAGER_ID = 0;
    protected Socket mClientSocket = null;

    public Worker(int workerId, int portNumber, int[] neighbors) {

        mWorkerId = workerId;
        mPortNumber = portNumber;
        mNeighbors = neighbors;

        System.out.println("mWorker id = " + mWorkerId);
        System.out.println("mPortNumber id = " + mPortNumber);
        System.out.println("mNeighbors = " + mNeighbors);
    }

    protected int getMessageLength() throws IOException {

        int a = mClientSocket.getInputStream().read();
        int b = mClientSocket.getInputStream().read();

        int length = (256 * a + b);
        return length;

    }

    protected QuicksortTask getTaskFromInput() throws Exception {

        return null;
    }

    protected void start() {

        try {
            mClientSocket = new Socket((String) null, mPortNumber);
            QuicksortTask task = null;

            do{
                System.out.println("Before WORKER " + mWorkerId + " receive message");
                task = receiveMessage();
                System.out.println("After WORKER " + mWorkerId + " receive message");
                if(task != null){
                    QuicksortTask result = task.executeTask();
                    sendMessage(0, result);
                }
                // He then sends another message.
            }while(task != null);
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

    protected QuicksortTask receiveMessage() throws Exception {

        System.out.println("Getting input stream");
        InputStream in = mClientSocket.getInputStream();
        System.out.println("Getting length");
        int length = getMessageLength();
        System.out.println("Length is " + length);
        byte[] buffer = new byte[length];
        System.out.println("Reading buffer");

        in.read(buffer, 0, length);

        Message m = (Message) SerializationUtilities.deserialize(buffer, 0, length);

        // Now we get the task:
        byte[] payload = m.getPayload();
        QuicksortTask task = (QuicksortTask)SerializationUtilities.deserialize(payload, 0, payload.length);


        return task;

    }
    protected void sendMessage(int receiverId, QuicksortTask task) throws Exception {

        OutputStream out = mClientSocket.getOutputStream();
        // Sserialize task
        byte[] payload = SerializationUtilities.serialize(task);

        Message m = new Message(mWorkerId, receiverId, payload);

        // Now we serialize the message:
        byte[] data = SerializationUtilities.serialize(m);

        // two first bytes indicate the length in big endian format
        int a, b;

        a = (data.length / 256);
        b = (data.length % 256);

        out.write(a);
        out.write(b);
        out.write(data);


    }


}
