package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB {

	private Connection conn;
	private String name;
	private PreparedStatement insertFile, selectNumChunksFile, deleteFilebyName, selectFileIDByName, selectFileNameByID;
	/**
	 * Instancia uma objecto DB que liga a uma base de dados SQLite 
	 * @param databaseName
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public DB(String databaseName) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");		
		this.name = databaseName;
		conn = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db"); 
		insertFile = conn.prepareStatement("insert into files values (?,?,?);");
		selectNumChunksFile = conn.prepareStatement("SELECT numberOfChunks FROM files WHERE files.[fileID]=?");
		selectFileNameByID = conn.prepareStatement("SELECT fileName FROM files WHERE files.[fileID]=?");
		selectFileIDByName = conn.prepareStatement("SELECT fileID FROM files WHERE files.[fileName]=?");
		deleteFilebyName = conn.prepareStatement("DELETE FROM files WHERE fileID=?");

	}
	
	
	/**
	 * Adds a file registry to the database.
	 * @param fileID
	 * @param fileName
	 * @param numberOfChunksSent
	 */
	public void addFile(String fileID, String fileName, int numberOfChunksSent){
		try {

			insertFile.setString(1, fileID);			
			insertFile.setString(2, fileName);	
			insertFile.setInt(3, numberOfChunksSent);		
			insertFile.executeUpdate();

		}catch(SQLException e){
			System.err.println(e.getErrorCode()+" - "+e.getMessage());
			System.err.println(e.getSQLState());
		}
	}

	/**
	 * Retrieves the number of chunks a file by the fileID has
	 * @param fileID
	 * @return chunkNo
	 */
	public int getChunkNumberbyFileID(String fileID){
		int chunkNo = -1;
		try {
			selectNumChunksFile.setString(1, fileID);			
			ResultSet result = selectNumChunksFile.executeQuery();

			try{	
				result.next();
				chunkNo = result.getInt("numberOfChunks");
				return chunkNo;
			}finally{
				result.close();
			}
		} catch (SQLException e) {
			System.err.println("Error executing getChunkNumberbyFileID query");
			System.err.println(e.getErrorCode()+" - "+e.getMessage());
		}
		return chunkNo;
	}

	/**
	 * Retrieves the fileID of a file by it's file name.
	 * @param fileName
	 * @return
	 */
	public String getFileIDbyFileName(String fileName){
		String fileID = "";
		try {
			selectFileIDByName.setString(1, fileName);			
			ResultSet result = selectFileIDByName.executeQuery();

			try{	
				result.next();
				fileID = result.getString("fileID");						
			}finally{
				result.close();
			}
			return fileID;
		} catch (SQLException e) {
			System.err.println("Error executing getFileIDbyFileName query");
			System.err.println(e.getErrorCode()+" - "+e.getMessage());
			return null;
		}		
	}
	
	/**
	 * Retrieves the file name of a file by it's file ID.
	 * @param fileID
	 * @return fileName
	 */
	public String getFileNamebyFileID(String fileID){
		String fileName = "";
		try {
			selectFileNameByID.setString(1, fileID);			
			ResultSet result = selectFileNameByID.executeQuery();

			try{	
				result.next();
				fileName = result.getString("fileName");						
			}finally{
				result.close();
			}
			return fileName;
		} catch (SQLException e) {
			System.err.println("Error executing getFileNamebyFileID query");
			System.err.println(e.getErrorCode()+" - "+e.getMessage());
			return null;
		}		
	}
	
	/**
	 * Deletes a file by it's name
	 * @param fileName
	 */
	public void deleteFilebyName(String fileName){
		try {
			deleteFilebyName.setString(1, fileName);			
			ResultSet result = deleteFilebyName.executeQuery();

			try{	
				result.next();
				fileName = result.getString("fileName");						
			}finally{
				result.close();
			}

		} catch (SQLException e) {
			System.err.println("Error executing Delete on file: "+fileName);
			System.err.println(e.getErrorCode()+" - "+e.getMessage());			
		}	
	}

	/**
	 * closes the connection to the database
	 */
	public void close(){
		try {
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error closing connection to \""+name+"\" database...");
			e.printStackTrace();
		}
	}
	
	/**
	 * Main para testar a base de dados
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {	        
		DB database = new DB("db");
		database.addFile("1ijfj2382u5m98u85u2","bino.jpg", 3);
		database.addFile("3jf92rf28ur2mu24umr","lel.gif", 5);
		database.addFile("jf29r924r9249ru29ru","OMG.png", 10);
		System.out.println(database.getChunkNumberbyFileID("1ijfj2382u5m98u85u2"));
		System.out.println(database.getFileNamebyFileID("1ijfj2382u5m98u85u2"));
		System.out.println(database.getFileIDbyFileName("lel.gif"));
		String bla = database.getFileIDbyFileName("lel.gif");
		if(bla == null)
			System.out.println("NOPE");
		
		database.close();
	}
}
