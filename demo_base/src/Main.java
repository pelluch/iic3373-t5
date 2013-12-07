import java.io.OutputStream;
import java.net.Socket;

public class Main {
    // This simulation assumes the existence of two processes of ids 0 and 1
    // where the one with id 0 is the main process.

    public static void main(String[] args) {
        // Study the received arguments to know what to parse

        if(args.length != 6) {
            System.out.println("Error: incorrect number of arguments");
        }

        int workerId = 0;
        int portNumber = 0;
        int numNeighbors = 0;
        boolean isManager = false;

        for(int i = 0; i < args.length; ++i) {
            if(args[i] == "-id") {
                i++;
                workerId = Integer.parseInt(args[i]);
            }
            else if(args[i] == "-neighbors") {
                i++;
                numNeighbors = Integer.parseInt(args[i]);
            }
            else if(args[i] == "-port") {
                i++;
                portNumber = Integer.parseInt(args[i]);
            }
            else if(args[i] == "-main") {
                isManager = true;
            }
            else {
                System.out.println("Error: Unknown argument " + args[i]);
            }
        }

        Worker worker = null;
        if(isManager) {
            worker = new Manager(workerId, portNumber, numNeighbors);
        }
        else {
            worker = new Worker(workerId, portNumber, numNeighbors);
        }

        try {

            if (args.length > 6 && args[6].equals("-main")) {
                // Main process sends a message to neighbor of id 1

            	int[] toSort = new int[300];
                for(int i = 0; i < toSort.length; ++i) {
                    toSort[i] = i;
                }

            	ArrayMessage arrayMessage = new ArrayMessage(0, 0, toSort);
                byte[] data = SerializationUtilities.serialize(arrayMessage);

                //OutputStream out = socket.getOutputStream();
                // two first bytes indicate the length in big endian format
                byte a = (byte)(data.length / 256);
                byte b = (byte)(data.length % 256);

                //out.write(a);
                //out.write(b);
                //out.write(data);

                System.out.println("Message length = " + data.length);
                System.out.println("Message a = " + (int)a);
                System.out.println("Message b = " + (int)b);
                System.out.println("Message length2 = " + (a * 256 + b));

            } else {


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
