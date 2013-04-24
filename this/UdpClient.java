import java.io.*; 
import java.net.*; 

class UdpClient { 

	public static void sendto(String ip,int port,String message) throws Exception 
	{ 
		try {
			DatagramSocket clientSocket = new DatagramSocket();

			String serverHostname=ip;

			InetAddress IPAddress = InetAddress.getByName(serverHostname); 
			System.out.println ("Attemping to connect to " + IPAddress + 
					") via UDP port ");

			byte[] sendData = new byte[1024]; 
			byte[] receiveData = new byte[1024]; 
			String sentence = message; 
			sendData = sentence.getBytes();         
			DatagramPacket sendPacket = 
					new DatagramPacket(sendData, sendData.length, IPAddress, port); 

			clientSocket.send(sendPacket); 
			DatagramPacket receivePacket = 
					new DatagramPacket(receiveData, receiveData.length); 
			clientSocket.setSoTimeout(10000);

			try {
				clientSocket.receive(receivePacket); 
				String modifiedSentence = new String(receivePacket.getData()); 
				System.out.println(modifiedSentence.trim()); 

			}
			catch (SocketTimeoutException ste)
			{
				System.out.println ("UNABLE TO REACH SERVER");
			}

			clientSocket.close(); 
		}
		catch (UnknownHostException ex) { 
			System.out.println ("INVALID IP OR PORT");
		}
		catch (IOException ex) {
			System.out.println ("INVALID IP OR PORT");
		}
	} 
} 
