package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Random;

import dataStruct.*;

public class Control implements Runnable{
	private static final int _TTL = 1;
	private  int restorePort;
	private  int controlPort;
	private  int backupPort;
	private InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;
	
	public Control(InetAddress mCastGroupAddress, Integer restorePort, Integer controlPort, Integer backupPort) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		this.restorePort = restorePort;
		this.controlPort = controlPort;
		this.backupPort = backupPort;	
		controlSocket = new MulticastSocket(this.controlPort);
		this.joinMCGroup();
	}	
	
	public void stop(){
		
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
		
	
	}
	@Override
	public void run() {
		Boolean receivingbody=false;
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
					// Chegámos ao fim do chunk
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
			
		}
	}
	

	protected void joinMCGroup() throws IOException{
		controlSocket.joinGroup(mCastGroupAddress);
	}

	//******************Getters
	
	public InetAddress getmCastGroupAddress() {
		return mCastGroupAddress;
	}

	
	//******************Setters 
	
	public void setmCastGroupAddress(InetAddress mCastGroupAddress) {
		this.mCastGroupAddress = mCastGroupAddress;
	}

	
}
