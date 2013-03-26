package cli;
import java.net.InetAddress;

import threads.Control;
import threads.Restore;

public class MFSS {	
	public static final int _RANDOMSLEEPTIME = 401;
	public static final int _TTL = 1;	
	public static final byte _CRLF = (byte) 0xDA;
	public static final char _VERSIONMAJOR = '1';
	public static final char _VERSIONMINOR = '0';
	private static final int _MDRPORT = 3002;
	private static final int _MCPORT = 3000;
	private static final int _MDBPORT = 3001;

	public static void main(String args[]){
		InetAddress controlGroupAddress;
		InetAddress restoreGroupAddress;
		
		try {
			controlGroupAddress = InetAddress.getByName("224.0.0.1");
			restoreGroupAddress = InetAddress.getByName("224.0.0.1");
			
			System.out.println("Starting M.F.S.S.");
			Control c = new Control(controlGroupAddress,_MCPORT);
			System.out.println("Control object initialized with:");
			System.out.println("\t "+"Control group address: "+controlGroupAddress.getHostAddress());
			System.out.println("\t "+"MCPORT: "+_MCPORT);
		
			
			Restore r = new Restore(restoreGroupAddress, _MDRPORT);
			System.out.println("Restore object initialized with:");
			System.out.println("\t "+"Restore group address: "+restoreGroupAddress.getHostAddress());
			System.out.println("\t "+"MDRPORT: "+_MDRPORT);
		
			
			Thread ct = new Thread(c);
			ct.start();
			System.out.println("Control thread started...");
			Thread rt = new Thread(r);
			rt.start();
			System.out.println("Restore thread started...");
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
}
