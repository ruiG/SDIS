package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import cli.MFSS;
import dataStruct.Chunk;
import dataStruct.Message;

public class Backup extends Thread{

	private static int backupPort;
	private static InetAddress backupGroupAddress;
	private MulticastSocket backupSocket;
	volatile boolean finished = false;

	public Backup(InetAddress mCastGroupAddress, Integer backupPort) throws IOException{
		Backup.backupGroupAddress = mCastGroupAddress;
		Backup.backupPort = backupPort;
		backupSocket = new MulticastSocket(backupPort);
		backupSocket.setLoopbackMode(true);
		this.joinMCGroup();

	}

	public void stopMe(){
		finished = true;
	}

	protected void joinMCGroup() throws IOException{
		backupSocket.joinGroup(backupGroupAddress);
	}
	@Override
	public void run() {
		while(!finished){
			String version="", fileID="", chunknr="", repldeg="";
			byte[] receiveData = new byte[65000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				backupSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());
			if(MFSS.debugmode && sentence.length() > 20)
				System.out.println("RECEIVED IP: "+ receivePacket.getAddress()+ " " + sentence.substring(0, 20));
			String[] st = sentence.split("\r\n\r\n");
			String head = st[0];
			int sizeHead = st[0].length() +4;
			
			String[] tokens = Message.parseTokensFromString(head);
			if(tokens[0].equals("PUTCHUNK")){
				System.out.println("checking version...");
				version = tokens[1];
				if (version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)) {
					if (!MFSS.debugmode) {
						System.out.println("Parsing a PutChunk message...");
					}					
					fileID = tokens[2];
					chunknr = tokens[3];
					repldeg = tokens[4];
					byte[] chunkReceive = new byte[receivePacket.getLength() - sizeHead];	
					Message.readbytes(chunkReceive,receivePacket.getData(),sizeHead);
					try {
						parsePUTCHUNK(fileID, chunknr, chunkReceive,Integer.parseInt(repldeg));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					continue;
				}
				else {
					System.out.println("incorrect version received from: "+receivePacket.getAddress());
				}

			}else {
				System.out.println("incorrect token received from: "+receivePacket.getAddress());
			}
			
		}
		backupSocket.close();
	}

	private void parsePUTCHUNK(String fileID, String chunknr, byte body[], int repdeg){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID,body,repdeg);
		if(MFSS.used_disk_space + body.length <= MFSS.maximum_disk_space){
			System.out.println("CHUNK CREATED \n");
			c.save();
			MFSS.used_disk_space += body.length;
			if(MFSS.debugmode){
				System.out.println("Saved a chunk number: "+c.getChunkNo());
			}
			byte[] toSend = Message.STORED(fileID, chunknr);
			Message.sendMessage(backupSocket, Control.getmCastGroupAddress(), Control.getControlPort(), toSend);
			if(MFSS.debugmode){
				System.out.println("Stored message sent");
			}
		}
		else{
			System.out.println("failed to save chunk... Disk Space full");
		} 
	}

	public void closeSocket() throws SocketException{
		backupSocket.close();
	}
	
	//******************Getters

	public static InetAddress getmCastGroupAddress() {
		return backupGroupAddress;
	}

	public static int getControlPort() {
		return backupPort;
	}
	
	//******************Setters 

	public void setControlPort(int backupPort) {
		Backup.backupPort = backupPort;
	}

	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		Backup.backupGroupAddress = mCastGroupAddress;
	}
}
