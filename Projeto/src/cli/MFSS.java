package cli;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;


import threads.Backup;
import threads.Control;
import threads.Restore;
import dataStruct.BackupFile;
import dataStruct.SearchDir;


public class MFSS {	

	//IP_CONTROL PORT_CONTROL IP_BACKUP PORT_BACKUP IP_RESTORE PORT_RESTORE

	public static final int _RANDOMSLEEPTIME = 401;
	public static final int _TTL = 1;	
	public static final String _CRLF = "\r\n";
	public static final char _VERSIONMAJOR = '1';
	public static final char _VERSIONMINOR = '0';
	private static int _MDRPORT = 3002; // Recover
	private static int _MCPORT = 3000; // Control
	private static int _MDBPORT = 3001; // Backup
	private InetAddress controlGroupAddress = null;
	private InetAddress restoreGroupAddress = null;
	private InetAddress backupGroupAddress = null;
	private boolean startThreads = true;
	public static boolean debugmode;
	private static int maximum_disk_space = 320000; // = 5 chunks (64000 Bytes cada chunk)
	static SearchDir search;
	static HashMap<String, BackupFile> local_files;
	private int chunkSize = 64000;
	private Backup backup;
	private Control control; 
	String ip_control="224.0.0.1";
	String ip_restore="224.0.0.2";
	String ip_backup="224.0.0.3";

