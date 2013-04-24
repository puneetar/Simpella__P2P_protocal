
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Database {
	
	static ArrayList<Node> in_conn=new ArrayList();
	static ArrayList<Node> out_conn=new ArrayList();
	static ArrayList<String> generatedPing=new ArrayList();
	static HashMap<String,Node> routeTablePing=new HashMap();
	
	static ArrayList<String> generatedQuery=new ArrayList();
	static HashMap<String,Node> routeTableQuery=new HashMap();
	
	static ArrayList<Result> list=new ArrayList();
	
	static ArrayList<Query> query_list=new ArrayList();
	static ArrayList<QueryHit> queryhit_recieved_list=new ArrayList();
	static ArrayList<QueryHit> queryhit_sent_list=new ArrayList();
	
	static ArrayList<Ping> ping_list=new ArrayList();
	static ArrayList<Pong> pong_recieved_list=new ArrayList();
	static ArrayList<Pong> pong_sent_list=new ArrayList();
	
	static ArrayList<Result> download=new ArrayList();
	static HashMap<Result,String> currentDownload=new HashMap();
	
	
	static HashMap<Socket,ConnInfo> conninfo=new HashMap(); 
	
	
	
			
}
