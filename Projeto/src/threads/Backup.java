package threads;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import cli.MFSS;
import dataStruct.Chunk;
import dataStruct.Message;

public class Backup extends Thread{

	private static int backupPort;
	private static InetAddress backupGroupAddress;
	private MulticastSocket backupSocket;
	volatile boolean finished = false;
	 private int used_disk_space = 0;
	
	public Backup(InetAddress mCastGroupAddress, Integer backupPort) throws IOException{
		Backup.backupGroupAddress = mCastGroupAddress;
		Backup.backupPort = backupPort;
		backupSocket = new MulticastSocket(backupPort);
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
		String version="", fileID="", chunknr="", repldeg="";

		while(!finished){
			byte[] receiveData = new byte[64000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				backupSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());
			if(MFSS.debugmode){
				System.out.println("RECEIVED: " + sentence.substring(0, 20));
			}else{
				System.out.println("Received a Putchunk message, parsing...");
			}
			String[] st = sentence.split("\r\n\r\n");
			String head = st[0];
			String b = st[1];
			String[] tokens = Message.parseTokensFromString(head);
			if(tokens[0].equals("PUTCHUNK")){
				version = tokens[1];
				if (version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)) {
					if (!MFSS.debugmode) {
						System.out.println("Received a PutChunk message, parsing...");
					}					
					fileID = tokens[2];
					chunknr = tokens[3];
					repldeg = tokens[4];
					try {
						parsePUTCHUNK(fileID, chunknr, b.getBytes("US-ASCII"),Integer.parseInt(repldeg));
					} catch (NumberFormatException | UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}
			}
		}	
		backupSocket.close();
	}

	public byte[] StrMessage(Chunk ck, int repdegree){	
		byte[] message = Message.STORED(ck.getFileId(), ck.getChunkNoAsString()).getBytes();
		return message;	
	}


	public static byte[] PutCkMessage(Chunk ck, int repdegree){	
		byte[] message = Message.PUTCHUNK(ck.getFileId(), ck.getChunkNoAsString(), ck.getRepDegAsChar(), ck.getData()).getBytes();
		return message;	
	}

	private void parsePUTCHUNK(String fileID, String chunknr, byte body[], int repdeg){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID,body,repdeg);
		 if(this.used_disk_space + body.length <= MFSS.maximum_disk_space){
			 System.out.println("CHUNK CREATED \n");
			c.save();
			used_disk_space += body.length;
			if(MFSS.debugmode){
				System.out.println("Saved a chunk number: "+c.getChunkNo());
			}
			String toSend = Message.STORED(fileID, chunknr);
			Message.sendMessage(backupSocket, Control.getmCastGroupAddress(), Control.getControlPort(), toSend);
			if(MFSS.debugmode){
				System.out.println("Stored message sent");
			}
		}
		 else{
			 System.out.println("Error! No Disk Space to store Chunk");
		} 
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
