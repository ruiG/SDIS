package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import cli.MFSS;

public class Restore implements Runnable{
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
		while(true){
			System.out.println("Restore Running...");
			String message = "Hi im a Restore thread!";
			byte[] sdata = message.getBytes();
			DatagramPacket pack;
			try {
				pack = new DatagramPacket(sdata, sdata.length,restoreGroupAddress, restorePort);
				restoreSocket.setTimeToLive(MFSS._TTL);
				restoreSocket.send(pack);
				Random r = new Random();
				Thread.sleep(r.nextInt(MFSS._RANDOMSLEEPTIME));
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}catch(IOException e1){
				e1.printStackTrace();
			}
		}
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