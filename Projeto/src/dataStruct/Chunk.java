package dataStruct;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Chunk {
	public String fileId;
	public int num;
	public int size;
	private byte[] data;
	
	public Chunk(int num, String fileId, byte[] data) {		
		this.num = num;
		this.data = data;
		this.size = data.length;
		this.fileId=fileId;
	}
	public Chunk(int num, String fileId){
		this.num=num;
		this.data=null;
		this.size=0;
		this.fileId=fileId;
	}
	
	public String getChunkNo(){
		return Integer.toString(num);
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
	public void save(){
		try{		
			RandomAccessFile f= new RandomAccessFile(fileId + num, "w");
				f.write(data,0,size);
				f.close();
		}catch(IOException e){
				return;
		}
	}
		
	public boolean load(){
		try{		
			byte [] b= new byte[1024];
			ByteBuffer body= ByteBuffer.allocate(64000);
			RandomAccessFile f= new RandomAccessFile(fileId + num, "r");
			
			while(f.read(b)== 1024){
				body.put(b);
				f.read(b);
			}
			body.put(b);
			size=body.position();
			body.flip();
			body.get(data,0,size);
			return true;
		}catch(IOException e){
				return false;
		}
	}
	

}