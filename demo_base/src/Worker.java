import java.io.IOException;
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
    }

    protected int getMessageLength() throws IOException {

        byte a = (byte)mClientSocket.getInputStream().read();
        byte b = (byte)mClientSocket.getInputStream().read();

        int length = (256 * a + b);
        return length;

    }

    protected QuicksortTask getTaskFromInput() throws Exception {
        int length = getMessageLength();
        byte[] buffer = new byte[length];

        mClientSocket.getInputStream().read(buffer, 0, length);

        Message m = (Message) SerializationUtilities.deserialize(
                buffer, 0, length);

        // Now we get the task:
        byte[] payload = m.getPayload();
        QuicksortTask task = (QuicksortTask)SerializationUtilities.deserialize(payload, 0, payload.length);
        return task;
    }

    protected void start() {
        QuicksortTask task = null;

        try {
            // He then sends another message.
            do if (task != null) {
                task.printTask();
                QuicksortTask result = task.executeTask();
                result.printTask();
                task = getTaskFromInput();
                // Now we send the result back to
                byte [] payload = SerializationUtilities.serialize(result);

                Message m = new Message(1, 0, payload);
                byte[] data = SerializationUtilities.serialize(m);


            } while(task != null);
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


    protected void sendMessage(int receiverId, byte[] payload) throws IOException {

        OutputStream out = mClientSocket.getOutputStream();
        // two first bytes indicate the length in big endian format

        int a = (byte)(payload.length / 256);
        int b = (byte)(payload.length % 256);

        out.write(a);
        out.write(b);
        out.write(payload);

    }


}
