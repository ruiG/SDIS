package cli;
import java.net.InetAddress;
import java.net.MulticastSocket;

import threads.Backup;
import threads.Control;
import threads.Restore;
import dataStruct.BackupFile;

public class MFSS {	
	public static final int _RANDOMSLEEPTIME = 401;
	public static final int _TTL = 1;	
	public static final String _CRLF = "\r\n";
	public static final char _VERSIONMAJOR = '1';
	public static final char _VERSIONMINOR = '0';
	private static final int _MDRPORT = 3002;
	private static final int _MCPORT = 3000;
	private static final int _MDBPORT = 3001;
	public static boolean debugmode;

	public static void main(String args[]){
		InetAddress controlGroupAddress;
		InetAddress restoreGroupAddress;
		InetAddress backupGroupAddress;
		debugmode = true;
		
		try {
			controlGroupAddress = InetAddress.getByName("224.0.0.1");
			restoreGroupAddress = InetAddress.getByName("224.0.0.2");
			backupGroupAddress = InetAddress.getByName("224.0.0.3");
			
			System.out.println("Starting M.F.S.S.");
			Control c = new Control(controlGroupAddress,_MCPORT);
			System.out.println("Control object initialized with:");
			System.out.println("\t "+"Control group address: "+controlGroupAddress.getHostAddress());
			System.out.println("\t "+"MCPORT: "+_MCPORT);
		
			
			Restore r = new Restore(restoreGroupAddress, _MDRPORT);
			System.out.println("Restore object initialized with:");
			System.out.println("\t "+"Restore group address: "+restoreGroupAddress.getHostAddress());
			System.out.println("\t "+"MDRPORT: "+_MDRPORT);
			
			Backup b = new Backup(backupGroupAddress, _MDBPORT);
			System.out.println("Backup object initialized with:");
			System.out.println("\t "+"Backup group address: "+backupGroupAddress.getHostAddress());
			System.out.println("\t "+"MDRPORT: "+_MDRPORT);
		
			
			
			c.start();
			System.out.println("Control thread started...");
			b.start();
			System.out.println("Backup thread started...");
			r.start();
			System.out.println("Restore thread started...");
			
			MulticastSocket sk = new MulticastSocket();
			sk.joinGroup(InetAddress.getByName("224.0.0.3"));
			BackupFile file = new BackupFile("image.jpg", 2);
			if (file.generateChunks()) {
				/*file.saveChunks();				
				file.StartRestore();
				file.loadChunks();
				file.RegenerateFileFromChunks();
				*/
				file.sendChunks(sk, backupGroupAddress, _MDBPORT);
				System.out.println("File chunks sent...");
			}else{
				System.err.println("Error creating chunks...");
			}
			
			file.RegenerateFileFromChunks();
			sk.close();
			System.out.println("Ending...");
			
			c.stopMe();
			b.stopMe();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
}