	public MFSS(){
		debugmode = true;

		System.out.print("Welcome  \n 1 - Change Amount of Disk Space \n 2 - Change IP/Ports \n 3 - Start Program \n\nOption: ");

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String ans = null;
		try {
			ans = inFromUser.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(!ans.equals("3")){
			while(!ans.equals("1") && !ans.equals("2") && !ans.equals("3") && !ans.equals("0")){
				System.out.print("  Error! \n Please enter:\n 1 - Change Amount of Disk Space \n 2 - Change IP/Ports \n 3 - Start Program \n\nOption: " );
				try {
					ans = inFromUser.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(ans.equals("0")){
				System.out.print("\nWelcome \n 1 - Change Amount of Disk Space \n 2 - Change IP/Ports \n 3 - Start Program \n\nOption: " );
				try {
					ans = inFromUser.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(ans.equals("2")){
				String change = "";
				System.out.println("\nEnter the IP and Ports: " +
						"\n'IP_CONTROL PORT_CONTROL IP_BACKUP PORT_BACKUP IP_RESTORE PORT_RESTORE '\n");
				try {
					change = inFromUser.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				String[] change_strings = change.split("\\s");

				while(change_strings.length != 6 || !isParsableToInt(change_strings[1]) || !isParsableToInt(change_strings[3]) && !isParsableToInt(change_strings[5])){
					System.out.println("\nEnter the IP and Ports: " +
							"\n'IP_CONTROL PORT_CONTROL IP_BACKUP PORT_BACKUP IP_RESTORE PORT_RESTORE'\n");
					try {
						change = inFromUser.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					change_strings = change.split("\\s");
				}
				ip_control=change_strings[0];
				_MCPORT =Integer.parseInt(change_strings[1]);
				ip_backup=change_strings[2];
				_MDBPORT =Integer.parseInt(change_strings[3]);
				ip_restore=change_strings[4];
				_MDRPORT =Integer.parseInt(change_strings[5]);



			}

			else if(ans.equals("1")){
				String change = "";
				System.out.println("\nEnter the disk space in Bytes: ");
				try {
					change = inFromUser.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				while(!isParsableToInt(change)){
					System.out.println("\nEnter the disk space in Bytes: ");
					try {
						change = inFromUser.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				maximum_disk_space = Integer.parseInt(change);
			}
			
			if(!ans.equals("3"))
				ans = "0";
		}

		local_files = new HashMap<String, BackupFile>();

		// ler dir local
		search = new SearchDir(); // Preenche area local


		programStart();
	}


	private void programStart() {
		while(true){

			fillLocalArea(); // Preenche estrutura com os ficheiros locais
			// visualiza os files da area local
			@SuppressWarnings("rawtypes")
			Iterator it = (Iterator) local_files.entrySet().iterator();
			Vector<String> fileIDs= new Vector<String>(local_files.size()); // Vector tempor�rio que guarda os FileID
			System.out.println("\n\nChoose a file to backup <INDEX REPLICATION_DEGREE-[1-9]> : ");
			int i = 0;
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pairs = (Entry) it.next();
				fileIDs.insertElementAt(pairs.getKey().toString(), i);
				System.out.println(" " + i + " - " + ((BackupFile)pairs.getValue()).getName());
				//	it.remove(); // avoids a ConcurrentModificationException
				i++;
			}

			System.out.print("\nOption: ");
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			String ans = null;
			try {
				ans = inFromUser.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Divide a string em Index e Replication Degree
			String[] token = ans.split(" ");
			
			while(token.length != 2 || !isParsableToInt(token[0]) || !isParsableToInt(token[1])
					|| Integer.parseInt(token[0]) < 0 || Integer.parseInt(token[0]) > i-1
					|| (Integer.parseInt(token[1]) < 1 ||  Integer.parseInt(token[1]) > 9)){
				System.out.print("\nOops!\nPlease try again!\nOption <INDEX REPLICATION_DEGREE-[1-9]> : ");
				inFromUser = new BufferedReader(new InputStreamReader(System.in));
				ans = null;
				try {
					ans = inFromUser.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// Divide a string em Index e Replication Degree
				token = ans.split(" ");
			}

			int index = Integer.parseInt(token[0]);
			int rep_degree = Integer.parseInt(token[1]);
			System.out.println("File: " + local_files.get(fileIDs.get(index)).getName());		

			// Inicia as Threads
			if(startThreads){
				connectAndStartThreads();
			}

			sendFileToBackup(fileIDs, index, rep_degree);
		}
	}

	public void sendFileToBackup(Vector<String> fileIDs, int index,
			int rep_degree) {
		MulticastSocket sk;
		try {
			sk = new MulticastSocket();
			sk.joinGroup(InetAddress.getByName(ip_backup));
			local_files.get(fileIDs.get(index)).setReplicationDegree(rep_degree); // Actualiza o Replication Degree

			if (local_files.get(fileIDs.get(index)).generateChunks()) {
				local_files.get(fileIDs.get(index)).sendChunks(sk, backupGroupAddress, _MDBPORT);
				System.out.println("File chunks sent...");
			}else{
				System.err.println("Error creating chunks...");
			}

			//		local_files.get(fileIDs.get(index)).RegenerateFileFromChunks();
			sk.close();
			System.out.println("Ending...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 */
	public void connectAndStartThreads() {
		try {
			startThreads = false;

			controlGroupAddress = InetAddress.getByName(ip_control);
			restoreGroupAddress = InetAddress.getByName(ip_restore);
			backupGroupAddress = InetAddress.getByName(ip_backup);

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


			//		c.stopMe();
			//		b.stopMe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fillLocalArea() {

		//FOUND search-id sha size name
		if(search.getFilenames().size() > 0){
			for(int i = 0; i < search.getFilenames().size(); i++){
				File fileSize = new File(search.getFilenames().get(i)); // Para ir buscar o size
				FileInputStream file = null;
				try {
					file = new FileInputStream(search.getFilenames().get(i));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				String fileID = null;
				try {
					fileID = getHexValue(file);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				local_files.put(fileID, new BackupFile(search.getFilenames().get(i), 1)); // Coloca file na area local

			}


		}		
	}



	/**
	 * @param file
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private String getHexValue(FileInputStream file)
			throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] dataBytes = new byte[chunkSize];

		int nread = 0;
		while ((nread = file.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		};
		byte[] mdbytes = md.digest();

		//convert the byte to hex format
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
	public static boolean isParsableToInt(String i)
	{
		try
		{
			Integer.parseInt(i);
			return true;
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
	}
	public static void main(String args[]){
		MFSS m = new MFSS();
	}


}

