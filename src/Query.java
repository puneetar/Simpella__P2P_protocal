import java.net.Socket;
import java.nio.ByteBuffer;


public class Query extends Message {
	
	Query(String find){
		super((byte)0x80);
		formPayload(find);
	}

	public void formPayload(String find){
		
		try {
//			System.out.println("********************QUERY find:"+find+"00000");
			
			ByteBuffer dbuf = ByteBuffer.allocate(2+find.length()+1);
			dbuf.putShort((short)0);
			dbuf.put((new String(find)+'\0').getBytes());
			addPayload(dbuf.array());
	//		System.out.println("Length of payload QUERY: "+dbuf.array().length);
			
		}catch (Exception io) {System.out.println("Improper Payload Query");}
	}
	
	public short getSpeed(){
		short speed=ByteBuffer.wrap(payload,0,2).getShort();
		return speed;
	}
	
	public String getQuery(){
		return new String(payload,2,payload.length-3);
	}
	
	public static void print(Query m){
	}
}
