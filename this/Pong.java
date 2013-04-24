import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class Pong extends Message {

//	private short port;
//	private String ipAddress;
//	private int fileCount;
//	private int fileSize;

	Pong(){
		super(PONG);
		formPayload();
	}
	
	Pong(byte[] guid){
		super(guid,PONG);
		formPayload();
	}

//	Pong(short port, String ipAddress, int fileCount, int fileSize) {
//		super(PONG);
//		//init(port, ipAddress, fileCount, fileSize);
//
//		this.port = port;
//		this.ipAddress = ipAddress;
//		this.fileCount = fileCount;
//		this.fileSize = fileSize;
//
//		
//	}

	void formPayload(){
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

	public short getPort() {
		short port=ByteBuffer.wrap(payload,0,2).getShort();
		return port;
	}
//
//	public void setPort(short port) {
//		this.port = port;
//	}
//
	public String getIpAddress() {
		String ipAddress=null;
		try {
			ipAddress=(InetAddress.getByAddress(Arrays.copyOfRange(payload,2,6))).getHostAddress();
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
			System.out.println("Invalid IP address");
		}
		return ipAddress;
	}

//	public void setIpAddress(String ipAddress) {
//		this.ipAddress = ipAddress;
//	}
//
	public int getFileCount() {
		int fileCount=ByteBuffer.wrap(payload,6,4).getInt();
		return fileCount;
	}
//
//	public void setFileCount(int fileCount) {
//		this.fileCount = fileCount;
//	}
//
	public Float getFileSize() {
		Float fileCount=ByteBuffer.wrap(payload,10,4).getFloat();
		return fileCount;
	}
//
//	public void setFileSize(int fileSize) {
//		this.fileSize = fileSize;
//	}
	
	public static void print(Pong m){
		System.out.println("\n\n*************PONG START**************");
		Message.print(m);
		
		System.out.print("PORT: "+m.getPort());
		
		System.out.println("\tIP: "+m.getIpAddress()+"\tFile Count: "+m.getFileCount()+"\tFile Size "+m.getFileSize());
		
		System.out.println("*************PONG END**************");
	}

}
