package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import cli.MFSS;

public class Restore implements Runnable{
	private  Integer restorePort;
	private InetAddress restoreGroupAddress;
	private MulticastSocket controlSocket;

	public Restore(InetAddress mCastGroupAddress, Integer restorePort) throws IOException{
		this.restoreGroupAddress = mCastGroupAddress;
		this.restorePort = restorePort;	  
		controlSocket = new MulticastSocket(this.restorePort);
	}	

	@Override
	public void run() {
		while(true){
			System.out.println("Restore Running...");
			String message = "Hi im a Restore thread!";
			byte[] sdata = message.getBytes();
			DatagramPacket pack;
			try {
				pack = new DatagramPacket(sdata, sdata.length,restoreGroupAddress, restorePort);
				controlSocket.setTimeToLive(MFSS._TTL);
				controlSocket.send(pack);
				Random r = new Random();
				Thread.sleep(r.nextInt(MFSS._RANDOMSLEEPTIME));
			} catch (InterruptedException | IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected void joinMCGroup() throws IOException{
		controlSocket.joinGroup(restoreGroupAddress);
	}

	//******************Getters

	public InetAddress getmCastGroupAddress() {
		return restoreGroupAddress;
	}


	//******************Setters 

	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		this.restoreGroupAddress = mCastGroupAddress;
	}


}
