package dataStruct;

import cli.MFSS;

public class Header{

	public static String getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo, char replicationDeg, byte[] body){
		String header = "";		
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo + " "+ replicationDeg +MFSS._CRLF+MFSS._CRLF;		
		String s = new String(body);
		header+= s;
		return header;
	}
	
	public static String getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo, byte[] body){
		String header = "";
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo;
		header+=MFSS._CRLF+MFSS._CRLF;
		String s = new String(body);
		header+= s;
		return header;
	}
	
	public static String getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo){
		String header = "";
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo;
		header+=MFSS._CRLF+MFSS._CRLF;
		return header;
	}
	
	public static String getHeader(String messageType, String fileId){
		String header = "";
		header=messageType+ " " + fileId;
		header+=MFSS._CRLF+MFSS._CRLF;
		return header;
	}
}