package dataStruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class LocalFile {
	private String name;
	long size;
	long n_chunks;
	private String fileID;
	Vector<Chunk> chunks;
	
	public LocalFile(final long s, final String n, String fileID){
		this.size = s;
		this.setName(n);
		chunks = new Vector<Chunk>();
		n_chunks =  (this.size-1)/64000 + 1;
		this.fileID = fileID;
		splitFile(fileID);
	}
	
	public void splitFile(String fileID){
		FileInputStream file = null;
		try {
			file = new FileInputStream(getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int bytesRead = 0;
		int chunkCount = 0; 
		int repDeg = 0; 
		
	//	byte[] sha = new byte[32];		
	//	sha = getSHABytes();
		
		while (bytesRead != size) {
			byte[] data =  new byte[64000]; 
			long bytes = 0;
			try {
				bytes = file.read(data);
			} catch (IOException e) {
				e.printStackTrace();
			}  
			bytesRead += bytes;  
			

			/* add chunk to vector */
			chunks.add(new Chunk(chunkCount, fileID, data,repDeg));
			chunkCount++;
		}
		
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	

}
