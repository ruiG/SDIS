package dataStruct;
import java.io.File;
import java.util.Vector;

public class SearchDir {
	File directoryOfPdfs;
	private Vector<String> filenames; // Ficheiros na area local
	Vector<String> resposta = null;
	int numFiles;
	
	public SearchDir(){
		directoryOfPdfs = new File("."); // Directory is just a list of files
		File[] listOfFiles = directoryOfPdfs.listFiles(); 
		setFilenames(new Vector<String>());
		
		for(int i = 0; i < listOfFiles.length; i++){
			 if (listOfFiles[i].isFile()){
				 getFilenames().add(listOfFiles[i].getName());
			 }
		}
	}

	public void setFilenames(Vector<String> filenames) {
		this.filenames = filenames;
	}

	public Vector<String> getFilenames() {
		return filenames;
	}
}
	
