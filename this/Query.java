import java.net.Socket;
import java.nio.ByteBuffer;


public class Query extends Message {
	
	Query(){
		super(QUERY);
		formPayload();
	}

	public void formPayload(){
		
		try {

			ByteBuffer dbuf = ByteBuffer.allocate(14);
			dbuf.putShort(Echoer.TCP_PORT);
			Socket a = new Socket("8.8.8.8",53);
			byte[] x=a.getLocalAddress().getAddress();
			dbuf.put(x);
			a.close();
			dbuf.putInt(ShareScan.getNo_of_files());
			dbuf.putFloat(ShareScan.getDir_size());

			//byte[] bytes = dbuf.array();
			addPayload(dbuf.array());
			assert(dbuf.array().length==14);
			
		}catch (Exception io) {System.out.println("UNABLE TO CONNECT TO IP 8.8.8.8:53");}
	}
}
