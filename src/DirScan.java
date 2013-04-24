import java.io.File;
import java.util.ArrayList;

public class DirScan {

	static int no_of_matches=0;
	static int temp1=0,temp2=1;
	static ArrayList<Result> match=new ArrayList();
	static int filenamesize=0;
	
	
	public static void scan(File dir,String find) {


		File[] filelist = dir.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {

			} else {
				if(filelist[i].getName().matches("(?i).*"+find+".*")){
					temp1=temp1+filelist[i].getName().length();
					if(temp1>4096||no_of_matches>=127){

						break;
					}
					else
					{

						match.add(new Result(temp2,((float)filelist[i].length()/1024),filelist[i].getName(),"ip",(short)0));
						filenamesize=temp1;
						no_of_matches++;
						temp2++;
					}
				}
			}
		}
	}

	public static int getNo_of_matches() {
		return DirScan.no_of_matches;
	}

	public static int getTemp1() {
		return DirScan.temp1;
	}

	public static ArrayList<Result> getMatch() {
		return DirScan.match;
	}

	public static int getFilenamesize() {
		return DirScan.filenamesize;
	}
}
