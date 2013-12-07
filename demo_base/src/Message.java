import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 6547552546625734904L;
    private int mSender;
    private int mReceiver;
    private byte[] mPayload;

    public Message(int sender, int receiver, byte[] payload) {
        mSender = sender;
        mReceiver = receiver;
        mPayload = payload;
    }

    public int getSender() {
        return mSender;
    }

    public int getReceiver() {
        return mReceiver;
    }

    public byte[] getPayload() {
        return mPayload;
    }

}
