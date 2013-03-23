// Comentario

package cli;
import java.net.InetAddress;

import threads.Control;
import threads.Restore;
import dataStruct.*;

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
			
			Message m=new Message();
			Hash h=new Hash("Filename", "12.01.2013", "43768374");
			System.out.println("PUTCHUNK:\n" + m.PUTCHUNK(h.getValue(), "0", '4', "fksljflksjdlk")+"\n------");
			/*System.out.println("STORED:\n" + m.STORED("FILEID", "1")+"\n------");
			System.out.println("GETCHUNK:\n" + m.GETCHUNK("FILEID", "1")+"\n------");
			System.out.println("CHUNK:\n" + m.CHUNK("FILEID", "1", "body12312321")+"\n------");
			System.out.println("DELETE:\n" + m.DELETE("FILEID")+"\n------");
			System.out.println("REMOVED:\n" + m.REMOVED("FILEID", "1")+"\n------");*/
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
}
