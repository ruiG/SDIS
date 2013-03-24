package dataStruct;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class Hash{
	private String hash_result;
	
	private void calc(String message){
		MessageDigest md;
		try{
			md=MessageDigest.getInstance("SHA-256");
			
		}catch(NoSuchAlgorithmException ex){
			System.out.println(ex.getMessage());
			return;
		}
		md.update(message.getBytes());
		byte[] shaDig= md.digest();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < shaDig.length; i++) {
			sb.append(Integer.toString((shaDig[i] & 0xff) + 0x100, 16).substring(1));
		} 

		hash_result = sb.toString();
	}
	
	public Hash(String filename, String modifiedDate, String size){
		String message=filename + modifiedDate + size;
		calc(message);
	}
	
	public String getValue(){
		return hash_result;
	}
}
