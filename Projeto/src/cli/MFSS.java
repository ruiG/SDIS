package cli;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import threads.Backup;
import threads.Control;
import threads.Restore;
import dataStruct.BackupFile;
import dataStruct.BackupFile.NoSuchFileException;
import dataStruct.Message;
import database.DB;


public class MFSS {	

	//IP_CONTROL PORT_CONTROL IP_BACKUP PORT_BACKUP IP_RESTORE PORT_RESTORE

	//*************Public Variables*****************

	public static final int _RANDOMSLEEPTIME = 401;
	public static final int _TTL = 1;	
	public static final String _CRLF = "\r\n";
	public static final char _VERSIONMAJOR = '1';
	public static final char _VERSIONMINOR = '0';
	public static boolean debugmode;
	public static Thread t;
	public static int maximum_disk_space = 3200000; // = 50 chunks (64000 Bytes cada chunk)
	public static int used_disk_space = 0;	

	//*************Private Variables*****************

	static int _MDRPORT = 3002; // Recover
	static int _MCPORT = 3000; // Control
	static int _MDBPORT = 3001; // Backup
	static InetAddress controlGroupAddress = null;
	static InetAddress restoreGroupAddress = null;
	static InetAddress backupGroupAddress = null;
	static boolean startThreads = true;
	static boolean returnToMainMenu = false;
	static HashMap<String, BackupFile> local_files;
	static int chunkSize = 64000;
	static Backup backup;
	static Control control;
	static Restore restore;
	static int key;
	static String ip_control="224.0.0.1";
	static String ip_restore="224.0.0.2";
	static String ip_backup="224.0.0.3";
	static Scanner scanner = new Scanner( System.in );
	static DB database;

	//**************State variables******************
	public volatile static String sentChunk = null;
	public volatile static String sentID = null;
	public volatile static String requestedFileID = null;
	public volatile static String requestedChunkNr = null;

	public static void main(String args[]){
		MFSS.t=Thread.currentThread();
		boolean exit = false;
		debugmode = true;	

		parseArgs(args);

		if(!connectToDatabase())
			return;
		startThreads();		
		do{
			int option = mainMenu();
			switch (option) {
			case 1:
				manageBkFiles();
				break;

			case 2:
				backupFileMenu();
				break;

			case 3:
				switchDebugmode();
				break;


			case 0:
				shutdown();
				return;

			default:
				break;
			}
			clearScreen();
		}while(!exit);
	}



	private static void parseArgs(String[] args) {
		// TODO parseArgs		
	}



	private static boolean connectToDatabase() {
		try {
			database = new DB("db");
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Error connecting to database");
			return false;
		}
	}



	private static int mainMenu() {
		String key = "";
		System.out.println("-------------------------MULTICAST FILE STORAGE SYSTEM-------------------------");

		nl();nl();
		System.out.println("1 ~ Manage Backed-up files");
		nl();
		System.out.println("2 ~ Back-up a file");
		nl();
		System.out.println("3 ~ Enable/disable debug mode");
		nl();
		System.out.println("0 ~ Exit");
		nl();nl();
		do{
			System.out.println("Please choose an option: ");		
			key = scanner.next();
			if(( key.equals("1") || key.equals("2") || key.equals("3") || key.equals("0") )){
				return Integer.parseInt(key);
			}else{
				return -1;
			}
		}while(true);	
	}

