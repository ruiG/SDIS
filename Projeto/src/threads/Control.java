package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
	
	public String parseCommandFromString(String data, int indice){
		int i=0;
		int posinit=0;
		int posfinal=0;
		while(i<indice){
			posinit=posfinal;
			posfinal=data.indexOf(' ', posinit+1);
		}
		if (data.charAt(posinit)==' '){
			posinit++;
		}
		return data.substring(posinit,posfinal);
	}
	
	public static byte[] CkMessage(Chunk ck, int repdegree){	
		byte[] message = Message.CHUNK(ck.getFileId(), ck.getChunkNoAsString(), ck.getData()).getBytes();
		return message;	
	}
	

	@Override
	public void run() {
	/*	Boolean receivingbody=false;
		ByteBuffer body= ByteBuffer.allocate(64000);
	
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
			if( receivingbody){
				body.put(sentence.getBytes());
				
				if(sentence.length()!=1024){
					// Cheg�mos ao fim do chunk
					receivingbody=false;
					int size=body.position();
					body.flip();
					byte [] b= new byte[size];
					body.get(b,0, size);
					// COMANDO=PUTCHUNK
					parsePUTCHUNK(fileID, chunknr, b);				
				}
				continue;
			}
			command=parseCommandFromString(sentence, 1);
			if(command.equals("PUTCHUNK")){
				version=parseCommandFromString(sentence,2);
				fileID=parseCommandFromString(sentence, 3);
				chunknr=parseCommandFromString(sentence, 4);
				repldeg=parseCommandFromString(sentence, 5);
				int pos=sentence.indexOf(0x0A);
				pos=sentence.indexOf(0x0A,pos+1)+1;
				body.clear();
				body.put(sentence.substring(pos, sentence.length()).getBytes());
				if(sentence.length()==1024){
					receivingbody=true;
				}
				else{
					int size=body.position();
					body.flip();
					byte []b=new byte[size];
					body.get(b,0,size);
					parsePUTCHUNK(fileID, chunknr, b);
				}
				continue;
			}
			if(command.equals("GETCHUNK")){
				version=parseCommandFromString(sentence,2);
				fileID=parseCommandFromString(sentence, 3);
				chunknr=parseCommandFromString(sentence, 4);
				parseGETCHUNK(fileID, chunknr);
				continue;
			}
			if(command.equals("DELETE")){
				fileID=parseCommandFromString(sentence, 2);
				parseDELETE(fileID);
				continue;
			}
			
		}*/
	}
	
	private void parsedGETCHUNK(String fileID, String chunknr){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID);
		if (c.load()){
			String toSend=Message.CHUNK(fileID,chunknr,c.getData());
			Message.sendMessage(controlSocket, Restore.getmCastGroupAddress(), Restore.getControlPort(), toSend.getBytes());
			}
		
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
