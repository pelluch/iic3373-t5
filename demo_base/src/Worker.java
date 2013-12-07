import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * Created by pablo on 12/7/13.
 */
public class Worker {

    protected int mWorkerId;
    protected int mPortNumber;
    protected int mNumNeighbors;

    protected ServerSocket mServerSocket = null;
    protected Socket mClientSocket = null;

    public Worker(int workerId, int portNumber, int numNeighbors) {

        mWorkerId = workerId;
        mPortNumber = portNumber;
        mNumNeighbors = numNeighbors;

        try {

            mServerSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void receiveMessages() {

        while(true) {
            try {
                mServerSocket.accept();
                //mClientSocket = new Socket((String) null, mPortNumber);
                // Luego de que recibe, blasocket.send
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void sendMessage(int receiverId) {


    }


}
