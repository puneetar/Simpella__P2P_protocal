import java.io.IOException;
import java.net.*;
import java.util.Formatter;
import java.io.*;

public class Echoer
{
	static String SIMPELLA_CONNECT="SIMPELLA CONNECT/0.6\r\n";
	static String SIMPELLA_OK="SIMPELLA/0.6 200 OK\r\n";
	static String SIMPELLA_THANK="SIMPELLA/0.6 200 thank you for accepting me\r\n";
	static String SIMPELLA_ERROR="SIMPELLA/0.6 503 Maximum number of connections reached. Sorry!\r\n";

	static short TCP_PORT;
	static short DOWNLOAD_PORT;
	static String QWN_IP;
	
	public static void main(String [] args)
	{
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

		final int tcp_port=Integer.parseInt(args[0]);
		TCP_PORT=(short)tcp_port;
		final int udp_port=Integer.parseInt(args[1]);
		DOWNLOAD_PORT=(short)udp_port;

		try
		{
			Thread t = new TcpServer(tcp_port);
			t.start();

			Thread t3=new UdpServer(udp_port);
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

					System.out.println("Local IP: "+addr.getHostAddress());
					System.out.println("Simpella Net Port: "+tcp_port+"\nDownloading Port: "+udp_port);
					System.out.println("simpella version 0.6 (c) 2002-2003 XYZ");

					BufferedReader read_cmd=new BufferedReader(new InputStreamReader(System.in));
					while(true)	{
						String[] cmd=null;
						try{
							cmd = read_cmd.readLine().split(" ");
							if(cmd[0].equalsIgnoreCase("info")){

								Formatter headerFormat=new Formatter();
								System.out.println(headerFormat.format("%20s | %30s | %13s | %12s","IP","hostname","udp port","tcp port"));
								headerFormat.close();

								Formatter headerFormat1=new Formatter();
								System.out.println(headerFormat1.format("%75s","------------------------------------------------------------------------------------"));
								headerFormat1.close();
								Formatter tabFormat=new Formatter();
								System.out.println(tabFormat.format("%20s | %30s | %13s | %12s",""+addr.getHostAddress(),""+addr.getCanonicalHostName(),""+udp_port,""+tcp_port));
								tabFormat.close();
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("share")){
								if(cmd[1].equalsIgnoreCase("-i")){
									System.out.println("sharing: "+ShareScan.getDir().getAbsolutePath());
								}
								else if (cmd[1].equalsIgnoreCase("dir")){
									ShareScan.setDir(cmd[2]);
									System.out.println("\n\n");
								}
								else{
									ShareScan.setDir(cmd[1]);
									System.out.println("\n\n");
								}
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("scan")){

								System.out.println("scanning: "+ShareScan.getDir().getAbsolutePath()+" for files...");
								System.out.println("Scanned "+ShareScan.getNo_of_files() +" files and "+ShareScan.getDir_size()+"bytes.");
								//ShareShow.setDir(cmd[1]);
								System.out.println("\n\n");

								continue;
							}
							else if(cmd[0].equalsIgnoreCase("open")){
								try {
									String s1=cmd[1].substring(0, cmd[1].indexOf(":"));
									int i1=Integer.parseInt(cmd[1].substring(cmd[1].indexOf(":")+1));
									if((s1.equalsIgnoreCase(addr.getHostAddress())||s1.equalsIgnoreCase(addr.getCanonicalHostName()))&&i1==tcp_port){
										System.out.println("CANNOT CONNECT TO SELF SERVER");
									}
									else{
										TcpClient.open(s1,i1);
									}

								} catch (Exception e) {

									System.out.println("\n CONNECTION NOT POSSIBLE : INVALID IP OR PORT");
								}
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("show")){
								TcpClient.show();
								System.out.println("\n\n");
								continue;

							}
							else if(cmd[0].equalsIgnoreCase("send")){
								String msg="";
								for(int i=2;i<cmd.length;i++){
									msg=msg+" "+cmd[i];
								}
								msg.trim();

								TcpClient.send(Integer.parseInt(cmd[1]),msg);
								System.out.println("\n\n");
								continue;

							}
							else if(cmd[0].equalsIgnoreCase("sendto")){
								String msg="";
								for(int i=3;i<cmd.length;i++){
									msg=msg+" "+cmd[i];
								}
								msg.trim();

								UdpClient.sendto(cmd[1], Integer.parseInt(cmd[2]),msg);
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("disconnect")){
								TcpClient.disconnect(Integer.parseInt(cmd[1]));
								System.out.println("\n\n");
								continue;
							}
							else if(cmd[0].equalsIgnoreCase("quit")){
								System.out.println("QUIT COMMAND");
								System.out.println("\n\n");
								System.exit(1);
							}
							else{
								System.out.println("No such command:"+cmd[0]);
								System.out.println("\n\n");
								continue;
							}


						}catch(Exception e){
							e.printStackTrace();
							System.out.println("No such command:"+cmd[0]);
							System.out.println("\n\n");
						}
					}

				}
			};
			t2.start();

		}catch(IOException e)
		{
			e.printStackTrace();
		}

	}
}


