package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import cli.MFSS;

import dataStruct.Chunk;

public class Backup implements Runnable{

	private  int backupPort;
	private InetAddress backupGroupAddress;
	private MulticastSocket backupSocket;

	public Backup(InetAddress mCastGroupAddress, Integer backupPort) throws IOException{
		this.backupGroupAddress = mCastGroupAddress;
		this.backupPort = backupPort;
		backupSocket = new MulticastSocket(backupPort);
		this.joinMCGroup();

	}

	protected void joinMCGroup() throws IOException{
		backupSocket.joinGroup(backupGroupAddress);
	}

	@Override
	public void run() {
		while(true){
			//TODO stuff...
		}	
	}

	public void send(Chunk ck, int repdegree) {
		byte[] sdata = PutCkMessage(ck, repdegree);
		System.out.println("MDB Send:");
		DatagramPacket pack;
		try {
			pack = new DatagramPacket(sdata, sdata.length,backupGroupAddress, backupPort);
			backupSocket.setTimeToLive(MFSS._TTL);
			backupSocket.send(pack);
			Random r = new Random();
			Thread.sleep(r.nextInt(MFSS._RANDOMSLEEPTIME));
		} catch (InterruptedException | IOException e1) {
			e1.printStackTrace();
		}
	}

	public byte[] PutCkMessage(Chunk ck, int repdegree){
		String body = ck.getData().toString();		
		String repD = Integer.toString(repdegree);
		String fileId = ck.getFileId();
		String version = MFSS._VERSION;
		String chN = ck.getChunkNo().toString();
		byte[] message = ("PUTCHUNK "+version+" "+fileId+" "+chN+" "+repD+" "+MFSS._CRLF+MFSS._CRLF+body).getBytes();
		return message;	
	}

	//******************Getters

	public InetAddress getmCastGroupAddress() {
		return backupGroupAddress;
	}


	//******************Setters 

	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		this.backupGroupAddress = mCastGroupAddress;
	}
}
