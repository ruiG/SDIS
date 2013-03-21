package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class Control implements Runnable{
	private static final int _TTL = 1;
	private  int restorePort;
	private  int controlPort;
	private  int backupPort;
	private InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;
	
	public Control(InetAddress mCastGroupAddress, Integer restorePort, Integer controlPort, Integer backupPort) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		this.restorePort = restorePort;
		this.controlPort = controlPort;
		this.backupPort = backupPort;	
		controlSocket = new MulticastSocket(this.controlPort);
		this.joinMCGroup();
	}	
	
	public void stop(){
		
	}
	
	@Override
	public void run() {
		while(true){
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				controlSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());
			System.out.println("RECEIVED: " + sentence);
		}
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
