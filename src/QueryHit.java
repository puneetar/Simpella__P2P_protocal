import java.io.File;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;


public class QueryHit extends Message {

	QueryHit(){

	}

	QueryHit(byte []guid,String find){
		super(guid,(byte)0x81);
		formPayload(new String(find));
	}

	void formPayload(String find){
		try {
//			System.out.println("***** I am in form payload");
			DirScan.filenamesize=0;
			DirScan.match=new ArrayList();
			DirScan.temp1=0;
			DirScan.temp2=0;
			DirScan.no_of_matches=0;

	//		System.out.println(ShareScan.getDir().getCanonicalPath());
		//	System.out.println("to find:"+find+"00000");

			DirScan.scan(ShareScan.getDir(),find);
	//		System.out.println("No. Of matches FOUND: "+DirScan.getNo_of_matches()+"\nTotal File Name Size: "+DirScan.getFilenamesize());

			int buffsize=27+(DirScan.getNo_of_matches()*9)+DirScan.getFilenamesize();

			ByteBuffer dbuf = ByteBuffer.allocate(buffsize);
			dbuf.put((byte)DirScan.getNo_of_matches());
			dbuf.putShort(Simpella.DOWNLOAD_PORT);

			Socket a = new Socket("8.8.8.8",53);
			byte[] x=a.getLocalAddress().getAddress();
			dbuf.put(x);
			a.close();

			dbuf.putInt(10000);

			for(Result r:DirScan.match){
				dbuf.putInt(r.getFileIndex());
				dbuf.putFloat(r.getFileSize());
				dbuf.put((r.getFilename()+'\0').getBytes());

			}

			dbuf.put(Simpella.SERVENTID);
			addPayload(dbuf.array());
		//	System.out.println("QUERYHIT payload length : "+dbuf.array().length);

		}catch (Exception io) {System.out.println("");}

	}

}



class QueryParser{


	byte no_of_hits=0;
	short port=(short)0;
	String ip=null;
	int speed;
	byte[] serventid;
	QueryHit query;
	ArrayList<Result> results=new ArrayList();


	QueryParser(QueryHit query){

		this.query=query;

		byte[] a=query.getPayload();
		no_of_hits=ByteBuffer.wrap(a, 0, 1).get();
		port=ByteBuffer.wrap(a, 1, 2).getShort();

		try {
			ip=InetAddress.getByAddress(Arrays.copyOfRange(a, 3, 7)).getHostAddress();
		} catch (UnknownHostException e) {

	//		e.printStackTrace();
		}

		int flag=0;
		byte[] rs=Arrays.copyOfRange(a, 11, a.length-11);

		int i=0;

		while(i<rs.length)
		{

			int index=ByteBuffer.wrap(rs,i,4).getInt();
			i=i+4;
			int size=ByteBuffer.wrap(rs,i,4).getInt();
			i=i+4;
			int k=i;
			byte name[]=new byte[4096];
			int j=0;
			while(rs[k]!=(byte)'\0')
			{
				name[j]=rs[k];
				j++;
				k++;
			}
			String fname=new String(name);
			Database.list.add(new Result(index,size,fname,ip,port));
			results.add(new Result(index,size,fname,ip,port));
			flag++;
			i=k+1;
			if(flag==no_of_hits)
				break;

		}
	}
}

