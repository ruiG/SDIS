package dataStruct;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class BakupFile {
	String name;
	String filename; //SHA256 generated
	long length;
	long lastmodified;
	int repDeg;
	int nrchunks;
	ArrayList<Chunk> chunks;
	
	public BakupFile(String name, int repDeg) {
		this.name = name;
		this.repDeg = repDeg;
		File file=new File(name);
		lastmodified=file.lastModified();
		length=file.length();
		generateFileName();	
		chunks=new ArrayList<Chunk>();
		chunks.clear();
	}

	public ArrayList<Chunk> generateChunks(){
		
		ArrayList<Chunk> arr = new ArrayList<Chunk>();
		arr.clear();
		try{		
			RandomAccessFile f = new RandomAccessFile(name, "r");
			nrchunks=(int)(length/64000);
			long rest=length%64000;
			
			byte b[]= new byte[64000];
			for (int i=0;i<nrchunks;i++){
				f.read(b);
				arr.add(new Chunk(i, filename, b, repDeg));
			}
			if (rest>0){
				byte c[]= new byte[(int) rest];
				f.read(c);
				arr.add(new Chunk(nrchunks, filename, c, repDeg));
				f.close();
			}
			else{
				arr.add(new Chunk(nrchunks, filename, null, repDeg));
				f.close();
			}	
		}catch(IOException e){
				return null;
			}
	
		return arr;
	}
	
	public void generateFileName(){
		filename = Hash.calc(name,Long.toString(lastmodified), Long.toString(length));	
	}
	
	public String getFileName(){
		return filename;
	}
	
	public void RegenerateFileFromChunks(){
		if(chunks.size()!=nrchunks)
			return;
		try{		
			RandomAccessFile f= new RandomAccessFile(name, "w");
			byte b[]= new byte[64000];
			for (int i=0;i<nrchunks;i++){
				b=chunks.get(i).getData();
				f.write(b);
			}
			Chunk c= chunks.get(nrchunks);
			byte d[]=c.getData();
			if(d.length!=0){
				f.write(d);
			}
			f.close();
		}catch(IOException e){
				return;
			}
		
		
	}
	public void StartRestore(){
		chunks.clear();
	}
	public void AddChunkForRenegeration(Chunk c){
		chunks.add(c.getChunkNo(), c);
	}
	
}
