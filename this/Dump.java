import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
public class Dump {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

int num=1999,num1=2220;
		ByteBuffer dbuf = ByteBuffer.allocate(8);
		dbuf.putInt(num);
		dbuf.putInt(num1);
		byte[] bytes = dbuf.array();
		
		byte b=(byte)1000;
		
		int k=(int)b;//Integer.parseInt(b+"");
		System.out.println(b+"    "+k);
		
	
		
		int i=ByteBuffer.wrap(bytes,0,4).getInt();
		int j=ByteBuffer.wrap(bytes,4,4).getInt();
		System.out.println(i+"            "+j);
		
		Socket a;
		try {
			a = new Socket("8.8.8.8",53);
			byte[] x=a.getLocalAddress().getAddress();
			
			for(byte q:x){
				System.out.println(q);
			}
			
			System.out.println(x.length+"    "+a.getLocalAddress());
			
		//	InetAddress tt;
			
			System.out.println(InetAddress.getByAddress(Arrays.copyOfRange(x,0,4)));
			
			String ipAddress=null;
			try {
				ipAddress=(InetAddress.getByAddress(Arrays.copyOfRange(x,0,4))).getHostAddress();
				System.out.println("i am gr8"+ipAddress);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte []asas=ByteBuffer.wrap(x,0,4).array();
			for(byte aw:asas ){
				System.out.println(aw);
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String cmd[]=new String[2];
		cmd[1]="hadar.cse.buffalo.edu:6346";
		String s1=cmd[1].substring(0, cmd[1].indexOf(":"));
		int i1=Integer.parseInt(cmd[1].substring(cmd[1].indexOf(":")+1));
		System.out.println(s1+"    "+i1);
		
		int y=dump1.x;
		y=y+2;
		dump1.x=dump1.x+3;
		dump1.print();
		
		System.out.println("*********************************");
		
		byte[] b11={12,-34,56,0};
		byte[] b12={12,-34,56,0};
	
		String b1=new String(b11);
		String b2=new String(b12);
		if(b2.equals(b1)){
			System.out.println("WTF");
		}
		else{
			System.out.println("again WTF");
		}
		
	}

}

class dump1{
	static int x=5;
	static void print(){
		System.out.println("haisdhasd        "+x);
	}
}
