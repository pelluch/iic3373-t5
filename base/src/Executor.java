import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Executor {
    private static final String TEST_FILE_NAME = "test_base.txt";
    private static final int PORT_COUNTER = 10030;

    private int mProcesses;
    private boolean[][] mAdjacencyMatrix;
    private TcpListener[] mListeners;
    private Random mRandom;
    private Semaphore[] mSemaphores;

    private String mProgramName;

    public Executor(String programName) {
        mRandom = new Random();
        mProgramName = programName;

        System.out
        .println("Select an option: 1- Random 5x5 adjacency matrix; 2- Custom adjacency matrix");
        Scanner s = new Scanner(System.in);
        String option = s.nextLine();
        try {
            int i = Integer.parseInt(option);
            if (i == 1) {
                initiateRandomAdjacencyMatrix();
            } else if (i == 2) {
                initiateCustomAdjacencyMatrix();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        s.close();
    }

    private void initiateCustomAdjacencyMatrix() throws FileNotFoundException {
        Scanner s = new Scanner(new File(TEST_FILE_NAME));
        int length = Integer.parseInt(s.nextLine());

        mProcesses = length;
        mListeners = new TcpListener[mProcesses];
        mSemaphores = new Semaphore[mProcesses];
        for (int i = 0; i < mProcesses; i++) {
            mSemaphores[i] = new Semaphore(0);
        }
        mAdjacencyMatrix = new boolean[mProcesses][mProcesses];

        for (int i = 0; i < length; i++) {
            String line = s.nextLine();
            String[] values = line.split(" ");
            for (int j = 0; j < values.length; j++) {
                int v = Integer.parseInt(values[j]);
                mAdjacencyMatrix[i][j] = v == 1;
            }
        }

        s.close();
    }

    private void initiateRandomAdjacencyMatrix() {
        mProcesses = 5;
        mListeners = new TcpListener[mProcesses];
        mSemaphores = new Semaphore[mProcesses];
        for (int i = 0; i < mProcesses; i++) {
            mSemaphores[i] = new Semaphore(0);
        }
        mAdjacencyMatrix = new boolean[mProcesses][mProcesses];

        for (int i = 0; i < mAdjacencyMatrix.length; i++) {
            for (int j = i; j < mAdjacencyMatrix[i].length; j++) {
                if (j == i) {
                    mAdjacencyMatrix[i][j] = true;
                } else {
                    int k = mRandom.nextInt(2);
                    if (k == 0) {
                        mAdjacencyMatrix[i][j] = mAdjacencyMatrix[j][i] = true;
                    } else {
                        mAdjacencyMatrix[i][j] = mAdjacencyMatrix[j][i] = false;
                    }
                }
            }
        }
    }

    public void execute() throws Exception {
        for (int i = 0; i < mProcesses; i++) {
            System.out.println("Starting listener: " + i);
            mListeners[i] = new TcpListener(i, PORT_COUNTER + i);
            mListeners[i].start();
        }

        for (int i = 0; i < mProcesses; i++) {
            System.out.println("Starting process: " + i);

            List<String> commands = new ArrayList<String>();
            commands.add("java");
            // -jar is the path to the runnable JAR
            commands.add("-jar");
            commands.add(mProgramName);
            // -id is the unique id associated with the process
            commands.add("-id");
            commands.add("" + i);
            // -neighbors is a comma separated list of ids of all the neighbors,
            // or the string none if it has no neighbors.
            commands.add("-neighbors");
            commands.add(getNeighbors(i));
            // -port is the port number of the localhost socket this process
            // should call to pass messages.
            commands.add("-port");
            commands.add("" + (PORT_COUNTER + i));

            // Only the process of id 0 has the -main attribute that specifies
            // it is the main process.
            if (i == 0) {
                commands.add("-main");
            }

            ProcessBuilder builder = new ProcessBuilder(commands);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.start();
        }

    }

    private String getNeighbors(int id) {
        String a = "";
        for (int i = 0; i < mProcesses; i++) {
            if (i == id) {
                continue;
            }

            if (mAdjacencyMatrix[id][i]) {
                a += i + ",";
            }
        }

        if (a.length() > 0) {
            a = a.substring(0, a.length() - 1);
        } else {
            a = "none";
        }
        return a;
    }

    private class TcpListener extends Thread {
        private int mId;
        private int mPort;
        private ServerSocket mServer;
        private Socket mSocket;

        public TcpListener(int id, int port) throws IOException {
            mId = id;
            mPort = port;
            mServer = new ServerSocket(mPort);
        }

        @Override
        public void run() {
            try {
                mSocket = mServer.accept();
                startRelayingMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
         * Enters an infinite loop that listens for incomming messages. Messages
         * must follow the format: bytes 0 - 1: message length L in big endian
         * format L following bytes: Serialized message with binary
         * serialization
         */
        private void startRelayingMessages() {
            try {
                mSemaphores[mId].release();
                byte[] buffer = new byte[65536];
                InputStream in = mSocket.getInputStream();
                while (true) {
                    int a = in.read();
                    if (a == -1) {
                        continue;
                    }

                    int b = in.read();
                    int length = a * 256 + b;
                    in.read(buffer, 0, length);

                    ArrayMessage arrayMessage = (ArrayMessage) SerializationUtilities.deserialize(
                            buffer, 0, length);
                    System.out.println("Message received from " + arrayMessage.getSender()
                            + " to " + arrayMessage.getReceiver());

                    int[] sortedArray = arrayMessage.getPayload();



                    for(int i = 0; i < sortedArray.length; ++i) {
                        System.out.print("" + i + ", ");
                    }
                    System.out.println("");
                    //mListeners[m.getReceiver()].sendMessage(m);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /*
         * Sends a message to the specified receiver. Messages have a size limit
         * of 64 kB. If the receiver is not a neighbor of the sender, this
         * method throws an exception.
         */
        public void sendMessage(Message message) throws Exception {
            if (!mAdjacencyMatrix[message.getSender()][message.getReceiver()]) {
                throw new Exception("Receiver not adjacent to sender.");
            }

            mSemaphores[mId].acquire();

            byte[] bytes = SerializationUtilities.serialize(message);
            int length = bytes.length;
            if (length > 65535) {
                throw new Exception("Message size limit exceeded.");
            }

            OutputStream out = mSocket.getOutputStream();
            out.write(length / 256);
            out.write(length % 256);
            out.write(bytes, 0, length);

            mSemaphores[mId].release();

        }

    }

}
