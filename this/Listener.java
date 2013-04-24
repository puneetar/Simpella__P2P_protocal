import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Listener extends Thread {

	Node node;
	private Socket socket;
	private ObjectOutputStream oostream;
	private ObjectInputStream oistream;
	private boolean isClient;

	Listener(Node n,boolean isClient){
		this.node=n;
		this.socket=node.getSocket();
		this.oostream=node.getOostream();
		this.oistream=node.getOistream();
		this.isClient=isClient;
		start();

	}

	public void run(){
		if(isClient){
			update();
		}

		while (true) {
			try {
				Object b=oistream.readObject();
				System.out.println("the class of the object is :"+b.getClass());
				if(b.getClass().getCanonicalName().equalsIgnoreCase("PING")){
					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handlePing((Ping)b);
				}
				if(b.getClass().getCanonicalName().equalsIgnoreCase("PONG")){
					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handlePong((Pong)b);
				}
				if(b.getClass().getCanonicalName().equalsIgnoreCase("QUERY")){
					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handleQuery((Query)b);
				}
				if(b.getClass().getCanonicalName().equalsIgnoreCase("QUERYHIT")){
					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handleQueryHit((QueryHit)b);
				}


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Disconnected to IP: "  + socket.getInetAddress().getHostAddress()+ " PORT: "+socket.getPort());
				e.printStackTrace();
				break;
			}catch (EOFException e){
				System.out.println("Disconnected to IP: "  + socket.getInetAddress().getHostAddress()+ " PORT: "+socket.getPort());
				e.printStackTrace();
				this.stop();
			}  
			catch (IOException e) {
				System.out.println("Disconnected to IP: "  + socket.getInetAddress().getHostAddress()+ " PORT: "+socket.getPort());
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}


		}


	}

	public void update(){
		Ping ping=new Ping();

		Ping.print(ping);
		Database.generatedPing.add(new String(ping.getGuid()));

		try {
			for(Node n:Database.in_conn){
				n.getOostream().writeObject(ping);
				System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
			for(Node n:Database.out_conn){
				n.getOostream().writeObject(ping);
				System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to send Ping");
			e.printStackTrace();
		}

	}


	public void handlePing(Ping ping){
		System.out.println("In handle PING");


		if(Database.generatedPing.contains(new String(ping.getGuid()))){
			System.out.println("I Generated this Ping :-P");

		}
		else{
			if(Database.routeTable.containsKey(new String(ping.getGuid()))){
				System.out.println("already present in route table");
			}
			else{
				Database.routeTable.put(new String(ping.getGuid()),node);
				Pong myPong=new Pong(ping.getGuid());
				Ping.print(ping);
				Pong.print(myPong);

				try {
					//Send my Pong message back to initiator 
					oostream.writeObject(myPong); 

					//Forwarding ping to other connections
					for(Node n:Database.in_conn){
						if(n.getSocket().equals(node.getSocket())){
							System.out.println("Not sending to initiator as: "+n.getSocket()+" = "+node.getSocket());
						}
						else{
							n.getOostream().writeObject(ping);
							System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
						}

					}
					for(Node n:Database.out_conn){
						if(n.getSocket().equals(node.getSocket())){
							System.out.println("Not sending to initiator as: "+n.getSocket()+" = "+node.getSocket());
						}
						else{
							n.getOostream().writeObject(ping);
							System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}




	}
	public void handlePong(Pong pong){
		
		try {
			Node node=Database.routeTable.get(new String(pong.getGuid()));
			if(node!=null){
				node.getOostream().writeObject(pong);	
			}
			else{
				System.out.println("IT is the LAST NODE");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void handleQuery(Query query){

	}
	public void handleQueryHit(QueryHit queryHit){

	}

}
