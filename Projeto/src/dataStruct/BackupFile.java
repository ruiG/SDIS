package dataStruct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import cli.MFSS;

public class BackupFile {

	
	private String fileName;
	private String fileID; //SHA256 generated
	private long length;
	private long lastModified;
	private int repDeg;
	private int nrChunks;
	

	private ArrayList<Chunk> chunks;
	
	public BackupFile(String name, int repDeg) throws NoSuchFileException {
		this.fileName = name;
		this.repDeg = repDeg;
		File file=new File(name);
		if(!file.exists())
			throw new NoSuchFileException();
		lastModified=file.lastModified();
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
			nrChunks=(int)(length/64000);
			long rest=length%64000;
			
			for (int i=0;i<nrChunks;i++){
				byte b[]= new byte[64000];
				f.read(b);
				arr.add(new Chunk(i, fileID, b, repDeg));
			}
			if (rest>0){
				byte c[]= new byte[(int) rest];
				f.read(c);
				arr.add(new Chunk(nrChunks, fileID, c, repDeg));
				f.close();
			}
			else{
				arr.add(new Chunk(nrChunks, fileID, null, repDeg));
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
			Chunk c = new Chunk(i, fileID);
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
		MFSS.sentChunk = null;
		MFSS.sentID = null;
		return true;				
	}
	
	public void generateFileName(){
		fileID = Hash.calc(getName(),Long.toString(lastModified), Long.toString(length));	
	}
	
	public void RegenerateFileFromChunks(){
		if(chunks.size()!=nrChunks+1)
			return;
		try{		
			File f = new File("a"+getName());
			FileOutputStream outputStream = new FileOutputStream(f);
			byte b[]= new byte[64000];
			for (int i=0;i<nrChunks;i++){
				b=chunks.get(i).getData();
				outputStream.write(b);
			}
			Chunk c= chunks.get(nrChunks);
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
		return fileName;
	}

	public String getID() {
		return fileID;
	}

	public int getRepDeg(){
		return repDeg;
	}
	
	public int getNrChunks() {
		return nrChunks;
	}
	
	@SuppressWarnings("serial")
	public final class NoSuchFileException extends Exception{}

}
