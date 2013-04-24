import java.io.*;


public class ShareScan {

	static File dir=null;
	static float dir_size=0.0f;
	static int no_of_files=0;
	static long long_foldersize = 0;

	{

	}

	public static void setDir(String path) {
		dir=null;
		dir_size=0.0f;
		no_of_files=0;
		long_foldersize = 0;

		dir=new File(path);
		if(dir.exists()&&dir.isDirectory()){
			long_foldersize=0;

			dir_size=(float)(getFolderSize(dir)/1024);	
//			System.out.println("new directory shared:"+path+" size of directory"+dir_size);
		}
		else{
			System.out.println("INVALID DIRECTORY NAME");
			dir=null;
		}
	}

	public static File getDir() {
		if(dir==null){
	//		System.out.println("NO DIRECTORY SHARED");
			return null;
		}
		else{
			return dir;	
		}
	}


	public static float getDir_size() {
		if(dir==null){
//			System.out.println("NO DIRECTORY SHARED");
			return 0.0f;
		}
		else{
			return dir_size;	
		}
		//return dir_size;
	}
	public static int getNo_of_files() {
		if(dir==null){
//			System.out.println("NO DIRECTORY SHARED");
			return 0;
		}
		else{
			return no_of_files;	
		}
		//return no_of_files;
	}

	public static long getFolderSize(File dir) {
		long foldersize = 0;

		File[] filelist = dir.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {
			//	foldersize += getFolderSize(filelist[i]);
			} else {
				no_of_files++;
				foldersize += filelist[i].length();
			}
		}
		return foldersize;
	}





}
