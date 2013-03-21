package threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Restore implements Runnable{
	private  int restorePort = 3002;
	private InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;
	
	public Restore(InetAddress mCastGroupAddress) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		controlSocket = new MulticastSocket(restorePort);
	}	
	
	@Override
	public void run() {
		//TODO stuff....	
	}
	
	protected void joinMCGroup() throws IOException{
		controlSocket.joinGroup(mCastGroupAddress);
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
