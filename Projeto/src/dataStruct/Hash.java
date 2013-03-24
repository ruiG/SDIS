package dataStruct;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash{	
	
	public static String calc(String filename, String modifiedDate, String size){
		MessageDigest md = null;
		String message=filename + modifiedDate + size;
		try{
			md=MessageDigest.getInstance("SHA-256");
			
		}catch(NoSuchAlgorithmException ex){
			System.out.println(ex.getMessage());
		}
		md.update(message.getBytes());
		byte[] shaDig= md.digest();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < shaDig.length; i++) {
			sb.append(Integer.toString((shaDig[i] & 0xff) + 0x100, 16).substring(1));
		} 

		return sb.toString();
	}
}
