import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationUtilities {

    public static byte[] serialize(Serializable object) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(baos);
        serializer.writeObject(object);
        return baos.toByteArray();
    }

    public static Object deserialize(byte[] object, int offset, int length)
            throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(object, offset,
                length);
        ObjectInputStream deserializer = new ObjectInputStream(bais);
        return deserializer.readObject();
    }

}

