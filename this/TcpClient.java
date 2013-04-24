import java.net.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.io.*;

public class TcpClient
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
//				System.out.println("Connected to IP: "
//						+ client.getInetAddress().getHostAddress() + " PORT: "
//						+ client.getPort());
//				
//				startHandshake(client);
//				String message="SIMPELLA CONNECT/0.6\r\n";
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				out.writeObject(Echoer.SIMPELLA_CONNECT);
				
				String msg=(String)in.readObject();
				if(msg.equalsIgnoreCase(Echoer.SIMPELLA_OK)){
					out.writeObject(Echoer.SIMPELLA_THANK);
					System.out.println("Connection Accepted: OK");
					Node n=new Node(client,out,in);
					
					Database.out_conn.add(n);
					
					new Listener(n,true);
				}
				else if(msg.equalsIgnoreCase(Echoer.SIMPELLA_ERROR)){
					System.out.println("Connection Rejected due to: Maximum number of connections reached");
				}
				else{
					System.out.println("Unknown response from Server");
				}
				
				//System.out.println(""+in.readObject());
				
			}else{
				System.out.println("UNABLE TO CONNECT : DUPLICATE CONNECTION");
			}
		} catch (Exception e) {
			System.out.println("UNABLE TO CONNECT : INVALID IP/PORT VALUE");
		}
	}
	
	public static void startHandshake(Socket socket){
		try {
			String message="SIMPELLA CONNECT/0.6\r\n";
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(message);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			System.out.println(""+in.readObject());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("UNABLE TO DO HANDSHAKE");
			e.printStackTrace();
		}
	}

	public static void send(int id,String m){
//		String message=m;
//		int conn_id=--id;
//		try {
//
//			Socket socket = (Socket) Database.out_conn.get(conn_id);
//			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//			out.writeUTF(message);
//			DataInputStream in = new DataInputStream(socket.getInputStream());
//			System.out.println(in.readUTF());
//		}catch (IndexOutOfBoundsException e){
//			System.out.println("Invalid Connection ID");
//		}
//		catch (IOException e) {
//			Database.out_conn.remove(conn_id);
//			System.out.println("SERVER NOT RESPONDING : KINDLY CONNECT AGAIN");
//		}
	}

	public static void disconnect(int id){
//		try {
//			int conn_id=--id;
//			Socket socket=(Socket)Database.out_conn.get(conn_id);
//			Database.out_conn.remove(conn_id);
//			socket.close();
//			System.out.println("Disconnected to IP: "  + socket.getInetAddress().getHostAddress()+ " PORT: "+socket.getPort());
//		} catch (IOException e) {
//			System.out.println("CANNOT CLOSE SOCKET");
//		}
	}

	public static void show(){
		Iterator iterator=Database.out_conn.iterator();
		Formatter headerFormat=new Formatter();
		System.out.println(headerFormat.format("%8s | %20s | %30s | %13s | %12s","conn. ID","IP","hostname","local port","remote port"));
		headerFormat.close();
		Formatter headerFormat1=new Formatter();
		System.out.println(headerFormat1.format("%75s","------------------------------------------------------------------------------------"));
		headerFormat1.close();
		if(!Database.out_conn.isEmpty()){ 
			int i=0;
			while(iterator.hasNext()){
				Formatter tabFormat=new Formatter();
				Socket so=(Socket)iterator.next();

				System.out.println(tabFormat.format("%8s | %20s | %30s | %13s | %12s",++i,""+so.getInetAddress().getHostAddress(),""+so.getInetAddress().getCanonicalHostName(),""+so.getLocalPort(),""+so.getPort()));
				tabFormat.close();
			}
		}else{
			System.out.println("******* NO ONGOING CONNECTIONS *******");
		}
	}
}

class TcpClientImpl extends Thread{
	
	
}
