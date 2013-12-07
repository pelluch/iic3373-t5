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
        int[] neighbors = null;
        boolean isManager = false;

        for(int i = 0; i < args.length; ++i) {
            if(args[i] == "-id") {
                i++;
                workerId = Integer.parseInt(args[i]);
            }
            else if(args[i] == "-neighbors") {
                i++;
                // Comma separated list of neighbor ids
                String[] neighborList = args[i].split(",");
                neighbors = new int[neighborList.length];
                for(int j = 0; j < neighbors.length; ++j) {
                    neighbors[j] = Integer.parseInt(neighborList[j]);
                }
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
            worker = new Manager(workerId, portNumber, neighbors);
        }
        else {
            worker = new Worker(workerId, portNumber, neighbors);
        }



    }


}
