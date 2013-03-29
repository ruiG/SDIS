package dataStruct;

public class Header{

	private static final String _CRLF = "\0x0D\0x0A";

	public static String getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo, char replicationDeg, byte[] body){
		String header = "";		
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo + " "+ replicationDeg +_CRLF+_CRLF;		
		String s = new String(body);
		header+= s;
		return header;
	}
	
	public static String getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo, byte[] body){
		String header = "";
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo;
		header+=_CRLF+_CRLF;
		String s = new String(body);
		header+= s;
		return header;
	}
	
	public static String getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo){
		String header = "";
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo;
		header+=_CRLF+_CRLF;
		return header;
	}
	
	public static String getHeader(String messageType, String fileId){
		String header = "";
		header=messageType+ " " + fileId;
		header+=_CRLF+_CRLF;
		return header;
	}
}