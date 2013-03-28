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

public class Backup implements Runnable{

	private static int backupPort;
	private static InetAddress backupGroupAddress;
	private MulticastSocket backupSocket;

	public Backup(InetAddress mCastGroupAddress, Integer backupPort) throws IOException{
		Backup.backupGroupAddress = mCastGroupAddress;
		Backup.backupPort = backupPort;
		backupSocket = new MulticastSocket(backupPort);
		this.joinMCGroup();

	}

	protected void joinMCGroup() throws IOException{
		backupSocket.joinGroup(backupGroupAddress);
	}

	@Override
	public void run() {
		//TODO check for version
		Boolean receivingbody=false;
		ByteBuffer body= ByteBuffer.allocate(64000);

		String command="", version="", fileID="", chunknr="", repldeg="";

		while(true){
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				backupSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData(),0,receivePacket.getLength());
			System.out.println("RECEIVED: " + sentence);
			if(receivingbody){
				body.put(sentence.getBytes());
				if(sentence.length()!=1024){
					// Chegamos ao fim do chunk
					receivingbody=false;
					int size=body.position();
					body.flip();
					byte [] b= new byte[size];
					body.get(b,0, size);
					// COMANDO=PUTCHUNK
					parsePUTCHUNK(fileID,chunknr,b,Integer.getInteger(repldeg));				
				}
				continue;
			}
			command=Message.parseCommandFromString(sentence, 1);
			if(command.equals("PUTCHUNK")){
				version=Message.parseCommandFromString(sentence,2);
				fileID=Message.parseCommandFromString(sentence, 3);
				chunknr=Message.parseCommandFromString(sentence, 4);
				repldeg=Message.parseCommandFromString(sentence, 5);
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
					parsePUTCHUNK(fileID,chunknr,b,Integer.getInteger(repldeg));
				}
				continue;
			}
		}	
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
		if(true){//TODO Verificar se ha espaco no disco para gravar chunk. 
			c.save();
			String toSend = Message.STORED(fileID, chunknr);
			Message.sendMessage(backupSocket, Control.getmCastGroupAddress(), Control.getControlPort(), toSend.getBytes());
		}
	}

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
