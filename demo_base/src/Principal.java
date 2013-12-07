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
            	
            	byte[] payload = new byte[256];
            	for(int i = 0; i < payload.length; i++)
            		payload[i] = (byte)(i % 8);

            	Message m = new Message(0, 1, payload);
                byte[] data = SerializationUtilities.serialize(m);

                OutputStream out = socket.getOutputStream();
                // two first bytes indicate the length in big endian format
                byte a = (byte)(data.length / 256);
                byte b = (byte)(data.length % 256);

                out.write(a);
                out.write(b);
                out.write(data);

                System.out.println("Message length = " + data.length);
                System.out.println("Message a = " + a);
                System.out.println("Message b = " + b);
                System.out.println("Message length2 = " + (a * 256 + b));

            } else {
                int a = socket.getInputStream().read();
                int b = socket.getInputStream().read();
                
                System.out.println("Message a = " + a);
                System.out.println("Message b = " + b);

                int length = (256 * a + b);
                
                System.out.println("Message length3 = " + length);
                
                byte[] buffer = new byte[length];
                
                socket.getInputStream().read(buffer, 0, length);

                Message m = (Message) SerializationUtilities.deserialize(
                        buffer, 0, length);
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
