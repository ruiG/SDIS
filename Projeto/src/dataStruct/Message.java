package dataStruct;

import dataStruct.Header;

public class Message{

	public static char versionMajor ='1';
	public static char versionMinor ='0';

	public static String PUTCHUNK(String fileId, String chunkNo, char replicationDeg, byte[] body){
		return Header.getHeader("PUTCHUNK", versionMajor, versionMinor, fileId, chunkNo, replicationDeg, body);
	}
	
	public static String STORED(String fileId, String chunkNo){
		return Header.getHeader("STORED", versionMajor, versionMinor, fileId, chunkNo);
	}
	
	public static String GETCHUNK(String fileId, String chunkNo){
		return Header.getHeader("GETCHUNK", versionMajor, versionMinor, fileId, chunkNo);
	}

	public static String CHUNK(String fileId, String chunkNo, byte[] body){
		return Header.getHeader("CHUNK", versionMajor, versionMinor, fileId, chunkNo, body);
	}
	
	public static String REMOVED(String fileId, String chunkNo){
		return Header.getHeader("REMOVED", versionMajor, versionMinor, fileId, chunkNo);
	}
	
	public static String DELETE(String fileId){
		return Header.getHeader("DELETE", fileId);
	}
	



}