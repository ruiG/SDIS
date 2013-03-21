package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class Backup implements Runnable{
	private static final int _TTL = 1;
	private  int restorePort;
	private  int controlPort;
	private  int backupPort;
	private InetAddress mCastGroupAddress;
	private MulticastSocket backupSocket;
	
	public Backup(InetAddress mCastGroupAddress, Integer restorePort, Integer controlPort, Integer backupPort) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		this.restorePort = restorePort;
		this.controlPort = controlPort;
		this.backupPort = backupPort;
		backupSocket = new MulticastSocket(backupPort);
		this.joinMCGroup();
		
	}
	
	protected void joinMCGroup() throws IOException{
		backupSocket.joinGroup(mCastGroupAddress);
	}
	
	@Override
	public void run() {
		while(true){
			System.out.println("Restore Running...");
			String message = "Hi im a Restore thread!";
			byte[] sdata = message.getBytes();
			DatagramPacket pack;
			try {
				pack = new DatagramPacket(sdata, sdata.length,mCastGroupAddress, controlPort);
				backupSocket.setTimeToLive(_TTL);
				backupSocket.send(pack);
				Random r = new Random();
				Thread.sleep(r.nextInt(500)+ 500);
			} catch (InterruptedException | IOException e1) {
				e1.printStackTrace();
			}
		}	
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