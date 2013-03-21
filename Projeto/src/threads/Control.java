package threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Control implements Runnable{
	private  int controlPort = 3000;
	private InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;
	
	public Control(InetAddress mCastGroupAddress) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		controlSocket = new MulticastSocket(controlPort);
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
