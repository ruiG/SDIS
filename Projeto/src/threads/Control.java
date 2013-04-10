package threads;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import cli.MFSS;
import dataStruct.Chunk;
import dataStruct.Message;

public class Control extends Thread{
	static int controlPort;
	static InetAddress mCastGroupAddress;
	MulticastSocket controlSocket;
	volatile boolean finished = false; 

	public Control(InetAddress mCastGroupAddress,Integer controlPort) throws IOException{
		Control.mCastGroupAddress = mCastGroupAddress;
		Control.controlPort = controlPort;	
		controlSocket = new MulticastSocket(Control.controlPort);
		controlSocket.setLoopbackMode(true);
		this.joinMCGroup();
	}	

	public void stopMe(){
		finished = true;
	}


	@Override
	public void run() {
		while(!finished){
			String version="", fileID="", chunknr="";
			byte[] receiveData = new byte[64000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			try {
				controlSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());

			if(MFSS.debugmode && sentence.length() > 20)
				System.out.println("RECEIVED IP: "+ receivePacket.getAddress()+ " " + sentence.substring(0, 20));

			String[] st = sentence.split("\r\n\r\n");
			String head = st[0];
			String[] tokens = Message.parseTokensFromString(head);			
			if(tokens[0].equals("GETCHUNK")){
				version=tokens[1];
				if(version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)){
					fileID=tokens[2];
					chunknr=tokens[3];
					parsedGETCHUNK(fileID, chunknr);					
				}
				continue;
			}
			else if(tokens[0].equals("DELETE")){
				fileID=tokens[1];
				parsedDELETE(fileID);
				continue;
			}

			else if(tokens[0].equals("REMOVED")){
				version=tokens[1];				
				if(version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)){
					fileID=tokens[2];
					chunknr=tokens[3];
					parsedREMOVED(fileID, chunknr);
				}
				continue;
			}

			else if(MFSS.sentID != null){
				if(tokens[0].equals("STORED")){
					version=tokens[1];
					if(version.equals(MFSS._VERSIONMAJOR+"."+MFSS._VERSIONMINOR)){
						fileID=tokens[2];
						chunknr=tokens[3];
						parsedSTORED(fileID, chunknr);
					}
					continue;
				}
			}
			else {
				System.out.println("incorrect token received from: "+receivePacket.getAddress());
			}

		}
		controlSocket.close();
	}

	private void parsedSTORED(String fileID, String chunknr) {
		if(chunknr.equals(MFSS.sentChunk) && fileID.equals(MFSS.sentID))
			MFSS.t.interrupt();
	}

	private void parsedGETCHUNK(String fileID, String chunknr){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID);
		if (c.load()){
			byte[] toSend= Message.CHUNK(fileID,chunknr,c.getData());
			Message.sendMessage(controlSocket, Restore.getmCastGroupAddress(), Restore.getControlPort(), toSend);
		}		
	}
	private void parsedDELETE(String fileID){
		int i = 0;
		while(true){
			File f = new File(fileID+"."+i);
			if(f.exists()){
				f.delete();
			}else{
				break;
			}
			i++;
		}		
	}

	public void parsedREMOVED(String fileID,String chunknr){
		//TODO parsedREMOVED
	}


	protected void joinMCGroup() throws IOException{
		controlSocket.joinGroup(mCastGroupAddress);
	}
	
	public void closeSocket() throws SocketException{
		controlSocket.close();
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
