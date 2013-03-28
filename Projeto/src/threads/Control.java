package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Random;

import cli.MFSS;

import dataStruct.Chunk;
import dataStruct.Message;

public class Control implements Runnable{
	private static int controlPort;
	private static InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;
	
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
	

	@Override
	public void run() {
		//TODO Check for version
		String command="", version="", fileID="", chunknr="", repldeg="";

		while(true){
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				controlSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());
			System.out.println("RECEIVED: " + sentence);
			
			command=Message.parseCommandFromString(sentence, 1);
			
			if(command.equals("GETCHUNK")){
				version=Message.parseCommandFromString(sentence,2);
				fileID=Message.parseCommandFromString(sentence, 3);
				chunknr=Message.parseCommandFromString(sentence, 4);
				parsedGETCHUNK(fileID, chunknr);
				continue;
			}
			if(command.equals("DELETE")){
				fileID=Message.parseCommandFromString(sentence, 2);
				parsedDELETE(fileID);
				continue;
			}
			
		}
	}
	
	private void parsedGETCHUNK(String fileID, String chunknr){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID);
		if (c.load()){
			String toSend=Message.CHUNK(fileID,chunknr,c.getData());
			Message.sendMessage(controlSocket, Restore.getmCastGroupAddress(), Restore.getControlPort(), toSend.getBytes());
			}
		
	}
	private void parsedDELETE(String fileID){
		//Chunk c = new Chunk()
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
