package dataStruct;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import cli.MFSS;

public class Header{

	public static byte[] getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo, char replicationDeg, byte[] body) throws UnsupportedEncodingException{
		String header = "";		
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo + " "+ replicationDeg +MFSS._CRLF+MFSS._CRLF;		
		byte[] byteHeader = header.getBytes(Charset.forName("US-ASCII"));

		byte[] combined = new byte[byteHeader.length + body.length];

		for (int i = 0; i < combined.length; ++i)	
		    combined[i] = i < byteHeader.length ? byteHeader[i] : body[i - byteHeader.length];
				
		return combined;		
	}

	public static byte[] getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo, byte[] body) throws UnsupportedEncodingException{
		String header = "";
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo;
		header+=MFSS._CRLF+MFSS._CRLF;
		byte[] byteHeader = header.getBytes(Charset.forName("US-ASCII"));

		byte[] combined = new byte[byteHeader.length + body.length];

		for (int i = 0; i < combined.length; ++i)		
		    combined[i] = i < byteHeader.length ? byteHeader[i] : body[i - byteHeader.length];
	
		return combined;		
	}

	public static byte[] getHeader(String messageType, char versionMajor, char versionMinor, String fileId, String chunkNo){
		String header = "";
		header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkNo;
		header+=MFSS._CRLF+MFSS._CRLF;
		byte[] byteHeader = header.getBytes(Charset.forName("US-ASCII"));
		return  byteHeader;
	}

	public static byte[] getHeader(String messageType, String fileId){
		String header = "";
		header=messageType+ " " + fileId;
		header+=MFSS._CRLF+MFSS._CRLF;
		byte[] byteHeader = header.getBytes(Charset.forName("US-ASCII"));
		return  byteHeader;
	}
}