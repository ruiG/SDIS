package dataStruct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class Chunk implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileId;
	private int repDeg;
	private int size;
	private int chunkNo;
	private byte[] data;

	public Chunk(int chunkNo, String fileId, byte[] data, int repDeg) {		
		this.repDeg = repDeg;
		this.chunkNo = chunkNo;
		this.data = data;
		this.size = data.length;
		this.fileId=fileId;
	}
	public Chunk(int chunkNo, String fileId){
		this.chunkNo = chunkNo;
		this.data = null;
		this.repDeg = 0;
		this.size = 0;
		this.fileId = fileId;
	}

	public String getChunkNoAsString(){
		return Integer.toString(chunkNo);
	};


	public char getRepDegAsChar(){
		String r = Integer.toString(repDeg);
		char[] dst = new char[1];
		r.getChars(0, 1, dst, 0);
		return dst[0];		
	};

	public int getChunkRepDeg(){
		return repDeg;
	};

	public int getChunkNo(){
		return chunkNo;
	};

	public String getFileId(){
		return fileId;
	}

	public byte[] getData(){
		return data;
	};

	public int getSize(){
		return size;
	}
	
	public boolean sendChunk(MulticastSocket skt, InetAddress groupAddress, int port){		
		String message = Message.PUTCHUNK(fileId, getChunkNoAsString(), getRepDegAsChar(), data);
		Message.sendMessage(skt, groupAddress, port, message.getBytes());
		return true;		
	}
	
	public void save(){
		try{
			System.out.println("saving chunk number: "+chunkNo);
			File f = new File(fileId+"."+chunkNo);
			FileOutputStream outputStream = new FileOutputStream(f);
			outputStream.write(data);
			outputStream.close();		
		}catch(IOException e){
			System.err.println("error saving file...");
			return;
		}
	}
	
	public static void deleteAllChunksFromFile(String fileID){
		int i = 0;
		while(true){
			File f = new File(fileID+"."+i);
			if(f.exists()){
				f.delete();
			}
			i++;
		}				
	}
	
	public boolean load(){
		try{		
			byte [] b= new byte[1024];
			ByteBuffer body= ByteBuffer.allocate(64000);
			RandomAccessFile f = new RandomAccessFile(fileId+"."+chunkNo, "r");

			while(f.read(b)== 1024){
				body.put(b);
				f.read(b);
			}
			body.put(b);
			size=body.position();
			body.flip();
			body.get(data,0,size);
			f.close();
			return true;
		}catch(IOException e){
			return false;
		}
	}


}