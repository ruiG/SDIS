package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import cli.MFSS;
import dataStruct.Chunk;
import dataStruct.Message;

public class Restore extends Thread{
	static int restorePort;
	static InetAddress restoreGroupAddress;
	MulticastSocket restoreSocket;
	volatile boolean finished = false; 

	public Restore(InetAddress mCastGroupAddress, Integer restorePort) throws IOException{
		Restore.restoreGroupAddress = mCastGroupAddress;
		Restore.restorePort = restorePort;		  
		restoreSocket = new MulticastSocket(Restore.restorePort);
		restoreSocket.setLoopbackMode(true);
		finished = false;
		this.joinMCGroup();
	}	

	@Override
	public void run() {

		while(!finished){
			String version="", fileID="", chunknr="";
			byte[] receiveData = new byte[65000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				restoreSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String(receivePacket.getData(),0,receivePacket.getLength());

			if(MFSS.debugmode && sentence.length() > 20)
				System.out.println("RECEIVED IP: "+ receivePacket.getAddress()+ " " + sentence.substring(0, 20));

			String[] st = sentence.split("\r\n\r\n");
			String head = st[0];	
			int sizeHead = st[0].length() +4;
			String[] tokens = Message.parseTokensFromString(head);

			if(tokens[0].equals("CHUNK")){
				if(MFSS.requestedFileID != null){

					version = tokens[1];
					if (version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)) {
						if (MFSS.debugmode) 
							System.out.println("Parsing chunk...");
						fileID = tokens[2];
						chunknr = tokens[3];
						if(!chunknr.equals(MFSS.requestedChunkNr) || !fileID.equals(MFSS.requestedFileID))
							continue;	
						byte[] chunkReceive = new byte[receivePacket.getLength() - sizeHead];	
						Message.readbytes(chunkReceive,receivePacket.getData(),sizeHead);
						try {
							parseCHUNK(fileID, chunknr, chunkReceive);
							MFSS.t.interrupt();
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						continue;
					}
				}
			}
			else{
				System.out.println("incorrect token received from: "+receivePacket.getAddress());
			}
		}
		restoreSocket.close();
	}



	private void parseCHUNK(String fileID, String chunknr, byte[] body) {
		if(MFSS.requestedChunkNr.equals(chunknr) && MFSS.requestedFileID.equals(fileID)){
			Chunk c=new Chunk(Integer.parseInt(chunknr), fileID,body,1);
			System.out.println("REQUESTED CHUNK CREATED \n");
			c.save();
			if(MFSS.debugmode){
				System.out.println("Created a chunk with number: "+c.getChunkNo());
			}
		}
	}

	protected void joinMCGroup() throws IOException{
		restoreSocket.joinGroup(restoreGroupAddress);
	}

	public void closeSocket() throws SocketException{
		restoreSocket.close();
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

	public void stopMe() {
		finished = true;		
	}



}