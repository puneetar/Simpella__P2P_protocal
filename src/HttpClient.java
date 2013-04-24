import java.net.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.io.*;

public class HttpClient extends Thread
{
	static ArrayList arr_socket=new ArrayList();
	Result r;

	HttpClient(Result result){

		this.r=result;
		start();
	}

	public void run(){

		try {
			Socket client = new Socket(r.getIp(),r.getPort());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			String get="GET /get/"+r.getFileIndex()+"/"+r.getFilename()+" HTTP/1.1\r\nUser-Agent: Simpella\r\nHost: "+r.getIp()+":"+r.getPort()+"\r\nConnection: Keep-Alive\r\nRange: bytes=0-\r\n\r\n";
	
			System.out.println("\n"+get+"\n");
			
			out.writeObject(get);

			String msg=(String)in.readObject();
			if(msg.startsWith("HTTP/1.1 200 OK")){

				System.out.println("\n"+msg+"\n");
				Database.download.add(r);
				
				File file=new File(ShareScan.dir,r.getFilename());
				file.createNewFile();
				System.out.println("Downloading file.... : "+file.getName()+"\t from : "+client.getInetAddress().getHostAddress()+":"+client.getPort());			
				BufferedInputStream bin=new BufferedInputStream(client.getInputStream(),1024);
				
				BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(file),1024);
				


				long l=0l;
				byte[] data = new byte[1024];

				while((bin.read(data,0,1024)>-1)){
    	            bout.write(data,0, data.length);
    	            l=l+1024;
    	            Database.currentDownload.put(r, ""+l);

				}
				
				bin.close();
				bout.flush();
				bout.close();
				System.out.println("Download complete of file : "+file.getName()+"\t from: "+client.getInetAddress().getHostAddress()+":"+client.getPort());
				Database.download.remove(r);
				client.close();
				
			}
			else if(msg.startsWith("HTTP/1.1 503 File not found")){
				System.out.println("HTTP/1.1 503 File not found");
			}
			else{
				System.out.println("Unknown response from Server");
			}
		} catch (UnknownHostException e) {
//			e.printStackTrace();
		} catch (Exception e) {
//			e.printStackTrace();
		}

	}


}
