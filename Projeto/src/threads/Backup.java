package threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Backup implements Runnable{
	private  int backupPort = 3001;
	private InetAddress mCastGroupAddress;
	private MulticastSocket backupSocket;
	
	public Backup(InetAddress mCastGroupAddress) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		backupSocket = new MulticastSocket(backupPort);
		this.joinMCGroup();
		
	}
	
	protected void joinMCGroup() throws IOException{
		backupSocket.joinGroup(mCastGroupAddress);
	}
	
	@Override
	public void run() {
		//TODO stuff....		
	}
	
	
	//******************Getters
	
	public InetAddress getmCastGroupAddress() {
		return mCastGroupAddress;
	}

	
	//******************Setters 
	
	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		this.mCastGroupAddress = mCastGroupAddress;
	}
	
	
	
}