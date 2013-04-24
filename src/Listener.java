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

		Database.conninfo.put(this.socket, new ConnInfo(0,0,0,0));

		start();


	}


	public void run(){
		if(isClient){
			sendPing();

		}

		while (true) {
			try {
				Object b=oistream.readObject();
//				System.out.println("the class of the object is :"+b.getClass());
				if(b.getClass().getCanonicalName().equalsIgnoreCase("PING")){
//					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handlePing((Ping)b);




				}
				if(b.getClass().getCanonicalName().equalsIgnoreCase("PONG")){
//					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handlePong((Pong)b);
				}
				if(b.getClass().getCanonicalName().equalsIgnoreCase("QUERY")){
//					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handleQuery((Query)b);
				}
				if(b.getClass().getCanonicalName().equalsIgnoreCase("QUERYHIT")){
//					System.out.println("Object Class:"+b.getClass().getCanonicalName());
					handleQueryHit((QueryHit)b);
				}


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Disconnected to IP: "  + socket.getInetAddress().getHostAddress()+ " PORT: "+socket.getPort());
//				e.printStackTrace();
				break;
			}catch (EOFException e){
				System.out.println("Disconnected to IP: "  + socket.getInetAddress().getHostAddress()+ " PORT: "+socket.getPort());
//				e.printStackTrace();
				this.stop();
			}  
			catch (IOException e) {
				System.out.println("Disconnected to IP: "  + socket.getInetAddress().getHostAddress()+ " PORT: "+socket.getPort());
				// TODO Auto-generated catch block
//				e.printStackTrace();
				break;
			}


		}


	}

	public void sendPing(){
		Ping ping=new Ping();

		//	Ping.print(ping);
		Simpella.simpella_files=0;
		Simpella.simpella_hosts=0;
		Simpella.simpella_file_size=0;

		Database.generatedPing.add(new String(ping.getGuid()));

		try {
			for(Node n:Database.in_conn){
				n.getOostream().writeObject(ping);
				
				ConnInfo ci1= Database.conninfo.get(this.socket);
				ci1.setB_sent(ci1.getB_sent()+23+ping.getPayload().length);
				ci1.setP_sent(ci1.getP_sent()+1);
				Database.conninfo.put(this.socket, ci1);

				Simpella.simpella_msg_sent++;
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+ping.getPayload().length;
				System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
			for(Node n:Database.out_conn){
				n.getOostream().writeObject(ping);
				ConnInfo ci1= Database.conninfo.get(this.socket);
				ci1.setB_sent(ci1.getB_sent()+23+ping.getPayload().length);
				ci1.setP_sent(ci1.getP_sent()+1);
				Database.conninfo.put(this.socket, ci1);
				Simpella.simpella_msg_sent++;
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+ping.getPayload().length;
				System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to send Ping");
//			e.printStackTrace();
		}

	}


//	ConnInfo ci= Database.conninfo.get(this.socket);
//	ci.setB_sent(ci.getB_sent()+0);
//	ci.setP_sent(ci.getP_sent()+0);
//	Database.conninfo.put(this.socket, ci);

	public void handlePing(Ping ping){


//		System.out.println("In handle PING");

		ConnInfo ci= Database.conninfo.get(this.socket);
		ci.setB_rcvd(ci.getB_rcvd()+23+ping.getPayload().length);
		ci.setP_rcvd(ci.getP_rcvd()+1);
		Database.conninfo.put(this.socket, ci);


		ping.setTtl((byte)(ping.getTtl()-1));
		ping.setHops((byte)(ping.getHops()+1));


		if(Database.generatedPing.contains(new String(ping.getGuid()))){
//			System.out.println("Dropped Ping because I initiated");

		}
		else{
			if(Database.routeTablePing.containsKey(new String(ping.getGuid()))){
//				System.out.println("already present in route table");
			}

			else{
				Database.routeTablePing.put(new String(ping.getGuid()),node);
				Pong myPong=new Pong(ping.getGuid());
				Ping.print(ping);
				Pong.print(myPong);

				Database.ping_list.add(ping);

				try {
					//Send my Pong message back to initiator 
					oostream.writeObject(myPong); 
					
					ConnInfo ci1= Database.conninfo.get(this.socket);
					ci1.setB_sent(ci1.getB_sent()+23+myPong.getPayload().length);
					ci1.setP_sent(ci1.getP_sent()+1);
					Database.conninfo.put(this.socket, ci1);
					
					Simpella.simpella_msg_sent++;
					Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+myPong.getPayload().length;
					Database.pong_sent_list.add(myPong);
					System.out.println("Pong sent to :"+node.getSocket().getInetAddress()+"at PORT: "+node.getSocket().getPort());
					//Forwarding ping to other connections
					for(Node n:Database.in_conn){
						if(n.getSocket().equals(node.getSocket())){
//							System.out.println("Ping NOT SENT to initiator as: "+n.getSocket()+" = "+node.getSocket());
						}
						else{
							if(ping.getTtl()>0){
								n.getOostream().writeObject(ping);
								ConnInfo ci12= Database.conninfo.get(this.socket);
								ci12.setB_sent(ci12.getB_sent()+23+ping.getPayload().length);
								ci12.setP_sent(ci12.getP_sent()+1);
								Database.conninfo.put(this.socket, ci12);
								Simpella.simpella_msg_sent++;
								Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+ping.getPayload().length;
								System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
							}
							else{
								System.out.println("PING DROPED as ttl <=0");
							}

						}

					}
					for(Node n:Database.out_conn){
						if(n.getSocket().equals(node.getSocket())){
//							System.out.println("Not sending to initiator as: "+n.getSocket()+" = "+node.getSocket());
						}
						else{
							if(ping.getTtl()>0){
								n.getOostream().writeObject(ping);
								ConnInfo ci12= Database.conninfo.get(this.socket);
								ci12.setB_sent(ci12.getB_sent()+23+ping.getPayload().length);
								ci12.setP_sent(ci12.getP_sent()+1);
								Database.conninfo.put(this.socket, ci12);
								Simpella.simpella_msg_sent++;
								Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+ping.getPayload().length;
								System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
							}
							else{
								System.out.println("PING DROPED as ttl <=0");
							}

						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
	//				e.printStackTrace();
				}
			}
		}




	}
	public void handlePong(Pong pong){

		ConnInfo ci= Database.conninfo.get(this.socket);
		ci.setB_rcvd(ci.getB_rcvd()+23+pong.getPayload().length);
		ci.setP_rcvd(ci.getP_rcvd()+1);
		Database.conninfo.put(this.socket, ci);

		pong.setTtl((byte)(pong.getTtl()-1));
		pong.setHops((byte)(pong.getHops()+1));
		try {
			Node node=Database.routeTablePing.get(new String(pong.getGuid()));
			if(node!=null){
				if(pong.getTtl()>0){

					node.getOostream().writeObject(pong);
					ConnInfo ci1= Database.conninfo.get(this.socket);
					ci1.setB_sent(ci1.getB_sent()+23+pong.getPayload().length);
					ci1.setP_sent(ci1.getP_sent()+1);
					Database.conninfo.put(this.socket, ci1);
					Simpella.simpella_msg_sent++;
					Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+pong.getPayload().length;
//					System.out.println("PONG FORWARDED to :"+node.getSocket().getInetAddress()+"at PORT: "+node.getSocket().getPort());	
				}
				else{
					System.out.println("PONG DROPED as ttl <=0");
				}


			}
			else{
				Database.pong_recieved_list.add(pong);
				Simpella.simpella_hosts++;
				Simpella.simpella_files=Simpella.simpella_files+pong.getFileCount();
				Simpella.simpella_file_size=Simpella.simpella_file_size+pong.getFileSize();
				System.out.println("Pong Recieved");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

	}


	public void sendQuery(String find){
		Query query=new Query(find);

		Query.print(query);
		Database.generatedQuery.add(new String(query.getGuid()));

		try {
			for(Node n:Database.in_conn){
				n.getOostream().writeObject(query);
				ConnInfo ci1= Database.conninfo.get(this.socket);
				ci1.setB_sent(ci1.getB_sent()+23+query.getPayload().length);
				ci1.setP_sent(ci1.getP_sent()+1);
				Database.conninfo.put(this.socket, ci1);
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+query.getPayload().length;
				Simpella.simpella_msg_sent++;
				System.out.println("Query sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
			for(Node n:Database.out_conn){
				n.getOostream().writeObject(query);
				ConnInfo ci1= Database.conninfo.get(this.socket);
				ci1.setB_sent(ci1.getB_sent()+23+query.getPayload().length);
				ci1.setP_sent(ci1.getP_sent()+1);
				Database.conninfo.put(this.socket, ci1);
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+query.getPayload().length;
				Simpella.simpella_msg_sent++;
				System.out.println("Query sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to send Query");
//			e.printStackTrace();
		}


	}



	public void handleQuery(Query query){


		ConnInfo ci= Database.conninfo.get(this.socket);
		ci.setB_rcvd(ci.getB_rcvd()+23+query.getPayload().length);
		ci.setP_rcvd(ci.getP_rcvd()+1);
		Database.conninfo.put(this.socket, ci);


//		System.out.println("In handle Query");
		query.setTtl((byte)(query.getTtl()-1));
		query.setHops((byte)(query.getHops()+1));

		if(Database.generatedQuery.contains(new String(query.getGuid()))){
	//		System.out.println("I Generated this Query :-P");

		}
		else{
			if(Database.routeTableQuery.containsKey(new String(query.getGuid()))){
//				System.out.println("QUERY already present in route table");
			}

			else{
				Database.routeTableQuery.put(new String(query.getGuid()),node);
				QueryHit myQueryHit=new QueryHit(query.getGuid(),query.getQuery());
				Query.print(query);
				QueryHit.print(myQueryHit);
				Database.queryhit_sent_list.add(myQueryHit);


				Database.query_list.add(query);



				try {
					//Send my Pong message back to initiator 
					if(myQueryHit.getPayload().length>28){

						oostream.writeObject(myQueryHit); 
						
						ConnInfo ci1= Database.conninfo.get(this.socket);
						ci1.setB_sent(ci1.getB_sent()+23+myQueryHit.getPayload().length);
						ci1.setP_sent(ci1.getP_sent()+1);
						Database.conninfo.put(this.socket, ci1);

						Simpella.simpella_msg_sent++;
						Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+myQueryHit.getPayload().length;
						System.out.println("QueryHit sent to :"+node.getSocket().getInetAddress()+"at PORT: "+node.getSocket().getPort());
					}

					//Forwarding ping to other connections
					for(Node n:Database.in_conn){
						if(n.getSocket().equals(node.getSocket())){
//							System.out.println("Query NOT SENT to initiator as: "+n.getSocket()+" = "+node.getSocket());
						}
						else{
							if(query.getTtl()>0){
								n.getOostream().writeObject(query);
								ConnInfo ci1= Database.conninfo.get(this.socket);
								ci1.setB_sent(ci1.getB_sent()+23+query.getPayload().length);
								ci1.setP_sent(ci1.getP_sent()+1);
								Database.conninfo.put(this.socket, ci1);
								Simpella.simpella_msg_sent++;
								Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+query.getPayload().length;
								System.out.println("Query sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());		
							}
							else{
								System.out.println("QUERY DROPED as ttl <=0");
							}

						}

					}
					for(Node n:Database.out_conn){
						if(n.getSocket().equals(node.getSocket())){
//							System.out.println("Query NOT SENT to initiator as: "+n.getSocket()+" = "+node.getSocket());
						}
						else{
							if(query.getTtl()>0){
								n.getOostream().writeObject(query);
								ConnInfo ci1= Database.conninfo.get(this.socket);
								ci1.setB_sent(ci1.getB_sent()+23+query.getPayload().length);
								ci1.setP_sent(ci1.getP_sent()+1);
								Database.conninfo.put(this.socket, ci1);
								Simpella.simpella_msg_sent++;
								Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+query.getPayload().length;
								System.out.println("Query sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());		
							}
							else{
								System.out.println("QUERY DROPED as ttl <=0");
							}
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
		//			e.printStackTrace();
				}
			}
		}




	}
	public void handleQueryHit(QueryHit queryHit){

		ConnInfo ci= Database.conninfo.get(this.socket);
		ci.setB_rcvd(ci.getB_rcvd()+23+queryHit.getPayload().length);
		ci.setP_rcvd(ci.getP_rcvd()+1);
		Database.conninfo.put(this.socket, ci);

		
		queryHit.setTtl((byte)(queryHit.getTtl()-1));
		queryHit.setHops((byte)(queryHit.getHops()+1));
		try {
			Node node=Database.routeTableQuery.get(new String(queryHit.getGuid()));
			if(node!=null){
				if(queryHit.getTtl()>0){
					node.getOostream().writeObject(queryHit);
					ConnInfo ci1= Database.conninfo.get(this.socket);
					ci1.setB_sent(ci1.getB_sent()+23+queryHit.getPayload().length);
					ci1.setP_sent(ci1.getP_sent()+1);
					Database.conninfo.put(this.socket, ci1);
					Simpella.simpella_msg_sent++;
					Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+queryHit.getPayload().length;
//					System.out.println("QUERY_HIT FORWARDED to :"+node.getSocket().getInetAddress()+"at PORT: "+node.getSocket().getPort());	
				}
				else{
					System.out.println("QUERY_HIT DROPED as ttl <=0");
				}
			}
			else{
				QueryParser q=new QueryParser(queryHit);
				Database.queryhit_recieved_list.add(queryHit);
				System.out.println("Query Hit Recieved");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
		}

	}
	
	public static void sendstaticPing(){
		Ping ping=new Ping();

		//	Ping.print(ping);
		Simpella.simpella_files=0;
		Simpella.simpella_hosts=0;
		Simpella.simpella_file_size=0;

		Database.generatedPing.add(new String(ping.getGuid()));

		try {
			for(Node n:Database.in_conn){
				n.getOostream().writeObject(ping);
	
				Simpella.simpella_msg_sent++;
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+ping.getPayload().length;
				System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
			for(Node n:Database.out_conn){
				n.getOostream().writeObject(ping);
				Simpella.simpella_msg_sent++;
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+ping.getPayload().length;
				System.out.println("Ping sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to send Ping");
	//		e.printStackTrace();
		}

	}
	
	public static void sendstaticQuery(String find){
		Query query=new Query(find);

		Query.print(query);
		Database.generatedQuery.add(new String(query.getGuid()));

		try {
			for(Node n:Database.in_conn){
				n.getOostream().writeObject(query);
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+query.getPayload().length;
				Simpella.simpella_msg_sent++;
				System.out.println("Query sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
			for(Node n:Database.out_conn){
				n.getOostream().writeObject(query);
				Simpella.simpella_byte_sent=Simpella.simpella_byte_sent+14+query.getPayload().length;
				Simpella.simpella_msg_sent++;
				System.out.println("Query sent to :"+n.getSocket().getInetAddress()+"at PORT: "+n.getSocket().getPort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to send Query");
//			e.printStackTrace();
		}


	}



}
