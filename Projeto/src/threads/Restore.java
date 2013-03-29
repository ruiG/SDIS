package threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Restore extends Thread{
	private static int restorePort;
	private static InetAddress restoreGroupAddress;
	private MulticastSocket restoreSocket;

	public Restore(InetAddress mCastGroupAddress, Integer restorePort) throws IOException{
		Restore.restoreGroupAddress = mCastGroupAddress;
		Restore.restorePort = restorePort;		  
		restoreSocket = new MulticastSocket(Restore.restorePort);
	}	

	@Override
	public void run() {
		
	}

	protected void joinMCGroup() throws IOException{
		restoreSocket.joinGroup(restoreGroupAddress);
	}

	//******************Getters
	
	public static InetAddress getmCastGroupAddress() {
		return restoreGroupAddress;
	}

	public static int getControlPort() {
		return restorePort;
	}
	
	//******************Setters 

	public void setControlPort(int restorePort) {
		Restore.restorePort = restorePort;
	}

	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		Restore.restoreGroupAddress = mCastGroupAddress;
	}



}