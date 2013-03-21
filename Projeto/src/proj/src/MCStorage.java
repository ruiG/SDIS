package proj.src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;



public class MCStorage {
	public static int  controlPort = 3000;
	public static int  backupPort = 3001;
	public static int  restorePort = 3002;
	MulticastSocket controlSocket;
	MulticastSocket backupSocket;
	MulticastSocket restoreSocket;
	
	
	public void MCcontrol(InetAddress MCastGroup) throws IOException{
		 
		controlSocket = new MulticastSocket(controlPort);
		controlSocket.joinGroup(MCastGroup);
		(new Thread(){
				public void run(){
					
					
				}
			
			}
		
		).start();
	}
	
	public void MCbackup(InetAddress MCastGroup) throws IOException{
		 
		backupSocket = new MulticastSocket(controlPort);
		backupSocket.joinGroup(MCastGroup);
		(new Thread(){
				public void run(){
					
				}
			
			}
		
		).start();
	}
	
	public void MCrestore(InetAddress MCastGroup) throws IOException{
		 
		backupSocket = new MulticastSocket(controlPort);
		backupSocket.joinGroup(MCastGroup);
		(new Thread(){
				public void run(){
					
				}
			
			}
		
		).start();
	}
	
}
