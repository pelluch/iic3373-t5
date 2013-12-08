import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class Main {
    // This simulation assumes the existence of two processes of ids 0 and 1
    // where the one with id 0 is the main process.


    // Class is of the form "net.viralpatel.itext.pdf.DemoClass"
    public static Class getTaskClass(String className) throws Exception {

        ClassLoader taskLoader = ClassLoader.getSystemClassLoader();
        Class taskClass = taskLoader.loadClass(className);

        //Method taskMethod = taskClass.getMethod("executeTask");

        //Object taskInstance = taskClass.newInstance();
        //Method myMethod = taskClass.getMethod("demoMethod",
         //                   new Class[] { String.class });
        //String returnValue = (String) myMethod.invoke(taskInstance,
         //                   new Object[] {"hi"});


      //  System.out.println("The value returned from the method is:"
      //          + returnValue);

        return taskClass;

    }

    public static void main(String[] args) {
        // Study the received arguments to know what to parse

        int workerId = 0;
        int portNumber = 0;
        int[] neighbors = null;
        boolean isManager = false;
        String taskClassName = "";

        for(int i = 0; i < args.length; ++i) {


            if(args[i].equals("-id")) {
                i++;
                workerId = Integer.parseInt(args[i]);
            }
            else if(args[i].equals("-neighbors")) {
                i++;
                // Comma separated list of neighbor ids
                String[] neighborList = args[i].split(",");
                neighbors = new int[neighborList.length];
                for(int j = 0; j < neighbors.length; ++j) {
                    neighbors[j] = Integer.parseInt(neighborList[j]);
                }
            }
            else if(args[i].equals("-port")) {
                i++;
                portNumber = Integer.parseInt(args[i]);
            }
            else if(args[i].equals("-task")) {
                i++;
                taskClassName = args[i];
            }
            else if(args[i].equals("-main")) {
                isManager = true;
            }
            else {
                System.out.println("Error: Unknown argument " + args[i]);
            }
        }

        Worker worker = null;
        Manager manager = null;

        if(isManager) {
            try {
                manager = new Manager(workerId, portNumber, neighbors, getTaskClass(taskClassName));
            } catch (Exception e) {
                e.printStackTrace();
            }
            manager.start();
        }
        else {
            worker = new Worker(workerId, portNumber, neighbors);
            worker.start();
        }




    }


}
