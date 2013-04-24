import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.io.*;
import java.net.Socket;

public class HttpServer extends Thread
{
	private ServerSocket serverSocket;

	public HttpServer(int port) throws IOException
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
				new TcpServerImpl(serverSocket.accept());
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




class TcpServerImpl extends Thread {

	protected Socket clientsocket;

	public TcpServerImpl(Socket s){
		clientsocket=s;
		start();
	}

	public void run(){
		try {
			System.out.println("DOWNLOAD Incomming Connection from IP: "  + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
			ObjectInputStream in =     new ObjectInputStream(clientsocket.getInputStream());
			ObjectOutputStream out =    new ObjectOutputStream(clientsocket.getOutputStream());
			while (true) {
				Object b=in.readObject();
				//	System.out.println("the class of the object is :"+b.getClass());
				String clientMessage = (String)b;
				if(clientMessage.startsWith("GET")){

					System.out.println("\n"+clientMessage+"\n");
					String filename=clientMessage.substring(clientMessage.indexOf("/", 9+1)+1, clientMessage.indexOf("HTTP/1.1")-2);
//					System.out.println("File is :"+filename+":");
					String fullpath=""+ShareScan.dir.getAbsolutePath()+"/"+filename;
//					System.out.println("Full path:"+fullpath+":");
					File file=new File(fullpath);
//					System.out.println("*****FILE ****"+file.getAbsolutePath());
					if(file.exists()&&file.isFile()){

						String ok=""+"HTTP/1.1 200 OK\r\nServer: Simpella0.6\r\nContent-type: application/binary\r\nContent-length: "+file.length()+"\r\n\r\n";
						out.writeObject(ok);
						
						System.out.println("\n"+ok+"\n");
						System.out.println("Uploading file... : "+file.getName()+"\t to : "+clientsocket.getInetAddress().getHostAddress()+":"+clientsocket.getPort());

						BufferedInputStream bin=new BufferedInputStream(new FileInputStream(file),1024);

						BufferedOutputStream bout=new BufferedOutputStream(clientsocket.getOutputStream(),1024);

						byte[] data = new byte[1024];
						
						while((bin.read(data,0,1024)>-1)){
							bout.write(data,0, data.length);
						}

						bin.close();
						bout.flush();
						bout.close();
						System.out.println("Upload complete of file : "+file.getName()+"\t to : "+clientsocket.getInetAddress().getHostAddress()+":"+clientsocket.getPort());
						clientsocket.close();
						
						
						break;
					}
					else{
						out.writeObject(new String("HTTP/1.1 503 File not found.\r\n\r\n"));
						System.out.println("HTTP/1.1 503 File not found");
						break;

					}
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

