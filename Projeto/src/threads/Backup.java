package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import cli.MFSS;

import dataStruct.Chunk;
import dataStruct.Message;

public class Backup implements Runnable{

	private static int backupPort;
	private static InetAddress backupGroupAddress;
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
	
	
	
	
	/*
	private void parseGETCHUNK(String fileID, String chunknr){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID);
		if (! c.load()){
			// N�o temos este chunk localmente.
			return;
		}
		Message m=new Message();
		String toSend=m.CHUNK(fileID,chunknr,c.getData().toString());
		// e enviar mensagem CHUNK
	}

	private void parseDELETE(String fileID){
		// Procurar na directoria todos os ficheiros come�ados por "fileID" e apag�-los.		

	}*/

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
