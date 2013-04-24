import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UdpServer extends Thread {

	private DatagramSocket serverSocket;
	byte[] receiveData = new byte[1024]; 
	byte[] sendData  = new byte[1024]; 
	public UdpServer(int port){

		try {
			serverSocket= new DatagramSocket(port);
			System.out.println("UDP_SERVER : STARTED");
		} catch (SocketException e) {
			System.out.println("PORT NUMBER ALREADY OCCUPIED");
			System.exit(1);

		}
	}

	public void run(){

		try{
			while(true) 
			{ 

				receiveData = new byte[1024]; 

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
				serverSocket.receive(receivePacket); 
				String sentence = new String(receivePacket.getData()); 
				InetAddress IPAddress = receivePacket.getAddress(); 
				int port = receivePacket.getPort(); 
				sendData = sentence.getBytes(); 
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,port); 
				serverSocket.send(sendPacket); 

				System.out.println("echoing " + sentence.trim()
						+ "\n\t to: IP = "
						+ IPAddress
						+ "\n\t type = udp\n\n");

			} 

		}
		catch (Exception ex) {
			System.out.println("UDP Port is occupied.");
			System.exit(1);
		}
	}

}
