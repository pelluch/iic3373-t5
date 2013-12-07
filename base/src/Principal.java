
public class Principal {

    public static void main(String[] args) {
        // Constructor parameter must be replaced by the path of the
        // runnable JAR defined by the student.
        Executor executor = new Executor(
                "../../demo_base/bin/demo_base.jar");
        try {
            executor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
