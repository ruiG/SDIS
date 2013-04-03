package dataStruct;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import cli.MFSS;


public class Message{


	public static String PUTCHUNK(String fileId, String chunkNo, char replicationDeg, byte[] body){
		return Header.getHeader("PUTCHUNK", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo, replicationDeg, body);
	}
	
	public static String STORED(String fileId, String chunkNo){
		return Header.getHeader("STORED", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo);
	}
	
	public static String GETCHUNK(String fileId, String chunkNo){
		return Header.getHeader("GETCHUNK", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo);
	}

	public static String CHUNK(String fileId, String chunkNo, byte[] body){
		return Header.getHeader("CHUNK", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo, body);
	}
	
	public static String REMOVED(String fileId, String chunkNo){
		return Header.getHeader("REMOVED", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo);
	}
	
	public static String DELETE(String fileId){
		return Header.getHeader("DELETE", fileId);
	}
	
	public static boolean sendMessage(MulticastSocket skt, InetAddress GroupAddress, int port, String message) {
		DatagramPacket pack;		
		try {
			byte[] messageBytes = message.getBytes("US-ASCII");
			Random r = new Random();
			Thread.sleep(r.nextInt(MFSS._RANDOMSLEEPTIME));
			pack = new DatagramPacket(messageBytes, messageBytes.length,GroupAddress, port);
			skt.setTimeToLive(MFSS._TTL);
			skt.send(pack);		
			
			return true;
		} catch (InterruptedException | IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	public static String[] parseTokensFromString(String data){			
		return data.split("(\\r?\\n)|\\s", 10);	
	}
}