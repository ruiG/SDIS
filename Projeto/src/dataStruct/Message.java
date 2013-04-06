package dataStruct;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import cli.MFSS;


public class Message{


	public static byte[] PUTCHUNK(String fileId, String chunkNo, char replicationDeg, byte[] body){
		try {
			return Header.getHeader("PUTCHUNK", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo, replicationDeg, body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] STORED(String fileId, String chunkNo){
		return Header.getHeader("STORED", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo);
	}
	
	public static byte[] GETCHUNK(String fileId, String chunkNo){
		return Header.getHeader("GETCHUNK", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo);
	}

	public static byte[] CHUNK(String fileId, String chunkNo, byte[] body){
		try {
			return Header.getHeader("CHUNK", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo, body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static byte[] REMOVED(String fileId, String chunkNo){
		return Header.getHeader("REMOVED", MFSS._VERSIONMAJOR, MFSS._VERSIONMINOR, fileId, chunkNo);
	}
	
	public static byte[] DELETE(String fileId){
		return Header.getHeader("DELETE", fileId);
	}
	
	public static boolean sendMessage(MulticastSocket skt, InetAddress GroupAddress, int port, byte[] message) {
		DatagramPacket pack;		
		try {
			Random r = new Random();
			try {
				Thread.sleep(r.nextInt(MFSS._RANDOMSLEEPTIME));
			} catch (InterruptedException e) {}
			pack = new DatagramPacket(message, message.length,GroupAddress, port);
			skt.setTimeToLive(MFSS._TTL);
			skt.send(pack);			
			return true;
		} catch (IOException e1) {
			return false;
		}
	}
	
	public static void readbytes(byte[] chunkReceive, byte[] data,int size) {
		for (int i = 0; i < chunkReceive.length; i++) {
			chunkReceive[i] = data[size+i];
		}		
	}
	
	public static String[] parseTokensFromString(String data){			
		return data.split("(\\r?\\n)|\\s", 10);	
	}
}