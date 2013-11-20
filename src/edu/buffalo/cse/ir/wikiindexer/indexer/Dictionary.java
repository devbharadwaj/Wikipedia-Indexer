
/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author nikhillo
 * An abstract class that represents a dictionary object for a given index
 */
//Added Serializable interface
public abstract class Dictionary implements Writeable{
	/**
	 * 
	 */

	protected Properties props;
	protected INDEXFIELD field;
	protected Map<String, Integer> tokenPostingForwardMap;
	protected Map<Integer,String> tokenPostingReverseMap;
	
	public Dictionary (Properties props, INDEXFIELD field) {
		//TODO Implement this method
		this.field=field;
		this.props=props;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */

	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		try{			
	
			File file = new File("sharedDictionary.dat");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw,10000);
			String mapping;
								
			for (Entry<String, Integer> etr : this.tokenPostingForwardMap.entrySet()) {
				mapping="<<"+etr.getKey()+"&#"+etr.getValue()+">>";
				bw.write(mapping);
			}
			bw.close();			
		}catch(Exception e){
			System.out.println("Exception occurred");
			e.printStackTrace();
		}
	}
	public void readFromDisk(){
		RandomAccessFile randomAccessFile;
		try {
			randomAccessFile = new RandomAccessFile("sharedDictionary.dat", "r");
			FileChannel inChannel = randomAccessFile.getChannel();
			MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			Charset charset=Charset.forName("UTF-8");
			CharBuffer cb=charset.decode(buffer);
			String readString=cb.toString();
			buffer.clear();
			buffer=null;		
			inChannel.close();
			randomAccessFile.close();
			StringBuffer sb=new StringBuffer(readString);
			readString=null;
			String temp;
			String[] temparr;
			int startIndex=-1;
			startIndex=sb.indexOf("<<");
			if(startIndex!=-1){				
				this.tokenPostingForwardMap=new HashMap<String,Integer>(1000);
				while(startIndex!=-1){
					temp=sb.substring(startIndex+2,sb.indexOf(">>",startIndex));
					temparr=temp.split("(&#)");
					this.tokenPostingForwardMap.put(temparr[0],Integer.valueOf(temparr[1]));	
					this.tokenPostingReverseMap.put(Integer.valueOf(temparr[1]), temparr[0]);	
					startIndex=sb.indexOf("<<",startIndex+2);
				}
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("c1");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("c2");
			e.printStackTrace();
		}

	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

		this.props=null;
		this.field=null;
		this.tokenPostingForwardMap=null;
		this.tokenPostingReverseMap=null;
	}
	
	/**
	 * Method to check if the given value exists in the dictionary or not
	 * Unlike the subclassed lookup methods, it only checks if the value exists
	 * and does not change the underlying data structure
	 * @param value: The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		//TODO Implement this method		
		if(tokenPostingForwardMap!=null){
			if(tokenPostingForwardMap.containsKey(value)){
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * MEthod to lookup a given string from the dictionary.
	 * The query string can be an exact match or have wild cards (* and ?)
	 * Must be implemented ONLY AS A BONUS
	 * @param queryStr: The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 * null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		//TODO: Implement this method (FOR A BONUS)
		return null;
	}
	
	/**
	 * Method to get the total number of terms in the dictionary
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		//TODO: Implement this method
		if(tokenPostingForwardMap!=null){
			return tokenPostingForwardMap.size();
		}
		return 0;
	}
	public String reverseLookUp(Integer Id){
		if(tokenPostingReverseMap.containsKey(Id)){
			return tokenPostingReverseMap.get(Id);
		}else{
			return null;
		}	
	}

	public Map<String, Integer> getTokenPostingForwardMap() {
		return tokenPostingForwardMap;
	}

	public Map<Integer, String> getTokenPostingReverseMap() {
		return tokenPostingReverseMap;
	}
}
