import java.io.Serializable;

enum SenderType { PROBE, ECHO }

public class NetworkTopologyTask implements Serializable{

	private static final long serialVersionUID = -4374996963873420847L;
	
	private SenderType mSenderType;
	private int mSenderId;
	private int[][] mGraph;
	
	public NetworkTopologyTask(SenderType senderType, int senderId, int[][] graph){
		this.mSenderType = senderType;
		this.mSenderId = senderId;
		this.mGraph = graph;
	}
}
