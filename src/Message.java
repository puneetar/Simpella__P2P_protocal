import java.io.Serializable;
import java.util.Random;

public class Message implements Serializable{

	byte guid[]=new byte[16];
	byte msg_type=0x00,ttl,hops;
	int pay_length;
	byte payload[];


	Message(){
		new Random().nextBytes(this.guid);
		this.guid[7]=(byte)(0xff);
		this.guid[15]=(byte)0x00;
		ttl=7;
		hops=0;
	}

	Message(byte type){
		this();
		this.msg_type=type;

		//PING
		byte PING = (byte)0x00;
		byte PONG = (byte)0x01;
		byte QUERY = (byte)0x80;
		byte QUERYHIT = (byte)0x81;
		int SIZE_PING_PAYLOAD = 0;
		int SIZE_PONG_PAYLOAD = 14;
		int SIZE_QUERY_PAYLOAD = 0; 
		int SIZE_QUERYHIT_PAYLOAD = 0;
		if(type==PING){		
			pay_length=SIZE_PING_PAYLOAD;
			payload=new byte[pay_length];

		}
		//PONG
		else if(type==PONG){
			pay_length=SIZE_PONG_PAYLOAD;
			payload=new byte[pay_length];
		}
		//QUERY
		else if(type==QUERY){
			pay_length=SIZE_QUERY_PAYLOAD;
			payload=new byte[pay_length];
		}
		//QUERYHIT
		else if(type==QUERYHIT){
			pay_length=SIZE_QUERYHIT_PAYLOAD;
			payload=new byte[pay_length];
		}
		else{
			System.out.println("INVALID MESSAGE FORMAT :CANNOT FORM MESSAGE FORMAT");
		}
	}

	Message(byte[] guid, byte type){
		this.guid=guid;
		this.msg_type=type;
		ttl=7;
		hops=0;

		//PING
		byte PING = (byte)0x00;
		byte PONG = (byte)0x01;
		byte QUERY = (byte)0x80;
		byte QUERYHIT = (byte)0x81;
		int SIZE_PING_PAYLOAD = 0;
		int SIZE_PONG_PAYLOAD = 14;
		int SIZE_QUERY_PAYLOAD = 0; 
		int SIZE_QUERYHIT_PAYLOAD = 0;
		if(type==PING){		
			pay_length=SIZE_PING_PAYLOAD;
			payload=new byte[pay_length];

		}
		//PONG
		else if(type==PONG){
			pay_length=SIZE_PONG_PAYLOAD;
			payload=new byte[pay_length];
		}
		//QUERY
		else if(type==QUERY){
			pay_length=SIZE_QUERY_PAYLOAD;
			payload=new byte[pay_length];
		}
		//QUERYHIT
		else if(type==QUERYHIT){
			pay_length=SIZE_QUERYHIT_PAYLOAD;
			payload=new byte[pay_length];
		}
		else{
			System.out.println("INVALID MESSAGE FORMAT :CANNOT FORM MESSAGE FORMAT");
		}
	}

	public void addPayload(byte[] payload){

		this.payload=payload;
		this.pay_length=payload.length;
		//	assert(this.payload.length==14);

	}

	public byte[] getGuid() {
		return guid;
	}

	public void setGuid(byte[] guid) {
		this.guid = guid;
	}

	public byte getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(byte msg_type) {
		this.msg_type = msg_type;
	}

	public byte getTtl() {
		return ttl;
	}

	public void setTtl(byte ttl) {
		this.ttl = ttl;
	}

	public byte getHops() {
		return hops;
	}

	public void setHops(byte hops) {
		this.hops = hops;
	}

	public int getPay_length() {
		return pay_length;
	}

	public void setPay_length(int pay_length) {
		this.pay_length = pay_length;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}



	public static void print(Message m){
	}



}

/* THings to do :
 * 1.	Query HIT
 * 2.	Set TTL and hops
 * 
 * 
 * 
 * 
 * 
 * byte[] get_guid(){
 * 
	 0x80 = QUERY
0x81 = QUERY HIT

		byte guid[]={(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)0xFF,
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)(0+(Math.random()*0xFF)),
				(byte)0x00};

		for(byte x:guid){
			System.out.println("value"+x);
		}

		return guid;
	}*/


