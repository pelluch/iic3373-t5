import java.io.Serializable;

/**
 * Created by pablo on 12/7/13.
 */
public class ArrayMessage implements Serializable {
    private static final long serialVersionUID = 6547552545625734904L;
    private int mSender;
    private int mReceiver;
    private int[] mPayload;

    public ArrayMessage(int sender, int receiver, int[] payload) {
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

    public int[] getPayload() {
        return mPayload;
    }



}
