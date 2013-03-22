package dataStruct;

public class Chunk {
	public int num;
	private byte[] data;
	
	public Chunk(int num, byte[] data) {		
		this.num = num;
		this.data = data;
	}

	public byte[] getChunkNo(){
		//TODO devolver codificado em ASCII
		return null;
	};
	
	public byte[] getData(){
		return data;
	};
}
