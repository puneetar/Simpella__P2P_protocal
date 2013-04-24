
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Server extends Thread
{
	private ServerSocket serverSocket;

	public Server(int port) throws IOException
	{
		try {
			serverSocket = new ServerSocket(port);
//			System.out.println("TCP_SERVER : STARTED");
		} catch (Exception e) {
			System.out.println("PORT NUMBER ALREADY OCCUPIED");
			System.exit(1);
		}
	}
	public void run()  {
		while(true)   {
			try   {
				new ServerImpl(serverSocket.accept());
			}catch(SocketTimeoutException s)         {
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e)       {
				System.out.println("\nCANNOT ACCEPT CONNECTION\n");
				break;
			}
		}
	}
}




class ServerImpl extends Thread {

	protected Socket clientsocket;

	public ServerImpl(Socket s){
		clientsocket=s;
		start();
	}

	public void run(){
		try {
			System.out.println("Incomming Connection from IP: "  + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
			ObjectInputStream in =     new ObjectInputStream(clientsocket.getInputStream());
			ObjectOutputStream out =    new ObjectOutputStream(clientsocket.getOutputStream());
			while (true) {
				Object b=in.readObject();
			//	System.out.println("the class of the object is :"+b.getClass());
				String clientMessage = (String)b;
				if(clientMessage.equalsIgnoreCase(Simpella.SIMPELLA_CONNECT)){
					if(Database.in_conn.size()<3){
						out.writeObject(Simpella.SIMPELLA_OK);
						System.out.println("HANDSHAKE: Accepted the Incomming Connection");
						continue;
					}
					else{
						out.writeObject(Simpella.SIMPELLA_ERROR);
						System.out.println("HANDSHAKE : Rejected Connection due to: Maximum number of connections reached");
						break;
						
					}
				}

				if(clientMessage.equalsIgnoreCase(Simpella.SIMPELLA_THANK) && Database.in_conn.size()<3){
					//out.writeObject(Echoer.SIMPELLA_OK);
					Node n= new Node(clientsocket,out,in);
					Database.in_conn.add(n);
					System.out.println("HANDSKAKE: Thank You");
					new Listener(n,false);
					
					break;
				}
				
				
			}
		}catch (EOFException e){
			System.out.println("Disconnected to IP: "  + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
			this.stop();
		} 
		catch (IOException e) {
			System.out.println("Disconnected to IP: "  + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
			this.stop();
		}
		catch (Exception e) {
			System.out.println("Disconnected to IP: "  + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
			this.stop();
		}

	}

}

