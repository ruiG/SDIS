package threads;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import cli.MFSS;
import dataStruct.BackupFile;
import dataStruct.Chunk;
import dataStruct.Message;

public class Control extends Thread{
	private static int controlPort;
	private static InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;
	volatile boolean finished = false; 
	
	public Control(InetAddress mCastGroupAddress,Integer controlPort) throws IOException{
		Control.mCastGroupAddress = mCastGroupAddress;
		Control.controlPort = controlPort;	
		controlSocket = new MulticastSocket(Control.controlPort);
		this.joinMCGroup();
	}	
	
	public static byte[] CkMessage(Chunk ck, int repdegree){	
		byte[] message = Message.CHUNK(ck.getFileId(), ck.getChunkNoAsString(), ck.getData()).getBytes();
		return message;	
	}
	
	public void stopMe(){
	    finished = true;
	}


	@Override
	public void run() {
		String version="", fileID="", chunknr="";

		while(!finished){
			byte[] receiveData = new byte[64000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				controlSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());
			if(MFSS.debugmode){
				System.out.println("RECEIVED: " + sentence.substring(0, 20));
			}			
			
			String[] st = sentence.split("\r\n\r\n");
			String head = st[0];
			String[] tokens = Message.parseTokensFromString(head);			
			if(tokens[0].equals("GETCHUNK")){
				version=tokens[1];
				if(version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)){
					fileID=tokens[2];
					chunknr=tokens[3];
					parsedGETCHUNK(fileID, chunknr);
					continue;
				}
			}
			if(tokens[0].equals("DELETE")){
				fileID=tokens[1];
				parsedDELETE(fileID);
				continue;
			}
			
			if(tokens[0].equals("REMOVED")){
				fileID=tokens[1];
				if(version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)){
					fileID=tokens[2];
					chunknr=tokens[3];
					parsedREMOVED(fileID, chunknr);
					continue;
				}
				continue;
			}
			
			
		}
		controlSocket.close();
	}
	
	private void parsedGETCHUNK(String fileID, String chunknr){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID);
		if (c.load()){
			String toSend= Message.CHUNK(fileID,chunknr,c.getData());
			Message.sendMessage(controlSocket, Restore.getmCastGroupAddress(), Restore.getControlPort(), toSend);
			}
		
	}
	private void parsedDELETE(String fileID){
		int i = 0;
		while(true){
			File f = new File(fileID+"."+i);
			if(f.exists()){
				f.delete();
				String toSend = Message.REMOVED(fileID, Integer.toString(i));
				Message.sendMessage(controlSocket, mCastGroupAddress, controlPort, toSend);
			}
			i++;
		}		
	}
	
	public void parsedREMOVED(String fileID,String chunknr){
		//TODO parse REMOVED
	}
	
	
	protected void joinMCGroup() throws IOException{
		controlSocket.joinGroup(mCastGroupAddress);
	}

	//******************Getters
	
	public static InetAddress getmCastGroupAddress() {
		return mCastGroupAddress;
	}

	public static int getControlPort() {
		return controlPort;
	}
	
	//******************Setters 

	public void setControlPort(int controlPort) {
		Control.controlPort = controlPort;
	}

	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		Control.mCastGroupAddress = mCastGroupAddress;
	}



}
