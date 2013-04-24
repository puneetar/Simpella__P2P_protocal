import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Random;
import java.io.*;

public class Simpella
{
	static String SIMPELLA_CONNECT="SIMPELLA CONNECT/0.6\r\n";
	static String SIMPELLA_OK="SIMPELLA/0.6 200 OK\r\n";
	static String SIMPELLA_THANK="SIMPELLA/0.6 200 thank you for accepting me\r\n";
	static String SIMPELLA_ERROR="SIMPELLA/0.6 503 Maximum number of connections reached. Sorry!\r\n";

	static short TCP_PORT;
	static short DOWNLOAD_PORT;
	static String QWN_IP;
	static byte[] SERVENTID=new byte[16];

	static int simpella_hosts=0;
	static long simpella_files=0;
	static double simpella_file_size=0;

	static int simpella_msg_sent;
	static double simpella_byte_sent;

	public static void main(String [] args)
	{
		new Random().nextBytes(SERVENTID);
		try{
			if(Integer.parseInt(args[0])>0&&Integer.parseInt(args[0])<60000 && Integer.parseInt(args[1])>0 && Integer.parseInt(args[1])<60000){

			}else
			{
				System.out.println("INVALID PORT VALUES");
				System.exit(1);
			}

		}catch(Exception e){
			System.out.println("INVALID PORT VALUES");
			System.exit(1);
		}


		try{
			final int tcp_port=Integer.parseInt(args[0]);
			TCP_PORT=(short)tcp_port;
		}
		catch(Exception e){
			final int tcp_port=6346;
			TCP_PORT=(short)tcp_port;
		}


		try{
			final int udp_port=Integer.parseInt(args[1]);
			DOWNLOAD_PORT=(short)udp_port;
		}catch(Exception e){
			final int udp_port=5635;
			DOWNLOAD_PORT=(short)udp_port;
		}


		try
		{
			Thread t = new Server(TCP_PORT);
			t.start();

			Thread t3=new HttpServer(DOWNLOAD_PORT);
			t3.start();



			Thread t2 = new Thread(){
				public void run(){
					InetAddress addr=null;
					try {
						Socket socket = new Socket("8.8.8.8", 53);
						addr = socket.getLocalAddress();
						String hostAddr = addr.getHostAddress();
						QWN_IP=hostAddr;
					} catch (UnknownHostException e1) {
						System.out.println("UNABLE TO  CONNECT TO PUBLIC SERVER 8.8.8.8");

					} catch (IOException e1) {
						System.out.println("UNABLE TO  CONNECT TO PUBLIC SERVER 8.8.8.8");

					}

					ShareScan.setDir(System.getProperty("user.dir"));
					System.out.println("Local IP: "+addr.getHostAddress());
					System.out.println("Simpella Net Port: "+TCP_PORT+"\nDownloading Port: "+DOWNLOAD_PORT);
					System.out.println("simpella version 0.6 (c) 2002-2003 XYZ");

					BufferedReader read_cmd=new BufferedReader(new InputStreamReader(System.in));
					while(true)	{
						String[] cmd=null;
						try{
							cmd = read_cmd.readLine().split(" ");
							if(cmd[0].equalsIgnoreCase("info")){

								try{
									cmd[1].length();
								}catch(Exception e){
									System.out.println("Invalid Input given \n\n\t\t info [cdhnqs] : Possible options for info command");
									continue;
								}

								if(cmd[1].equalsIgnoreCase("c")){

									/*				CONNECTION STATS:
										-----------------
										1)65.83.181.222:6346 Packs: 571:735 Bytes: 22.66k:32.37k
										2)64.2.56.31:6346 Packs: 305:1871 Bytes: 16.30k:70.78k
										3)65.32.58.209:6346 Packs: 0:0 Bytes: 0:0
									 */
									System.out.println("CONNECTION STATS:\n\t-----------------");

									Collection<ConnInfo> coll=Database.conninfo.values();
									Iterator iterator=coll.iterator();

									Collection key=Database.conninfo.keySet();
									Iterator keyiterator=key.iterator();

									for(int i=0;i<Database.conninfo.size()&&iterator.hasNext()&&keyiterator.hasNext();i++){
										ConnInfo ci=(ConnInfo)iterator.next();
										Socket s=(Socket)keyiterator.next();

										System.out.println(""+i+")"+s.getInetAddress().getHostAddress()+":"+s.getPort()+"\t\t Packs: "+ci.getP_sent()+":"+ci.getP_rcvd()+" Bytes: "+ci.getB_sent()+":"+ci.getB_rcvd());


									}

									System.out.println("---------------------------------------");							
									System.out.println("\n\n");
									continue;
								}
								else if(cmd[1].equalsIgnoreCase("d")){
									for(int i=0;i<Database.download.size();i++){
										Result r=Database.download.get(i);
										float curr=(Float.parseFloat(Database.currentDownload.get(r))/1000);
										float total=r.getFileSize();

										System.out.println("DOWNLOAD STATS:\n---------------\n\t"+i+") "+r.getIp()+":"+r.getPort()+" "+((curr/total)*100)+"% "+curr+"k/"+total+"k\n\t\tName: "+r.getFilename());	
									}
									System.out.println("---------------------------------------");
									System.out.println("\n\n");
									continue;
								}
								else if(cmd[1].equalsIgnoreCase("h")){

									simpella_hosts=0;
									simpella_files=0;
									simpella_file_size=0;

									Listener.sendstaticPing();
									System.out.println("Waiting for all pongs...");

									try {
										Thread.sleep(3000);
									} catch (Exception e) {

				//						e.printStackTrace();
									}
									System.out.println("HOST STATS:\n-----------\n\tHosts: "+Simpella.simpella_hosts+" Files: "+Simpella.simpella_files+" Size: "+Simpella.simpella_file_size +"kb");
									System.out.println("---------------------------------------");
									System.out.println("\n\n");
									continue;
								}
								else if(cmd[1].equalsIgnoreCase("n")){
									int msg_rcvd=Database.pong_recieved_list.size()+Database.ping_list.size()+Database.query_list.size()+Database.queryhit_recieved_list.size();
									int msg_sent=Simpella.simpella_msg_sent;

									double querysize=0.0d;
									for(Query q:Database.query_list){
										querysize=querysize+q.getPayload().length+23;
									}

									double queryhitsize=0.0d;
									for(QueryHit q:Database.queryhit_recieved_list){
										queryhitsize=queryhitsize+q.getPayload().length+23;
									}

									double bytes_rcvd=Database.pong_recieved_list.size()*37+Database.ping_list.size()*23+querysize+queryhitsize;
									double bytes_sent=Simpella.simpella_byte_sent;

									System.out.println("NET STATS:\n----------\n\tMsg Received: "+msg_rcvd+" Msg Sent: "+msg_sent+"\n\t\tUnique GUIDs in memory: "+(Database.routeTablePing.size()+Database.routeTableQuery.size())+"\n\tBytes Rcvd: "+bytes_rcvd+" Bytes Sent: "+bytes_sent);
									System.out.println("---------------------------------------");
									System.out.println("\n\n");
									continue;
								}
								else if(cmd[1].equalsIgnoreCase("q")){
									System.out.println("QUERY STATS:\n------------\n\tQueries: "+Database.query_list.size()+" Responses Sent: "+Database.queryhit_sent_list.size());
									System.out.println("---------------------------------------");
									System.out.println("\n\n");
									continue;
								}
								else if(cmd[1].equalsIgnoreCase("s")){
									System.out.println("SHARE STATS:\n------------\n\tNum Shared: "+ShareScan.no_of_files+" Size Shared: "+ShareScan.dir_size);
									System.out.println("---------------------------------------");
									System.out.println("\n\n");
									continue;
								}
								else{
									System.out.println("Invalid Input given \n\n\t\t info [cdhnqs] : Possible options for info command");
									continue;
								}

							}
							else if(cmd[0].equalsIgnoreCase("share")){



								if(cmd[1].equalsIgnoreCase("-i")){
									System.out.println("sharing: "+ShareScan.getDir().getAbsolutePath());
								}
								else if (cmd[1].equalsIgnoreCase("dir")){

									String msg1="";
									for(int i=2;i<cmd.length;i++){

										msg1=msg1+cmd[i]+" ";
									}
									msg1.trim();
									msg1=msg1.substring(0, msg1.length()-1);
								//	System.out.println(msg1);		
									ShareScan.setDir(msg1);
									System.out.println("---------------------------------------");
									System.out.println("\n\n");
								}
								else{

									String msg1="";
									for(int i=1;i<cmd.length;i++){

										msg1=msg1+cmd[i]+" ";
									}
									msg1.trim();
									msg1=msg1.substring(0, msg1.length()-1);

							//		System.out.println(msg1);

									ShareScan.setDir(msg1);
									System.out.println("---------------------------------------");
									System.out.println("\n\n");
								}
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("scan")){

								System.out.println("scanning: "+ShareScan.getDir().getAbsolutePath()+" for files...");
								System.out.println("Scanned "+ShareScan.getNo_of_files() +" files and "+ShareScan.getDir_size()+" Kb.");
								//ShareShow.setDir(cmd[1]);
								System.out.println("---------------------------------------");
								System.out.println("\n\n");

								continue;
							}
							else if(cmd[0].equalsIgnoreCase("open")){
								try {
									String s1=cmd[1].substring(0, cmd[1].indexOf(":"));
									int i1=Integer.parseInt(cmd[1].substring(cmd[1].indexOf(":")+1));
									if((s1.equalsIgnoreCase(addr.getHostAddress())||s1.equalsIgnoreCase(addr.getCanonicalHostName()))&&i1==TCP_PORT||i1==DOWNLOAD_PORT){
										System.out.println("CANNOT CONNECT TO SELF SERVER");
									}
									else{
										if(Database.out_conn.size()<3){
											Client.open(s1,i1);	
										}
										else{
											System.out.println("Cannot create more than 3 outgoing connections");
										}

									}

								} catch (Exception e) {

									System.out.println("\n CONNECTION NOT POSSIBLE : INVALID IP OR PORT");
								}
								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("update")){
								Listener.sendstaticPing();
								System.out.println("\n\tUPDATING \n");
								System.out.println("---------------------------------------");
								continue;

							}

							else if(cmd[0].equalsIgnoreCase("find")){
								String msg1="";
								for(int i=1;i<cmd.length;i++){
									msg1=msg1+cmd[i]+" ";
								}
								msg1.trim();
								msg1=msg1.substring(0, msg1.length()-1);

								if((msg1.length()+25)>256)
								{
									System.out.println("Find string will result to QUERY_packet.length() > 256. Thus, request dropped");
								}
								else{
									int listprevcount=Database.list.size();
									Listener.sendstaticQuery(msg1);
									System.out.println("\nsearching Simpella network for '"+msg1+"'");
									BufferedReader obj=new BufferedReader(new InputStreamReader(System.in));
									int i,k;
									try {
										Thread.sleep(3000);
									} catch (Exception e) {
							//			e.printStackTrace();
									}
									k=Database.list.size();
									System.out.println((k-listprevcount)+" responses received\n");
									for(i=0;i<k;i++)
									{
										if(i%10==0)
										{
											System.out.println("press enter to continue");
											System.out.println((k-listprevcount)+" responses received\n");
											System.out.println("------------------------------");
											obj.readLine();
										}
										Result r=Database.list.get(i);
										System.out.println((i+1)+")  "+r.getIp()+":"+r.getPort()+"\t\t"+"Size: "+r.getFileSize());
										System.out.println("Name: "+r.getFilename());
										k=Database.list.size();
									}
								}
								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("list")){

								BufferedReader obj=new BufferedReader(new InputStreamReader(System.in));
								int i,k;


								//		Iterator iterator=Database.list.iterator();
								k=Database.list.size();
								System.out.println(k+": Total list\n");
								for(i=0;i<k;i++)
								{

									if(i%10==0)
									{

										System.out.println("press enter to continue");

										System.out.println("------------------------------");
										obj.readLine();
									}

									Result r=Database.list.get(i);
									System.out.println((i+1)+")  "+r.getIp()+":"+r.getPort()+"\t\t"+"Size: "+r.getFileSize());
									System.out.println("Name: "+r.getFilename());
								}
								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								continue;

							}
							else if(cmd[0].equalsIgnoreCase("clear")){

								try{
									cmd[1].length();
								}catch(Exception e){
									Database.list=new ArrayList();
									System.out.println("List Cleared");
									System.out.println("---------------------------------------");
									continue;
								}

								if(cmd[1].length()!=0){
									try {
										Database.list.remove((Integer.parseInt(cmd[1])-1));
										System.out.println("File removed INDEX: "+cmd[1]);
									} catch (Exception e) {

										System.out.println("Invalid Index value");
										System.out.println("---------------------------------------");
										continue;
									}
								}
								else{
									Database.list=new ArrayList();
								}
								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("monitor")){

								System.out.println("MONITORING SIMPELLA NETWORK:");
								BufferedReader obj=new BufferedReader(new InputStreamReader(System.in));
								int i,k;
								k=Database.query_list.size();
								for(i=0;i<k;i++)
								{

									if(i%10==0)
									{

										System.out.println("press enter to continue");

										System.out.println("------------------------------");
										obj.readLine();
									}

									Query r=Database.query_list.get(i);

									System.out.println("Search: '"+r.getQuery());

								}
								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								continue;

							}
							else if(cmd[0].equalsIgnoreCase("download")){

								try{
									cmd[1].length();
								}catch(Exception e){

									System.out.println("Invalid Index");
									System.out.println("---------------------------------------");
									continue;
								}

								try{
									int i=Integer.parseInt(cmd[1]);
									HttpClient c=new HttpClient(Database.list.get(i-1));
								}catch(Exception e){
									System.out.println("Invalid Index");
									System.out.println("---------------------------------------");
									continue;
								}

								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("quit")){
								//	System.out.println("QUIT COMMAND");
								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								System.exit(1);
							}
							else{
								System.out.println("No such command:"+cmd[0]);
								System.out.println("---------------------------------------");
								System.out.println("\n\n");
								continue;
							}


						}catch(Exception e){
				//			e.printStackTrace();
							System.out.println("No such command:"+cmd[0]);
							System.out.println("---------------------------------------");
							System.out.println("\n\n");
						}
					}

				}
			};
			t2.start();

		}catch(IOException e)
		{
		//	e.printStackTrace();
		}

	}
}


