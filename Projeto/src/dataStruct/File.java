package dataStruct;

import java.util.ArrayList;

public class File {
	private ArrayList<Chunk> Chunks;
	String name;
	String filename; //SHA256 generated

	public File(String name) {
		this.name = name;
		// generateFileName() deve ser executado aqui
	}

	private ArrayList<Chunk> generateChunks(){
		//TODO Generate chunks and return an array list
		return null;		
	}

	public void generateFileName(){
		//TODO  generate FileName
	}

	public String getFileName(){
		return filename;
	}


}
