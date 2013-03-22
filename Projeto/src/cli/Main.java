package cli;
import java.net.InetAddress;

import threads.Control;
import threads.Restore;

public class Main {	
	
	private static final int _MDRPORT = 3002;
	private static final int _MCPORT = 3000;
	private static final int _MDBPORT = 3001;

	public static void main(String args[]){
		InetAddress mCastGroupAddress;
		try {
			mCastGroupAddress = InetAddress.getByName("224.0.0.1");	
			
			System.out.println("Starting M.F.S.S.");
			Control c = new Control(mCastGroupAddress, _MDRPORT, _MCPORT, _MDBPORT);
			System.out.println("Control object initialized with:");
			System.out.println("\t "+"Group: "+mCastGroupAddress.getHostAddress());
			System.out.println("\t "+"MDRPORT: "+_MDRPORT);
			System.out.println("\t "+"MCPORT: "+_MCPORT);
			System.out.println("\t "+"MDBPORT: "+_MDBPORT);
			
			Restore r = new Restore(mCastGroupAddress, _MDRPORT, _MCPORT, _MDBPORT);
			System.out.println("Restore object initialized with:");
			System.out.println("\t "+"Group: "+mCastGroupAddress.getHostAddress());
			System.out.println("\t "+"MDRPORT: "+_MDRPORT);
			System.out.println("\t "+"MCPORT: "+_MCPORT);
			System.out.println("\t "+"MDBPORT: "+_MDBPORT);

			System.out.println("bla");
			
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
