package dataStruct;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Chunk {
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
	public void save(){
		try{		
			RandomAccessFile f= new RandomAccessFile(fileId+"."+chunkNo, "w");
			f.write(data,0,size);
			f.close();
		}catch(IOException e){
			return;
		}
	}
	
	public static void deleteAllChunksFromFile(String fileID){
		int i = 0;
		while(true){
			File f = new File(fileID+"."+i);
			if(!f.exists()){
				break;
			}else{
				f.delete();
			}		
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