	private static void switchDebugmode() {
		if(debugmode)
			debugmode = false;
		else
			debugmode = true;
		System.out.println("Debug mode Switched...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}

	private static void backupFileMenu() {
		System.out.println("-------------------------MULTICAST FILE STORAGE SYSTEM-------------------------");
		nl();
		System.out.println("[                          [SEND A FILE TO BACKUP]                            ]");
		nl();
		System.out.print("Please input a file to send: ");
		String filename = scanner.next();
		if(new File(filename).exists()){
			int repDeg = -1;
			do{
				nl();
				System.out.print("Please input the desired replication degree: ");
				try {
					repDeg = scanner.nextInt();
					sendFileToBackup(filename, repDeg);
					return;
				} catch (InputMismatchException e) {
					nl();
					System.out.println("invalid input please retry");
					continue;
				}
			}while(repDeg < 0 && repDeg > 9);

		}
	}

	private static void shutdown() {
		try{
			backup.stopMe();
			backup.closeSocket();
			restore.stopMe();
			restore.closeSocket();
			control.stopMe();
			control.closeSocket();
		}catch(SocketException e){}
	}

	private static void manageBkFiles() {
		ArrayList<ArrayList<String>> list;
		System.out.println("-------------------------MULTICAST FILE STORAGE SYSTEM-------------------------");
		nl();
		System.out.println("[                               [BACKED UP FILES]                             ]");
		System.out.println("[[      filename                                        ][  number of chunks  ]");
		nl();
		list = database.listAllFiles();
		if(list != null){
			if(!list.isEmpty()){
				for (int i = 0; i < list.size(); i++) {
					System.out.println("["+list.get(i).get(0)+"]["+list.get(i).get(2)+"]");
				}
				nl();
				System.out.println("[               [ 1 - Restore file  ]   [ 2 - Delete File   ]                   ]");
				manageBkFilesAux();
			}
			else {
				nl();
				System.out.println("[                                  [No files]                                 ]");
			}
		}else {
			System.err.println("Error on database querry");
		}
		try {
			System.in.read();
		} catch (IOException e) {}
	}

	private static void manageBkFilesAux() {
		String key = scanner.next();
		if( key.equals("1") ){
			while(true){
				System.out.println("Please input the filename:");
				String name = scanner.next();
				String ID = database.getFileIDbyFileName(name);
				if( ID != null){
					if(recoverFileFormBackup(name,ID))
						System.out.println("Restore completed...");
					else
						System.err.println("Restore failed...");					
					return;
				}
			}
		}else if(key.equals("2")){
			while(true){
				System.out.println("Please input the filename:");
				String name = scanner.next();
				String ID = database.getFileIDbyFileName(name);
				if( ID != null){
					deleteFileFromBackup(name,ID);
					return;
				}
			}
		}		
	}

	private static boolean recoverFileFormBackup(String name, String iD) {
		System.out.println("Save as:");
		String svName = scanner.next();
		MulticastSocket sk;
		try {
			sk = new MulticastSocket();
			sk.joinGroup(InetAddress.getByName(ip_control));
			nl();
			int numbOfChk = database.getChunkNumberbyFileID(iD);
			requestedFileID = iD;
			for (int j = 0; j < numbOfChk; j++) {				
				int timeout = 400, to = 0;
				requestedChunkNr = Integer.toString(j);				
				while(to < 5){
					try{
						Message.sendMessage(sk, controlGroupAddress, _MCPORT,Message.GETCHUNK(iD, Integer.toString(j)));
								Thread.sleep(timeout);
					}catch(InterruptedException ie){
						System.out.println("Chunk received!");
						break;
					}
					timeout*=2;
					to++;				
				}
				if(to == 5) return false;		
			}	
			requestedChunkNr = null;
			requestedFileID = null;
			new BackupFile(svName,iD,numbOfChk-1).RegenerateFileFromChunks();
			return true;
		} catch (IOException e) {
			System.err.println("Error joining group: "+ip_backup);
			return false;
		}	
	}



	private static void deleteFileFromBackup(String name, String iD) {
		MulticastSocket sk;
		try {
			sk = new MulticastSocket();
			sk.joinGroup(InetAddress.getByName(ip_control));
			nl();
			for (int i = 0; i < 3; i++) {
				Message.sendMessage(sk, controlGroupAddress, _MCPORT,Message.DELETE(iD));
			}
			database.deleteFilebyName(name);
			sk.close();
			System.out.println("Ending...");
		} catch (IOException e) {
			System.err.println("Error joining group: "+ip_backup);
		}
	}



	public static void sendFileToBackup(String filename, int rep_degree) {
		MulticastSocket sk;
		try {
			sk = new MulticastSocket();
			sk.joinGroup(InetAddress.getByName(ip_backup));
			nl();
			BackupFile bf = new BackupFile(filename, rep_degree);

			if(bf.generateChunks()){
				if(bf.sendChunks(sk, backupGroupAddress, _MDBPORT)){
					System.out.println("File chunks sent...");
					database.addFile(bf.getID(),bf.getName(), bf.getNrChunks()+1);
					System.out.println("File info added to the database...");
				}					
				else
					System.err.println("Error sending chunks...");
			}else{
				System.err.println("Error creating chunks...");
			}
			sk.close();
			System.out.println("Ending...");
		} catch (IOException e) {
			System.err.println("Error joining group: "+ip_backup);
		}
		catch (NoSuchFileException e) {
			nl();
			System.err.println("No such file on directory...");
		}
	}

	private static boolean startThreads() {
		try {
			startThreads = false;

			controlGroupAddress = InetAddress.getByName(ip_control);
			restoreGroupAddress = InetAddress.getByName(ip_restore);
			backupGroupAddress = InetAddress.getByName(ip_backup);

			if(debugmode)
				System.out.println("Starting M.F.S.S...");

			control = new Control(controlGroupAddress,_MCPORT);
			if(debugmode){
				System.out.println("Control object initialized with:");
				System.out.println("\t "+"Control group address: "+controlGroupAddress.getHostAddress());
				System.out.println("\t "+"MCPORT: "+_MCPORT);
			}

			restore = new Restore(restoreGroupAddress, _MDRPORT);
			if(debugmode){
				System.out.println("Restore object initialized with:");
				System.out.println("\t "+"Restore group address: "+restoreGroupAddress.getHostAddress());
				System.out.println("\t "+"MDRPORT: "+_MDRPORT);
			}

			backup  = new Backup(backupGroupAddress, _MDBPORT);
			if(debugmode){
				System.out.println("Backup object initialized with:");
				System.out.println("\t "+"Backup group address: "+backupGroupAddress.getHostAddress());
				System.out.println("\t "+"MDRPORT: "+_MDRPORT);
			}



			control.start();
			if(debugmode)
				System.out.println("Control thread started...");
			backup.start();
			if(debugmode)
				System.out.println("Backup thread started...");
			restore.start();
			if(debugmode)	
				System.out.println("Restore thread started...");
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
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

	private static void clearScreen() {
		try {
			if(OSCheck.isWindows())
				Runtime.getRuntime().exec("cls");
			if(OSCheck.isMac())
				Runtime.getRuntime().exec("clear");
			if(OSCheck.isUnix())
				Runtime.getRuntime().exec("clear");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private static void nl(){
		System.out.println();
	}
}

