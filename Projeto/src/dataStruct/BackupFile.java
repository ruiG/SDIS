package dataStruct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import cli.MFSS;

public class BackupFile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String filename; //SHA256 generated
	private long length;
	private long lastmodified;
	private int repDeg;
	private int nrchunks;
	private ArrayList<Chunk> chunks;
	
	public BackupFile(String name, int repDeg) {
		this.setName(name);
		this.repDeg = repDeg;
		File file=new File(name);
		lastmodified=file.lastModified();
		length=file.length();
		generateFileName();	
		chunks=new ArrayList<Chunk>();
		chunks.clear();
	}

	public boolean generateChunks(){
		
		ArrayList<Chunk> arr = new ArrayList<Chunk>();
		arr.clear();
		try{		
			RandomAccessFile f = new RandomAccessFile(getName(), "r");
			nrchunks=(int)(length/64000);
			long rest=length%64000;
			
			for (int i=0;i<nrchunks;i++){
				byte b[]= new byte[64000];
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
				e.printStackTrace();
				return false;
			}
		chunks = arr;
		return true;
	}
	
	public void saveChunks(){
		for (int i = 0; i < chunks.size(); i++) {
			chunks.get(i).save();
		}
	}
	
	public void loadChunks(){
		int i = 0;
		while(true){			
			Chunk c = new Chunk(i, filename);
			if(c.load()){
				System.err.println(i);
				chunks.add(c);
			}
			else{
				break;
			}
			i++;
		}
	}
	
	public boolean sendChunks(MulticastSocket skt, InetAddress grpAddress, int port){
		for (int i = 0; i < chunks.size(); i++) {
			int timeout = 400, to = 0;
			MFSS.sentChunk = chunks.get(i).getChunkNoAsString();
			MFSS.sentID = chunks.get(i).getFileId();
			while(to < 5){
				try{
					if(!chunks.get(i).sendChunk(skt, grpAddress, port))
						return false;
					Thread.sleep(timeout);
				}catch(InterruptedException ie){
					System.out.println("Stored received!");
					break;
				}
				timeout*=2;
				to++;				
			}
			if(to == 5) return false;		
		}	
		return true;				
	}
	
	public void generateFileName(){
		filename = Hash.calc(getName(),Long.toString(lastmodified), Long.toString(length));	
	}
	
	public String getFileName(){
		return filename;
	}
	
	
	public void RegenerateFileFromChunks(){
		if(chunks.size()!=nrchunks+1)
			return;
		try{		
			File f = new File("a"+getName());
			FileOutputStream outputStream = new FileOutputStream(f);
			byte b[]= new byte[64000];
			for (int i=0;i<nrchunks;i++){
				b=chunks.get(i).getData();
				outputStream.write(b);
			}
			Chunk c= chunks.get(nrchunks);
			byte d[]=c.getData();
			if(d.length!=0){
				outputStream.write(d);
			}
			outputStream.close();
		}catch(IOException e){
			System.err.println("Error regenerating file...");
		return;
		}		
	}
	public void StartRestore(){
		chunks.clear();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReplicationDegree(int rep_degree) {
		this.repDeg = rep_degree;
	}

}
