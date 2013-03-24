package dataStruct;

public class Chunk {
	public int num;
	private byte[] data;
	private String fileID;
	
	public Chunk(int num, byte[] data, String fileID) {		
		this.num = num;
		this.data = data;
		this.fileID = fileID;
	}

	public byte[] getChunkNo(){
		//TODO devolver codificado em ASCII
		return null;
	};
	
	public byte[] getData(){
		return data;
	};
	
	public String getFileID(){
		return fileID;
	}
}
