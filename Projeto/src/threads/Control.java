package threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Control implements Runnable{
	private  int controlPort;
	private InetAddress mCastGroupAddress;
	private MulticastSocket controlSocket;
	
	public Control(InetAddress mCastGroupAddress,Integer controlPort) throws IOException{
		this.mCastGroupAddress = mCastGroupAddress;
		this.controlPort = controlPort;	
		controlSocket = new MulticastSocket(this.controlPort);
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
			
		}*/
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
