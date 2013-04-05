package threads;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
		finished = false;
		this.joinMCGroup();
	}	

	@Override
	public void run() {

		while(!finished){
			String version="", fileID="", chunknr="", repldeg="";
			byte[] receiveData = new byte[64000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				restoreSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());

			if(MFSS.debugmode)
				System.out.println("RECEIVED: " + sentence.substring(0, 20));

			String[] st = sentence.split("\r\n\r\n");
			String head = st[0];
			String b = st[1];
			String[] tokens = Message.parseTokensFromString(head);

			if(tokens[0].equals("CHUNK")){
				if(MFSS.numberOfChunksRequested > 0){
					version = tokens[1];
					if (version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)) {
						if (!MFSS.debugmode) 
							System.out.println("Received a Chunk message, parsing...");
						fileID = tokens[2];
						chunknr = tokens[3];
						repldeg = tokens[4];
						try {
							parseCHUNK(fileID, chunknr, b.getBytes("US-ASCII"),Integer.parseInt(repldeg));
						} catch (NumberFormatException | UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						continue;
					}
				}
			}

			restoreSocket.close();

		}
	}

	private void parseCHUNK(String fileID, String chunknr, byte[] body,	int repdeg) {
		if(MFSS.requestedChunkNr.equals(chunknr) && MFSS.requestedFileID.equals(fileID)){
			Chunk c=new Chunk(Integer.parseInt(chunknr), fileID,body,repdeg);
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