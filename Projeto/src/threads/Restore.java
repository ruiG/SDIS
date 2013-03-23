package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class Restore implements Runnable{
	private static final int _TTL = 1;
	private  Integer restorePort;
	private  Integer controlPort;
	private  Integer backupPort;
	private InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;

	public Restore(InetAddress mCastGroupAddress, Integer restorePort, Integer controlPort, Integer backupPort) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		this.restorePort = restorePort;
		this.controlPort = controlPort;
		this.backupPort = backupPort;		  
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
				pack = new DatagramPacket(sdata, sdata.length,mCastGroupAddress, controlPort);
				controlSocket.setTimeToLive(_TTL);
				controlSocket.send(pack);
				Random r = new Random();
				Thread.sleep(r.nextInt(500)+ 500);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}catch(IOException e1){
				e1.printStackTrace();
			}
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
