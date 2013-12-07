import java.io.OutputStream;
import java.net.Socket;

public class Principal {
    // This simulation assumes the existence of two processes of ids 0 and 1
    // where the one with id 0 is the main process.

    public static void main(String[] args) {
        // Study the received arguments to know what to parse
        for (String a : args) {
            System.out.println(a);
        }

        Socket socket = null;
        try {
            // Port number is included in args[5].
            socket = new Socket((String) null, Integer.parseInt(args[5]));

            if (args.length > 6 && args[6].equals("-main")) {
                // Main process sends a message to neighbor of id 1
                Message m = new Message(0, 1, new byte[] { 1, 2, 3 });
                byte[] data = SerializationUtilities.serialize(m);

                OutputStream out = socket.getOutputStream();
                // two first bytes indicate the length in big endian format
                out.write(0);
                out.write(data.length);
                out.write(data);

            } else {
                byte[] buffer = new byte[1024];

                int a = socket.getInputStream().read();
                a = socket.getInputStream().read();
                socket.getInputStream().read(buffer, 0, a);

                Message m = (Message) SerializationUtilities.deserialize(
                        buffer, 0, a);
                // The other process reads the message.

                m = new Message(1, 0, new byte[] { 4, 5, 6 });
                byte[] data = SerializationUtilities.serialize(m);

                OutputStream out = socket.getOutputStream();
                // two first bytes indicate the length in big endian format
                out.write(0);
                out.write(data.length);
                out.write(data);

                // He then sends another message.
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
