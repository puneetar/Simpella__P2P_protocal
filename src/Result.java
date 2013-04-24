import java.util.Arrays;


public class Result {
	
	int fileIndex;
	float fileSize;
	String filename;
	String ip;
	short port;

	Result(int index,float size,String fname,String ip,short port){
		this.fileIndex=index;
		this.fileSize=size;
		this.filename=fname;
		this.ip=ip;
		this.port=port;
		
	}

	public int getFileIndex() {
		return fileIndex;
	}

	public float getFileSize() {
		return fileSize;
	}

	public String getFilename() {
		return filename;
	}

	public String getIp() {
		return ip;
	}

	public short getPort() {
		return port;
	}

}
