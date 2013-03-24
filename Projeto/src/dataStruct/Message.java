package dataStruct;

import dataStruct.Header;

public class Message{

	public char VersionMajor='1';
	public char VersionMinor='0';
	
	public Message(){
	}
	public String PUTCHUNK(String fileId, String chunkID, 
			  	  char replicationDeg, String body){
		Header h= new Header("PUTCHUNK", VersionMajor,VersionMinor, fileId, chunkID, replicationDeg );
		return h.get() + body;
	}
	public String STORED(String fileId, String chunkID){
		Header h= new Header("STORED", VersionMajor,VersionMinor, fileId, chunkID);
		return h.get();
	}
	public String GETCHUNK(String fileId, String chunkID){
		Header h= new Header("GETCHUNK", VersionMajor,VersionMinor, fileId, chunkID);
		return h.get();
	}
	
	public String CHUNK(String fileId, String chunkID, String body){
		Header h= new Header("CHUNK", VersionMajor,VersionMinor, fileId, chunkID);
		return h.get() + body;
	}
	public String DELETE(String fileId){
		Header h= new Header("DELETE", fileId);
		return h.get();
	}
	public String REMOVED(String fileId, String chunkID){
		Header h= new Header("REMOVED", VersionMajor,VersionMinor, fileId, chunkID);
		return h.get();
	}

	
	
}