import java.net.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.io.*;

public class Client
{
	//static ArrayList arr_socket=new ArrayList();
	
	
	public static void open(String s,int p){
		try {
			String serverName=s;
			int port=p;

			int flag_duplicate=0;
			Iterator iterator=Database.out_conn.iterator();
			while(iterator.hasNext()&&flag_duplicate==0){
				Node sock=(Node)iterator.next();
				if((s.equalsIgnoreCase(sock.getSocket().getInetAddress().getHostAddress())||s.equalsIgnoreCase(sock.getSocket().getInetAddress().getCanonicalHostName()))&&p==sock.getSocket().getPort()){
					flag_duplicate=1; 
				}
				else{
					flag_duplicate=0;
				}
			}

			if (flag_duplicate==0) {
				Socket client = new Socket(serverName, port);
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				out.writeObject(Simpella.SIMPELLA_CONNECT);
				
				String msg=(String)in.readObject();
				if(msg.equalsIgnoreCase(Simpella.SIMPELLA_OK)){
					out.writeObject(Simpella.SIMPELLA_THANK);
					System.out.println("HANDSHAKE: Connection Accepted: OK");
					Node n=new Node(client,out,in);
					
					Database.out_conn.add(n);
					
					new Listener(n,true);
				}
				else if(msg.equalsIgnoreCase(Simpella.SIMPELLA_ERROR)){
					System.out.println("HANDSHAKE: Connection Rejected due to: Maximum number of connections reached");
				}
				else{
					System.out.println("HANDSHAKE:Unknown response from Server");
				}
				
				//System.out.println(""+in.readObject());
				
			}else{
				System.out.println("UNABLE TO CONNECT : DUPLICATE CONNECTION");
			}
		} catch (Exception e) {
			System.out.println("UNABLE TO CONNECT : INVALID IP/PORT VALUE");
		}
	}

}
