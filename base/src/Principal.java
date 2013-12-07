
public class Principal {

    public static void main(String[] args) {
        // Constructor parameter must be replaced by the path of the
        // runnable JAR defined by the student.
    	
    	// name of the jar file:
    	String jarRoute = "/home/pablo/src/iic3373/t5/main.jar";
    	
    	// Rebuilds the jar file:
        try {
	        Executor executor = new Executor(jarRoute);

	        executor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
