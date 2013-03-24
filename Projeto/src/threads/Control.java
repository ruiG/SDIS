package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class Control implements Runnable{
	private  int controlPort;
	private InetAddress controlGroupAddress;
	private MulticastSocket controlSocket;
	
	public Control(InetAddress mCastGroupAddress,Integer controlPort) throws IOException{
		this.controlGroupAddress = mCastGroupAddress;	
		this.controlPort = controlPort;		
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
		controlSocket.joinGroup(controlGroupAddress);
	}

	//******************Getters
	
	public InetAddress getmCastGroupAddress() {
		return controlGroupAddress;
	}

	
	//******************Setters 
	
	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		this.controlGroupAddress = mCastGroupAddress;
	}

	
}
