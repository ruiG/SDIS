package dataStruct;

public class Header{
		
		public String header="";
		
		public Header(String messageType, 
					  char versionMajor, 
					  char versionMinor, 
					  String fileId, 
					  String chunkID, 
					  char replicationDeg){
					  
			header=messageType+ " " + versionMajor + "." + versionMinor +  " " + fileId + " " + chunkID;
			if (replicationDeg != 'N'){
				header+= " " + replicationDeg;	
			} 
			header+="\0x0D\0x0A";
			//Add a empty line
			header+="\0x0D\0x0A";
		}
		
		public Header(String messageType, 
					  char versionMajor, 
					  char versionMinor, 
					  String fileId, 
					  String chunkID){
					  	this(messageType, versionMajor, versionMinor, fileId, chunkID,'N');
					  }
		
		public Header(String messageType, String fileId){
			header=messageType + " " + fileId + "\n\r\n\r";
		}
		
		public String get(){
			return header;
		}
}