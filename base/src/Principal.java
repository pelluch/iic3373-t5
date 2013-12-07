
public class Principal {

    public static void main(String[] args) {
        // Constructor parameter must be replaced by the path of the
        // runnable JAR defined by the student.
    	
    	// name of the jar file:
    	String route = "../demo_base/bin/";
    	String jarFile = "main.jar";
    	
    	// Rebuilds the jar file:
        try {
	        Executor executor = new Executor(route + jarFile);

	        executor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
