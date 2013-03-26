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

	private  int backupPort;
	private InetAddress backupGroupAddress;
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

	public void send(Chunk ck, int repdegree) {
		byte[] sdata = PutCkMessage(ck, repdegree);
		System.out.println("MDB Send:");
		DatagramPacket pack;
		try {
			pack = new DatagramPacket(sdata, sdata.length,backupGroupAddress, backupPort);
			backupSocket.setTimeToLive(MFSS._TTL);
			backupSocket.send(pack);
			Random r = new Random();
			Thread.sleep(r.nextInt(MFSS._RANDOMSLEEPTIME));
		} catch (InterruptedException | IOException e1) {
			e1.printStackTrace();
		}
	}

	public byte[] PutCkMessage(Chunk ck, int repdegree){	
		byte[] message = Message.PUTCHUNK(ck.getFileId(), ck.getChunkNoAsString(), ck.getRepDegAsChar(), ck.getData()).getBytes();
		return message;	
	}


	public byte[] StrMessage(Chunk ck, int repdegree){	
		byte[] message = Message.STORED(ck.getFileId(), ck.getChunkNoAsString()).getBytes();
		return message;	
	}



	/*
	private void parsePUTCHUNK(String fileID, String chunknr, byte body[]){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID,body);
		if(true){// Verificar se há espaço no disco para gravar chunk. 
			c.save();
			Message m= new Message();
			String toSend=m.STORED(fileID, chunknr);
			// e enviar mensagem STORED
		}
	}

	private void parseGETCHUNK(String fileID, String chunknr){
		Chunk c=new Chunk(Integer.parseInt(chunknr), fileID);
		if (! c.load()){
			// Não temos este chunk localmente.
			return;
		}
		Message m=new Message();
		String toSend=m.CHUNK(fileID,chunknr,c.getData().toString());
		// e enviar mensagem CHUNK
	}

	private void parseDELETE(String fileID){
		// Procurar na directoria todos os ficheiros começados por "fileID" e apagá-los.		

	}*/

	//******************Getters

	public InetAddress getmCastGroupAddress() {
		return backupGroupAddress;
	}


	//******************Setters 

	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		this.backupGroupAddress = mCastGroupAddress;
	}
}
